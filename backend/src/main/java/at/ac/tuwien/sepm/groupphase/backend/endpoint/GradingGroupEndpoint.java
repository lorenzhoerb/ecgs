package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BasicRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailGradeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/v1/groups")
public class GradingGroupEndpoint {

    private final GradingGroupService gradingGroupService;

    public GradingGroupEndpoint(GradingGroupService gradingGroupService) {
        this.gradingGroupService = gradingGroupService;
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_TOURNAMENT_MANAGER"})
    @Operation(summary = "Get details of a grading group", security = @SecurityRequirement(name = "apiKey"))
    public DetailedGradingGroupDto getOneById(@PathVariable Long id) {
        return gradingGroupService.getOneById(id);
    }

    @PostMapping("/{id}/constraints")
    @ResponseStatus(HttpStatus.CREATED)
    @Secured({"ROLE_TOURNAMENT_MANAGER"})
    @Operation(summary = "Set register constraints to a group.", security = @SecurityRequirement(name = "apiKey"))
    public Set<DetailedRegisterConstraintDto> setRegisterConstraint(@PathVariable Long id, @RequestBody List<BasicRegisterConstraintDto> constraints) {
        return gradingGroupService.setConstraints(id, constraints);
    }

    @Secured({"ROLE_PARTICIPANT", "ROLE_CLUB_MANAGER", "ROLE_TOURNAMENT_MANAGER"})
    @GetMapping(value = "/{id}/participants")
    @Operation(summary = "Get participants of competition", security = @SecurityRequirement(name = "apiKey"))
    public Page<UserDetailGradeDto> getParticipants(@PathVariable Long id, UserDetailFilterDto filter) {
        return gradingGroupService.getParticipants(id, filter);
    }
}
