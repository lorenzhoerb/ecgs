package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupWithRegisterToDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.CompetitionMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradeMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingGroupMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingSystemMapper;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.entity.grade.GradePk;
import at.ac.tuwien.sepm.groupphase.backend.exception.BadWebSocketRequestException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.StrategyException;
import at.ac.tuwien.sepm.groupphase.backend.exception.UnauthorizedException;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Grade;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradeVariable;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Station;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Variable;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.JudgeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradeService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingSystemService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import at.ac.tuwien.sepm.groupphase.backend.validation.CompetitionValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.GradeValidator;
import at.ac.tuwien.sepm.groupphase.backend.validation.GradingSystemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class GradeServiceImpl implements GradeService {


    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final CompetitionRepository competitionRepository;
    private final GradingGroupRepository gradingGroupRepository;

    private final JudgeRepository judgeRepository;
    private final GradeRepository gradeRepository;
    private final RegisterToRepository registerToRepository;

    private final CompetitionMapper competitionMapper;
    private final GradingGroupMapper gradingGroupMapper;
    private final GradingSystemRepository gradingSystemRepository;
    private final GradingSystemMapper gradingSystemMapper;
    private final GradingSystemValidator gradingSystemValidator;
    private final GradeValidator gradeValidator;
    private final UserMapper userMapper;
    private final GradeMapper gradeMapper;
    private final CompetitionValidator competitionValidator;
    private final GradingSystemService gradingSystemService;

    private final ApplicationUserRepository applicationUserRepository;
    private final SessionUtils sessionUtils;

    public GradeServiceImpl(CompetitionRepository competitionRepository, GradeRepository gradeRepository, RegisterToRepository registerToRepository,
                            CompetitionMapper competitionMapper, CompetitionValidator competitionValidator, UserMapper userMapper, GradeMapper gradeMapper,
                            SessionUtils sessionUtils, GradingGroupRepository gradingGroupRepository, GradingSystemService gradingSystemService,
                            GradingSystemRepository gradingSystemRepository, GradingSystemMapper gradingSystemMapper,
                            GradingSystemValidator gradingSystemValidator, GradeValidator gradeValidator, GradingGroupMapper gradingGroupMapper,
                            JudgeRepository judgeRepository, ApplicationUserRepository applicationUserRepository) {
        this.competitionRepository = competitionRepository;
        this.gradeRepository = gradeRepository;
        this.registerToRepository = registerToRepository;
        this.competitionMapper = competitionMapper;
        this.userMapper = userMapper;
        this.gradeMapper = gradeMapper;
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
    }

    @Transactional
    @Override
    public GradeResultDto updateCompetitionResults(Long competitionId, Long gradingGroupId, String stationName, GradeDto gradeDto) {
        gradeValidator.validate(gradeDto);

        ApplicationUser user = sessionUtils.getSessionUser();

        this.checkGradingRequestIntegrity(user, competitionId, gradingGroupId, gradeDto);

        Competition competition = this.getCompetition(competitionId);

        this.checkUserIsJudgeAtCompetition(user, competition);
        this.checkParticipantExistsInGradingGroup(gradeDto.participantId(), competition);

        GradingGroup gradingGroup = this.getGradingGroup(competition, gradingGroupId);
        GradingSystem gradingSystem = new GradingSystem(gradingGroup.getGradingSystem().getFormula());

        Station station = getStation(gradingSystem, gradeDto.stationId(), stationName);

        Grade grade = new Grade(gradeDto.grade());

        this.checkGradeIsComplete(grade, station);

        at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade gradeEntity = gradeMapper.gradeDtoToGrade(gradeDto);


        List<at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade> existingGrades =
            gradeRepository.findAllByGradePkParticipantIdAndGradePkCompetitionIdAndGradePkGradingGroupIdAndGradePkStationId(gradeDto.participantId(),
                gradeDto.competitionId(), gradeDto.gradingGroupId(), gradeDto.stationId());

        List<Grade> combinedGrades = new LinkedList<>();
        combinedGrades.add(grade);

        if (existingGrades.size() > 0) {
            combinedGrades.addAll(existingGrades.stream().filter(g -> !Objects.equals(g.getGradePk().getJudgeId(), gradeEntity.getGradePk().getJudgeId()))
                .map(g -> new Grade(g.getGrading())).collect(Collectors.toList()));
        }

        for (Variable variable : station.getVariables()) {
            List<Double> grades = combinedGrades.stream().flatMap(g -> Arrays.stream(g.grades)).filter(gv -> Objects.equals(gv.getId(), variable.getId()))
                .map(gv -> gv.getValue()).collect(Collectors.toList());

            variable.setValues(grades);
        }

        Double result = Double.NaN;
        boolean isValid = true;

        try {
            result = station.evaluate();
        } catch (StrategyException e) {
            isValid = false;
        }

        var existingGradeEntity = existingGrades.stream().filter(g -> Objects.equals(g.getGradePk().getJudgeId(), gradeDto.judgeId())).findFirst();

        if (existingGradeEntity.isPresent()) {
            existingGradeEntity.get().setGrading(gradeEntity.getGrading());
        } else {
            gradeEntity.setCompetition(competition);
            gradeEntity.setGradingGroup(gradingGroup);
            gradeEntity.setJudge(user);
            //Note: Get is safe here we already checked existence
            gradeEntity.setParticipant(applicationUserRepository.findById(gradeDto.participantId()).get());
            gradeEntity.setValid(isValid);
            gradeRepository.save(gradeEntity);
        }

        for (var g : existingGrades) {
            g.setValid(isValid);
        }

        gradeRepository.saveAll(existingGrades);

        GradeResultDto resultDto = gradeMapper.gradeDtoToGradeResultDto(gradeDto).withIsValid(isValid).withResult(result);


        return resultDto;
    }

    public Long verifyJudgeAndReturnId(Long competitionId, Long gradingGroupId, Long stationId) {
        ApplicationUser user = sessionUtils.getSessionUser();
        Competition competition = this.getCompetition(competitionId);
        this.checkUserIsJudgeAtCompetition(user, competition);

        return user.getId();
    }

    public List<GradeResultDto> getAllGradesForStation(Long competitionId, Long gradingGroupId, Long stationId) {
        ApplicationUser user = sessionUtils.getSessionUser();
        Competition competition = this.getCompetition(competitionId);
        this.checkUserIsJudgeAtCompetition(user, competition);

        List<GradeResultDto> resultDtos =
            gradeRepository.findAllByGradePkCompetitionIdAndGradePkGradingGroupIdAndGradePkStationId(competitionId, gradingGroupId, stationId).stream()
                .map(gradeMapper::gradeToGradeResultDto).collect(Collectors.toList());

        ArrayList<Double> deb = new ArrayList<Double>();
        calculateResults(resultDtos);

        return resultDtos;
    }


    /** calculates for all grades the result if possible (with inplace changes).
     *
     * @param grades list of grades
     */
    private void calculateResults(List<GradeResultDto> grades) {
        if (grades.stream().filter(g -> g.isValid()).collect(Collectors.toList()).isEmpty()) {
            return;
        }
        for (Long competitionId : grades.stream().filter(g -> g.isValid()).map(g -> g.competitionId()).distinct().collect(Collectors.toList())) {
            List<GradeResultDto> sameCompetition = grades.stream().filter(g -> Objects.equals(g.competitionId(), competitionId)).collect(Collectors.toList());

            Competition competition = this.getCompetition(competitionId);

            for (Long gradingGroupId : sameCompetition.stream().map(g -> g.gradingGroupId()).distinct().collect(Collectors.toList())) {
                List<GradeResultDto> sameGroup =
                    sameCompetition.stream().filter(g -> Objects.equals(g.gradingGroupId(), gradingGroupId)).collect(Collectors.toList());

                GradingGroup gradingGroup = this.getGradingGroup(competition, gradingGroupId);
                GradingSystem gradingSystem = new GradingSystem(gradingGroup.getGradingSystem().getFormula());

                for (Long stationId : sameGroup.stream().map(g -> g.stationId()).distinct().collect(Collectors.toList())) {
                    List<GradeResultDto> sameStation = sameGroup.stream().filter(g -> Objects.equals(g.stationId(), stationId)).collect(Collectors.toList());

                    Station station = Arrays.stream(gradingSystem.stations).filter(s -> Objects.equals(s.getId(), stationId)).findFirst().get();

                    for (Long participantId : sameStation.stream().map(g -> g.participantId()).distinct().collect(Collectors.toList())) {
                        List<GradeResultDto> sameParticipant =
                            sameStation.stream().filter(g -> Objects.equals(g.participantId(), participantId)).collect(Collectors.toList());

                        List<Grade> combinedGrades = new LinkedList<>();

                        sameParticipant.forEach(g -> combinedGrades.add(new Grade(g.grade())));

                        for (Variable variable : station.getVariables()) {
                            List<Double> bindings =
                                combinedGrades.stream().flatMap(g -> Arrays.stream(g.grades)).filter(gv -> Objects.equals(gv.getId(), variable.getId()))
                                    .map(gv -> gv.getValue()).collect(Collectors.toList());

                            variable.setValues(bindings);
                        }

                        Double result = Double.NaN;
                        boolean isValid = true;

                        try {
                            result = station.evaluate();
                        } catch (StrategyException e) {
                            isValid = false;
                        }

                        Double fckingJavaFinalResult = result;

                        if (isValid) {
                            sameParticipant.forEach(g -> grades.set(grades.indexOf(g), g.withResult(fckingJavaFinalResult)));
                        }
                    }
                }
            }
        }

    }

    private void checkGradeIsComplete(Grade grade, Station station) {
        if (grade.grades.length != station.getVariables().length) {
            throw new BadWebSocketRequestException("Grade and station Variable count differs");
        }

        for (Variable stationVar : station.getVariables()) {
            Optional<GradeVariable> gradeVariableOptional = Arrays.stream(grade.grades).filter(g -> Objects.equals(g.getId(), stationVar.getId())).findFirst();

            if (gradeVariableOptional.isEmpty()) {
                throw new BadWebSocketRequestException("Missing Variable with name: " + stationVar.getDisplayName());
            }
        }
    }

    private void checkGradingRequestIntegrity(ApplicationUser user, Long competitionId, Long gradingGroupId, GradeDto grade) {
        if (user == null || !Objects.equals(user.getId(), grade.judgeId())) {
            throw new UnauthorizedException("Can't enter grades for other judges");
        }
        if (!Objects.equals(competitionId, grade.competitionId())) {
            throw new BadWebSocketRequestException("Grade competitionId differs from destination competitionId");
        }
        if (!Objects.equals(gradingGroupId, grade.gradingGroupId())) {
            throw new BadWebSocketRequestException("Grade gradingGroupId differs from destination gradingGroupId");
        }
    }

    private void checkUserIsJudgeAtCompetition(ApplicationUser user, Competition competition) {
        if (competition.getJudges().stream().filter(j -> Objects.equals(j.getId(), user.getId())).count() != 1) {
            throw new UnauthorizedException("Only Judges can access grades");
        }
    }

    private void checkParticipantExistsInGradingGroup(Long participantId, Competition competition) {
        if (this.registerToRepository.findByGradingGroupCompetitionIdAndParticipantId(competition.getId(), participantId).isEmpty()) {
            throw new UnauthorizedException("Participant not in ");
        }
    }

    private Competition getCompetition(Long competitionId) {
        Optional<Competition> competitionOptional = competitionRepository.findById(competitionId);
        if (competitionOptional.isEmpty()) {
            throw new NotFoundException("Competition does not exist");
        }
        return competitionOptional.get();
    }

    private GradingGroup getGradingGroup(Competition competition, Long gradingGroupId) {
        Optional<GradingGroup> gradingGroupOptional =
            competition.getGradingGroups().stream().filter(g -> Objects.equals(g.getId(), gradingGroupId)).findFirst();
        if (gradingGroupOptional.isEmpty()) {
            throw new NotFoundException("GradingGroup does not exist");
        }
        return gradingGroupOptional.get();
    }

    private Station getStation(GradingSystem gradingSystem, Long stationId, String stationName) {
        Optional<Station> stationOptional = Arrays.stream(gradingSystem.stations).filter(s -> Objects.equals(s.getId(), stationId)).findFirst();

        if (stationOptional.isEmpty()) {
            throw new NotFoundException("Station does not exist");
        }

        Station station = stationOptional.get();
        if (!Objects.equals(station.getDisplayName(), stationName)) {
            throw new BadWebSocketRequestException("Station's name from grade stationId is not the same as destination stationName");
        }
        return station;
    }

}

