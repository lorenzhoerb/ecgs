package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ViewEditGradingSystemDto;
import at.ac.tuwien.sepm.groupphase.backend.repository.projections.GradingSystemProjectIdAndNameAndIsPublicAndEditable;

import java.util.List;

public interface GradingSystemService {
    /**
     * Creates a gradingSystem for a given gradingGroup competition.
     *
     * @param gradingSystemDetailDto gradingSystem to create
     * @return created gradingSystem
     */
    GradingSystemDetailDto createGradingSystem(GradingSystemDetailDto gradingSystemDetailDto);

    /**
     * Gets a grading system in draft mode by its id.
     *
     * @param id the id of the grading system
     * @return the resulting grading system dto
     */
    ViewEditGradingSystemDto getDraftGradingSystemById(Long id);

    /**
     * Fetches new grading system without defined formula for creating purposes.
     *
     * @return the new grading system
     */
    List<GradingSystemProjectIdAndNameAndIsPublicAndEditable> getSimpleDraftGradingSystem();

    /**
     * Update a grading system in draft mode to the one given.
     *
     * @param gradingSystemDto the updated grading system
     * @return the updated grading system as now in the database
     */
    ViewEditGradingSystemDto updateDraftGradingSystem(ViewEditGradingSystemDto gradingSystemDto);

    /**
     * Removes a grading system in draft mode from the database.
     *
     * @param gradingSystemId the id of the grading system to remove
     */
    void deleteDraftGradingSystem(Long gradingSystemId);
}
