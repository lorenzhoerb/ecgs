package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder.GradingGroupBuilder;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder.UserBuilder;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.Flags;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.entity.ManagedBy;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade;
import at.ac.tuwien.sepm.groupphase.backend.entity.grade.GradePk;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.FlagsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterConstraintRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Profile({"generateDataMr"})
@Transactional
@Component
public class Mr3Generator {

    private final CompetitionRepository competitionRepository;
    private final GradingGroupRepository gradingGroupRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final RegisterToRepository registerToRepository;
    private final GradingSystemRepository gradingSystemRepository;
    private final CustomUserDetailService customUserDetailService;
    private final RegisterConstraintRepository registerConstraintRepository;
    private final FlagsRepository flagsRepository;
    private final ManagedByRepository managedByRepository;
    private final UserBuilder userBuilder;
    private final GradingGroupBuilder gradingGroupBuilder;
    private final GradingSystemFactory gradingSystemFactory;
    private final GradeRepository gradeRepository;
    private ApplicationUser creator;
    private ApplicationUser clubManager;
    private List<ApplicationUser> participants;
    private List<ApplicationUser> judges;
    private GradingSystem gsMale;
    private GradingSystem gsFemale;
    private Random rand;

    public Mr3Generator(CompetitionRepository competitionRepository, GradingGroupRepository gradingGroupRepository, ApplicationUserRepository applicationUserRepository, RegisterToRepository registerToRepository,
                        GradingSystemRepository gradingSystemRepository, CustomUserDetailService customUserDetailService, RegisterConstraintRepository registerConstraintRepository, FlagsRepository flagsRepository,
                        ManagedByRepository managedByRepository,
                        UserBuilder userBuilder, GradingGroupBuilder gradingGroupBuilder, GradingSystemFactory gradingSystemFactory, GradeRepository gradeRepository) {
        this.competitionRepository = competitionRepository;
        this.gradingGroupRepository = gradingGroupRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.registerToRepository = registerToRepository;
        this.gradingSystemRepository = gradingSystemRepository;
        this.customUserDetailService = customUserDetailService;
        this.registerConstraintRepository = registerConstraintRepository;
        this.flagsRepository = flagsRepository;
        this.managedByRepository = managedByRepository;
        this.userBuilder = userBuilder;
        this.gradingGroupBuilder = gradingGroupBuilder;
        this.gradingSystemFactory = gradingSystemFactory;
        this.gradeRepository = gradeRepository;
    }

    @PostConstruct
    public void generate() {
        rand = new Random();
        creator = getCreator();
        clubManager = getClubManager();
        participants = getParticipantUsers(300);
        judges = getJudgeUser(20);

        userBuilder
            .builder()
            .withLogin("constraint@email.com", "12345678")
            .withName("Constantin", "Constraint")
            .withDateOfBirth(new Date(99, 12, 2))
            .withRole(ApplicationUser.Role.PARTICIPANT)
            .withGender(ApplicationUser.Gender.MALE)
            .create();

        gsMale = gradingSystemFactory
            .createGradingSystem("turn10M", "Turn 10 männlich", true, true, creator);

        gsFemale = gradingSystemFactory
            .createGradingSystem("turn10W", "Turn 10 weiblich", true, true, creator);

        generateCompetitionOpenForRegistration();
        generateCompetitionOpenForRegistrationV2();
        generateClosedCompetitionInEditState();
        generateCompetitionWithClosedRegistration();
        generateSmallCompetitionWithCompleteGrading();

        // assign members to club manager
        for (int i = 100; i < 150; i++) {
            assignUserToManager(clubManager.getId(), participants.get(i).getId(), "Götzner Turnverband");
        }
    }

    /**
     * show final grades
     */
    private void generateSmallCompetitionWithCompleteGrading() {
        LocalDateTime now = LocalDateTime.now();
        Competition c = new Competition();
        c.setName("Vereinsmeisterschaft 2023");
        c.setDescription("Wir laden alle Turner und Turnerinnen ein, an unserer Vereinsmeisterschaft im Turnen teilzunehmen!" +
            " Zeigt eure Fähigkeiten auf den Geräten Boden, Pferd, Reck, Barren und Ringe und kämpft um den Titel des Vereinsmeisters." +
            " Ob Anfänger oder fortgeschrittener Turner, jeder ist willkommen, um sein Können zu präsentieren und sich mit anderen zu messen." +
            " Der Wettkampf findet am [Datum] statt und wird ein unvergessliches Erlebnis für alle Teilnehmer sein." +
            " Meldet euch jetzt an und bereitet euch auf einen spannenden Wettbewerb vor!");
        c.setDraft(false);
        c.setPublic(true);
        c.setEmail("otv@gmail.at");
        c.setBeginOfRegistration(now.minusDays(10));
        c.setEndOfRegistration(now.minusDays(4));
        c.setBeginOfCompetition(now.minusDays(2));
        c.setEndOfCompetition(now.minusDays(1));
        c.setJudges(Set.of(judges.get(0), judges.get(1), judges.get(2)));
        c.setCreator(creator);
        Competition competition = competitionRepository.save(c);

        var group1 = gradingGroupBuilder.builder().withTitle("AK7M")
            .withGradingSystem(gradingSystemFactory.createGradingSystem("Turn10M", "Bewertungssystem AK7M", true, false, creator))
            .withCompetition(competition).create();

        var group2 = gradingGroupBuilder.builder().withTitle("AK12M")
            .withGradingSystem(gradingSystemFactory.createGradingSystem("Turn10M", "Bewertungssystem AK12M", true, false, creator))
            .withCompetition(competition)
            .create();

        var group3 = gradingGroupBuilder.builder().withTitle("AK12W")
            .withGradingSystem(gradingSystemFactory.createGradingSystem("Turn10W", "Bewertungssystem AK12W", true, false, creator))
            .withCompetition(competition)
            .create();

        Flags f = new Flags("R001");
        f.setRegistrations(new HashSet<>());
        Flags fSaved = flagsRepository.save(f);

        for (int i = 0; i < 5; i++) {
            RegisterTo reg = registerUserToGroup(group1.getId(), participants.get(i).getId(), true);
            fSaved = assignFlag(reg, fSaved);
            gradeTurn10Part(List.of(judges.get(0), judges.get(1)), participants.get(i), competition, group1);
        }

        for (int i = 5; i < 10; i++) {
            RegisterTo reg = registerUserToGroup(group2.getId(), participants.get(i).getId(), true);
            gradeTurn10Part(List.of(judges.get(0), judges.get(1)), participants.get(i), competition, group2);
        }

        for (int i = 10; i < 15; i++) {
            registerUserToGroup(group3.getId(), participants.get(i).getId(), true);
            gradeTurn10Part(List.of(judges.get(0), judges.get(1)), participants.get(i), competition, group3);
        }

        // add participant that is managed by club manager
        registerUserToGroup(group3.getId(), participants.get(101).getId(), true);
        gradeTurn10Part(List.of(judges.get(0), judges.get(1)), participants.get(101), competition, group3);
    }

    private void gradeTurn10Part(List<ApplicationUser> judges, ApplicationUser part, Competition c, GradingGroup g) {
        for (int judgeC = 0; judgeC < judges.size(); judgeC++) {
            for (int station = 1; station <= 2; station++) {
                gradeRepository.save(new Grade(
                    new GradePk(
                        judges.get(judgeC).getId(),
                        part.getId(),
                        c.getId(),
                        g.getId(),
                        Long.valueOf(station)
                    ),
                    judges.get(judgeC),
                    part,
                    c,
                    g,
                    "{\"grades\":[{\"id\":1,\"value\":" + (rand.nextInt(10) + 1) + "},{\"id\":2,\"value\":" + (rand.nextInt(10) + 1) + "},{\"id\":3,\"value\":" + (rand.nextInt(4) + 1) + "}]}",
                    true
                ));
            }
        }
    }

    /**
     * No special feature. Just for filtering purposes
     */
    private void generateClosedCompetitionInEditState() {
        LocalDateTime now = LocalDateTime.now();
        Competition c = new Competition();
        c.setName("Draft: Turn 10 Mannschaftsmeisterschaft Österreich");
        c.setDescription("Die diesejährige Turn10 Mannschaftsmeisterschaft findet dieses Jahr in Wien statt. Es werden X Teilnehmer...");
        c.setDraft(true);
        c.setPublic(true);
        c.setEmail("tszdornbirn@gmail.at");
        c.setBeginOfRegistration(now.plusDays(30));
        c.setEndOfRegistration(now.plusDays(36));
        c.setBeginOfCompetition(now.plusDays(50));
        c.setEndOfCompetition(now.plusDays(51));
        c.setJudges(Set.of(judges.get(0), judges.get(1), judges.get(2)));
        c.setCreator(creator);
        Competition competition = competitionRepository.save(c);

        gradingGroupBuilder.builder().withTitle("AK7M")
            .withGradingSystem(gradingSystemFactory.createGradingSystem("Turn10M", "Bewertungssystem AK7M", true, false, creator))
            .withCompetition(competition).create();
    }

    /**
     * Nates feature. Competition is ongoing and some judges already entered the grades
     */
    private void generateCompetitionWithClosedRegistration() {
        LocalDateTime now = LocalDateTime.now();
        Competition c = new Competition();
        c.setName("Weltmeisterschaft Leistungsturnen 2023");
        c.setDescription(
            "Die Weltmeisterschaft im Leistungsturnen 2023 ist ein prestigeträchtiger " +
                "internationaler Wettbewerb, bei dem die besten Turner aus aller Welt " +
                "gegeneinander antreten. Die Teilnehmer zeigen ihre atemberaubenden athletischen " +
                "Fähigkeiten auf den Geräten Boden, Pferd, Reck, Barren und Ringe. Der Wettbewerb findet " +
                "einmal jährlich statt und zielt darauf ab, die besten Turner zu küren und ihre Fähigkeiten " +
                "auf den Weltbühnen zu feiern.");
        c.setDraft(false);
        c.setPublic(true);
        c.setEmail("otv@gmail.at");
        c.setBeginOfRegistration(now.minusDays(10));
        c.setEndOfRegistration(now.minusDays(4));
        c.setBeginOfCompetition(now.minusDays(1));
        c.setEndOfCompetition(now.plusDays(1));
        c.setJudges(Set.of(judges.get(0), judges.get(1), judges.get(2)));
        c.setCreator(creator);
        Competition competition = competitionRepository.save(c);

        var group1 = gradingGroupBuilder.builder().withTitle("L1M")
            .withGradingSystem(gradingSystemFactory.createGradingSystem("Turn10M", "Bewertungssystem L1M", true, false, creator))
            .withCompetition(competition).create();


        var group2 = gradingGroupBuilder.builder().withTitle("L1W")
            .withGradingSystem(gsFemale)
            .withCompetition(competition)
            .create();

        Flags f = new Flags("R001");
        f.setRegistrations(new HashSet<>());
        Flags fSaved = flagsRepository.save(f);
        for (int i = 0; i < 25; i++) {
            RegisterTo reg = registerUserToGroup(group1.getId(), participants.get(i).getId(), true);
            fSaved = assignFlag(reg, fSaved);
            gradeTurn10Part(List.of(judges.get(0), judges.get(1)), participants.get(i), competition, group1);
        }

        Flags f2 = new Flags("R002");
        f2.setRegistrations(new HashSet<>());
        Flags fSaved2 = flagsRepository.save(f2);

        Flags f3 = new Flags("Single");
        f3.setRegistrations(new HashSet<>());
        Flags fSaved3 = flagsRepository.save(f3);
        for (int i = 25; i < 50; i++) {
            RegisterTo reg = registerUserToGroup(group2.getId(), participants.get(i).getId(), true);
            fSaved2 = assignFlag(reg, fSaved2);
            if (i == 49) {
                fSaved2 = assignFlag(reg, fSaved3);
            }
        }

        for (int i = 25; i < 49; i++) {
            gradeTurn10Part(List.of(judges.get(0), judges.get(1)), participants.get(i), competition, group2);
        }
    }

    /**
     * Lorenz special feature
     */
    private void generateCompetitionOpenForRegistration() {
        LocalDateTime now = LocalDateTime.now();
        Competition c = new Competition();
        c.setName("10th Annual Gymnastics Competition");
        c.setDescription(
            "Das 10. jährliche Turnwettkampf findet am Wochenende des 20. und 21. März statt und " +
                "versammelt die besten Turnerinnen und Turner aus dem ganzen Land. Die Veranstaltung " +
                "findet im Stadion X statt und bietet eine atemberaubende Show mit atemberaubenden Tricks" +
                " und spektakulären Leistungen.\n" +
                "\n" +
                "Am Samstag werden die Wettkämpfe im Bereich Boden, Reck und Pferd stattfinden, während " +
                "am Sonntag die Wettkämpfe am Barren, Sprung und den Mehrkampf gezeigt werden. Jeder Teilnehmer wird in seiner Altersklasse bewertet und es werden Preise für die besten Leistungen vergeben.\n" +
                "\n" +
                "Die Teilnehmer haben sich in den letzten Monaten hart darauf vorbereitet und zeigen " +
                "ihr Können vor einer begeisterten Zuschauerkulisse. Die Zuschauer können erwarten, " +
                "beeindruckende Akrobatik, synchronisierte Bewegungen und makellose Landungen zu sehen.");
        c.setDraft(false);
        c.setPublic(true);
        c.setEmail("tszdornbirn@gmail.at");
        c.setPhone("+4369917184795");
        c.setBeginOfRegistration(now.minusDays(10));
        c.setEndOfRegistration(now.plusDays(10));
        c.setBeginOfCompetition(now.plusDays(40));
        c.setEndOfCompetition(now.plusDays(41));
        c.setJudges(Set.of(judges.get(0), judges.get(1), judges.get(2)));
        c.setCreator(creator);
        Competition savedC = competitionRepository.save(c);

        RegisterConstraint rc = new RegisterConstraint();
        rc.setType(RegisterConstraint.ConstraintType.AGE);
        rc.setOperator(RegisterConstraint.Operator.EQUALS);
        rc.setConstraintValue("10");

        RegisterConstraint rc2 = new RegisterConstraint();
        rc2.setType(RegisterConstraint.ConstraintType.GENDER);
        rc2.setOperator(RegisterConstraint.Operator.EQUALS);
        rc2.setConstraintValue("MALE");

        GradingGroup ak10M = gradingGroupBuilder.builder().withTitle("AK10M")
            .withCompetition(savedC).withGradingSystem(gradingSystemFactory.createGradingSystem("Turn10M", "Bewertungssystem AK10M", true, false, creator)).withConstraints(List.of(rc, rc2)).create();
        GradingGroup ak14M = gradingGroupBuilder.builder().withTitle("AK14M")
            .withCompetition(savedC).withGradingSystem(gradingSystemFactory.createGradingSystem("Turn10M", "Bewertungssystem AK14M", true, false, creator)).create();
        GradingGroup ak10W = gradingGroupBuilder.builder().withTitle("AK10W")
            .withCompetition(savedC).withGradingSystem(gradingSystemFactory.createGradingSystem("Turn10W", "Bewertungssystem AK10W", true, false, creator)).create();
        GradingGroup ak14W = gradingGroupBuilder.builder().withTitle("AK14W")
            .withCompetition(savedC).withGradingSystem(gradingSystemFactory.createGradingSystem("Turn10W", "Bewertungssystem AK14W", true, false, creator)).create();

        // Assign ak10M
        for (int i = 0; i < 10; i++) {
            registerUserToGroup(ak10M.getId(), participants.get(i).getId(), true);
        }

        // Assign ak10W
        for (int i = 10; i < 20; i++) {
            registerUserToGroup(ak10W.getId(), participants.get(i).getId(), false);
        }

        // Assign ak14M
        for (int i = 20; i < 25; i++) {
            registerUserToGroup(ak14M.getId(), participants.get(i).getId(), false);
        }

        // Assign ak14W
        for (int i = 25; i < 30; i++) {
            registerUserToGroup(ak14W.getId(), participants.get(i).getId(), true);
        }

        // Assign to club manager
        for (int i = 100; i < 140; i++) {
            assignUserToManager(creator.getId(), participants.get(i).getId(), "Test");
        }
    }

    /**
     * Lorenz special feature
     */
    private void generateCompetitionOpenForRegistrationV2() {
        LocalDateTime now = LocalDateTime.now();
        Competition c = new Competition();
        c.setName("Landesmeisterschaft Bayern 2023");
        c.setDescription(
            "Das 10. jährliche Turnfest steht bevor und versammelt die besten Turner und Turnerinnen aus dem ganzen Land am Wochenende des 20. und 21. März. Das Spektakel findet im Stadion X statt und bietet eine fesselnde Show voller atemberaubender Tricks und beeindruckender Leistungen.\n" +
                "\n" +
                "Am Samstag stehen Boden, Reck und Pferd im Mittelpunkt, während Sonntag der Barren, Sprung und Mehrkampf im Fokus stehen. Jeder Teilnehmer wird in seiner Altersklasse bewertet und es gibt Preise für herausragende Leistungen zu gewinnen.\n" +
                "\n" +
                "Die Athleten haben hart gearbeitet und sich auf die Veranstaltung vorbereitet, um ihr Können vor einer begeisterten Zuschauermenge zu zeigen. Die Zuschauer können atemberaubende Akrobatik, perfekte Synchronität und makellose Landungen erwarten. Kommen Sie und erleben Sie das 10. jährliche Turnfest!");
        c.setDraft(false);
        c.setPublic(true);
        c.setEmail("bayern-turn@gmail.at");
        c.setPhone("+4369917184793");
        c.setBeginOfRegistration(now.minusDays(10));
        c.setEndOfRegistration(now.plusDays(10));
        c.setBeginOfCompetition(now.plusDays(40));
        c.setEndOfCompetition(now.plusDays(41));
        c.setJudges(Set.of(judges.get(0), judges.get(1), judges.get(2)));
        c.setCreator(creator);
        Competition savedC = competitionRepository.save(c);

        RegisterConstraint rc = new RegisterConstraint();
        rc.setType(RegisterConstraint.ConstraintType.AGE);
        rc.setOperator(RegisterConstraint.Operator.GREATER_EQUALS_THAN);
        rc.setConstraintValue("9");

        RegisterConstraint rc2 = new RegisterConstraint();
        rc2.setType(RegisterConstraint.ConstraintType.GENDER);
        rc2.setOperator(RegisterConstraint.Operator.EQUALS);
        rc2.setConstraintValue("MALE");

        RegisterConstraint rc3 = new RegisterConstraint();
        rc3.setType(RegisterConstraint.ConstraintType.AGE);
        rc3.setOperator(RegisterConstraint.Operator.LESS_EQUALS_THAN);
        rc3.setConstraintValue("11");

        GradingGroup ak10M = gradingGroupBuilder.builder().withTitle("AK10M")
            .withCompetition(savedC).withGradingSystem(gradingSystemFactory.createGradingSystem("Turn10M", "Bewertungssystem AK10M", true, false, creator)).withConstraints(List.of(rc, rc2, rc3)).create();
        GradingGroup ak14M = gradingGroupBuilder.builder().withTitle("AK14M")
            .withCompetition(savedC).withGradingSystem(gradingSystemFactory.createGradingSystem("Turn10M", "Bewertungssystem AK14M", true, false, creator)).create();
        GradingGroup ak10W = gradingGroupBuilder.builder().withTitle("AK10W")
            .withCompetition(savedC).withGradingSystem(gradingSystemFactory.createGradingSystem("Turn10W", "Bewertungssystem AK10W", true, false, creator)).create();
        GradingGroup ak14W = gradingGroupBuilder.builder().withTitle("AK14W")
            .withCompetition(savedC).withGradingSystem(gradingSystemFactory.createGradingSystem("Turn10W", "Bewertungssystem AK14W", true, false, creator)).create();
    }

    private ApplicationUser getClubManager() {
        return userBuilder
            .builder()
            .withLogin("cm@email.com", "12345678")
            .withName("Andrea", "Schilling")
            .withRole(ApplicationUser.Role.CLUB_MANAGER)
            .withGender(ApplicationUser.Gender.FEMALE)
            .withTeamName("Andreas Freizeitclub")
            .create();
    }

    private ManagedBy assignUserToManager(Long managerId, Long memberId, String teamName) {
        ApplicationUser manager = applicationUserRepository.findById(managerId).get();
        ApplicationUser member = applicationUserRepository.findById(memberId).get();
        ManagedBy managedBy = new ManagedBy();
        managedBy.setManager(manager);
        managedBy.setMember(member);
        managedBy.setTeamName(teamName);
        return managedByRepository.save(managedBy);
    }

    private RegisterTo registerUserToGroup(Long gradingGroupId, Long participantId, boolean accepted) {
        GradingGroup g = gradingGroupRepository.findById(gradingGroupId).get();
        ApplicationUser u = applicationUserRepository.findById(participantId).get();

        RegisterTo registerTo = new RegisterTo();
        registerTo.setGradingGroup(g);
        registerTo.setParticipant(u);
        registerTo.setAccepted(accepted);
        return registerToRepository.save(registerTo);
    }

    private ApplicationUser getCreator() {
        return userBuilder.builder().withLogin("tm@email.com", "12345678").withName("Franz", "Fischer").withRole(ApplicationUser.Role.TOURNAMENT_MANAGER).withGender(ApplicationUser.Gender.MALE).withTeamName("Creators Club").create();
    }

    private List<ApplicationUser> getParticipantUsers(int n) {
        List<ApplicationUser> users = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ApplicationUser rmdUser = UserProvider.getRandomAppUser();
            ApplicationUser user = userBuilder
                .builder()
                .withLogin(getMockEmail("pa", i), "12345678")
                .withName(rmdUser.getFirstName(), rmdUser.getLastName()).
                withRole(ApplicationUser.Role.PARTICIPANT).
                withDateOfBirth(rmdUser.getDateOfBirth()).
                withGender(rmdUser.getGender()).create();

            users.add(user);
        }
        return users;
    }

    private List<ApplicationUser> getJudgeUser(int n) {
        List<ApplicationUser> users = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ApplicationUser rmdUser = UserProvider.getRandomAppUser();
            ApplicationUser user = userBuilder
                .builder()
                .withLogin(getMockEmail("ju", i), "12345678")
                .withName(rmdUser.getFirstName(), rmdUser.getLastName()).
                withRole(ApplicationUser.Role.PARTICIPANT).
                withDateOfBirth(rmdUser.getDateOfBirth()).
                withGender(rmdUser.getGender()).create();
            users.add(user);
        }
        return users;
    }

    private String getMockEmail(String role, int id) {
        return role + id + "@email.com";
    }

    private Flags assignFlag(RegisterTo register, Flags flag) {
        flag.getRegistrations().add(register);
        return flagsRepository.save(flag);
    }
}
