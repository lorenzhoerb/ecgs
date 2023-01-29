package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlag;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.FlagsMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingSystemMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.ManagedBy;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade;
import at.ac.tuwien.sepm.groupphase.backend.entity.grade.GradePk;
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
import at.ac.tuwien.sepm.groupphase.backend.repository.FlagsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradeService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReportService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
public abstract class TestDataProvider {

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private GradingGroupRepository gradingGroupRepository;

    @Autowired
    private ManagedByRepository managedByRepository;

    @Autowired
    private FlagsRepository flagsRepository;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private GradingSystemRepository gradingSystemRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GradingSystemMapper gradingSystemMapper;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private RegisterToRepository registerToRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private FlagsMapper flagsMapper;

    protected static final String BASE_URI = "/api/v1";
    protected static final String COMPETITION_URI = "/competitions";
    protected static final String GRADING_GROUP_URI = "/groups";
    protected static final String GRADING_GROUPS_BASE_URI = BASE_URI + GRADING_GROUP_URI;

    protected static final String GRADES_BASE_URI = BASE_URI + "/grades";
    protected static final String COMPETITION_BASE_URI = BASE_URI + COMPETITION_URI;
    protected static final String GRADING_GROUP_BASE_URI = BASE_URI + "/grading-systems";
    protected static final String USER_URI = "/user";
    protected static final String USER_BASE_URI = BASE_URI + USER_URI;
    protected static final String COMPETITION_SELF_REGISTRATION_BASE_URI = USER_BASE_URI + "/competitions/";

    protected static final String TEST_USER_BASIC_EMAIL = "basic@email.com";
    protected static final String TEST_USER_COMPETITION_MANAGER_EMAIL = "comp.manager@email.com";
    protected static final String TEST_USER_CLUB_MANAGER_EMAIL = "club.manager@email.com";
    protected static final String TEST_USER_PARTICIPANT_EMAIL = "participant@email.com";

    protected UserRegisterDto getValidRegistrationDtoForCompetitionManager() {
        return new UserRegisterDto(
            TEST_USER_COMPETITION_MANAGER_EMAIL,
            "12345678",
            "firstNameTest",
            "lastNameTest",
            ApplicationUser.Gender.FEMALE,
            new Date(99, 3, 1),
            ApplicationUser.Role.TOURNAMENT_MANAGER);
    }

    protected UserRegisterDto getValidRegistrationDtoForParticipant() {
        return new UserRegisterDto(
            TEST_USER_PARTICIPANT_EMAIL,
            "12345678",
            "Firsname",
            "Lastname",
            ApplicationUser.Gender.MALE,
            new Date(99, 3, 2),
            ApplicationUser.Role.PARTICIPANT);
    }

    protected UserRegisterDto getValidRegistrationDtoForClubManager() {
        return new UserRegisterDto(
            TEST_USER_CLUB_MANAGER_EMAIL,
            "12345678",
            "Firsname",
            "Lastname",
            ApplicationUser.Gender.MALE,
            new Date(99, 3, 3),
            ApplicationUser.Role.CLUB_MANAGER);
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

    protected GradingGroupDto[] getValidGradingGroupDtos() throws JsonProcessingException {
        return new GradingGroupDto[]{
            new GradingGroupDto().withTitle("Group 1")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
            new GradingGroupDto().withTitle("Group 2")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
            new GradingGroupDto().withTitle("Group 3")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
        };
    }

    protected GradingGroupDto[] getGradingGroupDtosWithNameDuplicates() throws JsonProcessingException {
        return new GradingGroupDto[]{
            new GradingGroupDto().withTitle("Group 1")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
            new GradingGroupDto().withTitle("Group 1")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
            new GradingGroupDto().withTitle("Group 2")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
        };
    }

    protected GradingGroupDto[] getGradingGroupDtosWithEmptyNames() throws JsonProcessingException {
        return new GradingGroupDto[]{
            new GradingGroupDto().withTitle("Group 1")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
            new GradingGroupDto().withTitle("")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
            new GradingGroupDto().withTitle("Group 2")
                .withGradingSystemDto(getValidGradingSystemDetailDto()),
        };
    }

    protected ApplicationUser createValidParticipantUser(ApplicationUserRepository applicationUserRepository,
                                                   SecurityUserRepository securityUserRepository) {
        SecurityUser securityUser = new SecurityUser("notDuplicate@email.com", "password");

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

        return applicationUser;
    }

    protected ApplicationUser createValidJudgeUser(ApplicationUserRepository applicationUserRepository,
                                                   SecurityUserRepository securityUserRepository, String email) {
        SecurityUser securityUser = new SecurityUser(email, "password");

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

        return applicationUser;
    }


    protected void setUpCompetitionUserWithEMail(String email) {
        String password = "12345678";

        UserRegisterDto userRegisterDto = new UserRegisterDto(
            email,
            password,
            "firstNameTest",
            "lastNameTest",
            ApplicationUser.Gender.FEMALE,
            new Date(),
            ApplicationUser.Role.TOURNAMENT_MANAGER);

        customUserDetailService.registerUser(userRegisterDto);
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setPassword(password);
        userLoginDto.setEmail(email);
        customUserDetailService.login(userLoginDto);
    }

    protected void assignUserToManager(ApplicationUser manager, ApplicationUser user, String team) {
        ApplicationUser createdUser = applicationUserRepository.save(user);
        managedByRepository.save(new ManagedBy(manager, createdUser, team));
    }

    protected List<ParticipantRegistrationDto> getParticipantRegistrationDto(Long managerId, Long groupId) {
        List<ParticipantRegistrationDto> registrations = new ArrayList<>();
        List<ManagedBy> managedBy = managedByRepository.findByManagerId(managerId);
        return managedBy
            .stream()
            .map(e -> new ParticipantRegistrationDto(e.getMember().getId(), groupId))
            .toList();
    }

    protected void clubManagerWithManagedUsers(int users) {
        ApplicationUser clubManager = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_CLUB_MANAGER_EMAIL).get();
        for (int i = 0; i < users; i++) {
            ApplicationUser createdAppUser = applicationUserRepository.save(new ApplicationUser(
                ApplicationUser.Role.PARTICIPANT,
                "Lorenz",
                "Test",
                ApplicationUser.Gender.MALE,
                new Date(),
                null
            ));
            assignUserToManager(clubManager, createdAppUser, "Test");
        }
    }

    protected ApplicationUser setUpCompetitionUser() {
        return customUserDetailService
            .registerUser(getValidRegistrationDtoForCompetitionManager());
    }

    protected ApplicationUser setUpAlternateCompetitionUser(String username, String password) {
        UserRegisterDto toRegister = getValidRegistrationDtoForCompetitionManager();
        toRegister.setEmail(username);
        toRegister.setPassword(password);
        return customUserDetailService
            .registerUser(toRegister);
    }

    protected void setUpParticipantUser() {
        customUserDetailService.registerUser(getValidRegistrationDtoForParticipant());
    }

    protected void setUpClubManagerUser() {
        customUserDetailService.registerUser(getValidRegistrationDtoForClubManager());
    }

    protected Competition createCompetitionEntity(
        ApplicationUserRepository applicationUserRepository,
        RegisterToRepository registerToRepository,
        GradingGroupRepository gradingGroupRepository,
        CompetitionRepository competitionRepository,
        boolean accepted,
        boolean draft
    ) throws JsonProcessingException {
        return createCompetitionEntity(applicationUserRepository,
            registerToRepository, gradingGroupRepository,
            competitionRepository, accepted, draft, null, null);
    }

    protected Competition createCompetitionEntity(
        ApplicationUserRepository applicationUserRepository,
        RegisterToRepository registerToRepository,
        GradingGroupRepository gradingGroupRepository,
        CompetitionRepository competitionRepository,
        boolean accepted,
        boolean draft,
        Set<ApplicationUser> judges,
        GradingSystemRepository gradingSystemRepository
    ) throws JsonProcessingException {
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

        if(judges != null) {
            competition.setJudges(judges);
        }

        ApplicationUser user = new ApplicationUser(
            ApplicationUser.Role.PARTICIPANT,
            "first", "last",
            ApplicationUser.Gender.MALE,
            new Date(2000, 10, 10),
            ""
        );


        ApplicationUser creator = new ApplicationUser(
            ApplicationUser.Role.TOURNAMENT_MANAGER,
            "first", "last",
            ApplicationUser.Gender.MALE,
            new Date(2000, 10, 10),
            ""
        );

        applicationUserRepository.save(user);
        applicationUserRepository.save(creator);

        competition.setCreator(creator);

        GradingGroup group = new GradingGroup("group 1");

        if (gradingSystemRepository != null) {
            group.setGradingSystem(new at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem(
                "Default",
                "Default",
                true,
                false,
                getGradingSystemFormula(),
                Set.of()
            ));
        }


        competition.setGradingGroups(Set.of(group));
        group.setCompetitions(competition);

        if (gradingSystemRepository != null) {
            gradingSystemRepository.save(group.getGradingSystem());
        }

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
        system.stations = new Station[]{
            new Station(1L, "Station", new Variable[] {
            }, Op)
        };
        system.formula = new VariableRef(1L);

        return mapper.writeValueAsString(system);
    }

    protected <T extends Operation> String getSingleOp2Formula(T op) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        GradingSystem system = new GradingSystem();
        system.stations = new Station[]{
            new Station(1L, "Station", new Variable[] {
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
        system.stations = new Station[]{
            new Station(1L, "Station", new Variable[] {
                new Variable(1L, "Var1", minJudgeCount, strat),
            }, new VariableRef(1L))
        };
        system.formula = new VariableRef(1L);

        return mapper.writeValueAsString(system);
    }

    protected String getGradingSystemFormula() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        GradingSystem system = new GradingSystem();
        system.stations = new Station[]{
            new Station(1L, "Station 1", new Variable[]{
                new Variable(1L, "Var 1", 2L, new Mean()),
                new Variable(2L, "Var 2", 1L, new Mean()),
            }, new Add(new VariableRef(1L), new VariableRef(2L))),
            new Station(2L, "Station 2", new Variable[] {
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

        return new UserDetailDto[]{
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

        return new UserDetailDto[]{
            userMapper.applicationUserToUserDetailDto(applicationUser1),
            userMapper.applicationUserToUserDetailDto(applicationUser2)
        };
    }

    protected UserDetailDto[] getValidCompManagerJudge(
        ApplicationUserRepository applicationUserRepository,
        SecurityUserRepository securityUserRepository
    ) {

        ApplicationUser applicationUser1 = new ApplicationUser(
            ApplicationUser.Role.TOURNAMENT_MANAGER,
            "CompManager", "Judge",
            ApplicationUser.Gender.MALE,
            new Date(2000, 11, 1),
            ""
        );
        applicationUser1.setUser(new SecurityUser(TEST_USER_COMPETITION_MANAGER_EMAIL,"password"));
        applicationUserRepository.save(applicationUser1);

        return new UserDetailDto[]{
            userMapper.applicationUserToUserDetailDto(applicationUser1)
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
                    random.nextInt(12) + 1,
                    random.nextInt(27) + 1),
                ""
            );
            applicationUser.setUser(securityUser);
            securityUser.setUser(applicationUser);

            securityUserRepository.save(securityUser);
            applicationUserRepository.save(applicationUser);
        }
    }

    protected List<ImportFlag> flagsImport_setupTestFlags() {
        var managedByes = flagsImport_setupTestManagedBy();

        return new ArrayList<ImportFlag>() {
            {
                add(new ImportFlag(
                    "part1@test.test",
                    "cool"
                ));
                add(new ImportFlag(
                    "part2@test.test",
                    "cool"
                ));
                add(new ImportFlag(
                    "part3@test.test",
                    "cool"
                ));
            }
        };
    }

    protected List<ImportFlag> flagsImport_setupTestFlags2() {
        var managedByes = flagsImport_setupTestManagedBy();

        return new ArrayList<ImportFlag>() {
            {
                add(new ImportFlag(
                    "part1@test.test",
                    "cool"
                ));
                add(new ImportFlag(
                    "part2@test.test",
                    "cool"
                ));
                add(new ImportFlag(
                    "part3@test.test",
                    "cool"
                ));
            }
        };
    }
    protected List<ApplicationUser> flagsImport_setupTestParticipants(){
        var part1 = customUserDetailService.registerUser(new UserRegisterDto(
        "part1@test.test",
        "rootroot",
        "fnOne",
        "lnOne",
        ApplicationUser.Gender.MALE,
        new Date(1041697924000L),
        ApplicationUser.Role.PARTICIPANT
        ));
        var part2 = customUserDetailService.registerUser(new UserRegisterDto(
            "part2@test.test",
            "rootroot",
            "fnSec",
            "lnSec",
            ApplicationUser.Gender.MALE,
            new Date(1041697924000L),
            ApplicationUser.Role.PARTICIPANT
        ));
        var part3 = customUserDetailService.registerUser(new UserRegisterDto(
            "part3@test.test",
            "rootroot",
            "fnThi",
            "lnThi",
            ApplicationUser.Gender.MALE,
            new Date(1041697924000L),
            ApplicationUser.Role.PARTICIPANT
        ));
        var part4 = customUserDetailService.registerUser(new UserRegisterDto(
            "part4@test.test",
            "rootroot",
            "fnFou",
            "lnFou",
            ApplicationUser.Gender.MALE,
            new Date(1041697924000L),
            ApplicationUser.Role.PARTICIPANT
        ));

        var part5 = customUserDetailService.registerUser(new UserRegisterDto(
            "part5@test.test",
            "rootroot",
            "fnFif",
            "lnFif",
            ApplicationUser.Gender.MALE,
            new Date(1041697924000L),
            ApplicationUser.Role.PARTICIPANT
        ));

        return new ArrayList<>() {
            {
                add(part1);
                add(part2);
                add(part3);
                add(part4);
                add(part5);
            }
        };
    }
    protected List<ApplicationUser> flagsImport_setupTestClubManagers() {
        var cm1 = customUserDetailService.registerUser(new UserRegisterDto(
            "cm_1@test.test",
            "rootroot",
            "fnnn",
            "lnnn",
            ApplicationUser.Gender.MALE,
            new Date(1041697924000L),
            ApplicationUser.Role.CLUB_MANAGER
        ));

        return new ArrayList<>() {
            {
                add(cm1);
            }
        };
    }
    protected List<ManagedBy> flagsImport_setupTestManagedBy() {
        var cms = flagsImport_setupTestClubManagers();
        var parts = flagsImport_setupTestParticipants();

        var ret = new ArrayList<ManagedBy>();
        ret.add(managedByRepository.save(
            new ManagedBy(
                cms.get(0),
                parts.get(0),
                "Kekus"
            )
        ));
        ret.add(managedByRepository.save(
            new ManagedBy(
                cms.get(0),
                parts.get(1),
                "Kekus"
            )
        ));
        ret.add(managedByRepository.save(
            new ManagedBy(
                cms.get(0),
                parts.get(2),
                "Kekus"
            )
        ));
        ret.add(managedByRepository.save(
            new ManagedBy(
                cms.get(0),
                parts.get(3),
                "Kekus"
            )
        ));

        return ret;
    }

    protected List<at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem> setupGradingSystems() {
        var newTM = customUserDetailService.registerUser(new UserRegisterDto(
            "gs_test_1@test.test",
            "rootroot",
            "fff",
            "sss",
            ApplicationUser.Gender.MALE,
            new Date(),
            ApplicationUser.Role.TOURNAMENT_MANAGER
        ));
        var newTM2 = customUserDetailService.registerUser(new UserRegisterDto(
            "gs_test_2@test.test",
            "rootroot",
            "fff",
            "sss",
            ApplicationUser.Gender.MALE,
            new Date(),
            ApplicationUser.Role.TOURNAMENT_MANAGER
        ));

        List<at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem> out = new ArrayList<>();
        out.add(gradingSystemRepository.save(
            new at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem(
                "GS_NAME1",
                "GS_DESC1",
                true,
                true,
                "{}",
                null,
                newTM
            ))
        );
        out.add(gradingSystemRepository.save(
            new at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem(
                "GS_NAME2",
                "GS_DESC2",
                false,
                true,
                "{}",
                null,
                newTM
            ))
        );
        out.add(gradingSystemRepository.save(
            new at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem(
                "GS_NAME3",
                "GS_DESC3",
                true,
                false,
                "{}",
                null,
                newTM
            ))
        );
        out.add(gradingSystemRepository.save(
            new at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem(
                "GS_NAME4",
                "GS_DESC4",
                false,
                false,
                "{}",
                null,
                newTM
            ))
        );
        out.add(gradingSystemRepository.save(
            new at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem(
                "GS_NAME5",
                "GS_DESC5",
                true,
                true,
                "{}",
                null,
                newTM2
            ))
        );
        out.add(gradingSystemRepository.save(
            new at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem(
                "GS_NAME6",
                "GS_DESC6",
                false,
                true,
                "{}",
                null,
                newTM2
            ))
        );
        out.add(gradingSystemRepository.save(
            new at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem(
                "GS_NAME7",
                "GS_DESC7",
                true,
                false,
                "{}",
                null,
                newTM2
            ))
        );
        out.add(gradingSystemRepository.save(
            new at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem(
                "GS_NAME8",
                "GS_DESC8",
                false,
                false,
                "{}",
                null,
                newTM2
            ))
        );

        return out;
    }

    protected GradingGroupDto[] reportTests_setupGradingGroups() {
        var gradingGroups = new GradingGroupDto[2];
        gradingGroups[0] = new GradingGroupDto(
            "GG1", new GradingSystemDetailDto("GS1", "GS1DESC", false, false,
            "{\"stations\":[{\"id\":1,\"displayName\":\"GG1_S1\",\"variables\":[{\"id\":1,\"displayName\":\"GG1_S1_V1\",\"minJudgeCount\":2,\"strategy\":{\"type\":\"mean\"},\"values\":[]},{\"id\":2,\"displayName\":\"GG1_S1_V2\",\"minJudgeCount\":1,\"strategy\":{\"type\":\"mean\"},\"values\":[]}],\"formula\":{\"typeHint\":\"add\",\"left\":{\"typeHint\":\"variableRef\",\"value\":1},\"right\":{\"typeHint\":\"variableRef\",\"value\":2}}},{\"id\":2,\"displayName\":\"GG1_S2\",\"variables\":[{\"id\":1,\"displayName\":\"GG1_S2_V1\",\"minJudgeCount\":2,\"strategy\":{\"type\":\"mean\"},\"values\":[]}],\"formula\":{\"typeHint\":\"div\",\"left\":{\"typeHint\":\"variableRef\",\"value\":1},\"right\":{\"typeHint\":\"const\",\"value\":2}}}],\"formula\":{\"typeHint\":\"add\",\"left\":{\"typeHint\":\"variableRef\",\"value\":1},\"right\":{\"typeHint\":\"variableRef\",\"value\":2}}}"
        ));
        gradingGroups[1] = new GradingGroupDto(
            "GG2", new GradingSystemDetailDto("GS2", "GS2DESC", false, false,
            "{\"stations\":[{\"id\":1,\"displayName\":\"GG2_S1\",\"variables\":[{\"id\":1,\"displayName\":\"GG2_S1_V1\",\"minJudgeCount\":2,\"strategy\":{\"type\":\"mean\"},\"values\":[]},{\"id\":2,\"displayName\":\"GG2_S1_V2\",\"minJudgeCount\":1,\"strategy\":{\"type\":\"mean\"},\"values\":[]}],\"formula\":{\"typeHint\":\"add\",\"left\":{\"typeHint\":\"variableRef\",\"value\":1},\"right\":{\"typeHint\":\"variableRef\",\"value\":2}}},{\"id\":2,\"displayName\":\"GG2_S2\",\"variables\":[{\"id\":1,\"displayName\":\"GG2_S2_V1\",\"minJudgeCount\":2,\"strategy\":{\"type\":\"mean\"},\"values\":[]}],\"formula\":{\"typeHint\":\"div\",\"left\":{\"typeHint\":\"variableRef\",\"value\":1},\"right\":{\"typeHint\":\"const\",\"value\":2}}}],\"formula\":{\"typeHint\":\"mult\",\"left\":{\"typeHint\":\"variableRef\",\"value\":1},\"right\":{\"typeHint\":\"variableRef\",\"value\":2}}}"
        ));

        return gradingGroups;
    }

    protected UserDetailDto[] reportTests_getJudges() {
        var judges = new UserDetailDto[3];
        judges[0] = userMapper.applicationUserToUserDetailDto(customUserDetailService.registerUser(new UserRegisterDto(
            "judge1@report.test",
            "12345678",
            "JUDGEoneFN",
            "JUDGEoneLN",
            ApplicationUser.Gender.MALE,
            new Date(),
            ApplicationUser.Role.PARTICIPANT
        )));
        judges[1] = userMapper.applicationUserToUserDetailDto(customUserDetailService.registerUser(new UserRegisterDto(
            "judge2@report.test",
            "12345678",
            "JUDGEtwoFN",
            "JUDGEtwoLN",
            ApplicationUser.Gender.MALE,
            new Date(),
            ApplicationUser.Role.PARTICIPANT
        )));
        judges[2] = userMapper.applicationUserToUserDetailDto(customUserDetailService.registerUser(new UserRegisterDto(
            "judge3@report.test",
            "12345678",
            "JUDGEthreeFN",
            "JUDGEthreeLN",
            ApplicationUser.Gender.MALE,
            new Date(),
            ApplicationUser.Role.PARTICIPANT
        )));

        return judges;
    }

    protected ApplicationUser[] reportTests_getNonRegisteredToCompetitonParticipants() {
        var participants = new ApplicationUser[5];
        participants[0] = customUserDetailService.registerUser(new UserRegisterDto(
            "participant1@report.test",
            "12345678",
            "PARTICIPANToneFN",
            "PARTICIPANToneLN",
            ApplicationUser.Gender.MALE,
            new GregorianCalendar(2000, Calendar.MARCH, 1).getTime(),
            ApplicationUser.Role.PARTICIPANT
        ));
        participants[1] = customUserDetailService.registerUser(new UserRegisterDto(
            "participant2@report.test",
            "12345678",
            "PARTICIPANTtwoFN",
            "PARTICIPANTtwoLN",
            ApplicationUser.Gender.MALE,
            new GregorianCalendar(2000, Calendar.MARCH, 2).getTime(),
            ApplicationUser.Role.PARTICIPANT
        ));
        participants[2] = customUserDetailService.registerUser(new UserRegisterDto(
            "participant3@report.test",
            "12345678",
            "PARTICIPANTthreeFN",
            "PARTICIPANTthreeLN",
            ApplicationUser.Gender.MALE,
            new GregorianCalendar(2000, Calendar.MARCH, 3).getTime(),
            ApplicationUser.Role.PARTICIPANT
        ));
        participants[3] = customUserDetailService.registerUser(new UserRegisterDto(
            "participant4@report.test",
            "12345678",
            "PARTICIPANTfourFN",
            "PARTICIPANTfourLN",
            ApplicationUser.Gender.MALE,
            new GregorianCalendar(2000, Calendar.MARCH, 4).getTime(),
            ApplicationUser.Role.PARTICIPANT
        ));
        participants[4] = customUserDetailService.registerUser(new UserRegisterDto(
            "participant5@report.test",
            "12345678",
            "PARTICIPANTfiveFN",
            "PARTICIPANTfiveLN",
            ApplicationUser.Gender.MALE,
            new GregorianCalendar(2000, Calendar.MARCH, 5).getTime(),
            ApplicationUser.Role.PARTICIPANT
        ));

        return participants;
    }

    protected ApplicationUser[] reportTests_getClubManagers() {
        var clubManagers = new ApplicationUser[4];
        clubManagers[0] = customUserDetailService.registerUser(new UserRegisterDto(
            "club_manager1@report.test",
            "12345678",
            "CLUBMANAGERoneFN",
            "CLUBMANAGERoneLN",
            ApplicationUser.Gender.MALE,
            new GregorianCalendar(2000, Calendar.FEBRUARY, 1).getTime(),
            ApplicationUser.Role.CLUB_MANAGER
        ));
        clubManagers[1] = customUserDetailService.registerUser(new UserRegisterDto(
            "club_manager2@report.test",
            "12345678",
            "CLUBMANAGERtwoFN",
            "CLUBMANAGERtwoLN",
            ApplicationUser.Gender.MALE,
            new GregorianCalendar(2000, Calendar.FEBRUARY, 2).getTime(),
            ApplicationUser.Role.CLUB_MANAGER
        ));
        clubManagers[2] = customUserDetailService.registerUser(new UserRegisterDto(
            "club_manager3@report.test",
            "12345678",
            "CLUBMANAGERthreeFN",
            "CLUBMANAGERthreeLN",
            ApplicationUser.Gender.MALE,
            new GregorianCalendar(2000, Calendar.FEBRUARY, 3).getTime(),
            ApplicationUser.Role.CLUB_MANAGER
        ));
        clubManagers[3] = customUserDetailService.registerUser(new UserRegisterDto(
            "club_manager4@report.test",
            "12345678",
            "CLUBMANAGERfourFN",
            "CLUBMANAGERfourLN",
            ApplicationUser.Gender.MALE,
            new GregorianCalendar(2000, Calendar.FEBRUARY, 4).getTime(),
            ApplicationUser.Role.CLUB_MANAGER
        ));

        return clubManagers;
    }

    protected ApplicationUser reportTests_setupCompetitionUser() {
        customUserDetailService
            .registerUser(new UserRegisterDto(
                "comp_manager2@report.test",
                "12345678",
                "COMPMANAGERtwoFN",
                "COMPMANAGERtwoLN",
                ApplicationUser.Gender.FEMALE,
                new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime(),
                ApplicationUser.Role.TOURNAMENT_MANAGER)
            );

        return customUserDetailService
            .registerUser(new UserRegisterDto(
                "comp_manager1@report.test",
                "12345678",
                "COMPMANAGERoneFN",
                "COMPMANAGERoneLN",
                ApplicationUser.Gender.FEMALE,
                new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime(),
                ApplicationUser.Role.TOURNAMENT_MANAGER)
            );
    }

    protected Competition beforeEachReportTest() {
        var authBefore = SecurityContextHolder.getContext().getAuthentication();
        var compManager = new User("comp_manager1@report.test", "12345678", Arrays.asList(new SimpleGrantedAuthority("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)));
        Authentication auth = new UsernamePasswordAuthenticationToken(
            compManager,
            null,
            compManager.getAuthorities());

        // Set the security context for the current test
        SecurityContextHolder.getContext().setAuthentication(auth);
        reportTests_setupCompetitionUser();
        var compEntity = reportTests_setupCompetition();
        reportService.calculateResultsOfCompetition(compEntity.getId());

        SecurityContextHolder.getContext().setAuthentication(authBefore);
        return compEntity;
    }

    @Transactional
    @WithMockUser
    protected Competition reportTests_setupCompetition() {
        //reportTests_setupCompetitionUser();
        var gradingGroups = reportTests_setupGradingGroups();

        var competition = new CompetitionDetailDto();
        competition.setName("TESTNAMECOMP");
        competition.setDescription("TESTDESCCOMP");
        competition.setBeginOfRegistration(LocalDateTime.of(2023, Month.APRIL, 15, 12, 0));
        competition.setEndOfRegistration(LocalDateTime.of(2023, Month.APRIL, 17, 12, 0));
        competition.setBeginOfCompetition(LocalDateTime.of(2023, Month.APRIL, 18, 12, 0));
        competition.setEndOfCompetition(LocalDateTime.of(2023, Month.APRIL, 19, 12, 0));
        competition.setPublic(true);
        competition.setEmail("alooo@dont.care");
        competition.setPhone("+55555555555");
        competition.setGradingGroups(gradingGroups);
        competition.setJudges(reportTests_getJudges());
        var comp = competitionService.create(competition);
        var compEntity = competitionRepository.findById(comp.getId()).get();
        var clubManagers = reportTests_getClubManagers();
        var participantsToRegister = reportTests_getNonRegisteredToCompetitonParticipants();
        var gg1 = gradingGroupRepository.findFirstByTitleIs("GG1").get();
        var gg2 = gradingGroupRepository.findFirstByTitleIs("GG2").get();

        registerToRepository.save(new RegisterTo(
            participantsToRegister[0],
            gg1,
            true
        ));
        registerToRepository.save(new RegisterTo(
            participantsToRegister[1],
            gg1,
            true
        ));
        registerToRepository.save(new RegisterTo(
            participantsToRegister[2],
            gg1,
            true
        ));

        registerToRepository.save(new RegisterTo(
            participantsToRegister[1],
            gg2,
            true
        ));
        registerToRepository.save(new RegisterTo(
            participantsToRegister[2],
            gg2,
            true
        ));
        registerToRepository.save(new RegisterTo(
            participantsToRegister[3],
            gg2,
            true
        ));

        // registerToRepository.save(new RegisterTo(
        //     participantsToRegister[3],
        //     gg2,
        //     true
        // ));

        registerToRepository.save(new RegisterTo(
            clubManagers[2],
            gg1,
            true
        ));


        managedByRepository.save(new ManagedBy(
            clubManagers[0],
            participantsToRegister[0],
            "TEAMNAME"
        ));
        managedByRepository.save(new ManagedBy(
            clubManagers[0],
            participantsToRegister[1],
            "TEAMNAME"
        ));
        managedByRepository.save(new ManagedBy(
            clubManagers[0],
            participantsToRegister[4],
            "TEAMNAME"
        ));
        managedByRepository.save(new ManagedBy(
            clubManagers[1],
            participantsToRegister[1],
            "TEAMNAME2"
        ));

        var judgeIter = compEntity.getJudges().iterator();
        var judges = new ApplicationUser[] {
            judgeIter.next(), judgeIter.next(), judgeIter.next()
        };

        // PARTICIPANT 1
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                participantsToRegister[0].getId(),
                compEntity.getId(),
                gg1.getId(),
                1L
            ),
            judges[0],
            participantsToRegister[0],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":4},{\"id\":2,\"value\":5}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[1].getId(),
                participantsToRegister[0].getId(),
                compEntity.getId(),
                gg1.getId(),
                1L
            ),
            judges[1],
            participantsToRegister[0],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":9},{\"id\":2,\"value\":1}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                participantsToRegister[0].getId(),
                compEntity.getId(),
                gg1.getId(),
                2L
            ),
            judges[0],
            participantsToRegister[0],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":12}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[2].getId(),
                participantsToRegister[0].getId(),
                compEntity.getId(),
                gg1.getId(),
                2L
            ),
            judges[2],
            participantsToRegister[0],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":8}]}",
            true
        ));


        // PATRITIPANT 2
        // GG1
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                participantsToRegister[1].getId(),
                compEntity.getId(),
                gg1.getId(),
                1L
            ),
            judges[0],
            participantsToRegister[1],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":4},{\"id\":2,\"value\":5}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[1].getId(),
                participantsToRegister[1].getId(),
                compEntity.getId(),
                gg1.getId(),
                1L
            ),
            judges[1],
            participantsToRegister[1],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":9},{\"id\":2,\"value\":1}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                participantsToRegister[1].getId(),
                compEntity.getId(),
                gg1.getId(),
                2L
            ),
            judges[0],
            participantsToRegister[1],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":12}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[2].getId(),
                participantsToRegister[1].getId(),
                compEntity.getId(),
                gg1.getId(),
                2L
            ),
            judges[2],
            participantsToRegister[1],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":8}]}",
            true
        ));
        // GG2
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                participantsToRegister[1].getId(),
                compEntity.getId(),
                gg2.getId(),
                1L
            ),
            judges[0],
            participantsToRegister[1],
            compEntity,
            gg2,
            "{\"grades\":[{\"id\":1,\"value\":5},{\"id\":2,\"value\":4}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[1].getId(),
                participantsToRegister[1].getId(),
                compEntity.getId(),
                gg2.getId(),
                1L
            ),
            judges[1],
            participantsToRegister[1],
            compEntity,
            gg2,
            "{\"grades\":[{\"id\":1,\"value\":2.2},{\"id\":2,\"value\":4}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                participantsToRegister[1].getId(),
                compEntity.getId(),
                gg2.getId(),
                2L
            ),
            judges[0],
            participantsToRegister[1],
            compEntity,
            gg2,
            "{\"grades\":[{\"id\":1,\"value\":4}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[2].getId(),
                participantsToRegister[1].getId(),
                compEntity.getId(),
                gg2.getId(),
                2L
            ),
            judges[2],
            participantsToRegister[1],
            compEntity,
            gg2,
            "{\"grades\":[{\"id\":1,\"value\":6}]}",
            true
        ));

        // PATRITIPANT 3
        // GG1
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                participantsToRegister[2].getId(),
                compEntity.getId(),
                gg1.getId(),
                1L
            ),
            judges[0],
            participantsToRegister[2],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":1.4},{\"id\":2,\"value\":2}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[1].getId(),
                participantsToRegister[2].getId(),
                compEntity.getId(),
                gg1.getId(),
                1L
            ),
            judges[1],
            participantsToRegister[2],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":5},{\"id\":2,\"value\":6}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                participantsToRegister[2].getId(),
                compEntity.getId(),
                gg1.getId(),
                2L
            ),
            judges[0],
            participantsToRegister[2],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":12}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[2].getId(),
                participantsToRegister[2].getId(),
                compEntity.getId(),
                gg1.getId(),
                2L
            ),
            judges[2],
            participantsToRegister[2],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":10}]}",
            true
        ));
        // GG2
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                participantsToRegister[2].getId(),
                compEntity.getId(),
                gg2.getId(),
                1L
            ),
            judges[0],
            participantsToRegister[2],
            compEntity,
            gg2,
            "{\"grades\":[{\"id\":1,\"value\":5},{\"id\":2,\"value\":3}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[1].getId(),
                participantsToRegister[2].getId(),
                compEntity.getId(),
                gg2.getId(),
                1L
            ),
            judges[1],
            participantsToRegister[2],
            compEntity,
            gg2,
            "{\"grades\":[{\"id\":1,\"value\":1},{\"id\":2,\"value\":1}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                participantsToRegister[2].getId(),
                compEntity.getId(),
                gg2.getId(),
                2L
            ),
            judges[0],
            participantsToRegister[2],
            compEntity,
            gg2,
            "{\"grades\":[{\"id\":1,\"value\":23}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[2].getId(),
                participantsToRegister[2].getId(),
                compEntity.getId(),
                gg2.getId(),
                2L
            ),
            judges[2],
            participantsToRegister[2],
            compEntity,
            gg2,
            "{\"grades\":[{\"id\":1,\"value\":3}]}",
            true
        ));

        // PARTICIPANT 4
        // GG2
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                participantsToRegister[3].getId(),
                compEntity.getId(),
                gg2.getId(),
                1L
            ),
            judges[0],
            participantsToRegister[3],
            compEntity,
            gg2,
            "{\"grades\":[{\"id\":1,\"value\":4},{\"id\":2,\"value\":12}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[1].getId(),
                participantsToRegister[3].getId(),
                compEntity.getId(),
                gg2.getId(),
                1L
            ),
            judges[1],
            participantsToRegister[3],
            compEntity,
            gg2,
            "{\"grades\":[{\"id\":1,\"value\":31},{\"id\":2,\"value\":3.5}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                participantsToRegister[3].getId(),
                compEntity.getId(),
                gg2.getId(),
                2L
            ),
            judges[0],
            participantsToRegister[3],
            compEntity,
            gg2,
            "{\"grades\":[{\"id\":1,\"value\":7}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[2].getId(),
                participantsToRegister[3].getId(),
                compEntity.getId(),
                gg2.getId(),
                2L
            ),
            judges[2],
            participantsToRegister[3],
            compEntity,
            gg2,
            "{\"grades\":[{\"id\":1,\"value\":9}]}",
            true
        ));

        // CLUB MANAGER 3
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                clubManagers[2].getId(),
                compEntity.getId(),
                gg1.getId(),
                1L
            ),
            judges[0],
            clubManagers[2],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":5},{\"id\":2,\"value\":7}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[1].getId(),
                clubManagers[2].getId(),
                compEntity.getId(),
                gg1.getId(),
                1L
            ),
            judges[1],
            clubManagers[2],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":7},{\"id\":2,\"value\":7}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[0].getId(),
                clubManagers[2].getId(),
                compEntity.getId(),
                gg1.getId(),
                2L
            ),
            judges[0],
            clubManagers[2],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":8}]}",
            true
        ));
        gradeRepository.save(new Grade(
            new GradePk(
                judges[2].getId(),
                clubManagers[2].getId(),
                compEntity.getId(),
                gg1.getId(),
                2L
            ),
            judges[2],
            clubManagers[2],
            compEntity,
            gg1,
            "{\"grades\":[{\"id\":1,\"value\":2}]}",
            true
        ));

        return compEntity;
    }
}
