package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CalenderViewCompetitionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GeneralResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.helptypes.StatusText;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.helprecords.ClubManagerTeamImportResults;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.ClubManagerTeamImportDtoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/user")
public class UserEndpoint {
    public static final String BASE_URI = "/api/v1/user";
    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final UserService userService;
    private final CompetitionMapper competitionMapper;
    private final SessionUtils sessionUtils;
    private final UserMapper userMapper;

    public UserEndpoint(SessionUtils sessionUtils, UserService userService,
                        CompetitionMapper competitionMapper, UserMapper userMapper) {
        this.sessionUtils = sessionUtils;
        this.userService = userService;
        this.competitionMapper = competitionMapper;
        this.userMapper = userMapper;
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

}
