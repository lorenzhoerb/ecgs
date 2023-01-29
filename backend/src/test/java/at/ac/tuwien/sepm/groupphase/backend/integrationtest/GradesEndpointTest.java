package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BasicRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradeResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.LiveResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradeMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;
import at.ac.tuwien.sepm.groupphase.backend.entity.grade.Grade;
import at.ac.tuwien.sepm.groupphase.backend.entity.grade.GradePk;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradeVariable;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterConstraintRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.GradeService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingGroupService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class GradesEndpointTest extends TestDataProvider {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    @Autowired
    private SecurityUserRepository securityUserRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private GradingGroupRepository gradingGroupRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private GradingSystemRepository gradingSystemRepository;

    @Autowired
    private RegisterToRepository registerToRepository;

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GradeMapper gradeMapper;


    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private GradeService gradeService;

    private ApplicationUser judge;
    private ApplicationUser judge2;
    private Competition competition;

    @BeforeEach
    public void beforeEach() throws Exception {
        this.gradeRepository.deleteAll();
        this.applicationUserRepository.deleteAll();
        this.competitionRepository.deleteAll();
        this.gradingGroupRepository.deleteAll();
        this.gradingSystemRepository.deleteAll();
        this.registerToRepository.deleteAll();

        this.judge = createValidJudgeUser(applicationUserRepository, securityUserRepository, "judge1@email.net");
        this.judge2 = createValidJudgeUser(applicationUserRepository, securityUserRepository, "judge2@email.net");

        this.competition = createCompetitionEntity(applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true, false, Set.of(this.judge, this.judge2),
            gradingSystemRepository);

        setUpParticipantUser();
    }

    @AfterEach
    public void afterEach() {
        this.gradeRepository.deleteAll();
        this.applicationUserRepository.deleteAll();
        this.competitionRepository.deleteAll();
        this.gradingGroupRepository.deleteAll();
        this.gradingSystemRepository.deleteAll();
        this.registerToRepository.deleteAll();
    }

    @Test
    public void givenNoGrades_getAllGrades_expect200EmptyArray() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(
            get(GRADES_BASE_URI + "/{competitionId}/{gradingGroupId}/{stationId}",
                this.competition.getId(),
                    this.competition.getGradingGroups().stream().findFirst().get().getId(),
                    1L)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        this.judge.getUser().getEmail(),
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        List<GradeResultDto> resp = objectMapper.readValue(response.getContentAsString(),
            new TypeReference<List<GradeResultDto>>(){});

        assertTrue(resp != null && resp.isEmpty());
    }

    @Test
    @WithMockUser(username = "judge1@email.net")
    public void givenValidGrades_getAllGrades_expect200ArrayWithResults() throws Exception {
        GradeDto gradeDto1 = this.getValidEnterGradeDto();
        GradeDto gradeDto2 = this.getValidEnterGradeDto().withJudgeId(this.judge2.getId());
        Grade grade1 = this.gradeMapper.gradeDtoToGrade(gradeDto1);
        Grade grade2 = this.gradeMapper.gradeDtoToGrade(gradeDto2);


        grade1.setCompetition(competition);
        grade1.setGradingGroup(competition.getGradingGroups().stream().toList().get(0));
        grade1.setJudge(this.judge);
        //Note: Get is safe here we already checked existence
        grade1.setParticipant(applicationUserRepository.findById(gradeDto1.participantId()).get());
        grade1.setValid(true);

        gradeRepository.save(grade1);

        grade2.setCompetition(competition);
        grade2.setGradingGroup(competition.getGradingGroups().stream().toList().get(0));
        grade2.setJudge(this.judge2);
        //Note: Get is safe here we already checked existence
        grade2.setParticipant(applicationUserRepository.findById(gradeDto1.participantId()).get());
        grade2.setValid(true);

        gradeRepository.save(grade2);

        MvcResult mvcResult = this.mockMvc.perform(
                get(GRADES_BASE_URI + "/{competitionId}/{gradingGroupId}/{stationId}",
                    this.competition.getId(),
                    this.competition.getGradingGroups().stream().findFirst().get().getId(),
                    1L)
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(
                            this.judge.getUser().getEmail(),
                            List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                        ))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        List<GradeResultDto> resp = objectMapper.readValue(response.getContentAsString(),
            new TypeReference<List<GradeResultDto>>(){});

        assertNotNull(resp);
        assertEquals(2, resp.size());
        assertEquals(10.0, resp.get(0).result());
    }

    @Test
    @WithMockUser(username = "judge1@email.net")
    public void givenValidGrades_getAllLiveResults_expect200ArrayWithResults() throws Exception {
        GradeDto gradeDto1 = this.getValidEnterGradeDto();
        GradeDto gradeDto2 = this.getValidEnterGradeDto().withJudgeId(this.judge2.getId());
        Grade grade1 = this.gradeMapper.gradeDtoToGrade(gradeDto1);
        Grade grade2 = this.gradeMapper.gradeDtoToGrade(gradeDto2);


        grade1.setCompetition(competition);
        grade1.setGradingGroup(competition.getGradingGroups().stream().toList().get(0));
        grade1.setJudge(this.judge);
        //Note: Get is safe here we already checked existence
        grade1.setParticipant(applicationUserRepository.findById(gradeDto1.participantId()).get());
        grade1.setValid(true);

        gradeRepository.save(grade1);

        grade2.setCompetition(competition);
        grade2.setGradingGroup(competition.getGradingGroups().stream().toList().get(0));
        grade2.setJudge(this.judge2);
        //Note: Get is safe here we already checked existence
        grade2.setParticipant(applicationUserRepository.findById(gradeDto1.participantId()).get());
        grade2.setValid(true);

        gradeRepository.save(grade2);

        MvcResult mvcResult = this.mockMvc.perform(
                get(GRADES_BASE_URI + "/live-results/{competitionId}",
                    this.competition.getId(),
                    this.competition.getGradingGroups().stream().findFirst().get().getId(),
                    1L)
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(
                            this.judge.getUser().getEmail(),
                            List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                        ))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        List<LiveResultDto> resp = objectMapper.readValue(response.getContentAsString(),
            new TypeReference<List<LiveResultDto>>(){});

        assertNotNull(resp);
        assertEquals(1, resp.size());
        assertEquals(2, resp.get(0).grades().size());
        assertEquals(10.0, resp.get(0).grades().get(0).result());
    }

    @Test
    @WithMockUser(username = "judge1@email.net")
    public void givenAGrade_getAllGrades_expect200ArrayWithAResult() throws Exception {
        GradeDto gradeDto1 = this.getValidEnterGradeDto();

        GradeResultDto resultDto = this.gradeService.updateCompetitionResults(
            this.competition.getId(),
            this.competition.getGradingGroups().stream().findFirst().get().getId(),
            "Station 1",
            gradeDto1
            );

        assertNotNull(resultDto);

        //remove uuid and change result from NaN to null because uuid won't be used in this context
        resultDto = resultDto.withUuid(null).withResult(null);


        MvcResult mvcResult = this.mockMvc.perform(
                get(GRADES_BASE_URI + "/{competitionId}/{gradingGroupId}/{stationId}",
                    this.competition.getId(),
                    this.competition.getGradingGroups().stream().findFirst().get().getId(),
                    1L)
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(
                            this.judge.getUser().getEmail(),
                            List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                        ))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        List<GradeResultDto> resp = objectMapper.readValue(response.getContentAsString(),
            new TypeReference<List<GradeResultDto>>(){});

        assertNotNull(resp);
        assertEquals(1, resp.size());
        assertEquals(resultDto, resp.get(0));
    }


    @Test
    public void givenUserWhoIsNotAJudge_getAllGrades_expect401Unauthorized() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(
                get(GRADES_BASE_URI + "/{competitionId}/{gradingGroupId}/{stationId}",
                    this.competition.getId(),
                    this.competition.getGradingGroups().stream().findFirst().get().getId(),
                    1L)
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(
                            TEST_USER_PARTICIPANT_EMAIL,
                            List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                        ))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    public void givenBadGradingGroupId_getAllGrades_expect404NotFound() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(
                get(GRADES_BASE_URI + "/{competitionId}/{gradingGroupId}/{stationId}",
                    this.competition.getId(),
                    this.competition.getGradingGroups().stream().findFirst().get().getId()-1,
                    1L)
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(
                            this.judge.getUser().getEmail(),
                            List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                        ))
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }


    private GradeDto getValidEnterGradeDto() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Grade grade = new at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Grade();
        grade.grades = new GradeVariable[] {
            new GradeVariable(1L, 7.6),
            new GradeVariable(2L, 2.4)
        };

        return new GradeDto(
            UUID.randomUUID(),
            this.judge.getId(),
            this.competition.getGradingGroups().stream().toList().get(0).getRegistrations().stream().toList().get(0).getParticipant().getId(),
            this.competition.getId(),
            this.competition.getGradingGroups().stream().toList().get(0).getId(),
            1L, mapper.writeValueAsString(grade));
    }
}
