package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ViewEditGradingSystemDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingSystemMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.projections.GradingSystemProjectIdAndNameAndIsPublicAndEditable;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingSystemService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.GradingSystemValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.ViewEditGradingSystemUpdateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class GradingSystemServiceImpl implements GradingSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final GradingSystemRepository gradingSystemRepository;
    private final GradingSystemMapper gradingSystemMapper;
    private final GradingSystemValidator gradingSystemValidator;
    private final SessionUtils sessionUtils;
    private final ViewEditGradingSystemUpdateValidator viewEditGradingSystemValidator;

    public GradingSystemServiceImpl(
        GradingSystemRepository gradingSystemRepository,
        GradingSystemMapper gradingSystemMapper,
        GradingSystemValidator gradingSystemValidator,
        SessionUtils sessionUtils,
        ViewEditGradingSystemUpdateValidator viewEditGradingSystemValidator) {
        this.gradingSystemRepository = gradingSystemRepository;
        this.gradingSystemMapper = gradingSystemMapper;
        this.gradingSystemValidator = gradingSystemValidator;
        this.sessionUtils = sessionUtils;
        this.viewEditGradingSystemValidator = viewEditGradingSystemValidator;
    }

    @Override
    public GradingSystemDetailDto createGradingSystem(
        GradingSystemDetailDto gradingSystemDetailDto
    ) {
        LOGGER.debug("Create grading system {}", gradingSystemDetailDto);
        if (!sessionUtils.isCompetitionManager()) {
            throw new ForbiddenException("No Permission to create a grading system");
        }
        gradingSystemValidator.validate(gradingSystemDetailDto);

        ApplicationUser creator = sessionUtils.getSessionUser();

        GradingSystem given =
            gradingSystemMapper.gradingSystemDetailDtoToGradingSystem(gradingSystemDetailDto);

        if (given.getTemplate()) {
            if (gradingSystemRepository
                .findFirstByNameAndCreatorAndIsTemplateIsTrue(given.getName(), creator)
                .isPresent()) {
                throw new ConflictException("Grading System Template Name already in use", new ArrayList<>());
            }

            given.setCreator(creator);
        }

        GradingSystem persisted = gradingSystemRepository.save(given);

        return gradingSystemMapper.gradingSystemToGradingSystemDetailDto(persisted);
    }

    @Override
    public ViewEditGradingSystemDto getDraftGradingSystemById(Long id) {
        LOGGER.debug("Get draft grading systems by id");
        if (!sessionUtils.isCompetitionManager()) {
            throw new ForbiddenException("No Permission to see drafts");
        }

        var foundGradingSystemOpt = gradingSystemRepository.findTemplateByIdIfNotOthersPrivate(sessionUtils.getSessionUser().getId(), id);
        if (foundGradingSystemOpt.isEmpty()) {
            throw new NotFoundException("No such grading system found");
        }

        return gradingSystemMapper.gradingSystemToViewEditGradingSystemDto(foundGradingSystemOpt.get());
    }

    @Override
    public List<GradingSystemProjectIdAndNameAndIsPublicAndEditable> getSimpleDraftGradingSystem() {
        LOGGER.debug("Get simple draft grading systems");
        if (!sessionUtils.isCompetitionManager()) {
            throw new ForbiddenException("No Permission to get drafts");
        }

        return gradingSystemRepository.findIdsAndNamesAndIsPublicAndEditableOfAllEligibleToViewOrEditDrafts(
            sessionUtils.getSessionUser().getId()
        );
    }

    @Override
    public ViewEditGradingSystemDto updateDraftGradingSystem(ViewEditGradingSystemDto gradingSystemDto) {
        LOGGER.debug("Update draft grading system");
        if (!sessionUtils.isCompetitionManager()) {
            throw new ForbiddenException("No Permission to update a competition");
        }

        viewEditGradingSystemValidator.validate(gradingSystemDto);

        var foundGradingSystemOpt = gradingSystemRepository.findTemplateByIdIfNotOthersPrivate(
            sessionUtils.getSessionUser().getId(), gradingSystemDto.id()
        );
        if (foundGradingSystemOpt.isEmpty()) {
            throw new NotFoundException("No such grading system found");
        }

        var foundGradingSystem = foundGradingSystemOpt.get();
        foundGradingSystem.setName(gradingSystemDto.name());
        foundGradingSystem.setDescription(gradingSystemDto.description());
        foundGradingSystem.setFormula(gradingSystemDto.formula());
        foundGradingSystem.setPublic(gradingSystemDto.isPublic());

        return gradingSystemMapper.gradingSystemToViewEditGradingSystemDto(foundGradingSystem);
    }

    @Override
    public void deleteDraftGradingSystem(Long gradingSystemId) {
        LOGGER.debug("Delete draft grading system by id");
        if (!sessionUtils.isCompetitionManager()) {
            throw new ForbiddenException("No Permission to delete a competition");
        }
        var foundGradingSystemOpt = gradingSystemRepository.findTemplateByIdIfBelongsTo(
            sessionUtils.getSessionUser().getId(), gradingSystemId
        );
        if (foundGradingSystemOpt.isEmpty()) {
            throw new NotFoundException("No such grading system found");
        }

        gradingSystemRepository.deleteById(gradingSystemId);
    }
}
