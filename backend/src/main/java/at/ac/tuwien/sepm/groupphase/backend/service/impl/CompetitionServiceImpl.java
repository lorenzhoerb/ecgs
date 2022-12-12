package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.CompetitionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompetitionServiceImpl implements CompetitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CompetitionRepository competitionRepository;
    private final CompetitionMapper competitionMapper;
    private final UserMapper userMapper;
    private final CompetitionValidator competitionValidator;
    private final SessionUtils sessionUtils;

    public CompetitionServiceImpl(CompetitionRepository competitionRepository, CompetitionMapper competitionMapper,
                                  UserMapper userMapper, CompetitionValidator competitionValidator,
                                  SessionUtils sessionUtils) {
        this.competitionRepository = competitionRepository;
        this.competitionMapper = competitionMapper;
        this.userMapper = userMapper;
        this.competitionValidator = competitionValidator;
        this.sessionUtils = sessionUtils;
    }

    @Override
    public CompetitionDetailDto create(CompetitionDetailDto competitionDetailDto) {
        LOGGER.debug("Create competition {}", competitionDetailDto);
        if (!sessionUtils.isCompetitionManager()) {
            throw new ForbiddenException("No Permission to create a competition");
        }
        competitionValidator.validate(competitionDetailDto);

        Competition competition = competitionMapper
            .competitionDetailDtoToCompetition(competitionDetailDto);

        ApplicationUser sessionUser = sessionUtils.getSessionUser();

        competition.setCreator(sessionUser);
        return competitionMapper
            .competitionToCompetitionDetailDto(competitionRepository.save(competition));
    }

    @Override
    public Set<UserDetailDto> getParticipants(Long id) {
        LOGGER.debug("List participants for competition {}", id);

        if (sessionUtils.getApplicationUserRole() == null) {
            throw new ForbiddenException("No Permission to get participants");
        }

        Optional<Competition> competitionOptional = competitionRepository.findById(id);

        if (competitionOptional.isEmpty()) {
            throw new NotFoundException("Didn't find competition with id " + id);
        }

        Competition competition = competitionOptional.get();

        if (competition.getDraft()) {
            // @Notice: competition shown as not found if in draft mode
            // as a competition in draft should not be exposed.
            throw new NotFoundException("Didn't find competition with id " + id);
        }

        Set<GradingGroup> gradingGroups = competition.getGradingGroups();
        List<Set<RegisterTo>> registerToSets =
            gradingGroups.stream().map(GradingGroup::getRegistrations).toList();
        List<RegisterTo> registerTos =
            registerToSets.stream().flatMap(Set::stream).filter(RegisterTo::getAccepted).toList();
        Set<ApplicationUser> participants =
            registerTos.stream().map(RegisterTo::getParticipant).collect(Collectors.toSet());

        return userMapper.applicationUserSetToUserDetailDtoSet(participants);
    }

    public Competition findOne(Long id) {
        LOGGER.debug("Find message with id {}", id);
        Optional<Competition> competition = competitionRepository.findById(id);

        if (competition.isPresent()) {
            return competition.get();
        }

        throw new NotFoundException(String.format("Could not find competition with id %s", id));
    }
}
