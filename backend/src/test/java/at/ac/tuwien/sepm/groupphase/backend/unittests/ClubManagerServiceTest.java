package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.ClubManagerTeamImportDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.ClubManagerTeamImportGeneratorHelper;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.DataCleaner;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseMultiParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.ManagedBy;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionRegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.helprecords.ClubManagerTeamImportResults;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class ClubManagerServiceTest extends TestDataProvider {
    private final UserService userService;
    private final ApplicationUserRepository userRepository;
    private final DataCleaner cleaner;
    private final ClubManagerTeamImportDataGenerator cmGenerator;

    private final CompetitionRepository competitionRepository;

    private final CompetitionRegistrationService competitionRegistrationService;

    private final GradingGroupRepository gradingGroupRepository;

    private final RegisterToRepository registerToRepository;

    private final ManagedByRepository managedByRepository;
    private final SessionUtils sessionUtils;

    private Competition createdComp;

    private List<GradingGroup> createdGroups;


    @Autowired
    public ClubManagerServiceTest(UserService userService, ApplicationUserRepository userRepository, DataCleaner cleaner, ClubManagerTeamImportDataGenerator cmGenerator,
                                  CompetitionRepository competitionRepository, CompetitionRegistrationService competitionRegistrationService,
                                  GradingGroupRepository gradingGroupRepository, RegisterToRepository registerToRepository,
                                  ManagedByRepository managedByRepository, SessionUtils sessionUtils) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.cleaner = cleaner;
        this.cmGenerator = cmGenerator;
        this.competitionRepository = competitionRepository;
        this.competitionRegistrationService = competitionRegistrationService;
        this.gradingGroupRepository = gradingGroupRepository;
        this.registerToRepository = registerToRepository;
        this.managedByRepository = managedByRepository;
        this.sessionUtils = sessionUtils;
    }

    @BeforeEach
    public void refreshTestDate() {
        cleaner.clear();
        cmGenerator.setup();
        Competition competition = new Competition(
            "Test Competition",
            LocalDateTime.of(2022, 11, 4, 8, 0),
            LocalDateTime.of(2022, 11, 5, 23, 55),
            LocalDateTime.of(2022, 11, 7, 14, 0),
            LocalDateTime.of(2022, 11, 11, 8, 0),
            "This is a test competition",
            "",
            true,
            false,
            "test@mail.com",
            "+436666660666"
        );
        competition.setPublic(true);
        competition.setDraft(false);
        GradingGroup g1 = new GradingGroup("G1");
        GradingGroup g2 = new GradingGroup("G2");
        g1.setCompetitions(competition);
        g2.setCompetitions(competition);
        competition.setGradingGroups(Set.of(g1, g2));
        createdComp = competitionRepository.save(competition);
        createdGroups = new LinkedList<>();
        createdGroups.add(gradingGroupRepository.save(g1));
        createdGroups.add(gradingGroupRepository.save(g2));
        setUpCompetitionUser();
        setUpParticipantUser();
        setUpClubManagerUser();
    }

    @AfterEach
    public void cleanUp(){
        registerToRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        userRepository.deleteAll();
        competitionRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenTeamMembersThatAreNotInDB_whenImportingTeam_expectToCorrectlyCountNewAndOldMembers() {
        ClubManagerTeamImportDto relevantTestTeam = ClubManagerTeamImportGeneratorHelper.testTeams.get(0);
        long initUserCount = userRepository.count();
        ClubManagerTeamImportResults results = userService.importTeam(
            relevantTestTeam
        );
        long laterUserCount = userRepository.count();
        assertThat(results.getNewParticipantsCount()).isEqualTo(4);
        assertThat(results.getOldParticipantsCount()).isEqualTo(0);
        assertThat(laterUserCount).isEqualTo(initUserCount + 4L);
        var mapToAssert = assertThat(userRepository.findAll())
            .map(ApplicationUser::getFirstName, ApplicationUser::getLastName,
                ApplicationUser::getGender, ApplicationUser::getType,
                (au) -> au.getUser().getEmail());
        for (var member: relevantTestTeam.teamMembers()) {
            mapToAssert.contains(tuple(member.firstName(), member.lastName(),
                member.gender(), ApplicationUser.Role.PARTICIPANT,
                member.email()));
        }
    }

    @Test
    @WithMockUser(username = "club.manager@email.com")
    public void givenValidParticipantsAndAClubManager_whenRegisteringParticipantsToCompetition_expectToCorrectCountCalendarCompetitions(){
        Competition c = getValidCompetitionEntity();
        c.setBeginOfCompetition(LocalDateTime.of(2023, 1, 3, 8, 0));
        c.setEndOfCompetition(LocalDateTime.of(2023, 1, 6, 8, 0));
        c.setPublic(true);
        c.setDraft(false);
        c.setBeginOfRegistration(LocalDateTime.now().minusDays(2));
        c.setEndOfRegistration(LocalDateTime.now().plusDays(4));
        GradingGroup g1 = new GradingGroup("G1");
        GradingGroup g2 = new GradingGroup("G2");
        g1.setCompetitions(c);
        g2.setCompetitions(c);
        c.setGradingGroups(Set.of(g1, g2));

        Competition cc = competitionRepository.save(c);
        GradingGroup gc1 = gradingGroupRepository.save(g1);
        GradingGroup gc2 = gradingGroupRepository.save(g2);
        GradingGroup defaultGradingGroup = gradingGroupRepository.findFirstByCompetitionIdOrderByIdAsc(c.getId()).get();

        ApplicationUser clubManager = userRepository.findApplicationUserByUserEmail(TEST_USER_CLUB_MANAGER_EMAIL).get();
        clubManagerWithManagedUsers(5);
        List<ParticipantRegistrationDto> registrations = getParticipantRegistrationDto(clubManager.getId(), gc1.getId());
        registrations.get(0).setGroupPreference(gc1.getId());

        ResponseMultiParticipantRegistrationDto response = competitionRegistrationService.registerParticipants(cc.getId(), registrations);

        assertEquals(cc.getId(), response.getCompetitionId());
        Set<Competition> competitionSet = userService.getCompetitionsForCalendar(2023,1);
        assertEquals(1,competitionSet.size());
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenTeamThatIsAlreadyPartiallyPresentInDB_whenImportingTeam_expectToReturnCorrectNumberOfNewAndOldMembers() throws Exception {
        userService.importTeam(
            ClubManagerTeamImportGeneratorHelper.testTeams.get(0)
        );

        ClubManagerTeamImportDto relevantTestTeam = ClubManagerTeamImportGeneratorHelper.testTeams.get(1);
        long initUserCount = userRepository.count();
        ClubManagerTeamImportResults results = userService.importTeam(
            relevantTestTeam
        );
        long laterUserCount = userRepository.count();
        assertThat(results.getNewParticipantsCount()).isEqualTo(1);
        assertThat(results.getOldParticipantsCount()).isEqualTo(1);
        assertThat(laterUserCount).isNotEqualTo(initUserCount + 2L);
        var memberToTest = relevantTestTeam.teamMembers().get(1);
        assertThat(userRepository.findAll())
            .map(ApplicationUser::getFirstName, ApplicationUser::getLastName,
                ApplicationUser::getGender, ApplicationUser::getType,
                (au) -> au.getUser().getEmail())
            .contains(tuple(memberToTest.firstName(), memberToTest.lastName(),
                memberToTest.gender(), ApplicationUser.Role.PARTICIPANT,
                memberToTest.email()));
    }
}

