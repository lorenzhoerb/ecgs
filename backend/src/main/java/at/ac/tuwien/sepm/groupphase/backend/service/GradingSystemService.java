package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;

public interface GradingSystemService {
    /**
     * Creates a gradingSystem for a given gradingGroup competition.
     *
     * @param gradingSystemDetailDto gradingSystem to create
     * @return created gradingSystem
     */
    GradingSystemDetailDto createGradingSystem(GradingSystemDetailDto gradingSystemDetailDto);
}
