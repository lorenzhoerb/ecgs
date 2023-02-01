package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportGenerationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PageableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantCompetitionResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.enums.ExcelReportGenerationRequestInclusionRule;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.ManagedBy;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.report.GradableEntityInfo;
import at.ac.tuwien.sepm.groupphase.backend.report.Report;
import at.ac.tuwien.sepm.groupphase.backend.report.ranking.ParticipantRankingResults;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReportRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradeService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReportService;
import at.ac.tuwien.sepm.groupphase.backend.service.helprecords.GradingGroupTitleAndFormula;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.ExcelReportGenerationRequestDtoValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReportServiceImpl implements ReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CompetitionRepository competitionRepository;
    private final RegisterToRepository registerToRepository;
    private final GradeService gradeService;
    private final ApplicationUserRepository applicationUserRepository;
    private final SessionUtils sessionUtils;
    private final ManagedByRepository managedByRepository;
    private final GradingGroupRepository gradingGroupRepository;
    private final ReportRepository reportRepository;
    private final CompetitionService competitionService;
    private final ExcelReportGenerationRequestDtoValidator excelReportGenerationRequestDtoValidator;

    private final CompetitionMapper competitionMapper;

    @Autowired
    public ReportServiceImpl(
        CompetitionRepository competitionRepository, RegisterToRepository registerToRepository, GradeService gradeService,
        ApplicationUserRepository applicationUserRepository, SessionUtils sessionUtils,
        ManagedByRepository managedByRepository, GradingGroupRepository gradingGroupRepository,
        ReportRepository reportRepository, CompetitionMapper competitionMapper,
        CompetitionService competitionService, ExcelReportGenerationRequestDtoValidator excelReportGenerationRequestDtoValidator) {
        this.competitionRepository = competitionRepository;
        this.registerToRepository = registerToRepository;
        this.gradeService = gradeService;
        this.applicationUserRepository = applicationUserRepository;
        this.sessionUtils = sessionUtils;
        this.managedByRepository = managedByRepository;
        this.gradingGroupRepository = gradingGroupRepository;
        this.reportRepository = reportRepository;
        this.competitionMapper = competitionMapper;
        this.competitionService = competitionService;
        this.excelReportGenerationRequestDtoValidator = excelReportGenerationRequestDtoValidator;
    }

    @Override
    public void calculateResultsOfCompetition(Long competitionId) {
        LOGGER.debug("calculateResultsOfCompetition({})", competitionId);

        var competitionOpt = competitionRepository.findById(competitionId);
        if (competitionOpt.isEmpty()) {
            throw new NotFoundException("Such competition was not found");
        }
        var competition = competitionOpt.get();
        if (!sessionUtils.isCompetitionManager()
            || !competition.getCreator().getId().equals(sessionUtils.getSessionUser().getId())) {
            throw new ForbiddenException("No permissions to do this");
        }

        var report = new Report();
        // gradingGroupId -> formula
        Map<Long, GradingGroupTitleAndFormula> mapOfAllGradingGroupIdsToFormulas = new HashMap<>();

        // Setting station/grades results and names;
        for (var gradingGroup : competition.getGradingGroups()) {
            if (!mapOfAllGradingGroupIdsToFormulas.containsKey(gradingGroup.getId())) {
                mapOfAllGradingGroupIdsToFormulas.put(
                    gradingGroup.getId(),
                    new GradingGroupTitleAndFormula(
                        gradingGroup.getTitle(),
                        gradingGroup.getGradingSystem().getFormula()
                    )
                );
            }

            GradingSystem parsedGradingSystem = new GradingSystem(gradingGroup.getGradingSystem().getFormula());
            for (var station : parsedGradingSystem.stations) {
                var stationGrades = gradeService.getAllGradesForStation(competitionId, gradingGroup.getId(), station.getId());
                for (var stationGrade : stationGrades) {
                    if (stationGrade.result() == null) {
                        throw new ConflictException("Not all grades were entered");
                    }
                    report.putStationResult(
                        stationGrade.gradingGroupId(),
                        stationGrade.participantId(),
                        stationGrade.stationId(),
                        new GradableEntityInfo(
                            stationGrade.savedStationThatCalculatedResult().getDisplayName(),
                            stationGrade.result())
                    );
                    for (var grade : stationGrade.savedStationThatCalculatedResult().getVariables()) {
                        report.putGradeResult(
                            stationGrade.gradingGroupId(),
                            stationGrade.participantId(),
                            stationGrade.stationId(),
                            grade.getId(),
                            new GradableEntityInfo(
                                grade.getDisplayName(),
                                grade.evaluate()
                            )
                        );
                    }
                }
            }
        }

        // The rest is calculating final results of participant and setting names to grading group and participants;
        for (var gradingGroupEntry : report.entrySet()) {
            var participantIdsAndInitialsList = applicationUserRepository.findAllIdsAndInitialsById(
                StreamSupport.stream(report.getParticipantIds(gradingGroupEntry.getKey()).spliterator(), false)
                    .collect(Collectors.toList())
            );
            var participantInitialsMap = new HashMap<Long, String>();
            for (var participantIdAndInitial : participantIdsAndInitialsList) {
                participantInitialsMap.put(participantIdAndInitial.getId(), participantIdAndInitial.getInitials());
            }
            // fill in participant info
            var foundGradingGroupTitleAndFormula = mapOfAllGradingGroupIdsToFormulas.get(gradingGroupEntry.getKey());
            gradingGroupEntry.getValue().setName(foundGradingGroupTitleAndFormula.title());
            for (var participantEntry : gradingGroupEntry.getValue().entrySet()) {
                // fill in graing group info
                // For each station u bind its id with value
                participantEntry.getValue().setName(participantInitialsMap.get(participantEntry.getKey()));
                var gradingSystemForCurrentGradingGroup = new GradingSystem(
                    foundGradingGroupTitleAndFormula.formula()
                );
                for (var stationEntry : participantEntry.getValue().entrySet()) {
                    gradingSystemForCurrentGradingGroup.bindStation(
                        stationEntry.getKey(),
                        stationEntry.getValue().getResults()
                    );
                }
                // evaluate and save.
                gradingSystemForCurrentGradingGroup.validate();
                gradingSystemForCurrentGradingGroup.validateFormula();
                var stationResults = gradingSystemForCurrentGradingGroup.evaluateCurrentFormula();
                report.putParticipantResult(
                    gradingGroupEntry.getKey(),
                    participantEntry.getKey(),
                    new GradableEntityInfo(
                        participantInitialsMap.get(participantEntry.getKey()),
                        stationResults
                    )
                );
            }
        }

        report.generateRankings();

        saveReportResults(report);
    }


    @Override
    public List<ParticipantCompetitionResultDto> getParticipantResults() {
        LOGGER.debug("getParticipantResults()");

        if (!sessionUtils.isAuthenticated()) {
            throw new ForbiddenException("Not authenticated");
        }

        List<ParticipantCompetitionResultDto> results = new ArrayList<ParticipantCompetitionResultDto>();

        ApplicationUser user = sessionUtils.getSessionUser();
        Optional<List<RegisterTo>> registrationsOpt = registerToRepository.findAllByParticipantId(user.getId());

        if (registrationsOpt.isEmpty()) {
            return results;
        }

        List<RegisterTo> registrations = registrationsOpt.get();

        List<GradingGroup> finishedGroups = registrations
            .stream()
            .map(reg -> reg.getGradingGroup())
            .filter(group -> group.getReport() != null)
            .toList();

        if (finishedGroups.isEmpty()) {
            return results;
        }

        for (GradingGroup group : finishedGroups) {
            String groupResult = group.getReport().getResults();

            Report report = Report.reportFromGradingGroupRankingResultsJson(groupResult);

            Optional<Map.Entry<Long, ParticipantRankingResults>> partEntry = report.getGradingGroupRankingResults()
                .get(0)
                .getParticipantsRankingResults()
                .stream()
                .filter(patRes -> Objects.equals(patRes.getValue().getId(), user.getId()))
                .findFirst();

            if (partEntry.isEmpty()) {
                continue;
            }

            Map.Entry<Long, ParticipantRankingResults> entry = partEntry.get();


            ParticipantCompetitionResultDto res =
                new ParticipantCompetitionResultDto(
                    user.getId(),
                    competitionMapper.competitionToCompetitionViewDto(group.getCompetition()),
                    group.getTitle(),
                    entry.getKey(),
                    entry.getValue().getResults(),
                    entry.getValue().getStationsRankingResults());

            results.add(res);
        }

        return results;
    }

    @Override
    public Report generateFilteredReport(ExcelReportGenerationRequestDto requestDto) {
        LOGGER.debug("generateFilteredReport({})", requestDto);

        excelReportGenerationRequestDtoValidator.validate(requestDto);
        if (!sessionUtils.isAuthenticated()) {
            throw new ForbiddenException("Not authenticated");
        }
        var foundCompetitionOpt = competitionRepository.findById(requestDto.getCompetitionId());
        if (foundCompetitionOpt.isEmpty()) {
            throw new NotFoundException("No such competition");
        }
        var foundCompetition = foundCompetitionOpt.get();

        var downloadInclusionRuleOptions = competitionService.getCurrentUserReportDownloadInclusionRuleOptions(
            requestDto.getCompetitionId()
        );
        List<Long> includedParticipantIds = new ArrayList<>();
        switch (requestDto.getInclusionRule()) {
            case ONLY_YOU -> {
                if (!downloadInclusionRuleOptions.getCanGenerateReportForSelf()) {
                    throw new ConflictException("You don't have any results to show");
                }
                includedParticipantIds.add(sessionUtils.getSessionUser().getId());
                // if (foundCompetition.getGradingGroups().stream().anyMatch(
                //     currentUserRegistrations::contains
                // )) {
                // } else {
                // }
            }
            case ONLY_YOUR_TEAM -> {
                if (!downloadInclusionRuleOptions.getCanGenerateReportForSelf() && !downloadInclusionRuleOptions.getCanGenerateReportForTeam()) {
                    throw new ConflictException("You neither manage any participating members nor you are registered");
                }
                var members = managedByRepository.findAllByManagerIs(sessionUtils.getSessionUser());
                if (!members.isEmpty()) {
                    for (ManagedBy managedBy : members) {
                        var registrationsOfMemberToAnyGradingGroupOfCurrentCompetition =
                            registerToRepository.findAllByGradingGroupCompetitionIdAndParticipantId(
                                requestDto.getCompetitionId(), managedBy.getMember().getId()
                            );
                        if (!registrationsOfMemberToAnyGradingGroupOfCurrentCompetition.isEmpty()) {
                            includedParticipantIds.add(managedBy.getMember().getId());
                        }
                    }
                }
                // sessionUtils.getSessionUser().getMembers().stream().map(ManagedBy::getMember).forEach(
                //     member -> {
                //         if (foundCompetition.getGradingGroups().stream()
                //             .anyMatch(
                //                 gg -> member.getRegistrations().stream()
                //                     .map(RegisterTo::getGradingGroup).collect(Collectors.toSet()
                //                     ).contains(gg)
                //             )
                //         ) {
                //             includedParticipantIds.add(member.getId());
                //         }
                //     }
                // );
                if (downloadInclusionRuleOptions.getCanGenerateReportForSelf()) {
                    includedParticipantIds.add(sessionUtils.getSessionUser().getId());
                }
            }
            case ALL_PARTICIPANTS -> {

            }
            default -> {
                throw new ConflictException("Server problem: unimplemented inclusion rule");
            }
        }
        if (includedParticipantIds.isEmpty()
            && !requestDto.getInclusionRule().equals(ExcelReportGenerationRequestInclusionRule.ALL_PARTICIPANTS)) {
            throw new ConflictException("No one to generate report for");
        }
        Report report = new Report();
        foundCompetition.getGradingGroups().forEach(
            gg -> {
                report.addGradingGroupRankingResultsAsJson(gg.getReport().getResults());
                if (!requestDto.getInclusionRule().equals(ExcelReportGenerationRequestInclusionRule.ALL_PARTICIPANTS)) {
                    report.getGradingGroupRankingResults().forEach(
                        g -> {
                            g.getParticipantsRankingResults().removeIf(
                                p -> !includedParticipantIds.contains(p.getValue().getId())
                            );

                        }
                    );
                }
            }
        );

        return report;
    }

    private void saveReportResults(Report report) {
        LOGGER.debug("saveReportResults({})", report);

        var objectMapper = new ObjectMapper();
        report.getGradingGroupRankingResults().forEach(ggrr -> {
            String jsonResults;
            try {
                jsonResults = objectMapper.writeValueAsString(ggrr);
            } catch (JsonProcessingException e) {
                throw new ValidationListException("SHOULD NOT BE THE CASE", "JSON WRONGLY PARSED: " + e.getMessage());
            }
            var foundGradingGroupOpt = gradingGroupRepository.findById(ggrr.getId());
            if (foundGradingGroupOpt.isEmpty()) {
                throw new ValidationListException("SHOULD NOT BE THE CASE", "COULD NOT FIND GRADING GROUP IN DB, THAT WAS IN GRADINGS");
            }
            var foundGradingGroup = foundGradingGroupOpt.get();
            var foundReportOpt = reportRepository.findByGradingGroupIs(foundGradingGroup);
            if (foundReportOpt.isPresent()) {
                var foundReport = foundReportOpt.get();
                foundReport.setResults(jsonResults);
                foundReport.setCreated(LocalDateTime.now());
            } else {
                reportRepository.save(new at.ac.tuwien.sepm.groupphase.backend.entity.Report(
                    LocalDateTime.now(),
                    jsonResults,
                    foundGradingGroup
                ));
            }
        });
    }
}
