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

    ViewEditGradingSystemDto getDraftGradingSystemById(Long id);

    List<GradingSystemProjectIdAndNameAndIsPublicAndEditable> getSimpleDraftGradingSystem();

    ViewEditGradingSystemDto updateDraftGradingSystem(ViewEditGradingSystemDto gradingSystemDto);

    void deleteDraftGradingSystem(Long gradingSystemId);
}
