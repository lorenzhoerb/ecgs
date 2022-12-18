package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
public abstract class TestDataProvider {

    @Autowired
    private CustomUserDetailService customUserDetailService;

    protected static final String BASE_URI = "/api/v1";
    protected static final String COMPETITION_URI = "/competitions";
    protected static final String COMPETITION_BASE_URI = BASE_URI + COMPETITION_URI;

    protected static final String TEST_USER_BASIC_EMAIL = "basic@email.com";
    protected static final String TEST_USER_TOURNAMENT_MANAGER_EMAIL = "comp.manager@email.com";

    protected UserRegisterDto getValidRegistrationDtoForCompetitionManager() {
        return new UserRegisterDto(
            TEST_USER_TOURNAMENT_MANAGER_EMAIL,
            "12345678",
            "firstNameTest",
            "lastNameTest",
            ApplicationUser.Gender.FEMALE,
            new Date(),
            ApplicationUser.Role.TOURNAMENT_MANAGER);
    }

    protected CompetitionDetailDto getValidCompetitionDetailDto() {
        return new CompetitionDetailDto()
            .setEmail("competition@gmx.at")
            .setName("TestTitle")
            .setDescription("Test Description")
            .setBeginOfCompetition(LocalDateTime.of(LocalDate.now().plusDays(20), LocalTime.of(9, 0)))
            .setEndOfCompetition(LocalDateTime.of(LocalDate.now().plusDays(20), LocalTime.of(18, 0)))
            .setBeginOfRegistration(LocalDateTime.now().plusDays(1))
            .setEndOfRegistration(LocalDateTime.now().plusDays(5))
            .setPublic(true)
            .setDraft(true);
    }

    protected GradingGroupDto[] getValidGradingGroupDtos() {
        return new GradingGroupDto[] {
            new GradingGroupDto().withTitle("Group 1"),
            new GradingGroupDto().withTitle("Group 2"),
            new GradingGroupDto().withTitle("Group 3"),
        };
    }

    protected GradingGroupDto[] getGradingGroupDtosWithNameDuplicates() {
        return new GradingGroupDto[] {
            new GradingGroupDto().withTitle("Group 1"),
            new GradingGroupDto().withTitle("Group 1"),
            new GradingGroupDto().withTitle("Group 2"),
        };
    }

    protected GradingGroupDto[] getGradingGroupDtosWithEmptyNames() {
        return new GradingGroupDto[] {
            new GradingGroupDto().withTitle("Group 1"),
            new GradingGroupDto().withTitle(""),
            new GradingGroupDto().withTitle("Group 2"),
        };
    }

    protected void setUpCompetitionUser() {
        customUserDetailService.registerUser(getValidRegistrationDtoForCompetitionManager());
    }

    protected Competition createCompetitionEntity(
        ApplicationUserRepository applicationUserRepository,
        RegisterToRepository registerToRepository,
        GradingGroupRepository gradingGroupRepository,
        CompetitionRepository competitionRepository,
        boolean accepted,
        boolean draft
    ) {
        Competition competition = new Competition(
            "Test Competition",
            LocalDateTime.of(2022, 11, 9, 8, 0),
            LocalDateTime.of(2022, 11, 10, 23, 55),
            LocalDateTime.of(2022, 11, 11, 14, 0),
            LocalDateTime.of(2022, 11, 11, 8, 0),
            "This is a test competition",
            "",
            true,
            draft,
            "test@mail.com",
            "+436666660666"

        );

        ApplicationUser user = new ApplicationUser(
            ApplicationUser.Role.PARTICIPANT,
            "first", "last",
            ApplicationUser.Gender.MALE,
            new Date(2000,10,10),
            ""
        );

        applicationUserRepository.save(user);

        GradingGroup group = new GradingGroup("group 1");

        competition.setGradingGroups(Set.of(group));
        group.setCompetitions(competition);

        competitionRepository.save(competition);
        gradingGroupRepository.save(group);

        RegisterTo registerTo = new RegisterTo();
        registerTo.setParticipant(user);
        registerTo.setGradingGroup(group);
        registerTo.setAccepted(accepted);

        group.setRegistrations(Set.of(registerTo));
        user.setRegistrations(Set.of(registerTo));

        registerToRepository.save(registerTo);

        return competition;
    }
}
