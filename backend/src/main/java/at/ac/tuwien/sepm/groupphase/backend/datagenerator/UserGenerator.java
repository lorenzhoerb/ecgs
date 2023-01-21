package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder.UserBuilder;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder.CompetitionBuilder;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.ManagedBy;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Set;
import java.util.List;

@Profile("generateData")
@Component
public class UserGenerator {

    private final CustomUserDetailService customUserDetailService;
    private final SecurityUserRepository securityUserRepository;
    private final ManagedByRepository managedByRepository;
    private final UserBuilder userBuilder;
    private final CompetitionRepository competitionRepository;
    private final GradingGroupRepository gradingGroupRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final RegisterToRepository registerToRepository;
    private final GradingSystemRepository gradingSystemRepository;

    public UserGenerator(CustomUserDetailService customUserDetailService, CompetitionRepository competitionRepository,
                         GradingGroupRepository gradingGroupRepository, ApplicationUserRepository applicationUserRepository,
                         RegisterToRepository registerToRepository, GradingSystemRepository gradingSystemRepository,
                         SecurityUserRepository securityUserRepository, ManagedByRepository managedByRepository, UserBuilder userBuilder) {
        this.customUserDetailService = customUserDetailService;
        this.competitionRepository = competitionRepository;
        this.gradingGroupRepository = gradingGroupRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.registerToRepository = registerToRepository;
        this.gradingSystemRepository = gradingSystemRepository;
        this.securityUserRepository = securityUserRepository;
        this.managedByRepository = managedByRepository;
        this.userBuilder = userBuilder;
    }

    @PostConstruct
    private void generateUsers() {
        ApplicationUser tm = customUserDetailService.registerUser(new UserRegisterDto(
            "tm@email.com",
            "12345678",
            "Franz",
            "Fischer",
            ApplicationUser.Gender.MALE,
            new Date(99, 1, 1),
            ApplicationUser.Role.TOURNAMENT_MANAGER
        ));

        ApplicationUser cm = customUserDetailService.registerUser(new UserRegisterDto(
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

        generateCompetitionWithOwner(tm, cm);
    }

    private void generateCompetitionWithOwner(ApplicationUser tm, ApplicationUser cm) {
        Set<ApplicationUser> judges = Set.of(tm, cm);

        new CompetitionBuilder(
            applicationUserRepository,
            competitionRepository,
            gradingGroupRepository,
            registerToRepository, gradingSystemRepository)
            .withParticipantsPerGroup(5)
            .withName("Judge Test")
            .setPublic(true)
            .createWithCreatorAndJudges(tm, judges);
        addManagedParticipants(cm.getId(), 20, "Team 1");
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
