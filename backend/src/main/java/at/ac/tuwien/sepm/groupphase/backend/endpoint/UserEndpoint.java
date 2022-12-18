package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantSelfRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CalenderViewCompetitionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GeneralResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.helptypes.StatusText;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionRegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.helprecords.ClubManagerTeamImportResults;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.Set;

@RestController
@RequestMapping(value = UserEndpoint.BASE_PATH)
public class UserEndpoint {
    public static final String BASE_URI = "/api/v1/user";
    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final UserMapper userMapper;
    static final String BASE_PATH = "/api/v1/user";
    private final CompetitionMapper competitionMapper;


    private final SessionUtils sessionUtils;
    private final CompetitionRegistrationService competitionRegistrationService;

    @Autowired
    public UserEndpoint(
        UserService userService,
        UserMapper userMapper,
        SessionUtils sessionUtils,
        CompetitionRegistrationService competitionRegistrationService,
        CompetitionMapper competitionMapper) {
        this.userService = userService;
        this.competitionMapper = competitionMapper;
        this.userMapper = userMapper;
        this.sessionUtils = sessionUtils;
        this.competitionRegistrationService = competitionRegistrationService;
    }

    @Secured({"ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER", "ROLE_PARTICIPANT"})
    @GetMapping("/calendar/{year}/{weekNumber}")
    public Set<CalenderViewCompetitionDto> getCompetitionsForCalender(@PathVariable int year, @PathVariable int weekNumber) {
        logger.info("GET {}/calender/{}/{}", BASE_URI, year, weekNumber);

        return competitionMapper.competitionSetToCalenderViewCompetitionDtoSet(
            userService.getCompetitionsForCalendar(sessionUtils.getSessionUser(), year, weekNumber)
        );
    }

    @Secured({"ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    // @PermitAll
    @PostMapping("/import-team")
    public GeneralResponseDto importTeam(@RequestBody ClubManagerTeamImportDto clubManagerTeamImportDto) {
        logger.info("POST {}", BASE_URI + "/import-team");

        ClubManagerTeamImportResults addedParticipants = userService.importTeam(sessionUtils.getSessionUser(), clubManagerTeamImportDto);

        return new GeneralResponseDto(
            StatusText.OK,
            String.format(
                "Team %s received %d new participant (%d from the list were already present).",
                clubManagerTeamImportDto.teamName(),
                addedParticipants.newParticipantsCount(),
                addedParticipants.oldParticipantsCount())
        );
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_TOURNAMENT_MANAGER", "ROLE_CLUB_MANAGER"})
    @GetMapping
    public UserInfoDto getUser() {
        ApplicationUser user = sessionUtils.getSessionUser();

        return UserInfoDto.builder()
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getType())
            .build();
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_TOURNAMENT_MANAGER", "ROLE_CLUB_MANAGER"})
    @PostMapping(value = "/competitions/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registers the authenticated user to a competition", security = @SecurityRequirement(name = "apiKey"))
    public ResponseParticipantRegistrationDto registerToCompetition(
        @PathVariable Long id,
        @RequestBody(required = false) ParticipantSelfRegistrationDto groupPreference) {
        logger.info("POST {}/competitions/{}\n{}", BASE_PATH, id, groupPreference);
        if (groupPreference == null) {
            return competitionRegistrationService.selfRegisterParticipant(id, null);
        }
        return competitionRegistrationService.selfRegisterParticipant(id, groupPreference.getGroupPreference());
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_TOURNAMENT_MANAGER", "ROLE_CLUB_MANAGER"})
    @GetMapping(value = "/competitions/{id}")
    @Operation(summary = "Checks if the authenticated user is registered to the competition.", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Void> authenticatedUserIsRegisteredToCompetition(@PathVariable Long id) {
        logger.info("GET {}/competitions/{}", BASE_PATH, id);
        boolean isRegistered = competitionRegistrationService.isRegisteredTo(id);
        if (isRegistered) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER"})
    @GetMapping("search")
    public Set<UserDetailDto> getUserByName(UserSearchDto searchDto) {
        return userService.findByUserName(searchDto);
    }

}
