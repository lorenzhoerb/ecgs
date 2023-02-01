package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
@Profile({"test", "manual-test"})
@DependsOn("DataCleaner")
public class ClubManagerTeamImportDataGenerator implements ClubManagerTeamImportGeneratorHelper {
    private final ApplicationUserRepository userRepository;
    private final UserService userService;

    public ClubManagerTeamImportDataGenerator(ApplicationUserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @PostConstruct
    public void setup() {
        userRepository.flush();
        setupClubManagers();
    }

    private void setupClubManagers() {
        generatedClubManagers.clear();

        generatedClubManagers.add(userService.registerUser(new UserRegisterDto(
            "cm_test@test.test",
            "rootroot",
            "CMoneTESTfn",
            "CMoneTESTln",
            ApplicationUser.Gender.MALE,
            new Date(99, 1, 1),
            ApplicationUser.Role.CLUB_MANAGER,
            "TEST_TEAM_NAME"
        )));
    }
}
