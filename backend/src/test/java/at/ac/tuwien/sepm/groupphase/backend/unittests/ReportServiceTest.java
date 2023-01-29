package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.report.Report;
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
import at.ac.tuwien.sepm.groupphase.backend.service.GradingGroupService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReportService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;
import java.util.stream.StreamSupport;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ReportServiceTest extends TestDataProvider {
    private final ReportService reportService;
    private final GradingGroupRepository gradingGroupRepository;
    private final ReportRepository reportRepository;
    private final GradeRepository gradeRepository;
    private final GradingGroupService gradingGroupService;
    private final GradingSystemRepository gradingSystemRepository;
    private final CompetitionRepository competitionRepository;
    private final RegisterToRepository registerToRepository;
    private final JudgeRepository judgeRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final RegisterConstraintRepository registerConstraintRepository;
    private final ReportFileRepository reportFileRepository;
    private final ManagedByRepository managedByRepository;



    @Autowired
    public ReportServiceTest(ReportService reportService, GradingGroupRepository gradingGroupRepository,
                             ReportRepository reportRepository, GradeRepository gradeRepository, GradingGroupService gradingGroupService,
                             GradingSystemRepository gradingSystemRepository, CompetitionRepository competitionRepository,
                             RegisterToRepository registerToRepository, JudgeRepository judgeRepository,
                             ApplicationUserRepository applicationUserRepository,
                             RegisterConstraintRepository registerConstraintRepository, ReportFileRepository reportFileRepository,
                             ManagedByRepository managedByRepository
    ) {
        this.reportService = reportService;
        this.gradingGroupRepository = gradingGroupRepository;
        this.reportRepository = reportRepository;
        this.gradeRepository = gradeRepository;
        this.gradingGroupService = gradingGroupService;
        this.gradingSystemRepository = gradingSystemRepository;
        this.competitionRepository = competitionRepository;
        this.registerToRepository = registerToRepository;
        this.judgeRepository = judgeRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.registerConstraintRepository = registerConstraintRepository;
        this.reportFileRepository = reportFileRepository;
        this.managedByRepository = managedByRepository;
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
    @WithMockUser(username = "club_manager1@report.test")
    public void calculateResultsOfCompetition_asNonCompetitionManager_shouldThrowForbiddenException() {
        var compEntity = beforeEachReportTest();
        var fe = assertThrows(ForbiddenException.class, () -> {
            reportService.calculateResultsOfCompetition(compEntity.getId());
        });

        assertTrue(fe.getMessage().contains("No permissions to do this"));
    }

    @Test
    @WithMockUser(username = "comp_manager1@report.test")
    public void calculateResultsOfCompetition_withIncorrectCompetitionId_shouldThrowNotFoundException() {
        var nfe = assertThrows(NotFoundException.class, () -> {
            reportService.calculateResultsOfCompetition(2222222L);
        });

        assertTrue(nfe.getMessage().contains("Such competition was not found"));
    }

    @Test
    @WithMockUser(username = "club_manager1@report.test")
    public void calculateResultsOfCompetition_asClubManager_shouldThrowForbiddenException() {
        var nfe = assertThrows(NotFoundException.class, () -> {
            reportService.calculateResultsOfCompetition(2222222L);
        });

        assertTrue(nfe.getMessage().contains("Such competition was not found"));
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(username = "comp_manager1@report.test")
    public void calculateResultsOfCompetition_withValidDataBeforehand_allGradingGroupsShouldHaveReports() {
        beforeEachReportTest();
        gradingGroupRepository.findAll().forEach(
            gg -> {
                assertNotNull(gg.getReport());
            }
        );
        var iterator = reportRepository.findAll().iterator();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
    }

    @Test
    @WithMockUser(username = "comp_manager1@report.test")
    public void calculateResultsOfCompetition_withValidDataBeforehand_shouldCorrectlyCalculateReports() {
        Report reportObj = new Report();
        beforeEachReportTest();
        var reportList = StreamSupport
            .stream(reportRepository.findAll().spliterator(), false)
            .toList();
        var r1 = reportList.stream().filter(
            r -> r.getResults().contains("GG1")
        ).toList().get(0).getResults();
        var r2 = reportList.stream().filter(
            r -> r.getResults().contains("GG2")
        ).toList().get(0).getResults();

        assertDoesNotThrow(() -> {
            reportObj.addGradingGroupRankingResultsAsJson(r1);
            reportObj.addGradingGroupRankingResultsAsJson(r2);
        });

        assertNotNull(reportObj.getGradingGroupRankingResults());
        assertEquals(reportObj.getGradingGroupRankingResults().size(), 2);

        var ggrr1 = reportObj.getGradingGroupRankingResults().get(0);
        var ggrr2 = reportObj.getGradingGroupRankingResults().get(1);
        assertNotNull(ggrr1);
        assertNotNull(ggrr2);

        var ggrr1_participantResults = ggrr1.getParticipantsRankingResults();
        assertEquals(ggrr1_participantResults.size(), 4);

        assertEquals(ggrr1_participantResults.get(0).getKey(), 1);
        assertEquals(ggrr1_participantResults.get(0).getValue().getResults(), 15.5);
        assertTrue(ggrr1_participantResults.get(0).getValue().getName().contains("CLUBMANAGERthreeFN CLUBMANAGERthreeLN (2000-02-03)"));

        assertEquals(ggrr1_participantResults.get(1).getKey(), 2);
        assertEquals(ggrr1_participantResults.get(1).getValue().getResults(), 14.5);
        assertTrue(
            ggrr1_participantResults.get(1).getValue().getName().contains("PARTICIPANToneFN PARTICIPANToneLN (2000-03-01)")
            || ggrr1_participantResults.get(1).getValue().getName().contains("PARTICIPANTtwoFN PARTICIPANTtwoLN (2000-03-02)")
        );

        assertEquals(ggrr1_participantResults.get(2).getKey(), 2);
        assertEquals(ggrr1_participantResults.get(2).getValue().getResults(), 14.5);
        assertTrue(
            ggrr1_participantResults.get(2).getValue().getName().contains("PARTICIPANToneFN PARTICIPANToneLN (2000-03-01)")
                || ggrr1_participantResults.get(2).getValue().getName().contains("PARTICIPANTtwoFN PARTICIPANTtwoLN (2000-03-02)")
        );
        assertEquals(ggrr1_participantResults.get(3).getKey(), 4);
        assertEquals(ggrr1_participantResults.get(3).getValue().getResults(), 12.7);
        assertTrue(ggrr1_participantResults.get(3).getValue().getName().contains("PARTICIPANTthreeFN PARTICIPANTthreeLN (2000-03-03)"));



        var ggrr2_participantResults = ggrr2.getParticipantsRankingResults();
        assertEquals(ggrr2_participantResults.size(), 3);

        assertEquals(ggrr2_participantResults.get(0).getKey(), 1);
        assertEquals(ggrr2_participantResults.get(0).getValue().getResults(), 101);
        assertTrue(ggrr2_participantResults.get(0).getValue().getName().contains("PARTICIPANTfourFN PARTICIPANTfourLN (2000-03-04)"));

        assertEquals(ggrr2_participantResults.get(1).getKey(), 2);
        assertEquals(ggrr2_participantResults.get(1).getValue().getResults(), 32.5);
        assertTrue(ggrr2_participantResults.get(1).getValue().getName().contains("PARTICIPANTthreeFN PARTICIPANTthreeLN (2000-03-03)"));

        assertEquals(ggrr2_participantResults.get(2).getKey(), 3);
        assertEquals(ggrr2_participantResults.get(2).getValue().getResults(), 19);
        assertTrue(ggrr2_participantResults.get(2).getValue().getName().contains("PARTICIPANTtwoFN PARTICIPANTtwoLN (2000-03-02)"));
    }

    @Test
    @WithMockUser(username = "comp_manager1@report.test")
    public void calculateResultsOfCompetition_withOneGradeMissing_shouldThrowConflictException() {
        var compEntity = beforeEachReportTest();
        reportRepository.deleteAll();
        gradeRepository.deleteById(gradeRepository.findAll().iterator().next().getGradePk());
        var ce = assertThrows(ConflictException.class, () -> {
            reportService.calculateResultsOfCompetition(compEntity.getId());
        });
        assertTrue(ce.getMessage().contains("Not all grades were entered"));
    }
}
