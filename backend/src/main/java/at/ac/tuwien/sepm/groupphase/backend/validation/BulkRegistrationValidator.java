package at.ac.tuwien.sepm.groupphase.backend.validation;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BulkErrorDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantManageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationBulkException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class BulkRegistrationValidator extends Validator<List<ParticipantManageDto>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ParticipantRegistrationValidator participantRegistrationValidator;
    private final GradingGroupRepository gradingGroupRepository;

    public BulkRegistrationValidator(
        javax.validation.Validator validator,
        ParticipantRegistrationValidator participantRegistrationValidator,
        GradingGroupRepository gradingGroupRepository) {
        super(validator);
        this.participantRegistrationValidator = participantRegistrationValidator;
        this.gradingGroupRepository = gradingGroupRepository;
    }

    public void validate(Competition competition, List<ParticipantRegistrationDto> registrationDtos) {
        List<BulkErrorDto> bulkValidationErrors = new ArrayList<>();
        for (ParticipantRegistrationDto prd : registrationDtos) {
            try {
                participantRegistrationValidator.validate(competition, prd);
            } catch (ValidationListException e) {
                bulkValidationErrors.add(
                    new BulkErrorDto(prd.getUserId(), e.errors())
                );
            }
        }
        if (!bulkValidationErrors.isEmpty()) {
            throw new ValidationBulkException("Validation for bulk registration failed", bulkValidationErrors);
        }
    }

    public void validateUpdate(Competition competition, List<ParticipantManageDto> participantManageDtos) {
        LOGGER.debug("validateUpdate({},{})", competition, participantManageDtos);
        List<BulkErrorDto> bulkValidationError = new ArrayList<>();
        validateAnnotations(participantManageDtos);
        for (ParticipantManageDto p : participantManageDtos) {
            if (p.getGroupId() != null) {
                if (!isGroupOfCompetition(competition.getId(), p.getGroupId())) {
                    bulkValidationError.add(new BulkErrorDto(p.getUserId(), List.of("Invalid group id.")));
                }
            }
        }
        if (!bulkValidationError.isEmpty()) {
            throw new ValidationBulkException("Error validating update", bulkValidationError);
        }
    }

    public boolean isGroupOfCompetition(Long competitionId, Long groupId) {
        LOGGER.debug("isGroupOfCompetition({},{})", competitionId, groupId);
        return gradingGroupRepository.findByIdAndCompetitionId(groupId, competitionId).isPresent();
    }

    @Override
    protected void validateCustom(List<ParticipantManageDto> toValidate) {

    }
}
