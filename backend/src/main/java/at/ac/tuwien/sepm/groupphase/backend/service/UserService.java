package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlag;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlagsResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantSelfRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserCredentialUpdateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserLoginDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;
import at.ac.tuwien.sepm.groupphase.backend.service.helprecords.ClubManagerTeamImportResults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService extends UserDetailsService {

    /**
     * Find a user in the context of Spring Security based on the email address
     * <br>
     * For more information have a look at this tutorial:
     * https://www.baeldung.com/spring-security-authentication-with-a-database
     *
     * @param email the email address
     * @return a Spring Security user
     * @throws UsernameNotFoundException is thrown if the specified user does not exists
     */
    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Find an application user based on the email address.
     *
     * @param email the email address
     * @return a application user
     */
    ApplicationUser findApplicationUserByEmail(String email);


    /**
     * Registers a user in the datastore.
     *
     * @param userRegisterDto the application User to create
     * @return the registered ApplicationUser
     */
    ApplicationUser registerUser(UserRegisterDto userRegisterDto);

    /**
     * Log in a user.
     *
     * @param userLoginDto login credentials
     * @return the JWT, if successful
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are bad
     */
    String login(UserLoginDto userLoginDto);


    /**
     * Sets the password reset token of the user specified by the email.
     *
     * @param email to identify the account whose password needs to be reset
     * @param token to only grant the person requesting the password reset access to it
     */
    void updateResetPasswordToken(String email, String token);


    /**
     * Gets an application user by the reset token set for his securityUser.
     *
     * @param token the token to search for the user
     * @return the ApplicationUser which SecurityUser matches the reset-token
     */
    Optional<SecurityUser> getSecurityUserByResetToken(String token);

    /**
     * Sets a new password for the given ApplicationUser.
     *
     * @param toUpdate    the ApplicationUser whose SecurityUsers password needs to be set
     * @param newPassword the new password to be set for the security user of the application user.
     */
    void updateSecurityUserPassword(SecurityUser toUpdate, String newPassword);

    /**
     * Find an security user based on the email address.
     *
     * @param email the email address
     * @return a security user
     */
    Optional<SecurityUser> findSecurityUserByEmail(String email);

    /**
     * Get competitions attached to current session user, for given year and week number.
     *
     * @param year the year to fetch calendar for
     * @param weekNumber the week number of @param{year} to fetch calendar for
     * @return set of competitions attached to user for that @param{year} and @param{weekNumber}
     */
    Set<Competition> getCompetitionsForCalendar(int year, int weekNumber);

    /**
     * Import a team as a club manager.
     *
     * @param clubManagerTeamImportDto a dto for club manager's team.
     * @return Dto that has a number of new participants added to the team and a number of already present ones. (present == managed by you)
     */
    ClubManagerTeamImportResults importTeam(ClubManagerTeamImportDto clubManagerTeamImportDto);

    /**
     * Search application user by name.
     *
     * @param searchDto dto with "first last" name string to search for and max results
     * @return Users matching the name
     */
    Set<UserDetailDto> findByUserName(UserSearchDto searchDto);

    /**
     * Prepares the reset password mail and sends it afterwards.
     *
     * @param userPasswordResetRequestDto The data containing the account to the send reset mail for.
     * @return the success message
     */
    String prepareAndSendPasswordResetMail(UserPasswordResetRequestDto userPasswordResetRequestDto);

    /**
     * Resets the password by the provided token and password.
     *
     * @param userPasswordResetDto the data containing the token and password to reset.
     * @return the success message
     */
    String resetPassword(UserPasswordResetDto userPasswordResetDto);

    /**
     * Changes the password of the logged in user.
     *
     * @param userCredentialUpdateDto the email and password of the user who wants to change them
     * @return the success message
     */
    String changePassword(@RequestBody UserCredentialUpdateDto userCredentialUpdateDto);

    /**
     * Registers the authenticated user to the competition by its id.
     *
     * @param id the id of the competitition to sign up to.
     * @param groupPreference the ParticipantSelfRegistrationDto groupPreference
     * @return the ResponseParticipantRegistrationDto of the registration
     */
    ResponseParticipantRegistrationDto registerToCompetition(Long id, ParticipantSelfRegistrationDto groupPreference);


    /**
     * Get current session user.
     *
     * @return the current session user
     */
    UserInfoDto getUser();

    UserDetailDto getUser(Long id);

    ImportFlagsResultDto importFlags(List<ImportFlag> flags);
}