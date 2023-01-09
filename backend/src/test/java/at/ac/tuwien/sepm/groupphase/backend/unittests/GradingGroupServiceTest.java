package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class GradingGroupServiceTest extends TestDataProvider {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private GradingGroupService gradingGroupService;

    @Autowired
    private GradingGroupRepository gradingGroupRepository;

    @BeforeEach
    public void beforeEach() {
        competitionRepository.deleteAll();
        applicationUserRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        setUpCompetitionUser();
        setUpParticipantUser();
    }

    @Test
    public void whenGettingAllGroupsOfCompetition_whenNoCompId_given_expectValidation() {
        assertThrows(ValidationListException.class, () -> {
            gradingGroupService.getAllByCompetition(null);
        });
    }

    @Test
    public void testGetAllGroupsOfCompetition() {
        Competition c = getValidCompetitionEntity();
        Competition c2 = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");
        GradingGroup g2 = new GradingGroup("G2");
        GradingGroup g3 = new GradingGroup("G2");

        c.setGradingGroups(Set.of(g1, g2));
        c2.setGradingGroups(Set.of(g3));

        g1.setCompetitions(c);
        g2.setCompetitions(c);
        g3.setCompetitions(c2);

        Competition cc1 = competitionRepository.save(c);
        Competition cc2 = competitionRepository.save(c2);

        gradingGroupRepository.save(g1);
        gradingGroupRepository.save(g2);
        gradingGroupRepository.save(g3);

        List<SimpleGradingGroupDto> gg1 = gradingGroupService.getAllByCompetition(cc1.getId());
        List<SimpleGradingGroupDto> gg2 = gradingGroupService.getAllByCompetition(cc2.getId());
        assertEquals(2, gg1.size());
        assertEquals(1, gg2.size());
    }
}
