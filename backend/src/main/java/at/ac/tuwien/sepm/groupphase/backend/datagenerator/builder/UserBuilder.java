package at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder;

import at.ac.tuwien.sepm.groupphase.backend.datagenerator.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Profile("generateData")
@Component
public class UserBuilder {

    private final CustomUserDetailService customUserDetailService;
    private final RegisterToRepository registerToRepository;

    public UserBuilder(CustomUserDetailService customUserDetailService, RegisterToRepository registerToRepository) {
        this.customUserDetailService = customUserDetailService;
        this.registerToRepository = registerToRepository;
    }

    public UserConstruct builder() {
        return new UserConstruct();
    }

    public class UserConstruct {
        private ApplicationUser applicationUser;
        private SecurityUser securityUser;
        private static int USER_COUNTER = 1;
        private List<ApplicationUser> clubMembers;

        public UserConstruct() {
            applicationUser = getDefaultApplicationUser();
            securityUser = getDefaultSecurityUser();
            USER_COUNTER++;
        }

        public UserConstruct withClubMembers(List<ApplicationUser> clubMembers) {
            this.clubMembers = clubMembers;
            return this;
        }

        public UserConstruct withApplicationUser(ApplicationUser applicationUser) {
            this.applicationUser = applicationUser;
            return this;
        }

        public UserConstruct withDateOfBirth(Date date) {
            this.applicationUser.setDateOfBirth(date);
            return this;
        }

        public UserConstruct withRole(ApplicationUser.Role role) {
            this.applicationUser.setType(role);
            return this;
        }

        public UserConstruct withGender(ApplicationUser.Gender gender) {
            this.applicationUser.setGender(gender);
            return this;
        }

        public UserConstruct withLogin(String email, String password) {
            this.securityUser.setEmail(email);
            this.securityUser.setPassword(password);
            return this;
        }

        public UserConstruct withEmail(String email) {
            this.securityUser.setEmail(email);
            return this;
        }

        public UserConstruct withPassword(String password) {
            this.securityUser.setPassword(password);
            return this;
        }

        public UserConstruct withName(String firstName, String lastName) {
            applicationUser.setFirstName(firstName);
            applicationUser.setLastName(lastName);
            return this;
        }

        public UserConstruct withFirstName(String firstName) {
            applicationUser.setFirstName(firstName);
            return this;
        }

        public UserConstruct withLastName(String lastName) {
            applicationUser.setLastName(lastName);
            return this;
        }

        public ApplicationUser create() {
            ApplicationUser appUser = customUserDetailService
                .registerUser(new UserRegisterDto(
                    securityUser.getEmail(),
                    securityUser.getPassword(),
                    applicationUser.getFirstName(),
                    applicationUser.getLastName(),
                    applicationUser.getGender(),
                    applicationUser.getDateOfBirth(),
                    applicationUser.getType()
                ));
            return appUser;
        }

        private ApplicationUser getDefaultApplicationUser() {
            return UserProvider.getRandomAppUser();
        }

        private SecurityUser getDefaultSecurityUser() {
            return new SecurityUser(
                "participant" + USER_COUNTER + "@email.com",
                "12345678"
            );
        }
    }
}
