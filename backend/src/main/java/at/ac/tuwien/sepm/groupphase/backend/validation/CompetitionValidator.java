package at.ac.tuwien.sepm.groupphase.backend.validation;


import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
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

    public void validateSelfRegisterToCompetition(Long competitionId, Long groupPreference) {
        if (competitionId == null) {
            throw new ValidationListException("Validation error when self register to competition", List.of("Competition id must be given"));
        }

        competitionRepository.findById(competitionId)
            .orElseThrow(() -> new NotFoundException("No competition with id " + competitionId));

        if (groupPreference != null && !isCompetitionGroup(competitionId, groupPreference)) {
            throw new ConflictException(
                "Conflict when registering to competition",
                List.of("Unknown group " + groupPreference + " for competition " + competitionId));
        }
    }

    public void validateParticipantRegistration(Long competitionId, ParticipantRegistrationDto registrationDto) {
        participantRegistrationValidator.validateAnnotations(registrationDto);
        List<String> conflictErrors = new ArrayList<>();
        Optional<Competition> competition = competitionRepository.findById(competitionId);
        Optional<ApplicationUser> applicationUser = applicationUserRepository.findById(registrationDto.getUserId());

        if (competition.isEmpty()) {
            throw new NotFoundException("Competition with id " + competitionId + " not found.");
        }

        if (applicationUser.isEmpty()) {
            throw new NotFoundException("Participant with id " + registrationDto.getUserId() + " not found.");
        }
        throw new ConflictException("Conflicts while register participant", conflictErrors);
    }

    private boolean isCompetitionGroup(Long competitionId, Long groupId) {
        return gradingGroupRepository.findByIdAndCompetitionId(groupId, competitionId).isPresent();
    }
}
