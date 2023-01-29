package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradeVariable;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReportIsDownloadableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReportIsDownloadableDto;
import at.ac.tuwien.sepm.groupphase.backend.constraint.operator.ConstraintOperator;
import at.ac.tuwien.sepm.groupphase.backend.constraint.parser.RegisterConstraintParser;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BasicRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BasicDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailGradeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.StationResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.RegisterConstraintMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConstraintParserException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterConstraintRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReportRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReportRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingGroupService;
import at.ac.tuwien.sepm.groupphase.backend.specification.ApplicationUserSpecs;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.ConstraintSetValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Transactional
public class GradingGroupServiceImpl implements GradingGroupService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ApplicationUserRepository applicationUserRepository;
    private final GradeRepository gradeRepository;
    private final GradingGroupRepository gradingGroupRepository;
    private final CompetitionRepository competitionRepository;
    private final SessionUtils sessionUtils;
    private final ConstraintSetValidator constraintValidator;
    private final RegisterConstraintMapper registerConstraintMapper;
    private final ReportRepository reportRepository;
    private final RegisterConstraintRepository registerConstraintRepository;
    private final RegisterConstraintParser registerConstraintParser;

    public GradingGroupServiceImpl(GradingGroupRepository gradingGroupRepository,
                                   CompetitionRepository competitionRepository,
                                   SessionUtils sessionUtils,
                                   ConstraintSetValidator constraintValidator,
                                   ApplicationUserRepository applicationUserRepository,
                                   GradeRepository gradeRepository,
                                   RegisterConstraintRepository registerConstraintRepository,
                                   RegisterConstraintMapper registerConstraintMapper,
                                   ReportRepository reportRepository, RegisterConstraintParser registerConstraintParser) {
        this.gradingGroupRepository = gradingGroupRepository;
        this.competitionRepository = competitionRepository;
        this.constraintValidator = constraintValidator;
        this.registerConstraintMapper = registerConstraintMapper;
        this.reportRepository = reportRepository;
        this.sessionUtils = sessionUtils;
        this.registerConstraintRepository = registerConstraintRepository;
        this.registerConstraintParser = registerConstraintParser;
        this.applicationUserRepository = applicationUserRepository;
        this.gradeRepository = gradeRepository;
    }

    @Override
    public ReportIsDownloadableDto checkAllGradingGroupsHaveReports(Long competitionId) {
        var foundCompetitionOpt = competitionRepository.findById(competitionId);
        if (!sessionUtils.isAuthenticated()) {
            throw new ForbiddenException("Not authenticated");
        }
        if (foundCompetitionOpt.isEmpty()) {
            throw new NotFoundException("Such competition was not found");
        }

        var foundCompetition = foundCompetitionOpt.get();
        return new ReportIsDownloadableDto(foundCompetition.getGradingGroups()
            .stream().allMatch(gg -> gg.getReport() != null));
    }

    @Override
    public List<SimpleGradingGroupDto> getAllByCompetition(Long competitionId) {
        LOGGER.debug("getAllByCompetition {}", competitionId);
        if (competitionId == null) {
            throw new ValidationListException("Error while getting grading group", "No componentId given");
        }

        Competition competition = competitionRepository.findById(competitionId)
            .orElseThrow(() -> new NotFoundException("Unknown competition " + competitionId));

        List<GradingGroup> gradingGroup = gradingGroupRepository.findAllByCompetitionId(competitionId);
        return gradingGroup
            .stream()
            .map((g) -> {
                SimpleGradingGroupDto simpleGradingGroupDto = new SimpleGradingGroupDto();
                simpleGradingGroupDto.setId(g.getId());
                simpleGradingGroupDto.setTitle(g.getTitle());
                if (g.getRegisterConstraints().isEmpty()) {
                    simpleGradingGroupDto.setConstraints(List.of());
                } else {
                    try {
                        List<ConstraintOperator<?>> constraints = registerConstraintParser.parse(g.getRegisterConstraints());
                        List<BasicDto> constraintDto = constraints.stream()
                            .map(c -> new BasicDto(c.getViolationMessage())).toList();
                        simpleGradingGroupDto.setConstraints(constraintDto);
                    } catch (ConstraintParserException e) {
                        throw new RuntimeException(e);
                    }

                }
                return simpleGradingGroupDto;
            })
            .toList();
    }

    @Override
    public DetailedGradingGroupDto getOneById(Long groupId) {
        LOGGER.debug("createConstraints({})", groupId);
        if (!sessionUtils.isCompetitionManager()) {
            throw new ForbiddenException("Not allowed to set constraints to a grading group");
        }

        if (groupId == null) {
            throw new ValidationListException("Error validating constraints", List.of("Group id must be given"));
        }

        GradingGroup gradingGroup = gradingGroupRepository
            .findByIdAndCompetitionCreatorId(groupId, sessionUtils.getSessionUser().getId())
            .orElseThrow(() -> new NotFoundException("There is no Grading Group with id " + groupId + " you own."));

        List<DetailedRegisterConstraintDto> constraints = gradingGroup.getRegisterConstraints()
            .stream()
            .map(registerConstraintMapper::registerConstraintToDetailedRegisterConstraintDto)
            .toList();

        return DetailedGradingGroupDto.builder()
            .id(gradingGroup.getId())
            .title(gradingGroup.getTitle())
            .constraints(constraints)
            .build();
    }

    @Override
    public Set<DetailedRegisterConstraintDto> setConstraints(Long groupId, List<BasicRegisterConstraintDto> constraints) {
        LOGGER.debug("createConstraints({},{})", groupId, constraints);
        if (!sessionUtils.isCompetitionManager()) {
            throw new ForbiddenException("Not allowed to set constraints to a grading group");
        }

        if (groupId == null) {
            throw new ValidationListException("Error validating constraints", List.of("Group id must be given"));
        }

        GradingGroup gradingGroup = gradingGroupRepository
            .findByIdAndCompetitionCreatorId(groupId, sessionUtils.getSessionUser().getId())
            .orElseThrow(() -> new NotFoundException("There is no Grading Group with id " + groupId + " you own."));

        constraintValidator.validate(constraints);

        List<RegisterConstraint> mappedRegisterConstraints = constraints.stream().map(c -> {
            RegisterConstraint cs = registerConstraintMapper.basicRegisterConstraintDtoToRegisterConstraint(c);
            cs.setGradingGroup(gradingGroup);
            return cs;
        }).toList();


        List<RegisterConstraint> existingConstraints = gradingGroup.getRegisterConstraints();
        if (existingConstraints != null) {
            for (RegisterConstraint constraint : existingConstraints) {
                registerConstraintRepository.delete(constraint);
            }
        }

        gradingGroup.setRegisterConstraints(mappedRegisterConstraints);
        for (RegisterConstraint r : mappedRegisterConstraints) {
            r.setGradingGroup(gradingGroup);
        }

        GradingGroup g = gradingGroupRepository.save(gradingGroup);

        return g.getRegisterConstraints().stream()
            .map(registerConstraintMapper::registerConstraintToDetailedRegisterConstraintDto)
            .collect(Collectors.toSet());
    }

    private UserDetailGradeDto mapUserGrades(ApplicationUser user, GradingGroup group, GradingSystem system) {
        List<Grade> result =
            gradeRepository.findAllByGradePkParticipantIdAndGradePkCompetitionIdAndGradePkGradingGroupId(
                user.getId(), group.getCompetition().getId(), group.getId());

        ObjectMapper mapper = new ObjectMapper();

        for (Grade g : result) {
            at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Grade parsed;

            try {
                parsed = mapper.readValue(g.getGrading(), at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Grade.class);
            } catch (JsonProcessingException e) {
                throw new ValidationListException(
                    "invalid grade",
                    List.of("invalid grade"));
            }

            for (GradeVariable grade : parsed.grades) {
                system.bindVariable(g.getGradePk().getStationId(), grade.getId(), grade.getValue());
            }
        }

        List<StationResultDto> stationResultDtos = new ArrayList<>(
            Arrays.stream(system.stations).map(s -> new StationResultDto(s.getId(), s.getDisplayName(), s.evaluate())).toList()
        );

        stationResultDtos.sort(Comparator.comparing(StationResultDto::id));

        return new UserDetailGradeDto(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getGender(),
            user.getDateOfBirth(),
            user.getPicturePath(),
            stationResultDtos,
            system.evaluate()
        );
    }

    @Override
    public Page<UserDetailGradeDto> getParticipants(Long id, UserDetailFilterDto filter) {
        LOGGER.debug("getParticipants({})", filter);

        if (!sessionUtils.isAuthenticated()) {
            throw new ForbiddenException("No permission to get participant details");
        }

        GradingGroup group = gradingGroupRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("No group found"));

        if (!this.checkAllGradingGroupsHaveReports(group.getCompetition().getId()).isDownloadable()) {
            throw new ValidationListException("competition is not finished", List.of("competition is not finished"));
        }

        int page = 0;
        int size = 10;

        if (filter == null) {
            filter = new UserDetailFilterDto();
        }

        if (filter.getPage() != null && filter.getPage() >= 0) {
            page = filter.getPage();
        }

        if (filter.getSize() != null && filter.getSize() >= 0) {
            size = filter.getSize();
        }

        Specification<ApplicationUser> specification = ApplicationUserSpecs.groupSpecs(id, filter);
        Pageable pageable = PageRequest.of(page, size);
        Page<ApplicationUser> partPage = applicationUserRepository.findAll(specification, pageable);

        GradingSystem system = new GradingSystem(group.getGradingSystem().getFormula());

        List<UserDetailGradeDto> list = new ArrayList<>(partPage.getContent()
            .stream()
            .map((u) -> mapUserGrades(u, group, system)).toList());

        list.sort(Comparator.comparing(UserDetailGradeDto::finalResult).reversed());

        return new PageImpl<>(list, pageable, partPage.getTotalElements());
    }
}
