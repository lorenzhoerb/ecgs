package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class CompetitionRepositoryTest {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Test
    public void createComponentWithValidInput_thenCheckReturnedWithId() {
        Competition competition = new Competition();
        competition.setName("Test1");
        competition.setDescription("Hello this is a description");
        competition.setPicturePath("this is a picture");
        competition.setBeginOfCompetition(LocalDateTime.now().plusDays(24));
        competition.setEndOfCompetition(LocalDateTime.now().plusDays(24));
        competition.setBeginOfRegistration(LocalDateTime.now());
        competition.setEndOfRegistration(LocalDateTime.now().plusDays(14));
        competition.setPublic(true);
        competition.setDraft(false);
        competition.setEmail("lorenz@gmx.at");

        Competition result = competitionRepository.save(competition);

        assertAll(
            () -> assertNotNull(result.getId()),
            () -> assertNotNull(competitionRepository.findById(result.getId())),
            () -> assertEquals("Test1", result.getName()),
            () -> assertEquals("Hello this is a description", result.getDescription()),
            () -> assertEquals("Hello this is a description", result.getDescription())
        );
    }

    @Test
    @Transactional
    public void createAndSearchForCompetitions_ShouldSuccess(){
        Competition competition = new Competition();
        competition.setName("Test1");
        competition.setDescription("Hello this is a description");
        competition.setPicturePath("this is a picture");
        competition.setBeginOfCompetition(LocalDateTime.now().plusDays(24));
        competition.setEndOfCompetition(LocalDateTime.now().plusDays(24));
        competition.setBeginOfRegistration(LocalDateTime.now().plusDays(1));
        competition.setEndOfRegistration(LocalDateTime.now().plusDays(14));
        competition.setPublic(true);
        competition.setDraft(false);
        competition.setEmail("lorenz@gmx.at");

        competitionRepository.save(competition);

        competition = new Competition();
        competition.setName("Test2");
        competition.setDescription("Hello this is a description2");
        competition.setPicturePath("this is a picture2");
        competition.setBeginOfCompetition(LocalDateTime.now().plusDays(25));
        competition.setEndOfCompetition(LocalDateTime.now().plusDays(25));
        competition.setBeginOfRegistration(LocalDateTime.now().plusDays(1));
        competition.setEndOfRegistration(LocalDateTime.now().plusDays(14));
        competition.setPublic(true);
        competition.setDraft(false);
        competition.setEmail("lorenz@gmx.at");

        competitionRepository.save(competition);

        competition = new Competition();
        competition.setName("Test3");
        competition.setDescription("Hello this is a description3");
        competition.setPicturePath("this is a picture3");
        competition.setBeginOfCompetition(LocalDateTime.now().minusDays(26));
        competition.setEndOfCompetition(LocalDateTime.now().plusDays(26));
        competition.setBeginOfRegistration(LocalDateTime.now().plusDays(1));
        competition.setEndOfRegistration(LocalDateTime.now().plusDays(14));
        competition.setPublic(true);
        competition.setDraft(false);
        competition.setEmail("lorenz@gmx.at");

        competitionRepository.save(competition);

        List<Competition> searchList = competitionRepository.findAllByBeginOfCompetitionAfterAndEndOfCompetitionAfterAndBeginOfRegistrationAfterAndEndOfRegistrationAfterAndNameContainingIgnoreCaseAndIsPublicIsTrue(LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(),  LocalDateTime.now(), "Test");

        assertEquals(2, searchList.size());
    }
}
