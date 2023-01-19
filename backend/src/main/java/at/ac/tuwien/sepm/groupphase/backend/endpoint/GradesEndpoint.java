package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserPasswordResetRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.service.GradeService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.lang.invoke.MethodHandles;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/grades")
public class GradesEndpoint {

    static final String BASE_PATH = "/api/v1/grades";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final GradeService gradeService;



    @Autowired
    public GradesEndpoint(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @GetMapping("/{competitionId}/{gradingGroupId}/{stationId}")
    @Operation(summary = "Get all grades for a station in given GradingGroup", security = @SecurityRequirement(name = "apiKey"))
    public List<GradeResultDto> getAllGrades(@PathVariable Long competitionId, @PathVariable Long gradingGroupId, @PathVariable Long stationId) {
        LOGGER.info("GET {}/{}/{}/{}", BASE_PATH, competitionId, gradingGroupId, stationId);
        return this.gradeService.getAllGradesForStation(competitionId, gradingGroupId, stationId);
    }

}
