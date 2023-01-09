package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingGroupMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingSystemMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingSystemService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.CompetitionValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.GradingSystemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
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
    private final GradingSystemRepository gradingSystemRepository;
    private final GradingSystemMapper gradingSystemMapper;
    private final GradingSystemValidator gradingSystemValidator;
    private final UserMapper userMapper;
    private final CompetitionValidator competitionValidator;
    private final GradingSystemService gradingSystemService;
    private final SessionUtils sessionUtils;

    public CompetitionServiceImpl(CompetitionRepository competitionRepository, CompetitionMapper competitionMapper,
                                  CompetitionValidator competitionValidator, UserMapper userMapper,
                                  SessionUtils sessionUtils, GradingGroupRepository gradingGroupRepository,
                                  GradingSystemService gradingSystemService,
                                  GradingSystemRepository gradingSystemRepository,
                                  GradingSystemMapper gradingSystemMapper,
                                  GradingSystemValidator gradingSystemValidator,
                                  GradingGroupMapper gradingGroupMapper) {
        this.competitionRepository = competitionRepository;
        this.competitionMapper = competitionMapper;
        this.userMapper = userMapper;
        this.competitionValidator = competitionValidator;
        this.sessionUtils = sessionUtils;
        this.gradingGroupMapper = gradingGroupMapper;
        this.gradingGroupRepository = gradingGroupRepository;
        this.gradingSystemService = gradingSystemService;
        this.gradingSystemRepository = gradingSystemRepository;
        this.gradingSystemMapper     = gradingSystemMapper;
        this.gradingSystemValidator = gradingSystemValidator;
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

            Set<GradingGroup> gradingGroups = new HashSet<>();
            Set<GradingSystem> gradingSystems = new HashSet<>();

            for (GradingGroupDto groupDto : competitionDetailDto.getGradingGroups()) {
                GradingGroup group = gradingGroupMapper.gradingGroupDetailDtoToGradingGroup(groupDto);

                gradingSystemValidator.validate(groupDto.getGradingSystemDto());
                GradingSystem gradingSystem = gradingSystemMapper.gradingSystemDetailDtoToGradingSystem(groupDto.getGradingSystemDto());
                gradingSystem.setGradingGroup(Set.of(group));
                group.setGradingSystem(gradingSystem);
                gradingSystems.add(gradingSystem);

                group.setCompetitions(competition);

                gradingGroups.add(group);
            }

            competition.setGradingGroups(gradingGroups);

            gradingGroupRepository.saveAll(gradingGroups);
            gradingSystemRepository.saveAll(gradingSystems);
        }

        if (competitionDetailDto.getJudges() != null) {
            Set<ApplicationUser> judges =
                userMapper.userDetailDtoSetToApplicationUserSet(
                    Set.of(competitionDetailDto.getJudges())
                );

            competition.setJudges(judges);
        }

        competition = competitionRepository.save(competition);

        GradingGroupDto[] gradingGroupDtos = new GradingGroupDto[] {};

        if (competition.getGradingGroups() != null) {
            gradingGroupDtos = competition.getGradingGroups().stream()
                .map(group -> {
                    GradingGroupDto dto =  gradingGroupMapper.gradingGroupToGradingGroupDetailDto(group);
                    dto.setGradingSystemDto(gradingSystemMapper.gradingSystemToGradingSystemDetailDto(group.getGradingSystem()));
                    return dto;
                })
                .toList()
                .toArray(gradingGroupDtos);
        }

        UserDetailDto[] judgeDtos = new UserDetailDto[] {};

        if (competition.getJudges() != null) {
            Set<UserDetailDto> judgeDtoSet = userMapper.applicationUserSetToUserDetailDtoSet(
                competition.getJudges()
            );

            judgeDtos = new UserDetailDto[judgeDtoSet.size()];
            judgeDtoSet.toArray(judgeDtos);
        }

        return competitionMapper
            .competitionToCompetitionDetailDto(competition)
            .setGradingGroups(gradingGroupDtos)
            .setJudges(judgeDtos);
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

        if (competition.getDraft() && !competition.getCreator().getId().equals(sessionUtils.getSessionUser().getId())) {
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

    @Override
    public List<CompetitionListDto> searchCompetitions(CompetitionSearchDto competitionSearchDto) {
        List<Competition> searchResult =
            competitionRepository.findAllByBeginOfCompetitionAfterAndEndOfCompetitionAfterAndBeginOfRegistrationAfterAndEndOfRegistrationAfterAndNameContainingIgnoreCaseAndIsPublicIsTrue(
                competitionSearchDto.getBeginDate(), competitionSearchDto.getEndDate(), competitionSearchDto.getBeginRegistrationDate(), competitionSearchDto.getEndRegistrationDate(), competitionSearchDto.getName());

        searchResult = searchResult.stream()
            .filter(s -> !s.getDraft() || (sessionUtils.getSessionUser() != null && s.getCreator().getId().equals(sessionUtils.getSessionUser().getId()))).collect(
            Collectors.toList());

        return competitionMapper.competitionListToCompetitionListDtoList(searchResult);
    }
}
