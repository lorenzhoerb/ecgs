package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionRegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

@Service
public class CompetitionRegistrationServiceImpl implements CompetitionRegistrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CompetitionRepository competitionRepository;
    private final SessionUtils sessionUtils;
    private final RegisterToRepository registerToRepository;
    private final GradingGroupRepository gradingGroupRepository;

    public CompetitionRegistrationServiceImpl(
        CompetitionRepository competitionRepository,
        SessionUtils sessionUtils,
        RegisterToRepository registerToRepository,
        GradingGroupRepository gradingGroupRepository) {
        this.competitionRepository = competitionRepository;
        this.sessionUtils = sessionUtils;
        this.registerToRepository = registerToRepository;
        this.gradingGroupRepository = gradingGroupRepository;
    }

    @Override
    public ResponseParticipantRegistrationDto selfRegisterParticipant(Long competitionId, Long groupPreference) {
        LOGGER.debug("selfRegisterParticipant({}, {})", competitionId, groupPreference);
        if (!sessionUtils.isAuthenticated()) {
            throw new ForbiddenException("No permissions to register to competitions. Authentication required.");
        }

        if (competitionId == null) {
            throw new ValidationListException("Validation errors while register to competition", "Competition id must be given");
        }

        Competition competition = competitionRepository.findById(competitionId)
            .orElseThrow(() -> new NotFoundException("Unknown competition " + competitionId));

        checkCompetitionRegistrationAccess(competition);
        ApplicationUser sessionUser = sessionUtils.getSessionUser();

        GradingGroup gradingGroup = getPreferenceOrDefaultGradingGroup(competitionId, groupPreference);
        if (gradingGroup == null) {
            throw new NotFoundException("Registration failed. Could not assign to grading group.");
        }

        RegisterTo registerTo = registerToOrElseFetch(competition, sessionUser, gradingGroup);

        return new ResponseParticipantRegistrationDto(
            competitionId,
            sessionUser.getId(),
            registerTo.getGradingGroup().getId()
        );
    }

    @Override
    public boolean isRegisteredTo(Long competitionId) {
        LOGGER.debug("isRegisteredTo({})", competitionId);
        if (!sessionUtils.isAuthenticated()) {
            throw new ForbiddenException("Requires authentication");
        }

        if (competitionId == null) {
            throw new ValidationListException("Validation error while checking registration status.", "competition must not be null");
        }

        return registerToRepository
            .findByGradingGroupCompetitionIdAndParticipantId(competitionId, sessionUtils.getSessionUser().getId())
            .isPresent();
    }

    /**
     * Checks if the user is already registered.
     *
     * @return If already registered the registered RegisterTo will be returned else the new registered entity
     */
    private RegisterTo registerToOrElseFetch(Competition competition, ApplicationUser user, GradingGroup group) {
        LOGGER.debug("registerToOrElseFetch({}, {}, {})", competition, user, group);
        return registerToRepository
            .findByGradingGroupCompetitionIdAndParticipantId(competition.getId(), user.getId())
            .orElseGet(() -> registerToRepository.save(new RegisterTo(user, group, false)));
    }

    private GradingGroup getPreferenceOrDefaultGradingGroup(Long competitionId, Long groupPreference) {
        LOGGER.debug("getPreferenceOrDefaultGradingGroup({}, {})", competitionId, groupPreference);
        GradingGroup gradingGroup;
        if (groupPreference == null) {
            gradingGroup = gradingGroupRepository
                .findFirstByCompetitionIdOrderByIdAsc(competitionId)
                .orElseGet(() -> null);
        } else {
            gradingGroup = gradingGroupRepository
                .findByIdAndCompetitionId(groupPreference, competitionId).orElseGet(() -> null);
        }
        return gradingGroup;
    }

    private void checkCompetitionRegistrationAccess(Competition competition) {
        LOGGER.debug("checkCompetitionRegistrationAccess({})", competition);
        if (!sessionUtils.isAuthenticated()) {
            throw new ForbiddenException("No permissions to register to competitions. Authentication required.");
        }

        if (competition.getDraft() || !competition.getPublic()) {
            throw new ForbiddenException("Registration forbidden.");
        }

        if (!isRegistrationOpen(competition)) {
            throw new ForbiddenException("Registration for competition " + competition.getId() + " is not closed.");
        }
    }

    private boolean isRegistrationOpen(Competition competition) {
        LOGGER.debug("isRegistrationOpen({})", competition);
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(competition.getBeginOfRegistration())
            && now.isBefore(competition.getEndOfRegistration());
    }
}
