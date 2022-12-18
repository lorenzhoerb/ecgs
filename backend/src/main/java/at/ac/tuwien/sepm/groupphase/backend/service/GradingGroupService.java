package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleGradingGroupDto;

import java.util.List;

public interface GradingGroupService {

    /**
     * Get all grading groups of a competitions.
     *
     * @param competitionId competition id.
     * @return List of simple grading group.
     */
    List<SimpleGradingGroupDto> getAllByCompetition(Long competitionId);
}
