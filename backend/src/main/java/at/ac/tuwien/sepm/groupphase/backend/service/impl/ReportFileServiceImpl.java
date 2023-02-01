package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportDownloadResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportGenerationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.enums.ExcelReportGenerationRequestInclusionRule;
import at.ac.tuwien.sepm.groupphase.backend.entity.ReportFile;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.FileInputException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UnauthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Station;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Variable;
import at.ac.tuwien.sepm.groupphase.backend.report.Report;
import at.ac.tuwien.sepm.groupphase.backend.report.ranking.GradeRankingResults;
import at.ac.tuwien.sepm.groupphase.backend.report.ranking.GradingGroupRankingResults;
import at.ac.tuwien.sepm.groupphase.backend.report.ranking.ParticipantRankingResults;
import at.ac.tuwien.sepm.groupphase.backend.report.ranking.StationRankingResults;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReportFileRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingGroupService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReportFileService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReportService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.ExcelReportGenerationRequestDtoValidator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportFileServiceImpl implements ReportFileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Value("${reportsFolder}")
    private String reportsFolder;
    private final ReportFileRepository reportFileRepository;
    private final SessionUtils sessionUtils;
    private final GradingGroupService gradingGroupService;
    private final ReportService reportService;
    private final CompetitionRepository competitionRepository;
    private final GradingGroupRepository gradingGroupRepository;
    private final CompetitionService competitionService;
    private final ExcelReportGenerationRequestDtoValidator excelReportGenerationRequestDtoValidator;

    @Autowired
    public ReportFileServiceImpl(
        ReportFileRepository reportFileRepository, SessionUtils sessionUtils,
        GradingGroupService gradingGroupService, ReportService reportService,
        CompetitionRepository competitionRepository, GradingGroupRepository gradingGroupRepository,
        CompetitionService competitionService,
        ExcelReportGenerationRequestDtoValidator excelReportGenerationRequestDtoValidator) {
        this.reportFileRepository = reportFileRepository;
        this.sessionUtils = sessionUtils;
        this.gradingGroupService = gradingGroupService;
        this.reportService = reportService;
        this.competitionRepository = competitionRepository;
        this.gradingGroupRepository = gradingGroupRepository;
        this.competitionService = competitionService;
        this.excelReportGenerationRequestDtoValidator = excelReportGenerationRequestDtoValidator;
    }

    @Override
    public ExcelReportDownloadResponseDto downloadExcelReport(ExcelReportGenerationRequestDto requestDto) {
        LOGGER.debug("downloadExcelReport({})", requestDto);

        excelReportGenerationRequestDtoValidator.validate(requestDto);
        if (!sessionUtils.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }
        if (competitionRepository.findById(requestDto.getCompetitionId()).isEmpty()) {
            throw new NotFoundException("Such competition was not found");
        }
        if (!gradingGroupService.checkAllGradingGroupsHaveReports(requestDto.getCompetitionId()).isDownloadable()) {
            throw new ConflictException("Reports are not downloadable yet");
        }
        var options = competitionService.getCurrentUserReportDownloadInclusionRuleOptions(requestDto.getCompetitionId());
        switch (requestDto.getInclusionRule()) {
            case ONLY_YOU -> {
                if (!options.getCanGenerateReportForSelf()) {
                    throw new ForbiddenException("You are not registered");
                }
            }
            case ONLY_YOUR_TEAM -> {
                if (!options.getCanGenerateReportForTeam() && !options.getCanGenerateReportForSelf()) {
                    throw new ConflictException("You neither manage any participating members nor you are registered");
                }
            }
            case ALL_PARTICIPANTS -> {

            }
            default -> {
                throw new ConflictException("Server error: unimplemented inclusion rule");
            }
        }

        cleanOutdatedReportFiles();
        var foundReportFileOpt = reportFileRepository.findFirstByCreatorIdAndCompetitionId(
            sessionUtils.getSessionUser().getId(), requestDto.getCompetitionId(), requestDto.getInclusionRule().getValue()
        );
        if (foundReportFileOpt.isPresent()) {
            var foundReportFile = foundReportFileOpt.get();
            if (reportFileExists(foundReportFile.getName())) {
                return new ExcelReportDownloadResponseDto(foundReportFileOpt.get().getName());
            }

            reportFileRepository.delete(foundReportFile);
        }

        var generatedReportInfo = generateExcelReportAndSaveLocally(requestDto);
        reportFileRepository.save(new ReportFile(
            generatedReportInfo.getName(),
            LocalDateTime.now(),
            requestDto.getInclusionRule().equals(ExcelReportGenerationRequestInclusionRule.ALL_PARTICIPANTS)
                ? LocalDateTime.now().plusDays(30)
                : LocalDateTime.now().plusDays(1),
            sessionUtils.getSessionUser(),
            requestDto.getInclusionRule(),
            competitionRepository.findById(requestDto.getCompetitionId()).get()
        ));

        return generatedReportInfo;
    }

    private void cleanOutdatedReportFiles() {
        LOGGER.debug("cleanOutdatedReportFiles()");

        var toDeleteEntries = reportFileRepository.findAllByDeleteAfterBefore(LocalDateTime.now());
        toDeleteEntries.forEach(
            rf -> {
                try {
                    Files.deleteIfExists(Path.of(reportsFolder, rf.getName()));
                } catch (IOException e) {
                    // Should not happen.
                    throw new RuntimeException(e);
                }
            }
        );

        reportFileRepository.deleteAllById(toDeleteEntries.stream().map(ReportFile::getId).toList());
    }

    private ExcelReportDownloadResponseDto generateExcelReportAndSaveLocally(ExcelReportGenerationRequestDto requestDto) {
        LOGGER.debug("generateExcelReportAndSaveLocally({})", requestDto);

        excelReportGenerationRequestDtoValidator.validate(requestDto);
        var filteredReport = reportService.generateFilteredReport(requestDto);
        var workbook = convertReportToExcel(filteredReport);
        var nameForExcel = figureOutExcelName(requestDto);

        return saveExcelLocally(workbook, nameForExcel);
    }

    private String figureOutExcelName(ExcelReportGenerationRequestDto requestDto) {
        LOGGER.debug("figureOutExcelName({})", requestDto);

        var foundCompetitionName = competitionRepository.findById(requestDto.getCompetitionId()).get().getName();

        foundCompetitionName = foundCompetitionName.replaceAll("[!@#$%^&*()|+=/_]", "-");
        if (foundCompetitionName.length() > 256) {
            foundCompetitionName = foundCompetitionName.substring(0, 256);
        }

        String inclusionRuleAsString = "";
        switch (requestDto.getInclusionRule()) {
            case ALL_PARTICIPANTS -> {
                inclusionRuleAsString = "all-participants";
            }
            case ONLY_YOU -> {
                var user = sessionUtils.getSessionUser();
                inclusionRuleAsString = user.getFirstName() + "_" + user.getLastName();
                inclusionRuleAsString = inclusionRuleAsString.replaceAll("[!@#$%^&*()|+=/_]", "-");
                if (inclusionRuleAsString.length() > 128) {
                    inclusionRuleAsString = inclusionRuleAsString.substring(0, 128);
                }
            }
            case ONLY_YOUR_TEAM -> {
                var user = sessionUtils.getSessionUser();
                inclusionRuleAsString = user.getFirstName() + "_" + user.getLastName();
                inclusionRuleAsString = inclusionRuleAsString.replaceAll("!@#\\$%\\^&*\\(\\)\\|+=/", "_");
                if (inclusionRuleAsString.length() > 123) {
                    inclusionRuleAsString = inclusionRuleAsString.substring(0, 123);
                }
                inclusionRuleAsString += "-team";
            }
            default -> {
                throw new IllegalStateException("NOT ALL ENUM MEMBERS WERE IMPLEMENTED");
            }
        }

        return foundCompetitionName + "__results__" + inclusionRuleAsString;
    }

    private boolean reportFileExists(String name) {
        LOGGER.debug("reportFileExists({})", name);

        return (new File(reportsFolder + "/" + name)).exists();
    }

    private ExcelReportDownloadResponseDto saveExcelLocally(XSSFWorkbook workbook, String name) {
        LOGGER.debug("saveExcelLocally({})", name);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            workbook.write(out);
        } catch (IOException e) {
            throw new RuntimeException("Should not happen");
        }
        var path = Paths.get(reportsFolder);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new FileInputException(e);
            }
        }

        String filename = name  + ".xlsx";
        try (OutputStream outputStream = new FileOutputStream(reportsFolder + "/" + filename)) {
            out.writeTo(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ExcelReportDownloadResponseDto(filename);
    }

    private XSSFWorkbook convertReportToExcel(Report report) {
        LOGGER.debug("convertReportToExcel({})", report);

        XSSFWorkbook workbook = new XSSFWorkbook();
        for (GradingGroupRankingResults gradingGroupRankingResult : report.getGradingGroupRankingResults()) {
            XSSFSheet sheet = workbook.createSheet(gradingGroupRankingResult.getName());

            var foundGradingSystemOpt = gradingGroupRepository.findById(gradingGroupRankingResult.getId());
            if (foundGradingSystemOpt.isEmpty()) {
                throw new RuntimeException("Report is malformed. Grading Group id mismatch");
            }
            int cellIndex = 0;
            Row row = sheet.createRow(0);
            row.createCell(cellIndex++).setCellValue("Rang");
            row.createCell(cellIndex++).setCellValue("Teilnehmer");
            // to separate station + variable results from other info.
            cellIndex++;

            var foundGradingSystem = new GradingSystem(foundGradingSystemOpt.get().getGradingSystem().getFormula());
            var stationVariableIndexing = new HashMap<Long, HashMap<Long, Integer>>();
            var stationIndexing = new HashMap<Long, Integer>();
            for (Station station : foundGradingSystem.stations) {
                if (stationVariableIndexing.containsKey(station.getId())) {
                    throw new RuntimeException("Malformed grading system. Duplicate station ids");
                }
                var variableIndexing = new HashMap<Long, Integer>();
                stationVariableIndexing.put(station.getId(), variableIndexing);
                for (Variable variable : station.getVariables()) {
                    row.createCell(cellIndex).setCellValue(variable.getDisplayName());
                    variableIndexing.put(variable.getId(), cellIndex++);
                }
                row.createCell(cellIndex).setCellValue(station.getDisplayName());
                stationIndexing.put(station.getId(), cellIndex++);
                cellIndex++;
            }
            int finalResultIndex = cellIndex;
            row.createCell(finalResultIndex).setCellValue("Endergebnis");

            int rowIndex = 1;
            for (Map.Entry<Long, ParticipantRankingResults> participantsRankingResult : gradingGroupRankingResult.getParticipantsRankingResults()) {
                row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(participantsRankingResult.getKey());
                row.createCell(1).setCellValue(participantsRankingResult.getValue().getName());

                for (StationRankingResults stationsRankingResult : participantsRankingResult.getValue().getStationsRankingResults()) {
                    var currentStationVariableIndexing = stationVariableIndexing.get(stationsRankingResult.getId());
                    if (currentStationVariableIndexing == null) {
                        throw new RuntimeException("Report is malformed. Station indexes mismatch with grading group");
                    }
                    for (GradeRankingResults gradesRankingResult : stationsRankingResult.getGradesRankingResults()) {
                        var foundVariableIndexingEntry = currentStationVariableIndexing.get(gradesRankingResult.getId());
                        if (foundVariableIndexingEntry == null) {
                            throw new RuntimeException("Report is malformed. Variable indexes mismatch");
                        }

                        row.createCell(foundVariableIndexingEntry).setCellValue(gradesRankingResult.getResults());
                    }

                    var foundStationIndexingEntry = stationIndexing.get(stationsRankingResult.getId());
                    if (foundStationIndexingEntry == null) {
                        throw new RuntimeException("Report is malformed. Station indexes mismatch");
                    }

                    row.createCell(foundStationIndexingEntry).setCellValue(stationsRankingResult.getResults());
                }

                row.createCell(finalResultIndex).setCellValue(participantsRankingResult.getValue().getResults());
            }

        }

        return workbook;
    }
}
