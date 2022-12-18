package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Profile("generateData")
@Component
public class CompetitionDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CompetitionRepository competitionRepository;
    private final GradingGroupRepository gradingGroupRepository;

    private static final String COMPETITION_TITLE = "Competition ";
    private static final String GROUP_TITLE = "Group ";
    private static final int NUMBER_OF_COMPETITIONS_TO_GENERATE = 2;

    public CompetitionDataGenerator(CompetitionRepository competitionRepository, GradingGroupRepository gradingGroupRepository) {
        this.competitionRepository = competitionRepository;
        this.gradingGroupRepository = gradingGroupRepository;
    }

    @PostConstruct
    private void generateCompetition() {
        LOGGER.debug("generateCompetition");
        // generate open
        LocalDateTime now = LocalDateTime.now();
        Competition open = getCompetition(0);
        open.setDraft(false);
        open.setPublic(true);
        open.setBeginOfRegistration(now.minusDays(10));
        open.setEndOfRegistration(now.plusDays(10));
        open.setBeginOfCompetition(now.plusDays(20));
        open.setEndOfCompetition(now.plusDays(21));
        Set<GradingGroup> gs1 = getGradingGroups(5);
        open.setGradingGroups(gs1);
        competitionRepository.save(open);
        gs1.forEach((g) -> g.setCompetitions(open));
        gradingGroupRepository.saveAll(gs1);

        Competition draft = getCompetition(1);
        draft.setDraft(true);
        draft.setPublic(true);
        Set<GradingGroup> gs2 = getGradingGroups(5);
        draft.setGradingGroups(gs2);
        competitionRepository.save(draft);
        gs1.forEach((g) -> g.setCompetitions(draft));
        gradingGroupRepository.saveAll(gs2);
    }

    private Competition getCompetition(int i) {
        LocalDateTime now = LocalDateTime.now();
        return new Competition(COMPETITION_TITLE + i, now, now.plusDays(5), now.plusDays(10), now.plusDays(11), "This is the first description", null, false, true, "test@gmx.at", null);
    }

    private Set<GradingGroup> getGradingGroups(int size) {
        Set<GradingGroup> out = new HashSet<>();
        for (int i = 0; i < size; i++) {
            out.add(gradingGroup(i));
        }
        return out;
    }

    private GradingGroup gradingGroup(int i) {
        return new GradingGroup(GROUP_TITLE + i);
    }


    private ApplicationUser getApplicationUser() {
        return new ApplicationUser();
    }
}
