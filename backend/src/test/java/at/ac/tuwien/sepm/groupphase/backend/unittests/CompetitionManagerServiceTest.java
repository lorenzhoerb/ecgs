package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.CalendarViewDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.help.CalendarViewHelp;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

import static at.ac.tuwien.sepm.groupphase.backend.help.CalendarViewHelp.CURRENT_WEEK_NUMBER;
import static at.ac.tuwien.sepm.groupphase.backend.help.CalendarViewHelp.CURRENT_YEAR;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class CompetitionManagerServiceTest extends TestDataProvider {
    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private CalendarViewDataGenerator generator;

    @BeforeEach
    public void beforeEach() {
        applicationUserRepository.deleteAll();
        competitionRepository.deleteAll();

        generator = new CalendarViewDataGenerator(applicationUserRepository,
                                                  competitionRepository);

        generator.setup();
    }

    @Test
    public void allManagedInTimeCompetitionsAreRetrieved() {
        var managedCompetitions = userService.getCompetitionsForCalendar(
            generator.generatedCompetitionManagers.get(0), 2022, 38);
        assertThat(managedCompetitions)
            .map(Competition::getName,
                (t -> t.getBeginOfRegistration().toLocalDate().toString()),
                (t -> t.getEndOfRegistration().toLocalDate().toString()),
                (t -> t.getBeginOfCompetition().toLocalDate().toString()),
                (t -> t.getEndOfCompetition().toLocalDate().toString()),
                Competition::getDescription, Competition::getPicturePath, Competition::getPublic, Competition::getDraft,
                Competition::getEmail, Competition::getPhone)
            // @TODO: are the checking dates computed correctly because this competition is not in the given week
            .contains(CalendarViewHelp.generateTupleOfCompetition(CalendarViewDataGenerator.testCompetitions.get(0)))
            .contains(CalendarViewHelp.generateTupleOfCompetition(CalendarViewDataGenerator.testCompetitions.get(2)));
    }

    @Test
    public void nonManagedCompetitionIsNotRetrieved() {

        var managedCompetitions = userService.getCompetitionsForCalendar(
            generator.generatedCompetitionManagers.get(0), CURRENT_YEAR, CURRENT_WEEK_NUMBER);
        assertThat(managedCompetitions)
            .map(Competition::getName,
                (t -> t.getBeginOfRegistration().toLocalDate().toString()),
                (t -> t.getEndOfRegistration().toLocalDate().toString()),
                (t -> t.getBeginOfCompetition().toLocalDate().toString()),
                (t -> t.getEndOfCompetition().toLocalDate().toString()),
                Competition::getDescription, Competition::getPicturePath, Competition::getPublic, Competition::getDraft,
                Competition::getEmail, Competition::getPhone)
            .doesNotContain(CalendarViewHelp.generateTupleOfCompetition(CalendarViewDataGenerator.testCompetitions.get(5)));
    }
}
