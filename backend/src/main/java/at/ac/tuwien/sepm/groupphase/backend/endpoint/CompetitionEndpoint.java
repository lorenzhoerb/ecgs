package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = CompetitionEndpoint.BASE_PATH)
public class CompetitionEndpoint {
    static final String BASE_PATH = "/api/v1/competitions";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CompetitionService competitionService;
    private final CompetitionMapper mapper;

    @Autowired
    public CompetitionEndpoint(CompetitionService service, CompetitionMapper mapper) {
        this.competitionService = service;
        this.mapper = mapper;
    }

    @PermitAll
    @GetMapping(value = "/{id}")
    @Operation(summary = "Get detailed information about a specific competition", security = @SecurityRequirement(name = "apiKey"))
    public CompetitionViewDto find(@PathVariable Long id) {
        LOGGER.info("GET /api/v1/messages/{}", id);

        CompetitionViewDto result =
            mapper.competitionToCompetitionViewDto(competitionService.findOne(id));

        if (!result.draft()) {
            return result;
        } else {
            throw new NotFoundException("competition not public or in draft!");
        }
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
}
