package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingSystemMapper;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingSystemService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = GradingGroupEndpoint.BASE_PATH)
public class GradingGroupEndpoint {
    static final String BASE_PATH = "/api/v1/grading-systems";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GradingSystemService gradingSystemService;
    private final GradingSystemMapper mapper;

    @Autowired
    public GradingGroupEndpoint(GradingSystemService service, GradingSystemMapper mapper) {
        this.gradingSystemService = service;
        this.mapper = mapper;
    }

    @Secured("ROLE_TOURNAMENT_MANAGER")
    @PostMapping()
    @Operation(summary = "Create grading system for a competition", security = @SecurityRequirement(name = "apiKey"))
    @ResponseStatus(code = HttpStatus.CREATED)
    public GradingSystemDetailDto createGradingSystem(
        @RequestBody GradingSystemDetailDto gradingSystemDetailDto
    ) {
        LOGGER.info("POST {}", BASE_PATH);
        return gradingSystemService.createGradingSystem(gradingSystemDetailDto);
    }
}
