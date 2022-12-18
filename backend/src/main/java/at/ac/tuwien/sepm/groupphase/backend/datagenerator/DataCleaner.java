package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

@Component("DataCleaner")
@Profile({"test", "manual-test"})
@Transactional
public class DataCleaner {
    private final ApplicationUserRepository applicationUserRepository;
    private final CompetitionRepository competitionRepository;

    public DataCleaner(ApplicationUserRepository applicationUserRepository, CompetitionRepository competitionRepository) {
        this.applicationUserRepository = applicationUserRepository;
        this.competitionRepository = competitionRepository;
    }

    @PostConstruct
    public void clear() {
        competitionRepository.deleteAll();
        applicationUserRepository.deleteAll();
    }
}
