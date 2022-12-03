package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SessionUtils {

    private final ApplicationUserRepository applicationUserRepository;
    private String dummyUserEmail;

    public SessionUtils(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    /**
     * Only to be used in DataGenerator.java and tests.
     *
     * @param email Email of dummy user
     */
    public void setSessionUserEmail(String email) {
        this.dummyUserEmail = email;
    }

    private Object getSessionUserDetails() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private String getSessionUserEmail() {
        if (dummyUserEmail != null) {
            return this.dummyUserEmail;
        }

        Object user = this.getSessionUserDetails();
        if (user instanceof UserDetails) {
            return ((UserDetails) user).getUsername();
        }
        if (user instanceof String) {
            return (String) user;
        }
        return null;
    }

    public ApplicationUser.Role getApplicationUserRole() {
        return getSessionUser() == null ? null : getSessionUser().getType();
    }

    public boolean isParticipant() {
        return getApplicationUserRole() == ApplicationUser.Role.PARTICIPANT;
    }

    public boolean isCompetitionManager() {
        return getApplicationUserRole() == ApplicationUser.Role.TOURNAMENT_MANAGER;
    }

    public boolean isClubManager() {
        return getApplicationUserRole() == ApplicationUser.Role.CLUB_MANAGER;
    }

    public ApplicationUser getSessionUser() {
        String sessionUserEmail = getSessionUserEmail();
        if (sessionUserEmail == null) {
            return null;
        }
        Optional<ApplicationUser> user = applicationUserRepository.findApplicationUserByUserEmail(sessionUserEmail);

        if (user.isEmpty()) {
            throw new RuntimeException("User is authenticated but not persisted. This should never happen");
        }

        return user.get();
    }
}
