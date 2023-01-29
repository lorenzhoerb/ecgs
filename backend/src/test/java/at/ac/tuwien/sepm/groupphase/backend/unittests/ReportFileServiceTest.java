package at.ac.tuwien.sepm.groupphase.backend.unittests;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportGenerationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.enums.ExcelReportGenerationRequestInclusionRule;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UnauthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.JudgeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterConstraintRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReportFileRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReportRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.ReportFileService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReportFileServiceTest extends TestDataProvider {
    @Value("${reportsFolder}")
    private String testReportsFolder;
    private final ReportFileService reportFileService;
    private final ReportRepository reportRepository;
    private final GradingSystemRepository gradingSystemRepository;
    private final CompetitionRepository competitionRepository;
    private final GradingGroupRepository gradingGroupRepository;
    private final RegisterToRepository registerToRepository;
    private final JudgeRepository judgeRepository;
    private final GradeRepository gradeRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final ReportFileRepository reportFileRepository;

    private final ManagedByRepository managedByRepository;
    private final RegisterConstraintRepository registerConstraintRepository;

    @Autowired
    public ReportFileServiceTest(ReportFileService reportFileService, ReportRepository reportRepository,
                                 GradingSystemRepository gradingSystemRepository, CompetitionRepository competitionRepository,
                                 GradingGroupRepository gradingGroupRepository, RegisterToRepository registerToRepository,
                                 JudgeRepository judgeRepository, GradeRepository gradeRepository, ApplicationUserRepository applicationUserRepository,
                                 ReportFileRepository reportFileRepository, ManagedByRepository managedByRepository, RegisterConstraintRepository registerConstraintRepository) {
        this.reportFileService = reportFileService;
        this.reportRepository = reportRepository;
        this.gradingSystemRepository = gradingSystemRepository;
        this.competitionRepository = competitionRepository;
        this.gradingGroupRepository = gradingGroupRepository;
        this.registerToRepository = registerToRepository;
        this.judgeRepository = judgeRepository;
        this.gradeRepository = gradeRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.reportFileRepository = reportFileRepository;
        this.managedByRepository = managedByRepository;
        this.registerConstraintRepository = registerConstraintRepository;
    }

    @BeforeEach
    public void beforeEach() {
        registerConstraintRepository.deleteAll();
        gradeRepository.deleteAll();
        reportRepository.deleteAll();
        reportFileRepository.deleteAll();
        managedByRepository.deleteAll();
        registerToRepository.deleteAll();
        applicationUserRepository.deleteAll();
        competitionRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        gradingSystemRepository.deleteAll();
        judgeRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        registerConstraintRepository.deleteAll();
        gradeRepository.deleteAll();
        reportRepository.deleteAll();
        reportFileRepository.deleteAll();
        managedByRepository.deleteAll();
        registerToRepository.deleteAll();
        applicationUserRepository.deleteAll();
        competitionRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        gradingSystemRepository.deleteAll();
        judgeRepository.deleteAll();
    }

    @Test
    public void downloadExcelReport_asNonAuthenticatedUser_shouldThrowUnauthenticatedException() {
        var compEntity = beforeEachReportTest();

        var nae = assertThrows(UnauthorizedException.class, () -> {
            reportFileService.downloadExcelReport(
                new ExcelReportGenerationRequestDto(compEntity.getId(), Set.of(), ExcelReportGenerationRequestInclusionRule.ALL_PARTICIPANTS));
        });

        assertTrue(nae.getMessage().contains("Not authenticated"));
    }

    @Test
    @WithMockUser(username = "participant2@report.test")
    public void downloadExcelReport_withIncorrentCompetitionId_shouldThrowNotFoundException() {
        beforeEachReportTest();

        var nfe = assertThrows(NotFoundException.class, () -> {
            reportFileService.downloadExcelReport(
                new ExcelReportGenerationRequestDto(22222222222L, Set.of(), ExcelReportGenerationRequestInclusionRule.ALL_PARTICIPANTS));
        });

        assertTrue(nfe.getMessage().contains("Such competition was not found"));
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(username = "participant5@report.test")
    public void downloadExcelReport_asNotRegisteredUser_shouldThrowForbiddenException() {
        var compEntity = beforeEachReportTest();
        var setOfGGIds = compEntity.getGradingGroups().stream().map(GradingGroup::getId).collect(Collectors.toSet());
        var fe = assertThrows(ForbiddenException.class, () -> {
            reportFileService.downloadExcelReport(
                new ExcelReportGenerationRequestDto(compEntity.getId(), setOfGGIds, ExcelReportGenerationRequestInclusionRule.ONLY_YOU));
        });

        assertTrue(fe.getMessage().contains("You are not registered"));
    }

    @Test
    @WithMockUser(username = "participant5@report.test")
    public void downloadExcelReport_whenReportsAreNotReady_shouldThrowConflictException() {
        var compEntity = beforeEachReportTest();
        var setOfGGIds = compEntity.getGradingGroups().stream().map(GradingGroup::getId).collect(Collectors.toSet());
        reportRepository.deleteAll();
        var ce = assertThrows(ConflictException.class, () -> {
            reportFileService.downloadExcelReport(
                new ExcelReportGenerationRequestDto(compEntity.getId(), setOfGGIds, ExcelReportGenerationRequestInclusionRule.ONLY_YOU));
        });

        assertTrue(ce.getMessage().contains("Reports are not downloadable yet"));
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(username = "club_manager4@report.test")
    public void downloadExcelReport_whenClubManagerDoesNotManageAnyCompetitionRegisteredUserAndHeIsNotRegistered_shouldThrowConflictException() {
        var compEntity = beforeEachReportTest();
        var setOfGGIds = compEntity.getGradingGroups().stream().map(GradingGroup::getId).collect(Collectors.toSet());
        var ce = assertThrows(ConflictException.class, () -> {
            reportFileService.downloadExcelReport(
                new ExcelReportGenerationRequestDto(compEntity.getId(), setOfGGIds, ExcelReportGenerationRequestInclusionRule.ONLY_YOUR_TEAM));
        });

        assertTrue(ce.getMessage().contains("You neither manage any participating members nor you are registered"));
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(username = "club_manager3@report.test")
    public void downloadExcelReport_forAllParticipants_excelShouldContainAllSheets() throws IOException {
        var wb = getWorkbook(null, null);

        assertEquals(wb.getNumberOfSheets(), 2);

        var gg1_sheet = wb.getSheet("GG1");
        assertNotNull(gg1_sheet);

        var gg2_sheet = wb.getSheet("GG2");
        assertNotNull(gg2_sheet);
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(username = "club_manager3@report.test")
    public void downloadExcelReport_forAllParticipants_excelShouldContainAllRows() throws IOException {
        var wb = getWorkbook(null, null);

        var gg1_sheet = wb.getSheet("GG1");
        assertEquals(gg1_sheet.getPhysicalNumberOfRows(), 5);

        var gg2_sheet = wb.getSheet("GG2");
        assertEquals(gg2_sheet.getPhysicalNumberOfRows(), 4);
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(username = "club_manager3@report.test")
    public void downloadExcelReport_forAllParticipants_excelShouldContainCorrectColumnNames() throws IOException {
        var wb = getWorkbook(null, null);

        var gg1_sheet = wb.getSheet("GG1");
        var gg1_row = gg1_sheet.getRow(0);
        assertEquals("Rang", gg1_row.getCell(0).getStringCellValue());
        assertEquals("Teilnehmer", gg1_row.getCell(1).getStringCellValue());
        assertNull(gg1_row.getCell(2));
        assertEquals("GG1_S1_V1", gg1_row.getCell(3).getStringCellValue());
        assertEquals("GG1_S1_V2", gg1_row.getCell(4).getStringCellValue());
        assertEquals("GG1_S1", gg1_row.getCell(5).getStringCellValue());
        assertNull(gg1_row.getCell(6));
        assertEquals("GG1_S2_V1", gg1_row.getCell(7).getStringCellValue());
        assertEquals("GG1_S2", gg1_row.getCell(8).getStringCellValue());
        assertNull(gg1_row.getCell(9));
        assertEquals("Endergebnis", gg1_row.getCell(10).getStringCellValue());

        var gg2_sheet = wb.getSheet("GG2");
        var gg2_row = gg2_sheet.getRow(0);
        assertEquals("Rang", gg2_row.getCell(0).getStringCellValue());
        assertEquals("Teilnehmer", gg2_row.getCell(1).getStringCellValue());
        assertNull(gg2_row.getCell(2));
        assertEquals("GG2_S1_V1", gg2_row.getCell(3).getStringCellValue());
        assertEquals("GG2_S1_V2", gg2_row.getCell(4).getStringCellValue());
        assertEquals("GG2_S1", gg2_row.getCell(5).getStringCellValue());
        assertNull(gg2_row.getCell(6));
        assertEquals("GG2_S2_V1", gg2_row.getCell(7).getStringCellValue());
        assertEquals("GG2_S2", gg2_row.getCell(8).getStringCellValue());
        assertNull(gg2_row.getCell(9));
        assertEquals("Endergebnis", gg2_row.getCell(10).getStringCellValue());
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(username = "club_manager3@report.test")
    public void downloadExcelReport_forAllParticipants_excelShouldContainCorrectParticipantResultsForGG1() throws IOException {
        var wb = getWorkbook(null, null);

        var gg1_sheet = wb.getSheet("GG1");
        var gg1_row = gg1_sheet.getRow(1);
        assertEquals(1, gg1_row.getCell(0).getNumericCellValue());
        assertEquals("CLUBMANAGERthreeFN CLUBMANAGERthreeLN (2000-02-03)", gg1_row.getCell(1).getStringCellValue());
        assertNull(gg1_row.getCell(2));
        assertEquals(6, gg1_row.getCell(3).getNumericCellValue());
        assertEquals(7, gg1_row.getCell(4).getNumericCellValue());
        assertEquals(13, gg1_row.getCell(5).getNumericCellValue());
        assertNull(gg1_row.getCell(6));
        assertEquals(5, gg1_row.getCell(7).getNumericCellValue());
        assertEquals(2.5, gg1_row.getCell(8).getNumericCellValue());
        assertNull(gg1_row.getCell(9));
        assertEquals(15.5, gg1_row.getCell(10).getNumericCellValue());

        gg1_row = gg1_sheet.getRow(2);
        assertEquals(2, gg1_row.getCell(0).getNumericCellValue());
        assertEquals("PARTICIPANToneFN PARTICIPANToneLN (2000-03-01)", gg1_row.getCell(1).getStringCellValue());
        assertNull(gg1_row.getCell(2));
        assertEquals(6.5, gg1_row.getCell(3).getNumericCellValue());
        assertEquals(3, gg1_row.getCell(4).getNumericCellValue());
        assertEquals(9.5, gg1_row.getCell(5).getNumericCellValue());
        assertNull(gg1_row.getCell(6));
        assertEquals(10, gg1_row.getCell(7).getNumericCellValue());
        assertEquals(5, gg1_row.getCell(8).getNumericCellValue());
        assertNull(gg1_row.getCell(9));
        assertEquals(14.5, gg1_row.getCell(10).getNumericCellValue());

        gg1_row = gg1_sheet.getRow(3);
        assertEquals(2, gg1_row.getCell(0).getNumericCellValue());
        assertEquals("PARTICIPANTtwoFN PARTICIPANTtwoLN (2000-03-02)", gg1_row.getCell(1).getStringCellValue());
        assertNull(gg1_row.getCell(2));
        assertEquals(6.5, gg1_row.getCell(3).getNumericCellValue());
        assertEquals(3, gg1_row.getCell(4).getNumericCellValue());
        assertEquals(9.5, gg1_row.getCell(5).getNumericCellValue());
        assertNull(gg1_row.getCell(6));
        assertEquals(10, gg1_row.getCell(7).getNumericCellValue());
        assertEquals(5, gg1_row.getCell(8).getNumericCellValue());
        assertNull(gg1_row.getCell(9));
        assertEquals(14.5, gg1_row.getCell(10).getNumericCellValue());

        gg1_row = gg1_sheet.getRow(4);
        assertEquals(4, gg1_row.getCell(0).getNumericCellValue());
        assertEquals("PARTICIPANTthreeFN PARTICIPANTthreeLN (2000-03-03)", gg1_row.getCell(1).getStringCellValue());
        assertNull(gg1_row.getCell(2));
        assertEquals(3.2, gg1_row.getCell(3).getNumericCellValue());
        assertEquals(4, gg1_row.getCell(4).getNumericCellValue());
        assertEquals(7.2, gg1_row.getCell(5).getNumericCellValue());
        assertNull(gg1_row.getCell(6));
        assertEquals(11, gg1_row.getCell(7).getNumericCellValue());
        assertEquals(5.5, gg1_row.getCell(8).getNumericCellValue());
        assertNull(gg1_row.getCell(9));
        assertEquals(12.7, gg1_row.getCell(10).getNumericCellValue());
    }


    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(username = "club_manager3@report.test")
    public void downloadExcelReport_forAllParticipants_excelShouldContainCorrectParticipantResultsForGG2() throws IOException {
        var wb = getWorkbook(null, null);

        var gg2_sheet = wb.getSheet("GG2");
        var gg2_row = gg2_sheet.getRow(1);
        assertEquals(1, gg2_row.getCell(0).getNumericCellValue());
        assertEquals("PARTICIPANTfourFN PARTICIPANTfourLN (2000-03-04)", gg2_row.getCell(1).getStringCellValue());
        assertNull(gg2_row.getCell(2));
        assertEquals(17.5, gg2_row.getCell(3).getNumericCellValue());
        assertEquals(7.75, gg2_row.getCell(4).getNumericCellValue());
        assertEquals(25.25, gg2_row.getCell(5).getNumericCellValue());
        assertNull(gg2_row.getCell(6));
        assertEquals(8, gg2_row.getCell(7).getNumericCellValue());
        assertEquals(4, gg2_row.getCell(8).getNumericCellValue());
        assertNull(gg2_row.getCell(9));
        assertEquals(101, gg2_row.getCell(10).getNumericCellValue());

        gg2_row = gg2_sheet.getRow(2);
        assertEquals(2, gg2_row.getCell(0).getNumericCellValue());
        assertEquals("PARTICIPANTthreeFN PARTICIPANTthreeLN (2000-03-03)", gg2_row.getCell(1).getStringCellValue());
        assertNull(gg2_row.getCell(2));
        assertEquals(3, gg2_row.getCell(3).getNumericCellValue());
        assertEquals(2, gg2_row.getCell(4).getNumericCellValue());
        assertEquals(5, gg2_row.getCell(5).getNumericCellValue());
        assertNull(gg2_row.getCell(6));
        assertEquals(13, gg2_row.getCell(7).getNumericCellValue());
        assertEquals(6.5, gg2_row.getCell(8).getNumericCellValue());
        assertNull(gg2_row.getCell(9));
        assertEquals(32.5, gg2_row.getCell(10).getNumericCellValue());

        gg2_row = gg2_sheet.getRow(3);
        assertEquals(3, gg2_row.getCell(0).getNumericCellValue());
        assertEquals("PARTICIPANTtwoFN PARTICIPANTtwoLN (2000-03-02)", gg2_row.getCell(1).getStringCellValue());
        assertNull(gg2_row.getCell(2));
        assertEquals(3.6, gg2_row.getCell(3).getNumericCellValue());
        assertEquals(4, gg2_row.getCell(4).getNumericCellValue());
        assertEquals(7.6, gg2_row.getCell(5).getNumericCellValue());
        assertNull(gg2_row.getCell(6));
        assertEquals(5, gg2_row.getCell(7).getNumericCellValue());
        assertEquals(2.5, gg2_row.getCell(8).getNumericCellValue());
        assertNull(gg2_row.getCell(9));
        assertEquals(19, gg2_row.getCell(10).getNumericCellValue());
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(username = "club_manager1@report.test")
    public void downloadExcelReport_forYourTeam_excelShouldContainCorrectParticipantResultsForGG1() throws IOException {
        var wb = getWorkbook(null, ExcelReportGenerationRequestInclusionRule.ONLY_YOUR_TEAM);

        var gg1_sheet = wb.getSheet("GG1");
        assertEquals(gg1_sheet.getLastRowNum(), 2);
        var gg1_row = gg1_sheet.getRow(1);
        assertEquals(2, gg1_row.getCell(0).getNumericCellValue());
        assertEquals("PARTICIPANToneFN PARTICIPANToneLN (2000-03-01)", gg1_row.getCell(1).getStringCellValue());
        assertNull(gg1_row.getCell(2));
        assertEquals(6.5, gg1_row.getCell(3).getNumericCellValue());
        assertEquals(3, gg1_row.getCell(4).getNumericCellValue());
        assertEquals(9.5, gg1_row.getCell(5).getNumericCellValue());
        assertNull(gg1_row.getCell(6));
        assertEquals(10, gg1_row.getCell(7).getNumericCellValue());
        assertEquals(5, gg1_row.getCell(8).getNumericCellValue());
        assertNull(gg1_row.getCell(9));
        assertEquals(14.5, gg1_row.getCell(10).getNumericCellValue());

        gg1_row = gg1_sheet.getRow(2);
        assertEquals(2, gg1_row.getCell(0).getNumericCellValue());
        assertEquals("PARTICIPANTtwoFN PARTICIPANTtwoLN (2000-03-02)", gg1_row.getCell(1).getStringCellValue());
        assertNull(gg1_row.getCell(2));
        assertEquals(6.5, gg1_row.getCell(3).getNumericCellValue());
        assertEquals(3, gg1_row.getCell(4).getNumericCellValue());
        assertEquals(9.5, gg1_row.getCell(5).getNumericCellValue());
        assertNull(gg1_row.getCell(6));
        assertEquals(10, gg1_row.getCell(7).getNumericCellValue());
        assertEquals(5, gg1_row.getCell(8).getNumericCellValue());
        assertNull(gg1_row.getCell(9));
        assertEquals(14.5, gg1_row.getCell(10).getNumericCellValue());
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(username = "club_manager1@report.test")
    public void downloadExcelReport_forYourTeam_excelShouldContainCorrectParticipantResultsForGG2() throws IOException {
        var wb = getWorkbook(null, ExcelReportGenerationRequestInclusionRule.ONLY_YOUR_TEAM);

        var gg2_sheet = wb.getSheet("GG2");
        assertEquals(gg2_sheet.getLastRowNum(), 1);
        var gg2_row = gg2_sheet.getRow(1);
        assertEquals(3, gg2_row.getCell(0).getNumericCellValue());
        assertEquals("PARTICIPANTtwoFN PARTICIPANTtwoLN (2000-03-02)", gg2_row.getCell(1).getStringCellValue());
        assertNull(gg2_row.getCell(2));
        assertEquals(3.6, gg2_row.getCell(3).getNumericCellValue());
        assertEquals(4, gg2_row.getCell(4).getNumericCellValue());
        assertEquals(7.6, gg2_row.getCell(5).getNumericCellValue());
        assertNull(gg2_row.getCell(6));
        assertEquals(5, gg2_row.getCell(7).getNumericCellValue());
        assertEquals(2.5, gg2_row.getCell(8).getNumericCellValue());
        assertNull(gg2_row.getCell(9));
        assertEquals(19, gg2_row.getCell(10).getNumericCellValue());
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(username = "club_manager3@report.test")
    public void downloadExcelReport_forYourTeamAsManagerWhoIsParticipatingWithNoTeam_excelShouldContainCorrectParticipantResultsForGG1() throws IOException {
        var wb = getWorkbook(null, ExcelReportGenerationRequestInclusionRule.ONLY_YOUR_TEAM);

        var gg1_sheet = wb.getSheet("GG1");
        var gg1_row = gg1_sheet.getRow(1);
        assertEquals(1, gg1_row.getCell(0).getNumericCellValue());
        assertEquals("CLUBMANAGERthreeFN CLUBMANAGERthreeLN (2000-02-03)", gg1_row.getCell(1).getStringCellValue());
        assertNull(gg1_row.getCell(2));
        assertEquals(6, gg1_row.getCell(3).getNumericCellValue());
        assertEquals(7, gg1_row.getCell(4).getNumericCellValue());
        assertEquals(13, gg1_row.getCell(5).getNumericCellValue());
        assertNull(gg1_row.getCell(6));
        assertEquals(5, gg1_row.getCell(7).getNumericCellValue());
        assertEquals(2.5, gg1_row.getCell(8).getNumericCellValue());
        assertNull(gg1_row.getCell(9));
        assertEquals(15.5, gg1_row.getCell(10).getNumericCellValue());
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(username = "participant2@report.test")
    public void downloadExcelReport_forSelf_excelShouldContainCorrectParticipantResultsForAllGradingGroups() throws IOException {
        var wb = getWorkbook(null, ExcelReportGenerationRequestInclusionRule.ONLY_YOU);
        assertEquals(wb.getNumberOfSheets(), 2);

        var gg1_sheet = wb.getSheet("GG1");
        var gg1_row = gg1_sheet.getRow(1);
        assertEquals(2, gg1_row.getCell(0).getNumericCellValue());
        assertEquals("PARTICIPANTtwoFN PARTICIPANTtwoLN (2000-03-02)", gg1_row.getCell(1).getStringCellValue());
        assertNull(gg1_row.getCell(2));
        assertEquals(6.5, gg1_row.getCell(3).getNumericCellValue());
        assertEquals(3, gg1_row.getCell(4).getNumericCellValue());
        assertEquals(9.5, gg1_row.getCell(5).getNumericCellValue());
        assertNull(gg1_row.getCell(6));
        assertEquals(10, gg1_row.getCell(7).getNumericCellValue());
        assertEquals(5, gg1_row.getCell(8).getNumericCellValue());
        assertNull(gg1_row.getCell(9));
        assertEquals(14.5, gg1_row.getCell(10).getNumericCellValue());

        var gg2_sheet = wb.getSheet("GG2");
        var gg2_row = gg2_sheet.getRow(1);
        assertEquals(3, gg2_row.getCell(0).getNumericCellValue());
        assertEquals("PARTICIPANTtwoFN PARTICIPANTtwoLN (2000-03-02)", gg2_row.getCell(1).getStringCellValue());
        assertNull(gg2_row.getCell(2));
        assertEquals(3.6, gg2_row.getCell(3).getNumericCellValue());
        assertEquals(4, gg2_row.getCell(4).getNumericCellValue());
        assertEquals(7.6, gg2_row.getCell(5).getNumericCellValue());
        assertNull(gg2_row.getCell(6));
        assertEquals(5, gg2_row.getCell(7).getNumericCellValue());
        assertEquals(2.5, gg2_row.getCell(8).getNumericCellValue());
        assertNull(gg2_row.getCell(9));
        assertEquals(19, gg2_row.getCell(10).getNumericCellValue());
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(username = "participant2@report.test")
    public void downloadExcelReport_forTeamAsNonClubManager_excelShouldContainCorrectParticipantResultsForAllGradingGroups() throws IOException {
        var wb = getWorkbook(null, ExcelReportGenerationRequestInclusionRule.ONLY_YOUR_TEAM);
        assertEquals(wb.getNumberOfSheets(), 2);

        var gg1_sheet = wb.getSheet("GG1");
        var gg1_row = gg1_sheet.getRow(1);
        assertEquals(2, gg1_row.getCell(0).getNumericCellValue());
        assertEquals("PARTICIPANTtwoFN PARTICIPANTtwoLN (2000-03-02)", gg1_row.getCell(1).getStringCellValue());
        assertNull(gg1_row.getCell(2));
        assertEquals(6.5, gg1_row.getCell(3).getNumericCellValue());
        assertEquals(3, gg1_row.getCell(4).getNumericCellValue());
        assertEquals(9.5, gg1_row.getCell(5).getNumericCellValue());
        assertNull(gg1_row.getCell(6));
        assertEquals(10, gg1_row.getCell(7).getNumericCellValue());
        assertEquals(5, gg1_row.getCell(8).getNumericCellValue());
        assertNull(gg1_row.getCell(9));
        assertEquals(14.5, gg1_row.getCell(10).getNumericCellValue());

        var gg2_sheet = wb.getSheet("GG2");
        var gg2_row = gg2_sheet.getRow(1);
        assertEquals(3, gg2_row.getCell(0).getNumericCellValue());
        assertEquals("PARTICIPANTtwoFN PARTICIPANTtwoLN (2000-03-02)", gg2_row.getCell(1).getStringCellValue());
        assertNull(gg2_row.getCell(2));
        assertEquals(3.6, gg2_row.getCell(3).getNumericCellValue());
        assertEquals(4, gg2_row.getCell(4).getNumericCellValue());
        assertEquals(7.6, gg2_row.getCell(5).getNumericCellValue());
        assertNull(gg2_row.getCell(6));
        assertEquals(5, gg2_row.getCell(7).getNumericCellValue());
        assertEquals(2.5, gg2_row.getCell(8).getNumericCellValue());
        assertNull(gg2_row.getCell(9));
        assertEquals(19, gg2_row.getCell(10).getNumericCellValue());
    }

    private XSSFWorkbook getWorkbook(Set<Long> ggids, ExcelReportGenerationRequestInclusionRule rule) throws IOException {
        var compEntity = beforeEachReportTest();
        if (ggids == null) {
            ggids = compEntity.getGradingGroups().stream().map(GradingGroup::getId).collect(Collectors.toSet());
        }
        if (rule == null) {
            rule = ExcelReportGenerationRequestInclusionRule.ALL_PARTICIPANTS;
        }
        var downloadDto = reportFileService.downloadExcelReport(
            new ExcelReportGenerationRequestDto(compEntity.getId(), ggids, rule)
        );
        assertDoesNotThrow(() -> {
            // FILE EXISTS
            new File(testReportsFolder + "/" + downloadDto.getName());
        });
        FileInputStream fis = new FileInputStream(testReportsFolder + "/" + downloadDto.getName());

        return new XSSFWorkbook(fis);
    }
}
