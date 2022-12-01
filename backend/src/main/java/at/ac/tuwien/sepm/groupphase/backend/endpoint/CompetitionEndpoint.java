package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = CompetitionEndpoint.BASE_PATH)
public class CompetitionEndpoint {
    static final String BASE_PATH = "/api/v1/competitions";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CompetitionService competitionService;

    public CompetitionEndpoint(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    //    @Secured("ROLE_COMP_MANAGER") //ToDo: change to role
    @PermitAll
    @PostMapping
    @Operation(summary = "Create a competition")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompetitionDetailDto create(@RequestBody CompetitionDetailDto competitionDetailDto) {
        LOGGER.info("POST {}", BASE_PATH);
        return competitionService.create(competitionDetailDto);
    }
}
