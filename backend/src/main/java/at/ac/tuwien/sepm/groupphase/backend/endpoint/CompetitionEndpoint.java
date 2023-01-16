package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PageableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantManageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseMultiParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionRegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingGroupService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = CompetitionEndpoint.BASE_PATH)
public class CompetitionEndpoint {
    static final String BASE_PATH = "/api/v1/competitions";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CompetitionService competitionService;
    private final GradingGroupService gradingGroupService;
    private final CompetitionRegistrationService competitionRegistrationService;
    private final CompetitionMapper mapper;
    private final SessionUtils sessionUtils;

    @Autowired
    public CompetitionEndpoint(
        CompetitionService service,
        GradingGroupService gradingGroupService,
        CompetitionRegistrationService competitionRegistrationService,
        CompetitionMapper mapper,
        SessionUtils sessionUtils) {
        this.competitionService = service;
        this.gradingGroupService = gradingGroupService;
        this.competitionRegistrationService = competitionRegistrationService;
        this.mapper = mapper;
        this.sessionUtils = sessionUtils;
    }

    @PermitAll
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get information about a specific competition", security = @SecurityRequirement(name = "apiKey"))
    public CompetitionViewDto find(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/messages/{}", id);
        return competitionService.findOne(id);
    }

    @Secured("ROLE_TOURNAMENT_MANAGER")
    @GetMapping(value = "/{id}/detail")
    @Operation(summary = "Get detailed information about a specific competition", security = @SecurityRequirement(name = "apiKey"))
    public CompetitionDetailDto findDetail(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/messages/{}", id);
        return competitionService.findOneDetail(id);
    }

    @Secured("ROLE_TOURNAMENT_MANAGER")
    @PostMapping
    @Operation(summary = "Create a competition", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompetitionDetailDto create(@RequestBody CompetitionDetailDto competitionDetailDto) {
        LOGGER.info("POST {}", BASE_PATH);
        LOGGER.trace("POST {}, value: {}", BASE_PATH, competitionDetailDto);
        return competitionService.create(competitionDetailDto);
    }


    @PermitAll
    @GetMapping("/search")
    @Operation(summary = "Search a competition list", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(code = HttpStatus.OK)
    public List<CompetitionListDto> search(@RequestParam("name") String name,
                                           @RequestParam("begin") String begin, @RequestParam("end") String end,
                                           @RequestParam(value = "beginRegistration") String beginRegistration,
                                           @RequestParam(value = "endRegistration") String endRegistration) {
        LOGGER.info("GET /{}", BASE_PATH);
        CompetitionSearchDto competitionSearchDto = new CompetitionSearchDto(
            name,
            LocalDateTime.parse(begin),
            LocalDateTime.parse(end),
            LocalDateTime.parse(beginRegistration),
            LocalDateTime.parse(endRegistration));
        return competitionService.searchCompetitions(competitionSearchDto);
    }


    @Secured({"ROLE_PARTICIPANT", "ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @GetMapping(value = "/{id}/participants")
    @Operation(summary = "Get participants of competition", security = @SecurityRequirement(name = "apiKey"))
    public Set<UserDetailDto> getParticipants(@PathVariable Long id) {
        LOGGER.info("GET {}", BASE_PATH);
        return competitionService.getParticipants(id);
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER"})
    @GetMapping(value = "/{competitionId}/participants/registrations")
    @Operation(summary = "Get participants of competition with details about registration.", security = @SecurityRequirement(name = "apiKey"))
    public Page<ParticipantRegDetailDto> getParticipantsRegistrationDetails(
        @PathVariable Long competitionId,
        @RequestParam(required = false) Boolean accepted,
        @RequestParam(required = false) String firstName,
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) ApplicationUser.Gender gender,
        @RequestParam(required = false) Long gradingGroup,
        @RequestParam(required = false, defaultValue = "15") Integer pageSize,
        @RequestParam(required = false, defaultValue = "0") Integer page
    ) {
        LOGGER.info("GET {}/{}/participants/registrations", BASE_PATH);
        return competitionService
            .getParticipantsRegistrationDetails(new PageableDto<>(
                new ParticipantFilterDto(
                    competitionId,
                    accepted,
                    firstName,
                    lastName,
                    gender,
                    gradingGroup
                    ),
                page,
                pageSize
            ));
    }

    @Secured({"ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @PostMapping(value = "/{id}/participants")
    @Operation(summary = "Register participants to a competition", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseMultiParticipantRegistrationDto registerParticipants(
        @PathVariable Long id,
        @RequestBody List<ParticipantRegistrationDto> registrations) {
        LOGGER.info("POST {}/{}/participants", BASE_PATH, id);
        return competitionRegistrationService.registerParticipants(id, registrations);
    }

    @Secured({"ROLE_TOURNAMENT_MANAGER"})
    @PatchMapping(value = "/{id}/participants")
    @Operation(summary = "Updates participants", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(code = HttpStatus.OK)
    public List<ParticipantManageDto> updateRegisteredParticipants(
        @PathVariable Long id,
        @RequestBody List<ParticipantManageDto> participants
    ) {
        LOGGER.info("PATCH {}/{}/participants", BASE_PATH, id);
        return competitionRegistrationService.updateParticipants(id, participants);
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @GetMapping(value = "/{id}/groups")
    @Operation(summary = "Get groups of a competition", security = @SecurityRequirement(name = "apiKey"))
    public List<SimpleGradingGroupDto> getGroups(@PathVariable Long id) {
        LOGGER.info("GET {}/{}/groups", BASE_PATH, id);
        return gradingGroupService.getAllByCompetition(id);
    }
}
