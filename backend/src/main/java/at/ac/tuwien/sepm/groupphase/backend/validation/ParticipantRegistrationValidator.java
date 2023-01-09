package at.ac.tuwien.sepm.groupphase.backend.validation;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import org.springframework.stereotype.Component;


@Component
public class ParticipantRegistrationValidator extends Validator<ParticipantRegistrationDto> {

    private final CompetitionRepository competitionRepository;
    private final GradingGroupRepository gradingGroupRepository;
    private final ApplicationUserRepository applicationUserRepository;

    protected ParticipantRegistrationValidator(javax.validation.Validator validator,
                                               CompetitionRepository competitionRepository,
                                               GradingGroupRepository gradingGroupRepository, ApplicationUserRepository applicationUserRepository) {
        super(validator);
        this.competitionRepository = competitionRepository;
        this.gradingGroupRepository = gradingGroupRepository;
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    protected void validateCustom(ParticipantRegistrationDto toValidate) {
    }

}
