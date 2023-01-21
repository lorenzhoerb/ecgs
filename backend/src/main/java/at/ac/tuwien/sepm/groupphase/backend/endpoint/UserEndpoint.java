package at.ac.tuwien.sepm.groupphase.backend.endpoint;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CalenderViewCompetitionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GeneralResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlagsResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantSelfRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlag;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CalenderViewCompetitionDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlag;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlagsResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantSelfRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailSetFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserInfoDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailSetFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleFlagDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionRegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.service.PictureService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import at.ac.tuwien.sepm.groupphase.backend.service.helprecords.ClubManagerTeamImportResults;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.PageAttributes;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.List;
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

    private final PictureService pictureService;


    private final SessionUtils sessionUtils;
    private final CompetitionRegistrationService competitionRegistrationService;

    @Autowired
    public UserEndpoint(
        UserService userService,
        UserMapper userMapper,
        SessionUtils sessionUtils,
        CompetitionRegistrationService competitionRegistrationService,
        CompetitionMapper competitionMapper, PictureService pictureService) {
        this.userService = userService;
        this.competitionMapper = competitionMapper;
        this.userMapper = userMapper;
        this.sessionUtils = sessionUtils;
        this.competitionRegistrationService = competitionRegistrationService;
        this.pictureService = pictureService;
    }

    @Secured({"ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER", "ROLE_PARTICIPANT"})
    @GetMapping("/calendar")
    public Set<CalenderViewCompetitionDto> getCompetitionsForCalender(@RequestParam int year, @RequestParam int weekNumber) {
        logger.info("GET {}/calender?year={}&month={}", BASE_URI, year, weekNumber);

        return competitionMapper.competitionSetToCalenderViewCompetitionDtoSet(userService.getCompetitionsForCalendar(year, weekNumber));
    }

    @Secured({"ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER", "ROLE_PARTICIPANT"})
    @PostMapping("/picture")
    @Operation(summary = "Changes the profile-picture of the authenticated user", security = @SecurityRequirement(name = "apiKey"))
    public String updateUserPicture(@RequestPart(name = "file") MultipartFile file) {
        logger.info("POST {}/user/picture/?multiPartFile={}", BASE_URI, file);
        pictureService.saveUserPicture(file);
        return "Picture successfully stored";
    }

    @Secured({"ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @PostMapping("/import-team")
    @ResponseStatus(code = HttpStatus.OK)
    public ClubManagerTeamImportResults importTeam(@RequestBody ClubManagerTeamImportDto clubManagerTeamImportDto) {
        logger.info("POST {}", BASE_URI + "/import-team");
        return userService.importTeam(clubManagerTeamImportDto);
    }

    @Secured({"ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @PostMapping("/flags")
    @ResponseStatus(HttpStatus.CREATED)
    public ImportFlagsResultDto importFlags(@RequestBody List<ImportFlag> flags) {
        logger.info("POST {}\n{}", BASE_URI + "/flags", flags);
        return userService.importFlags(flags);
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_TOURNAMENT_MANAGER", "ROLE_CLUB_MANAGER"})
    @GetMapping
    public UserInfoDto getUser() {
        logger.info("GET {}", BASE_PATH);
        return userService.getUser();
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_TOURNAMENT_MANAGER", "ROLE_CLUB_MANAGER"})
    @GetMapping("/detail")
    public UserDetailDto getUserDetail() {
        logger.info("GET {}/detail", BASE_PATH);
        return userService.getUser(sessionUtils.getSessionUser().getId());
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_TOURNAMENT_MANAGER", "ROLE_CLUB_MANAGER"})
    @GetMapping("/{id}")
    public UserDetailDto getUserDetail(@PathVariable Long id) {
        logger.info("GET {}/{}", BASE_PATH, id);
        return userService.getUser(id);
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_TOURNAMENT_MANAGER", "ROLE_CLUB_MANAGER"})
    @PostMapping(value = "/competitions/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registers the authenticated user to a competition", security = @SecurityRequirement(name = "apiKey"))
    public ResponseParticipantRegistrationDto registerToCompetition(@PathVariable Long id,
                                                                    @RequestBody(required = false) ParticipantSelfRegistrationDto groupPreference) {
        logger.info("POST {}/competitions/{}\n{}", BASE_PATH, id, groupPreference);
        return userService.registerToCompetition(id, groupPreference);
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_TOURNAMENT_MANAGER", "ROLE_CLUB_MANAGER"})
    @GetMapping(value = "/competitions/{id}")
    @Operation(summary = "Checks if the authenticated user is registered to the competition.", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Void> authenticatedUserIsRegisteredToCompetition(@PathVariable Long id) {
        logger.info("GET {}/competitions/{}", BASE_PATH, id);
        return competitionRegistrationService.isRegisteredTo(id) ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER"})
    @GetMapping("search")
    public Set<UserDetailDto> getUserByName(UserSearchDto searchDto) {
        logger.info("GET {}/search", BASE_PATH);
        return userService.findByUserName(searchDto);
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER", "ROLE_CLUB_MANAGER"})
    @GetMapping("my-flags")
    public List<SimpleFlagDto> getManagedFlags() {
        logger.info("GET {}/my-flags", BASE_PATH);
        return userService.getManagedFlags();
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER", "ROLE_CLUB_MANAGER"})
    @PostMapping("members/flags")
    public ResponseEntity<Void> addMemberFlags(@RequestBody UserDetailSetFlagDto members) {
        logger.info("GET {}/members/flags", BASE_PATH);
        userService.addFlagsForUsers(members);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER", "ROLE_CLUB_MANAGER"})
    @PatchMapping("members/flags")
    public ResponseEntity<Void> removeMemberFlags(@RequestBody UserDetailSetFlagDto members) {
        logger.info("GET {}/members/flags", BASE_PATH);
        userService.removeFlagsForUsers(members);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Secured({"ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @GetMapping(value = "/members")
    @Operation(summary = "Get members of club", security = @SecurityRequirement(name = "apiKey"))
    public List<UserDetailFlagDto> getMembers() {
        logger.info("GET {}/members", BASE_PATH);
        return userService.getMembers();
    }
}