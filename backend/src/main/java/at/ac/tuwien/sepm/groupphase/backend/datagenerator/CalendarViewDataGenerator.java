package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Profile({"test", "manual-test"})
@DependsOn("DataCleaner")
@Component
@Transactional
public class CalendarViewDataGenerator implements CompetitionGeneratorHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ApplicationUserRepository applicationUserRepository;
    private final CompetitionRepository competitionRepository;

    @Autowired
    public CalendarViewDataGenerator(ApplicationUserRepository applicationUserRepository, CompetitionRepository competitionRepository) {
        this.applicationUserRepository = applicationUserRepository;
        this.competitionRepository = competitionRepository;
    }

    @PostConstruct
    public void setup() {
        applicationUserRepository.flush();
        setupManagers();
        setupCompetitions();
    }

    private void setupCompetitions() {
        generatedCompetitions.clear();
        var iterator = testCompetitions.iterator();
        var t = iterator.next();
        t.setCreator(generatedCompetitionManagers.get(0));
        generatedCompetitions.add(competitionRepository.save(t));

        t = iterator.next();
        t.setCreator(generatedCompetitionManagers.get(0));
        generatedCompetitions.add(competitionRepository.save(t));

        t = iterator.next();
        t.setCreator(generatedCompetitionManagers.get(0));
        generatedCompetitions.add(competitionRepository.save(t));

        t = iterator.next();
        t.setCreator(generatedCompetitionManagers.get(1));
        generatedCompetitions.add(competitionRepository.save(t));

        t = iterator.next();
        t.setCreator(generatedCompetitionManagers.get(1));
        generatedCompetitions.add(competitionRepository.save(t));

        t = iterator.next();
        t.setCreator(generatedCompetitionManagers.get(2));
        generatedCompetitions.add(competitionRepository.save(t));

        t = iterator.next();
        t.setCreator(generatedCompetitionManagers.get(0));
        generatedCompetitions.add(competitionRepository.save(t));

        t = iterator.next();
        t.setCreator(generatedCompetitionManagers.get(0));
        generatedCompetitions.add(competitionRepository.save(t));

        t = iterator.next();
        t.setCreator(generatedCompetitionManagers.get(0));
        generatedCompetitions.add(competitionRepository.save(t));

        var temp = applicationUserRepository.findAll();

        reloadGeneratedManagers();
    }

    private void setupManagers() {
        generatedCompetitionManagers.clear();
        generatedCompetitionManagersSecUsers.clear();
        ApplicationUser au;
        for (int i = 0; i < testCompetitionManagers.size(); i++) {
            au = testCompetitionManagers.get(i);
            au.setUser(testCompetitionManagersManagersSecUsers.get(i));
            var savedAu = applicationUserRepository.save(au);
            generatedCompetitionManagers.add(savedAu);
            generatedCompetitionManagersSecUsers.add(savedAu.getUser());

        }

        applicationUserRepository.flush();
    }

    private void reloadGeneratedManagers() {
        for (int i = 0; i < generatedCompetitionManagers.size(); i++) {
            generatedCompetitionManagers.set(
                i,
                applicationUserRepository.findById(generatedCompetitionManagers.get(i).getId()).get());
        }
    }
}
