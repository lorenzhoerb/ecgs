package at.ac.tuwien.sepm.groupphase.backend.util;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

/**
 * Session Utils offers methods to interact with the current session user.
 */
@Component
public class SessionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
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
        LOGGER.debug("setSessionUserEmail {}", email);
        this.dummyUserEmail = email;
    }

    private Object getSessionUserDetails() {
        LOGGER.debug("getSessionUserDetails");
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private String getSessionUserEmail() {
        LOGGER.debug("getSessionUserEmail");
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

    /**
     * Gets the role of the session user. If no user is logged in, null is returned.
     *
     * @return Role of session user or null if session user is null.
     */
    public ApplicationUser.Role getApplicationUserRole() {
        LOGGER.debug("getApplicationUserRole");
        return getSessionUser() == null ? null : getSessionUser().getType();
    }

    /**
     * Checks if the role of the session user is participant.
     *
     * @return true if role is participant, else false.
     */
    public boolean isParticipant() {
        LOGGER.debug("isParticipant");
        return getApplicationUserRole() == ApplicationUser.Role.PARTICIPANT;
    }

    /**
     * Checks if the role of the session user is competition manager.
     *
     * @return true if role is competition manager, else false.
     */
    public boolean isCompetitionManager() {
        LOGGER.debug("isCompetitionManager");
        return getApplicationUserRole() == ApplicationUser.Role.TOURNAMENT_MANAGER;
    }

    /**
     * Checks if the role of the session user is club manager.
     *
     * @return true if role is club manager, else false.
     */
    public boolean isClubManager() {
        LOGGER.debug("isClubManager");
        return getApplicationUserRole() == ApplicationUser.Role.CLUB_MANAGER;
    }

    /**
     * Gets the current session user.
     *
     * @return session user if there is one else null
     */
    public ApplicationUser getSessionUser() {
        LOGGER.debug("getSessionUser");
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
