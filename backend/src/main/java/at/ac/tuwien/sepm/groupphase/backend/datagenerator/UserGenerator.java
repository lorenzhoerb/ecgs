package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder.UserBuilder;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ManagedBy;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

@Profile("generateData")
@Component
public class UserGenerator {

    private final CustomUserDetailService customUserDetailService;
    private final ApplicationUserRepository applicationUserRepository;
    private final SecurityUserRepository securityUserRepository;
    private final ManagedByRepository managedByRepository;
    private final UserBuilder userBuilder;

    public UserGenerator(CustomUserDetailService customUserDetailService,
                         ApplicationUserRepository applicationUserRepository,
                         SecurityUserRepository securityUserRepository, ManagedByRepository managedByRepository, UserBuilder userBuilder) {
        this.customUserDetailService = customUserDetailService;
        this.applicationUserRepository = applicationUserRepository;
        this.securityUserRepository = securityUserRepository;
        this.managedByRepository = managedByRepository;
        this.userBuilder = userBuilder;
    }

    @PostConstruct
    private void generateUsers() {
        ApplicationUser clubManager = customUserDetailService.registerUser(new UserRegisterDto(
            "cm@email.com",
            "12345678",
            "Andrea",
            "Schilling",
            ApplicationUser.Gender.FEMALE,
            new Date(70, 1, 1),
            ApplicationUser.Role.CLUB_MANAGER
        ));

        customUserDetailService.registerUser(new UserRegisterDto(
            "pa@email.com",
            "12345678",
            "Kevin",
            "Klein",
            ApplicationUser.Gender.FEMALE,
            new Date(70, 1, 1),
            ApplicationUser.Role.PARTICIPANT
        ));

        addManagedParticipants(clubManager.getId(), 20, "Team 1");
    }

    private void addManagedParticipants(Long managerId, int participants, String team) {
        ApplicationUser manager = applicationUserRepository.findById(managerId).get();
        List<ApplicationUser> users = UserProvider.getParticipants(participants);

        for (ApplicationUser user : users) {
            ApplicationUser createdUser = applicationUserRepository.save(user);
            managedByRepository.save(new ManagedBy(manager, createdUser, team));
        }


    }
}
