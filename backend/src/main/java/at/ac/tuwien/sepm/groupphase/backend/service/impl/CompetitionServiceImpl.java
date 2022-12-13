package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingGroupMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.CompetitionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompetitionServiceImpl implements CompetitionService {


    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CompetitionRepository competitionRepository;
    private final GradingGroupRepository gradingGroupRepository;
    private final CompetitionMapper competitionMapper;
    private final GradingGroupMapper gradingGroupMapper;
    private final UserMapper userMapper;
    private final CompetitionValidator competitionValidator;
    private final SessionUtils sessionUtils;

    public CompetitionServiceImpl(CompetitionRepository competitionRepository, CompetitionMapper competitionMapper,
                                  CompetitionValidator competitionValidator, UserMapper userMapper,
                                  SessionUtils sessionUtils, GradingGroupRepository gradingGroupRepository, 
                                  GradingGroupMapper gradingGroupMapper) {
        this.competitionRepository = competitionRepository;
        this.competitionMapper = competitionMapper;
        this.userMapper = userMapper;
        this.competitionValidator = competitionValidator;
        this.sessionUtils = sessionUtils;
        this.gradingGroupMapper = gradingGroupMapper;
        this.gradingGroupRepository = gradingGroupRepository;
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

        if (competitionDetailDto.getGradingGroups() != null) {
            Set<GradingGroup> gradingGroups = Arrays.stream(competitionDetailDto.getGradingGroups())
                .map(g -> gradingGroupMapper.gradingGroupDetailDtoToGradingGroup(g))
                .collect(Collectors.toSet());

            Competition finalCompetition = competition;
            gradingGroups.forEach(g -> g.setCompetitions(finalCompetition));

            competition.setGradingGroups(gradingGroups);

            gradingGroupRepository.saveAll(gradingGroups);
        }
        competition = competitionRepository.save(competition);

        GradingGroupDto[] gradingGroupDtos = new GradingGroupDto[] {};

        if (competition.getGradingGroups() != null) {
            gradingGroupDtos = competition.getGradingGroups().stream()
                .map(g -> gradingGroupMapper.gradingGroupToGradingGroupDetailDto(g))
                .collect(Collectors.toList())
                .toArray(gradingGroupDtos);
        }

        return competitionMapper
            .competitionToCompetitionDetailDto(competition)
            .setGradingGroups(gradingGroupDtos);
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


    /*
    //TO BE FIXXED , not RELEVANT FOR FEATURE 32
    @Override
    public List<CompetitionListDto> searchCompetitions(CompetitionSearchDto compoCompetitionSearchDto) {

        List<Competition> allByBeginOfCompetitionAfterAndNameStartingWithAndDescriptionContainingIgnoreCase =
            competitionRepository.findAllByBeginOfCompetitionAfterAndNameStartingWithAndDescriptionContainingIgnoreCase(compoCompetitionSearchDto.getName(),
                compoCompetitionSearchDto.getDescription(),
                compoCompetitionSearchDto.getBeginDate());
        return competitionMapper.competitionsToCompetitionListDto(allByBeginOfCompetitionAfterAndNameStartingWithAndDescriptionContainingIgnoreCase);
    }
    */
}
