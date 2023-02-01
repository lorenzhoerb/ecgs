package at.ac.tuwien.sepm.groupphase.backend.validation;


import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.ConstraintOperator;
import at.ac.tuwien.sepm.groupphase.backend.report.ranking.parser.RegisterConstraintParser;
import at.ac.tuwien.sepm.groupphase.backend.constraint.validator.RegisterConstraintValidator;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConstraintParserException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterConstraintRepository;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ParticipantRegistrationValidator extends Validator<ParticipantRegistrationDto> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final SessionUtils sessionUtils;
    private final ManagedByRepository managedByRepository;
    private final GradingGroupRepository gradingGroupRepository;
    private final RegisterConstraintRepository registerConstraintRepository;
    private final RegisterConstraintParser registerConstraintParser;
    private final RegisterConstraintValidator registerConstraintValidator;
    private final ApplicationUserRepository applicationUserRepository;

    protected ParticipantRegistrationValidator(
        javax.validation.Validator validator,
        SessionUtils sessionUtils,
        ManagedByRepository managedByRepository,
        GradingGroupRepository gradingGroupRepository,
        RegisterConstraintRepository registerConstraintRepository,
        RegisterConstraintParser registerConstraintParser,
        RegisterConstraintValidator registerConstraintValidator, RegisterConstraintValidator registerConstraintValidator1, ApplicationUserRepository applicationUserRepository) {
        super(validator);
        this.sessionUtils = sessionUtils;
        this.managedByRepository = managedByRepository;
        this.gradingGroupRepository = gradingGroupRepository;
        this.registerConstraintRepository = registerConstraintRepository;
        this.registerConstraintParser = registerConstraintParser;
        this.registerConstraintValidator = registerConstraintValidator1;
        this.applicationUserRepository = applicationUserRepository;
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

        //is grading group of competition
        Optional<GradingGroup> gradingGroupOptional = gradingGroupRepository
            .findByIdAndCompetitionId(registrationDto.getGroupPreference(), competition.getId());

        if (gradingGroupOptional.isEmpty()) {
            errors.add("Group preference is invalid");
        } else {
            GradingGroup gradingGroup = gradingGroupOptional.get();
            Optional<ApplicationUser> user = applicationUserRepository.findById(registrationDto.getUserId());
            user.ifPresent(applicationUser -> {
                try {
                    canRegisterToGroupWithConstraints(gradingGroup, applicationUser);
                } catch (ValidationListException e) {
                    errors.addAll(e.errors());
                }
            });
        }

        if (!errors.isEmpty()) {
            throw new ValidationListException("Error validating registration dto", errors);
        }
    }

    private void canRegisterToGroupWithConstraints(GradingGroup gradingGroup, ApplicationUser user) {
        if (!gradingGroup.getRegisterConstraints().isEmpty()) {
            try {
                List<ConstraintOperator<?>> constraints = registerConstraintParser
                    .parse(gradingGroup.getRegisterConstraints());
                registerConstraintValidator.validate(constraints, user);
            } catch (ConstraintParserException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean manages(Long managerId, Long memberId) {
        LOGGER.debug("manages({},{})", managerId, memberId);
        return managedByRepository.findByManagerIdAndMemberId(managerId, memberId).isPresent();
    }
}
