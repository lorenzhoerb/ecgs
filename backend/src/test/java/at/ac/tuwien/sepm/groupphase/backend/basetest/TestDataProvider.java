package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.*;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Strategy;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Mean;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Station;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Variable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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
    protected static final String TEST_USER_COMPETITION_MANAGER_EMAIL = "comp.manager@email.com";

    protected UserRegisterDto getValidRegistrationDtoForCompetitionManager() {
        return new UserRegisterDto(
            TEST_USER_COMPETITION_MANAGER_EMAIL,
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

    protected GradingGroupDto[] getValidGradingGroupDtos() throws JsonProcessingException{
        return new GradingGroupDto[] {
            new GradingGroupDto().withTitle("Group 1")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
            new GradingGroupDto().withTitle("Group 2")
            .withGradingSystemDto(getValidGradingSystemDetailDto()),
        new GradingGroupDto().withTitle("Group 3")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
        };
    }

    protected GradingGroupDto[] getGradingGroupDtosWithNameDuplicates() throws JsonProcessingException {
        return new GradingGroupDto[] {
            new GradingGroupDto().withTitle("Group 1")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
            new GradingGroupDto().withTitle("Group 1")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
            new GradingGroupDto().withTitle("Group 2")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
        };
    }

    protected GradingGroupDto[] getGradingGroupDtosWithEmptyNames() throws JsonProcessingException{
        return new GradingGroupDto[] {
            new GradingGroupDto().withTitle("Group 1")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
            new GradingGroupDto().withTitle("")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
            new GradingGroupDto().withTitle("Group 2")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
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

    protected <T extends Operation> String getSingleOp0Formula(T Op) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        GradingSystem system = new GradingSystem();
        system.stations = new Station[] {
            new Station(1L, "Station", new Variable[]{
            }, Op)
        };
        system.formula = new VariableRef(1L);

        return mapper.writeValueAsString(system);
    }

    protected <T extends Operation> String getSingleOp2Formula(T op) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        GradingSystem system = new GradingSystem();
        system.stations = new Station[] {
            new Station(1L, "Station", new Variable[]{
                new Variable(1L, "Var1"),
                new Variable(2L, "Var2")
            }, op)
        };
        system.formula = new VariableRef(1L);

        return mapper.writeValueAsString(system);
    }

    protected <T extends Strategy> String getSingleStrategyFormula(T strat, Long minJudgeCount) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        GradingSystem system = new GradingSystem();
        system.stations = new Station[] {
            new Station(1L, "Station", new Variable[]{
                new Variable(1L, "Var1", minJudgeCount, strat),
            }, new VariableRef(1L))
        };
        system.formula = new VariableRef(1L);

        return mapper.writeValueAsString(system);
    }

    protected String getGradingSystemFormula() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        GradingSystem system = new GradingSystem();
        system.stations = new Station[] {
            new Station(1L, "Station 1", new Variable[] {
                new Variable(1L, "Var 1", 2L, new Mean()),
                new Variable(2L, "Var 2", 1L, new Mean()),
            }, new Add(new VariableRef(1L), new VariableRef(2L))),
            new Station(2L, "Station 2", new Variable[]{
                new Variable(1L, "Var 1", 3L, new Mean()),
            }, new Divide(new VariableRef(1L), new Constant(2.0)))
        };
        system.formula = new Add(new VariableRef(1L), new VariableRef(2L));

        return mapper.writeValueAsString(system);
    }

    protected Competition getValidCompetitionEntity() {
        return new Competition(
            "Test Competition",
            LocalDateTime.of(2022, 11, 9, 8, 0),
            LocalDateTime.of(2022, 11, 10, 23, 55),
            LocalDateTime.of(2022, 11, 11, 14, 0),
            LocalDateTime.of(2022, 11, 11, 8, 0),
            "This is a test competition",
            "",
            true,
            false,
            "test@mail.com",
            "+436666660666"
        );
    }

    protected GradingSystemDetailDto getValidGradingSystemDetailDto() throws JsonProcessingException {
        return new GradingSystemDetailDto(
            "Grading System 1",
            "Test grading system for testing",
            false,
            getGradingSystemFormula()
        );
    }

}
