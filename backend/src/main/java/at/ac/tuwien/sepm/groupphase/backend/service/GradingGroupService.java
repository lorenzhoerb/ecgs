package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BasicRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailGradeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportGenerationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReportIsDownloadableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportGenerationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReportIsDownloadableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleGradingGroupDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

public interface GradingGroupService {

    /**
     * Get all grading groups of a competitions.
     *
     * @param competitionId competition id.
     * @return List of simple grading group.
     */
    List<SimpleGradingGroupDto> getAllByCompetition(Long competitionId);

    ReportIsDownloadableDto checkAllGradingGroupsHaveReports(Long competitionId);

    /**
     * Gets grading group details.
     *
     * @param groupId grading group id
     * @return grading group details
     */
    DetailedGradingGroupDto getOneById(Long groupId);

    /**
     * Sets register constraints to a grading group.
     *
     * @param groupId     group id
     * @param constraints constraints
     * @return created constraints with id
     */
    Set<DetailedRegisterConstraintDto> setConstraints(Long groupId, List<BasicRegisterConstraintDto> constraints);

    /**
     * Sets register constraints to a grading group.
     *
     * @param groupId     group id
     * @param filter      filter for users
     * @return created constraints with id
     */
    Page<UserDetailGradeDto> getParticipants(Long groupId, UserDetailFilterDto filter);
}
