package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Add;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Constant;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Divide;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Operation;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.VariableRef;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Mean;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Strategy;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Station;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Variable;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Random;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
public abstract class TestDataProvider {

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private UserMapper userMapper;

    protected static final String BASE_URI = "/api/v1";
    protected static final String COMPETITION_URI = "/competitions";
    protected static final String COMPETITION_BASE_URI = BASE_URI + COMPETITION_URI;
    protected static final String USER_URI = "/user";
    protected static final String USER_BASE_URI = BASE_URI + USER_URI;
    protected static final String COMPETITION_SELF_REGISTRATION_BASE_URI = USER_BASE_URI + "/competitions/";

    protected static final String TEST_USER_BASIC_EMAIL = "basic@email.com";
    protected static final String TEST_USER_COMPETITION_MANAGER_EMAIL = "comp.manager@email.com";
    protected static final String TEST_USER_PARTICIPANT_EMAIL = "participant@email.com";

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

    protected UserRegisterDto getValidRegistrationDtoForParticipant() {
        return new UserRegisterDto(
            TEST_USER_PARTICIPANT_EMAIL,
            "12345678",
            "Firsname",
            "Lastname",
            ApplicationUser.Gender.MALE,
            new Date(),
            ApplicationUser.Role.PARTICIPANT);
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

    protected void setUpParticipantUser() {
        customUserDetailService.registerUser(getValidRegistrationDtoForParticipant());
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
            new Date(2000, 10, 10),
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

    protected UserDetailDto[] getDuplicateJudges(
        ApplicationUserRepository applicationUserRepository,
        SecurityUserRepository securityUserRepository
    ) {
        SecurityUser securityUser = new SecurityUser("duplicate@email.com", "password");

        ApplicationUser applicationUser = new ApplicationUser(
            ApplicationUser.Role.PARTICIPANT,
            "firstName", "lastName",
            ApplicationUser.Gender.MALE,
            new Date(2000, 11, 1),
            ""
        );
        applicationUser.setUser(securityUser);
        securityUser.setUser(applicationUser);

        securityUserRepository.save(securityUser);
        applicationUserRepository.save(applicationUser);

        return new UserDetailDto[] {
            userMapper.applicationUserToUserDetailDto(applicationUser),
            userMapper.applicationUserToUserDetailDto(applicationUser)
        };
    }

    protected UserDetailDto[] getValidJudges(
        ApplicationUserRepository applicationUserRepository,
        SecurityUserRepository securityUserRepository
    ) {
        SecurityUser securityUser1 = new SecurityUser("valid1@email.com", "password");
        SecurityUser securityUser2 = new SecurityUser("valid2@email.com", "password");

        ApplicationUser applicationUser1 = new ApplicationUser(
            ApplicationUser.Role.PARTICIPANT,
            "firstName", "lastName",
            ApplicationUser.Gender.MALE,
            new Date(2000, 11, 1),
            ""
        );
        ApplicationUser applicationUser2 = new ApplicationUser(
            ApplicationUser.Role.CLUB_MANAGER,
            "firstName", "lastName",
            ApplicationUser.Gender.FEMALE,
            new Date(2000, 11, 1),
            ""
        );
        applicationUser1.setUser(securityUser1);
        securityUser1.setUser(applicationUser1);
        applicationUser2.setUser(securityUser2);
        securityUser2.setUser(applicationUser2);

        securityUserRepository.save(securityUser1);
        applicationUserRepository.save(applicationUser1);
        securityUserRepository.save(securityUser2);
        applicationUserRepository.save(applicationUser2);

        return new UserDetailDto[] {
            userMapper.applicationUserToUserDetailDto(applicationUser1),
            userMapper.applicationUserToUserDetailDto(applicationUser2)
        };
    }

    protected void setupRandomApplicationUsers(
        ApplicationUserRepository applicationUserRepository,
        SecurityUserRepository securityUserRepository
    ) {
        Random random = new Random();
        random.setSeed(0);

        String[] names = {
            "Naima Bryan", "Theodore Hewitt", "Ciara Macdonald", "Jayson Butler",
            "Ellie-Mae Wallace", "Pearl Daniel", "Karl Montgomery", "Victor Wolf",
            "Momeo Edwards", "Alma Snow", "Mazel Evans", "Lewis Gregory",
            "Connie Webb", "Sharon Connor", "Mdil Graves", "Brooklyn Bailey",
            "Elisa Price", "Made Huff", "Anaya Mckenzie", "Jonty Booth",
            "Alice David", "Louie Oconnell", "Henry Glenn", "Genevieve Wise",
            "Juan Farmer", "Ronan Archer", "Maksymilian Prince", "Saarah Sandoval",
            "Ela Stein", "Ali Atkins", "Maizie Roy", "Tasneem Fleming",
            "Penny Solis", "Muhammed Brady", "Aamina Hanna", "Aliyah Hicks",
            "Maryam Ball", "Khadijah Hahn", "Joel Lin", "Leyla Ortega", "Kezia Mcpherson", "Yousuf Robbins",
            "Jean Combs", "Clayton Reid", "Nettie Kirby", "Antony Proctor", "Madeleine Nelson",
            "Conner Ochoa", "Albie Simon", "Nikita Campos", "Elodie Acevedo",
            "Aya Finch", "Tahlia Holman", "Mya Yang", "Tom Underwood",
            "Miana Boyle", "Jake Webb", "Jannat Mcguire", "Michael Pitts",
            "Melvin Bailey", "Clyde Holder", "Stephen Butler", "Kayla Santana",
            "Junior Newton", "Serena Oconnell", "Asa Dillon", "Dana Buckley", "Abraham Benjamin",
            "Anton Hood", "Autumn Sykes", "Riya Watkins", "Mohamad Barron",
            "Chelsea Cotton", "Sabrina Rowe", "Damien Golden", "Haris John",
            "Krystal Carney", "Byron Humphrey", "Eleni Saunders", "Miriam Davila",
            "Gracie Huff", "Emilio Ryan", "Benedict Garza", "Emilia Lawrence",
            "Matie Holmes", "Eliot Curry", "Milly-May Arroyo", "Jane Griffith",
            "Isabella Clark", "Dalton Berry",
        };
        ApplicationUser.Role[] roles = ApplicationUser.Role.values();
        ApplicationUser.Gender[] genders = ApplicationUser.Gender.values();

        for (int i = 0; i < names.length; ++i) {
            String[] split = names[i].split(" ");
            String firstName = split[0];
            String lastName = split[1];

            SecurityUser securityUser = new SecurityUser("email" + i + "@email.com", "password");

            ApplicationUser applicationUser = new ApplicationUser(
                roles[random.nextInt(roles.length)],
                firstName, lastName,
                genders[random.nextInt(genders.length)],
                new Date(random.nextInt(100) + 1920,
                    random.nextInt(12)+1,
                    random.nextInt(27)+1),
                ""
            );
            applicationUser.setUser(securityUser);
            securityUser.setUser(applicationUser);

            securityUserRepository.save(securityUser);
            applicationUserRepository.save(applicationUser);
        }
    }


}
