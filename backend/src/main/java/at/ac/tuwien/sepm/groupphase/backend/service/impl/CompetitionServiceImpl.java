package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.AdvanceCompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupWithRegisterToDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PageableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingGroupMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingSystemMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.JudgeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingSystemService;
import at.ac.tuwien.sepm.groupphase.backend.specification.ApplicationUserSpecs;
import at.ac.tuwien.sepm.groupphase.backend.specification.CompetitionSpecification;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.CompetitionValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.GradeValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.GradingSystemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompetitionServiceImpl implements CompetitionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CompetitionRepository competitionRepository;
    private final GradingGroupRepository gradingGroupRepository;

    private final JudgeRepository judgeRepository;
    private final GradeRepository gradeRepository;

    private final CompetitionMapper competitionMapper;
    private final GradingGroupMapper gradingGroupMapper;
    private final GradingSystemRepository gradingSystemRepository;
    private final GradingSystemMapper gradingSystemMapper;
    private final GradingSystemValidator gradingSystemValidator;
    private final GradeValidator gradeValidator;
    private final UserMapper userMapper;
    private final CompetitionValidator competitionValidator;
    private final GradingSystemService gradingSystemService;

    private final ApplicationUserRepository applicationUserRepository;
    private final SessionUtils sessionUtils;
    private final RegisterToRepository registerToRepository;

    public CompetitionServiceImpl(CompetitionRepository competitionRepository, GradeRepository gradeRepository, CompetitionMapper competitionMapper,
                                  CompetitionValidator competitionValidator, UserMapper userMapper,
                                  SessionUtils sessionUtils, GradingGroupRepository gradingGroupRepository,
                                  GradingSystemService gradingSystemService,
                                  GradingSystemRepository gradingSystemRepository,
                                  GradingSystemMapper gradingSystemMapper,
                                  GradingSystemValidator gradingSystemValidator,
                                  GradeValidator gradeValidator,
                                  GradingGroupMapper gradingGroupMapper,
                                  ApplicationUserRepository applicationUserRepository,
                                  JudgeRepository judgeRepository,
                                  RegisterToRepository registerToRepository) {
        this.competitionRepository = competitionRepository;
        this.gradeRepository = gradeRepository;
        this.competitionMapper = competitionMapper;
        this.userMapper = userMapper;
        this.competitionValidator = competitionValidator;
        this.sessionUtils = sessionUtils;
        this.gradingGroupMapper = gradingGroupMapper;
        this.gradingGroupRepository = gradingGroupRepository;
        this.gradingSystemService = gradingSystemService;
        this.gradingSystemRepository = gradingSystemRepository;
        this.gradingSystemMapper = gradingSystemMapper;
        this.gradingSystemValidator = gradingSystemValidator;
        this.gradeValidator = gradeValidator;
        this.judgeRepository = judgeRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.registerToRepository = registerToRepository;
    }

    private void verifyCreator(Long id) {
        Optional<Competition> persistedCompetition =
            competitionRepository.findById(id);

        if (persistedCompetition.isEmpty()) {
            throw new ConflictException("The requested competition doesn't exist.",
                List.of("The requested competition doesn't exist."));
        }

        ApplicationUser sessionUser = sessionUtils.getSessionUser();
        ApplicationUser competitionOwner = persistedCompetition.get().getCreator();

        if (!sessionUser.getId().equals(competitionOwner.getId())) {
            throw new ForbiddenException("You have no editing access to this competition");
        }
    }

    @Override
    public CompetitionDetailDto create(CompetitionDetailDto competitionDetailDto) {
        LOGGER.debug("Create competition {}", competitionDetailDto);
        if (!sessionUtils.isCompetitionManager()) {
            throw new ForbiddenException("No Permission to create a competition");
        }

        competitionValidator.validate(competitionDetailDto);

        if (competitionDetailDto.getId() != null) {
            verifyCreator(competitionDetailDto.getId());
        }

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
                    GradingGroupDto dto = gradingGroupMapper.gradingGroupToGradingGroupDetailDto(group);
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

    private int compare(Object o1, Object o2) {
        ApplicationUser p1 = (ApplicationUser) o1;
        ApplicationUser p2 = (ApplicationUser) o2;
        int res =  p1.getLastName().compareToIgnoreCase(p2.getLastName());
        if (res != 0) {
            return res;
        }
        return p1.getFirstName().compareToIgnoreCase(p2.getFirstName());
    }

    private List<ApplicationUser> getParticipantsInternal(Long id) {
        LOGGER.debug("List participants internal for competition {}", id);

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
        List<ApplicationUser> participants =
            new ArrayList<>(registerTos.stream().map(RegisterTo::getParticipant).toList());

        participants.sort(this::compare);

        return participants;
    }

    @Transactional
    @Override
    public Set<GradingGroupWithRegisterToDto> getCompetitionGradingGroupsWithParticipants(Long competitionId) {
        LOGGER.debug("Find gradingGroupsWithParticipants of competition with id {}", competitionId);
        Optional<Competition> competition = competitionRepository.findById(competitionId);

        if (competition.isPresent()) {
            Competition comp = competition.get();
            Set<GradingGroup> gradingGroups = comp.getGradingGroups();
            gradingGroups.forEach(
                gradingGroup -> gradingGroup.setRegistrations(gradingGroup.getRegistrations().stream().filter(RegisterTo::getAccepted).collect(
                    Collectors.toSet())));
            return gradingGroupMapper.gradingGroupToGradingGroupRegistrationDto(gradingGroups);
        }

        throw new NotFoundException(String.format("Could not find competition with id %s", competitionId));
    }

    @Override
    public List<UserDetailDto> getParticipants(Long id) {
        LOGGER.debug("List participants for competition {}", id);

        List<ApplicationUser> participants = getParticipantsInternal(id);
        return userMapper.applicationUserListToUserDetailDtoList(participants);
    }


    @Override
    public Page<ParticipantRegDetailDto> getParticipantsRegistrationDetails(PageableDto<ParticipantFilterDto> filter) {
        LOGGER.debug("getParticipantsRegistrationDetails({})", filter);
        if (!sessionUtils.isCompetitionManager()) {
            throw new ForbiddenException("No permission to get participant registration details");
        }

        //User owns competition
        Competition competition = competitionRepository
            .findByIdAndCreatorId(filter.filters().getCompetitionId(), sessionUtils.getSessionUser().getId())
            .orElseThrow(() -> new NotFoundException("No competition found"));

        int page = 0;
        int size = 10;
        if (filter.page() != null && filter.page() >= 0) {
            page = filter.page();
        }

        if (filter.size() != null && filter.size() >= 0) {
            size = filter.size();
        }

        Specification<ApplicationUser> specification = ApplicationUserSpecs.specs(filter.filters());
        Pageable pageable = PageRequest.of(page, size);
        Page<ApplicationUser> partPage = applicationUserRepository.findAll(specification, pageable);

        return new PageImpl<>(partPage.getContent()
            .stream()
            .map(p -> mapPartRegDetailDto(
                p,
                competition.getId())).toList(),
            pageable,
            partPage.getTotalElements());
    }

    public CompetitionViewDto findOne(Long id) {
        LOGGER.debug("Find message with id {}", id);
        Optional<Competition> competitionOptional = competitionRepository.findById(id);

        if (competitionOptional.isPresent()) {
            Competition found = competitionOptional.get();
            CompetitionViewDto competition = competitionMapper.competitionToCompetitionViewDto(found);

            if (!found.getDraft()
                || (this.sessionUtils.getSessionUser() != null
                && found.getCreator().getId().equals(this.sessionUtils.getSessionUser().getId()))) {
                return competition;
            } else {
                throw new NotFoundException("competition not public or in draft!");
            }
        }

        throw new NotFoundException(String.format("Could not find competition with id %s", id));
    }

    public CompetitionDetailDto findOneDetail(Long id) {
        LOGGER.debug("Find message with id {}", id);
        Optional<Competition> competition = competitionRepository.findById(id);

        if (competition.isPresent()) {
            Competition found = competition.get();

            verifyCreator(found.getId());

            CompetitionDetailDto competitionDetailDto =
                competitionMapper.competitionToCompetitionDetailDto(found);

            competitionDetailDto.setCreator(
                userMapper.applicationUserToUserDetailDto(
                    found.getCreator()
            ));

            if (found.getGradingGroups() != null) {
                GradingGroupDto[] gradingGroupDtos =
                    new GradingGroupDto[found.getGradingGroups().size()];

                int i = 0;
                for (GradingGroup gradingGroup : found.getGradingGroups()) {
                    gradingGroupDtos[i] =
                        gradingGroupMapper.gradingGroupToGradingGroupDetailDto(gradingGroup);

                    if (gradingGroup.getGradingSystem() != null) {

                        gradingGroupDtos[i].setGradingSystemDto(
                            gradingSystemMapper.gradingSystemToGradingSystemDetailDto(
                                gradingGroup.getGradingSystem()
                            )
                        );
                    }

                    ++i;
                }

                competitionDetailDto.setGradingGroups(gradingGroupDtos);
            }

            if (found.getJudges() != null) {
                UserDetailDto[] judges = new UserDetailDto[found.getJudges().size()];

                int i = 0;
                for (ApplicationUser judge : found.getJudges()) {
                    judges[i++] = userMapper.applicationUserToUserDetailDto(judge);
                }

                competitionDetailDto.setJudges(judges);
            }

            return competitionDetailDto;
        }

        throw new NotFoundException(String.format("Could not find competition with id %s", id));
    }

    @Override
    public List<CompetitionListDto> searchCompetitions(CompetitionSearchDto competitionSearchDto) {
        List<Competition> searchResult =
            competitionRepository.findAllByBeginOfCompetitionAfterAndEndOfCompetitionAfterAndBeginOfRegistrationAfterAndEndOfRegistrationAfterAndNameContainingIgnoreCaseAndIsPublicIsTrue(
                competitionSearchDto.getBeginDate(), competitionSearchDto.getEndDate(), competitionSearchDto.getBeginRegistrationDate(),
                competitionSearchDto.getEndRegistrationDate(), competitionSearchDto.getName());

        searchResult = searchResult.stream()
            .filter(s -> !s.getDraft() || (sessionUtils.getSessionUser() != null && s.getCreator().getId().equals(sessionUtils.getSessionUser().getId())))
            .collect(
                Collectors.toList());

        return competitionMapper.competitionListToCompetitionListDtoList(searchResult);
    }

    private ParticipantRegDetailDto mapPartRegDetailDto(ApplicationUser u, Long compId) {
        RegisterTo registerTo = registerToRepository
            .findByGradingGroupCompetitionIdAndParticipantId(compId, u.getId()).get();
        return new ParticipantRegDetailDto(
            u.getId(),
            u.getFirstName(),
            u.getLastName(),
            u.getGender(),
            u.getDateOfBirth(),
            registerTo.getGradingGroup().getId(),
            registerTo.getAccepted()
        );
    }

    @Override
    public Page<CompetitionListDto> searchCompetitionsAdvanced(AdvanceCompetitionSearchDto searchDto) {
        LOGGER.debug("searchCompetitionsAdvanced({})", searchDto);
        if (searchDto == null) {
            searchDto = new AdvanceCompetitionSearchDto();
        }
        Specification<Competition> specs = CompetitionSpecification.isDraft(false);
        Specification<Competition> searchSpecs = CompetitionSpecification.getSpecs(searchDto);
        if (searchSpecs != null) {
            specs = specs.and(searchSpecs);
        }

        if (searchDto.getPage() == null || searchDto.getPage() < 0) {
            searchDto.setPage(0);
        }

        if (searchDto.getSize() == null || searchDto.getSize() < 0) {
            searchDto.setSize(10);
        }

        Pageable pageable = PageRequest.of(searchDto.getPage(), searchDto.getSize());
        Page<Competition> page = competitionRepository.findAll(specs, pageable);

        return new PageImpl<>(
            competitionMapper.competitionListToCompetitionListDtoList(page.getContent()),
            pageable,
            page.getTotalElements());
    }
}