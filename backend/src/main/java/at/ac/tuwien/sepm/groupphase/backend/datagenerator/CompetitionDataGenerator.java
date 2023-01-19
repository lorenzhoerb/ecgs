package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder.CompetitionBuilder;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder.UserBuilder;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Profile("generateData")
@Component
public class CompetitionDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CompetitionRepository competitionRepository;
    private final GradingGroupRepository gradingGroupRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final RegisterToRepository registerToRepository;
    private final GradingSystemRepository gradingSystemRepository;
    private final CustomUserDetailService customUserDetailService;
    private final UserBuilder userBuilder;

    public CompetitionDataGenerator(CompetitionRepository competitionRepository, GradingGroupRepository gradingGroupRepository, ApplicationUserRepository applicationUserRepository, RegisterToRepository registerToRepository,
                                    GradingSystemRepository gradingSystemRepository, CustomUserDetailService customUserDetailService, UserBuilder userBuilder) {
        this.competitionRepository = competitionRepository;
        this.gradingGroupRepository = gradingGroupRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.registerToRepository = registerToRepository;
        this.gradingSystemRepository = gradingSystemRepository;
        this.customUserDetailService = customUserDetailService;
        this.userBuilder = userBuilder;
    }

    @PostConstruct
    private void generateCompetition() {
        LOGGER.debug("generateCompetition");

        ApplicationUser user = userBuilder.builder()
            .withLogin("tm2@email.com", "12345678")
            .withName("Franz", "Fischer")
            .withRole(ApplicationUser.Role.TOURNAMENT_MANAGER)
            .withGender(ApplicationUser.Gender.MALE)
            .create();

        LocalDateTime now = LocalDateTime.now();

        new CompetitionBuilder(
            applicationUserRepository,
            competitionRepository,
            gradingGroupRepository,
            registerToRepository, gradingSystemRepository)
            .withName("Österreichische Turn10-Meisterschaft")
            .withCreator(user)
            .withDescription("550 Hobby-Turnerinnen und Turner von 9 bis 82 Jahren, aus 75 Vereinen und allen neun "
                + "Bundesländern bildeten das Feld des österreichweiten Turn10-Jahreshöhepunktes.\n"
                + "\n"
                +
                "In der Sporthalle Schwaz gingen jene Kinder und Jugendlichen an den Start, die sich dafür in "
                + "ihrem jeweiligen Landesturnverband qualifiziert hatten - sowie zahlreiche Erwachsene, die sich ihrem Hobby nach wie vor wettkampforientiert widmen.\n"
                + "\n"
                + "Nach der langen Corona-Unterbrechung bzw. Einschränkungen merkte man allen den Spaß daran an, endlich "
                + "weder einen „normalen“ und teilnehmerstarken Wettkampf mit den Kolleg*innen aus ganz Österreich turnen zu können!\n"
                + "\n"
                +
                "Übrigens gingen die Medaillen an 37 Vereine und in jedes einzelne Bundesland: Um die österreichweite Durchdringung "
                + "und Verbreitung von hoher Gerätturn-Kompetenz muss man sich also auch aktuell keinerlei Sorgen machen! :)")
            .create();

        new CompetitionBuilder(
            applicationUserRepository,
            competitionRepository,
            gradingGroupRepository,
            registerToRepository, gradingSystemRepository)
            .withParticipantsPerGroup(5)
            .withCreator(user)
            .withName("Weltmeisterschaft Leistungsturnen")
            .withParticipantsPerGroup(10)
            .setPublic(false)
            .create();

        new CompetitionBuilder(
            applicationUserRepository,
            competitionRepository,
            gradingGroupRepository,
            registerToRepository, gradingSystemRepository)
            .withParticipantsPerGroup(10)
            .withName("Österreicher Mannschaftsmeisterschaft 2023")
            .withDescription("In der diesjähringen Mannschaftmeisterschaft treten 33 Teams an.")
            .withParticipantsPerGroup(50)
            .withCreator(user)
            .withRegistrationDates(now.plusYears(1), now.plusYears(1).plusDays(4))
            .withCompetitionDates(now.plusYears(1).plusDays(10), now.plusYears(1).plusDays(11))
            .create();

        new CompetitionBuilder(
            applicationUserRepository,
            competitionRepository,
            gradingGroupRepository,
            registerToRepository, gradingSystemRepository)
            .withParticipantsPerGroup(10)
            .withName("Luxembourg Open 2023")
            .withDescription("- Men's Artistic Gymnastics (Junior / Senior)\n"
                + "- Women's Artistic Gymnastics (Junior / Senior)")
            .withParticipantsPerGroup(10)
            .withRegistrationDates(now.plusYears(1), now.plusYears(1).plusDays(4))
            .withCompetitionDates(now.plusYears(1).plusDays(30), now.plusYears(1).plusDays(31))
            .withCreator(user)
            .create();

        new CompetitionBuilder(
            applicationUserRepository,
            competitionRepository,
            gradingGroupRepository,
            registerToRepository, gradingSystemRepository)
            .withParticipantsPerGroup(10)
            .withName("International Tournament Sofia Cup 2023")
            .withDescription("International sofia World Cup. Add more detailed description")
            .withParticipantsPerGroup(0)
            .withRegistrationDates(now.plusYears(1).plusDays(5), now.plusYears(1).plusDays(8))
            .withCompetitionDates(now.plusYears(1).plusDays(60), now.plusYears(1).plusDays(61))
            .setDraft(true)
            .withCreator(user)
            .create();

        new CompetitionBuilder(
            applicationUserRepository,
            competitionRepository,
            gradingGroupRepository,
            registerToRepository, gradingSystemRepository)
            .withParticipantsPerGroup(10)
            .withName("Austrian Golf Cup 2022")
            .withDescription("Sign up for the austrian golf cup.")
            .withGradingGroups(Set.of("M40", "M50", "M60", "W40", "W50", "W60"))
            .withRegistrationDates(now.minusDays(4), now.plusDays(4))
            .withCompetitionDates(now.plusDays(50), now.plusDays(51))
            .withCreator(user)
            .create();

        new CompetitionBuilder(
            applicationUserRepository,
            competitionRepository,
            gradingGroupRepository,
            registerToRepository, gradingSystemRepository)
            .withParticipantsPerGroup(0)
            .withName("The Real World Cup 2022")
            .withDescription("The real world cup is as real as never before. Sign up and compete against the realest persons.")
            .withGradingGroups(Set.of("M40", "M50", "M60", "W40", "W50", "W60"))
            .withRegistrationDates(now.minusDays(4), now.plusDays(4))
            .withCompetitionDates(now.plusDays(50), now.plusDays(51))
            .withCreator(user)
            .create();
    }
}
