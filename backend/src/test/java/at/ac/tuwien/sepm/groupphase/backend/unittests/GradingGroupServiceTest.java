package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BasicRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailGradeDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
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
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingGroupService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReportService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

import static at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint.ConstraintType.AGE;
import static at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint.ConstraintType.DATE_OF_BIRTH;
import static at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint.ConstraintType.GENDER;
import static at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint.Operator.BORN_AFTER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class GradingGroupServiceTest extends TestDataProvider {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private GradingGroupService gradingGroupService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private GradingGroupRepository gradingGroupRepository;

    @Autowired
    private RegisterConstraintRepository registerConstraintRepository;

    @Autowired
    private GradingSystemRepository gradingSystemRepository;


    @Autowired
    private RegisterToRepository registerToRepository;

    @Autowired
    private ReportFileRepository reportFileRepository;

    @Autowired
    private ManagedByRepository managedByRepository;

    @Autowired
    private JudgeRepository judgeRepository;

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
        setUpCompetitionUser();
        setUpParticipantUser();
    }

    @AfterEach
    public void afterAll() {
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
    public void whenGettingAllGroupsOfCompetition_whenNoCompId_given_expectValidation() {
        assertThrows(ValidationListException.class, () -> {
            gradingGroupService.getAllByCompetition(null);
        });
    }

    @Test
    public void testGetAllGroupsOfCompetition() {
        Competition c = getValidCompetitionEntity();
        Competition c2 = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");
        GradingGroup g2 = new GradingGroup("G2");
        GradingGroup g3 = new GradingGroup("G2");

        c.setGradingGroups(Set.of(g1, g2));
        c2.setGradingGroups(Set.of(g3));

        g1.setCompetitions(c);
        g2.setCompetitions(c);
        g3.setCompetitions(c2);

        Competition cc1 = competitionRepository.save(c);
        Competition cc2 = competitionRepository.save(c2);

        gradingGroupRepository.save(g1);
        gradingGroupRepository.save(g2);
        gradingGroupRepository.save(g3);

        List<SimpleGradingGroupDto> gg1 = gradingGroupService.getAllByCompetition(cc1.getId());
        List<SimpleGradingGroupDto> gg2 = gradingGroupService.getAllByCompetition(cc2.getId());
        assertEquals(2, gg1.size());
        assertEquals(1, gg2.size());
    }

    @Test
    public void setGradingGroupConstraint_whenUnauthenticated_expectForbidden() {
        assertThrows(ForbiddenException.class, () -> {
            gradingGroupService.setConstraints(
                1L,
                List.of(new BasicRegisterConstraintDto(
                    RegisterConstraint.ConstraintType.GENDER,
                    RegisterConstraint.Operator.EQUALS,
                    "MALE")));
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void setGradingGroupConstraint_onNonExistingGg_expectNotFound() {
        assertThrows(NotFoundException.class, () -> {
            gradingGroupService.setConstraints(
                1L,
                List.of(new BasicRegisterConstraintDto(
                    RegisterConstraint.ConstraintType.GENDER,
                    RegisterConstraint.Operator.EQUALS,
                    "MALE")));
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void setGradingGroupConstraint_onExistingButNotOwnedGg_expectNotFound() {
        ApplicationUser a = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_PARTICIPANT_EMAIL).get();
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");
        c.setCreator(a);
        c.setGradingGroups(Set.of(g1));
        Competition cc1 = competitionRepository.save(c);
        g1.setCompetitions(cc1);
        assertThrows(NotFoundException.class, () -> {
            gradingGroupService.setConstraints(
                g1.getId(),
                List.of(new BasicRegisterConstraintDto(
                    RegisterConstraint.ConstraintType.GENDER,
                    RegisterConstraint.Operator.EQUALS,
                    "MALE")));
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void setGradingGroupConstraints_expectCreated() {
        ApplicationUser a = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = getValidCompetitionEntity();
        c.setCreator(a);

        Competition c1 = competitionRepository.save(c);

        GradingGroup g = new GradingGroup("G1");
        g.setCompetitions(c1);
        GradingGroup g1 = gradingGroupRepository.save(g);

        c1.setGradingGroups(Set.of(g1));


        gradingGroupService.setConstraints(
            g1.getId(),
            List.of(new BasicRegisterConstraintDto(
                RegisterConstraint.ConstraintType.GENDER,
                RegisterConstraint.Operator.EQUALS,
                "MALE")));
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void setGradingGroupConstraints_withNullId_expectValidationException() {
        assertThrows(ValidationListException.class, () -> {
            gradingGroupService.setConstraints(
                null,
                List.of(new BasicRegisterConstraintDto(
                    RegisterConstraint.ConstraintType.GENDER,
                    RegisterConstraint.Operator.EQUALS,
                    "MALE")));
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void setGradingGroupConstraints_withInvalidAgeInput_expectValidationException() {
        ApplicationUser a = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");

        c.setCreator(a);
        c.setGradingGroups(Set.of(g1));

        g1.setCompetitions(c);
        competitionRepository.save(c);

        gradingGroupRepository.save(g1);

        assertThrows(ValidationListException.class, () -> {
            gradingGroupService.setConstraints(
                g1.getId(),
                List.of(new BasicRegisterConstraintDto(
                    AGE,
                    RegisterConstraint.Operator.EQUALS,
                    "MALE")));
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void setGradingGroupConstraints_withInvalidAgeInput_zero_expectValidationException() {
        ApplicationUser a = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");

        c.setCreator(a);
        c.setGradingGroups(Set.of(g1));

        g1.setCompetitions(c);
        competitionRepository.save(c);

        gradingGroupRepository.save(g1);

        assertThrows(ValidationListException.class, () -> {
            gradingGroupService.setConstraints(
                g1.getId(),
                List.of(new BasicRegisterConstraintDto(
                    AGE,
                    RegisterConstraint.Operator.EQUALS,
                    "0")));
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void setGradingGroupConstraints_withInvalidDateOfBirthInput_expectValidationException() {
        ApplicationUser a = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");

        c.setCreator(a);
        c.setGradingGroups(Set.of(g1));

        g1.setCompetitions(c);
        competitionRepository.save(c);

        gradingGroupRepository.save(g1);

        assertThrows(ValidationListException.class, () -> {
            gradingGroupService.setConstraints(
                g1.getId(),
                List.of(new BasicRegisterConstraintDto(
                    DATE_OF_BIRTH,
                    BORN_AFTER,
                    "ababa")));
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void setGradingGroupConstraints_withInvalidGenderInput_expectValidationException() {
        ApplicationUser a = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");

        c.setCreator(a);
        c.setGradingGroups(Set.of(g1));

        g1.setCompetitions(c);
        competitionRepository.save(c);

        gradingGroupRepository.save(g1);

        assertThrows(ValidationListException.class, () -> {
            gradingGroupService.setConstraints(
                g1.getId(),
                List.of(new BasicRegisterConstraintDto(
                    GENDER,
                    BORN_AFTER,
                    "abraham")));
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void setGradingGroupConstraints_withValidDateOfBirthInput_expectCreated() {
        ApplicationUser a = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");

        c.setCreator(a);
        c.setGradingGroups(Set.of(g1));

        g1.setCompetitions(c);
        competitionRepository.save(c);

        gradingGroupRepository.save(g1);


        Set<DetailedRegisterConstraintDto> saved = gradingGroupService.setConstraints(
            g1.getId(),
            List.of(new BasicRegisterConstraintDto(
                DATE_OF_BIRTH,
                BORN_AFTER,
                "1999-11-23")));

        assertNotNull(saved);
        assertEquals(1, saved.size());

        for (DetailedRegisterConstraintDto s : saved) {
            assertEquals(DATE_OF_BIRTH, s.getType());
            assertEquals(BORN_AFTER, s.getOperator());
            assertEquals("1999-11-23", s.getValue());
        }

    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    @Transactional
    public void setGradingGroupConstraints_when2Set_deleteOldConstraints_expectCreated() {
        ApplicationUser a = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");

        c.setCreator(a);
        c.setGradingGroups(Set.of(g1));

        g1.setCompetitions(c);
        competitionRepository.save(c);

        gradingGroupRepository.save(g1);


        Set<DetailedRegisterConstraintDto> saved = gradingGroupService.setConstraints(
            g1.getId(),
            List.of(new BasicRegisterConstraintDto(
                DATE_OF_BIRTH,
                BORN_AFTER,
                "1999-11-23")));

        assertNotNull(saved);
        assertEquals(1, saved.size());

        for (DetailedRegisterConstraintDto s : saved) {
            assertEquals(DATE_OF_BIRTH, s.getType());
            assertEquals(BORN_AFTER, s.getOperator());
            assertEquals("1999-11-23", s.getValue());
        }

        Set<DetailedRegisterConstraintDto> saved2 = gradingGroupService.setConstraints(
            g1.getId(),
            List.of(new BasicRegisterConstraintDto(
                DATE_OF_BIRTH,
                BORN_AFTER,
                "1999-11-23")));

        assertNotNull(saved2);
        assertEquals(1, saved2.size());

        for (DetailedRegisterConstraintDto s : saved2) {
            assertEquals(DATE_OF_BIRTH, s.getType());
            assertEquals(BORN_AFTER, s.getOperator());
            assertEquals("1999-11-23", s.getValue());
        }

        List<RegisterConstraint> constraints = registerConstraintRepository.findAllByGradingGroup_Id(g1.getId());
        assertEquals(1, constraints.size());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void getGradingGroupDetails_Details_expectDetails() {
        ApplicationUser a = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");
        c.setCreator(a);
        c.setGradingGroups(Set.of(g1));
        g1.setCompetitions(c);
        competitionRepository.save(c);
        gradingGroupRepository.save(g1);
        gradingGroupService.setConstraints(
            g1.getId(),
            List.of(new BasicRegisterConstraintDto(
                DATE_OF_BIRTH,
                BORN_AFTER,
                "1999-11-23")));

        DetailedGradingGroupDto detailed = gradingGroupService.getOneById(g1.getId());

        assertNotNull(detailed);
        assertEquals(g1.getTitle(), detailed.getTitle());
        assertEquals(g1.getId(), detailed.getId());
        assertNotNull(detailed.getConstraints());
        assertEquals(g1.getId(), detailed.getId());
        assertEquals(1, detailed.getConstraints().size());
        assertEquals(DATE_OF_BIRTH, detailed.getConstraints().get(0).getType());
        assertEquals(BORN_AFTER, detailed.getConstraints().get(0).getOperator());
        assertEquals("1999-11-23", detailed.getConstraints().get(0).getValue());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void getGradingGroupDetails_nonExistingGradingGroup_expectNotFound() {
        assertThrows(NotFoundException.class, () -> {
            gradingGroupService.getOneById(-1L);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void getGradingGroupDetails_withNotOwningGradingGroup() {
        ApplicationUser a = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");
        c.setCreator(a);
        c.setGradingGroups(Set.of(g1));
        g1.setCompetitions(c);
        competitionRepository.save(c);
        gradingGroupRepository.save(g1);
        assertThrows(NotFoundException.class, () -> {
            gradingGroupService.getOneById(g1.getId() + 1);
        });
    }

    @Test
    public void getGradingGroupDetails_whenNotAuthenticated_expectForbidden() {
        assertThrows(ForbiddenException.class, () -> {
            gradingGroupService.getOneById(1L);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_PARTICIPANT_EMAIL)
    public void getGradingGroupDetails_whenNotTournamentManager_expectForbidden() {
        assertThrows(ForbiddenException.class, () -> {
            gradingGroupService.getOneById(1L);
        });
    }

    @Test
    @WithMockUser(value = "club_manager3@report.test")
    @Transactional(Transactional.TxType.NEVER)
    public void checkIfReportsAreDownloadable_whenAllGradingGroupsHaveReports_shouldReturnTrue() throws Exception {
        var compEntity = beforeEachReportTest();
        var res = gradingGroupService.checkAllGradingGroupsHaveReports(compEntity.getId());

        assertTrue(res.isDownloadable());
    }

    @Test
    public void checkIfReportsAreDownloadable_asUnauthorizedUser_shouldThrowForbiddenException() throws Exception {
        var compEntity = beforeEachReportTest();
        var fe = assertThrows(ForbiddenException.class, () -> {
            gradingGroupService.checkAllGradingGroupsHaveReports(compEntity.getId());
        });

        assertTrue(fe.getMessage().contains("Not authenticated"));
    }

    @Test
    @WithMockUser(username = "comp_manager1@report.test")
    public void checkIfReportsAreDownloadable_withIncorrectId_shouldThrowNotFoundException() throws Exception {
        var compEntity = beforeEachReportTest();
        var nfe = assertThrows(NotFoundException.class, () -> {
            gradingGroupService.checkAllGradingGroupsHaveReports(333333333L);
        });

        assertTrue(nfe.getMessage().contains("Such competition was not found"));
    }

    @Test
    @WithMockUser(value = "comp_manager1@report.test")
    public void checkIfReportsAreDownloadable_whenOneGradeIsMissing_shouldReturnFalse() throws Exception {
        var compEntity = beforeEachReportTest();
        reportRepository.deleteAll();
        gradeRepository.deleteById(gradeRepository.findAll().iterator().next().getGradePk());
        var ce = assertThrows(ConflictException.class, () -> {
            reportService.calculateResultsOfCompetition(compEntity.getId());
        });
        assertTrue(ce.getMessage().contains("Not all grades were entered"));
        var res = gradingGroupService.checkAllGradingGroupsHaveReports(compEntity.getId());

        assertFalse(res.isDownloadable());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void getParticipants_forNotFinishedGradingGroup_shouldThrowValidation() throws Exception {
        Competition competition = createCompetitionEntity(
            applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true,
            true
        );

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            gradingGroupService.getParticipants(gradingGroupRepository.findAll().iterator().next().getId(), null);
        });

        assertEquals(e.errors().get(0), "competition is not finished");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void getParticipants_forNotExistingGradingGroup_shouldThrowNotFound() throws Exception {
        Competition competition = createCompetitionEntity(
            applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true,
            false
        );

        NotFoundException e = assertThrows(NotFoundException.class, () -> {
            gradingGroupService.getParticipants(-1L, null);
        });

        assertEquals(e.getMessage(), "No group found");
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    @WithMockUser(value = "club_manager3@report.test")
    public void getParticipants_forFinishedCompetition_shouldSucceed() throws Exception {
        var compEntity = beforeEachReportTest();

        for (GradingGroup group : compEntity.getGradingGroups()) {
            Page<UserDetailGradeDto> result = gradingGroupService.getParticipants(group.getId(), null);
        }
    }

    @Test
    public void getParticipants_givenNotLoggedInUser_shouldThrowForbidden() throws Exception {
        Competition competition = createCompetitionEntity(
            applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true,
            false
        );

        ForbiddenException e = assertThrows(ForbiddenException.class, () -> {
            gradingGroupService.getParticipants(gradingGroupRepository.findAll().iterator().next().getId(), null);
        });

        assertEquals(e.getMessage(), "No permission to get participant details");
    }

}
