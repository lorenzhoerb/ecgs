package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.datagenerator.ClubManagerTeamImportDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.ClubManagerTeamImportGeneratorHelper;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.DataCleaner;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.helprecords.ClubManagerTeamImportResults;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.junit.jupiter.api.*;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClubManagerServiceTest {
    private final UserService userService;
    private final ApplicationUserRepository userRepository;
    private final DataCleaner cleaner;
    private final ClubManagerTeamImportDataGenerator cmGenerator;

    private final SessionUtils sessionUtils;

    @Autowired
    public ClubManagerServiceTest(UserService userService, ApplicationUserRepository userRepository, DataCleaner cleaner, ClubManagerTeamImportDataGenerator cmGenerator, SessionUtils sessionUtils) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.cleaner = cleaner;
        this.cmGenerator = cmGenerator;
        this.sessionUtils = sessionUtils;
    }

    @BeforeEach
    public void refreshTestDate() {
        cleaner.clear();
        cmGenerator.setup();
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void importTeam_whenTeamMembersAreNotInDB_shouldCountCorrectlyNewAndOldMembers() {
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
    @WithMockUser(username = "cm_test@test.test")
    public void importTeam_whenTeamMembersArePartiallyInDB_shouldCountCorrectlyNewAndOldMembers() {
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

