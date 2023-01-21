package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

@Component
@Profile({"test", "manual-test"})
@DependsOn("DataCleaner")
public class ClubManagerTeamImportDataGenerator implements ClubManagerTeamImportGeneratorHelper {
    private final ApplicationUserRepository userRepository;

    public ClubManagerTeamImportDataGenerator(ApplicationUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void setup() {
        userRepository.flush();
        setupClubManagers();
    }

    private void setupClubManagers() {
        generatedClubManagers.clear();
        var cm = testClubManagers.get(0);
        var sucm = testClubManagersSecUsers.get(0);
        cm.setUser(sucm);
        generatedClubManagers.add(userRepository.saveAndFlush(cm));
    }
}
