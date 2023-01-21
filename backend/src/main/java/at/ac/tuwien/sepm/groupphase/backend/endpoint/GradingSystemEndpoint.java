package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ViewEditGradingSystemDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingSystemMapper;
import at.ac.tuwien.sepm.groupphase.backend.repository.projections.GradingSystemProjectIdAndNameAndIsPublicAndEditable;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingSystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.List;


@RestController
@RequestMapping(value = GradingSystemEndpoint.BASE_PATH)
public class GradingSystemEndpoint {
    static final String BASE_PATH = "/api/v1/grading-systems";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final GradingSystemService gradingSystemService;
    private final GradingSystemMapper mapper;

    @Autowired
    public GradingSystemEndpoint(GradingSystemService service, GradingSystemMapper mapper) {
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

    @Secured("ROLE_TOURNAMENT_MANAGER")
    @GetMapping("/drafts/{id}")
    @Operation(summary = "Get grading system in draft by id", security = @SecurityRequirement(name = "apiKey"))
    public ViewEditGradingSystemDto getDraftGradingSystemById(
        @PathVariable Long id
    ) {
        LOGGER.info("GET {}/drafts/{}", BASE_PATH, id);
        return gradingSystemService.getDraftGradingSystemById(id);
    }

    @Secured("ROLE_TOURNAMENT_MANAGER")
    @GetMapping("/drafts/simple")
    @Operation(summary = "Get simple grading systems in draft (ids and names)", security = @SecurityRequirement(name = "apiKey"))
    public List<GradingSystemProjectIdAndNameAndIsPublicAndEditable> getSimpleDraftGradingSystems() {
        LOGGER.info("GET {}/drafts/simple", BASE_PATH);
        return gradingSystemService.getSimpleDraftGradingSystem();
    }

    @PutMapping("/drafts")
    @Secured("ROLE_TOURNAMENT_MANAGER")
    @Operation(summary = "Update grading systems in draft", security = @SecurityRequirement(name = "apiKey"))
    public ViewEditGradingSystemDto updateDraftGradingSystem(
        @RequestBody ViewEditGradingSystemDto viewEditGradingSystemDto
    ) {
        LOGGER.info("PUT {}/drafts\nData:{}", BASE_PATH, viewEditGradingSystemDto);
        return gradingSystemService.updateDraftGradingSystem(viewEditGradingSystemDto);
    }

    @DeleteMapping("/drafts/{id}")
    @Secured("ROLE_TOURNAMENT_MANAGER")
    @Operation(summary = "Delete grading systems in draft", security = @SecurityRequirement(name = "apiKey"))
    public ResponseEntity<Void> deleteDraftGradingSystem(
        @PathVariable Long id
    ) {
        LOGGER.info("DELETE {}/drafts/{}", BASE_PATH, id);
        gradingSystemService.deleteDraftGradingSystem(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
