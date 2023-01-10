package at.ac.tuwien.sepm.groupphase.backend.validation;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class CompetitionValidator extends Validator<CompetitionDetailDto> {

    private final CompetitionRepository competitionRepository;
    private final GradingGroupRepository gradingGroupRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final ParticipantRegistrationValidator participantRegistrationValidator;

    protected CompetitionValidator(javax.validation.Validator validator,
                                   CompetitionRepository competitionRepository,
                                   GradingGroupRepository gradingGroupRepository, ApplicationUserRepository applicationUserRepository,
                                   ParticipantRegistrationValidator participantRegistrationValidator) {
        super(validator);
        this.competitionRepository = competitionRepository;
        this.gradingGroupRepository = gradingGroupRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.participantRegistrationValidator = participantRegistrationValidator;
    }

    @Override
    protected void validateCustom(CompetitionDetailDto toValidate) {

    }
}
