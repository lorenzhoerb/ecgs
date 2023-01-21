package at.ac.tuwien.sepm.groupphase.backend.validation;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class ParticipantRegistrationValidator extends Validator<ParticipantRegistrationDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SessionUtils sessionUtils;
    private final ManagedByRepository managedByRepository;
    private final GradingGroupRepository gradingGroupRepository;

    protected ParticipantRegistrationValidator(
        javax.validation.Validator validator,
        SessionUtils sessionUtils,
        ManagedByRepository managedByRepository,
        GradingGroupRepository gradingGroupRepository) {
        super(validator);
        this.sessionUtils = sessionUtils;
        this.managedByRepository = managedByRepository;
        this.gradingGroupRepository = gradingGroupRepository;
    }

    @Override
    protected void validateCustom(ParticipantRegistrationDto toValidate) {
    }

    public void validate(Competition competition, ParticipantRegistrationDto registrationDto) {
        LOGGER.debug("validate({},{})", competition, registrationDto);
        validateAnnotations(registrationDto);
        List<String> errors = new ArrayList<>();
        ApplicationUser sessionUser = sessionUtils.getSessionUser();

        if (!manages(sessionUser.getId(), registrationDto.getUserId())) {
            errors.add("User is not managed by you");
        }

        if (registrationDto.getGroupPreference() != null) {
            if (!isGroupOfCompetition(competition.getId(), registrationDto.getGroupPreference())) {
                errors.add("Group preference is invalid");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationListException("Error validating registration dto", errors);
        }
    }

    public boolean manages(Long managerId, Long memberId) {
        LOGGER.debug("manages({},{})", managerId, memberId);
        return managedByRepository.findByManagerIdAndMemberId(managerId, memberId).isPresent();
    }

    public boolean isGroupOfCompetition(Long competitionId, Long groupId) {
        LOGGER.debug("isGroupOfCompetition({},{})", competitionId, groupId);
        return gradingGroupRepository.findByIdAndCompetitionId(groupId, competitionId).isPresent();
    }
}
