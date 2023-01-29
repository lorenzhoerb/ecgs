package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder.CompetitionBuilder;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ErrorListRestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportDownloadResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ExcelReportGenerationRequestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReportDownloadInclusionRuleOptionsDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ReportIsDownloadableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.enums.ExcelReportGenerationRequestInclusionRule;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.JudgeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterConstraintRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReportFileRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReportRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradeService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReportFileService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReportService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.TestData.ADMIN_USER;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class CompetitionEndpointTest extends TestDataProvider {
    static int compId = 0;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private GradingGroupRepository gradingGroupRepository;

    @Autowired
    private RegisterToRepository registerToRepository;

    @Autowired
    private JudgeRepository judgeRepository;

    @Autowired
    private ReportFileService reportFileService;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private GradingSystemRepository gradingSystemRepository;

    @Autowired
    private SecurityUserRepository securityUserRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private RegisterConstraintRepository registerConstraintRepository;

    @Autowired
    private ReportFileRepository reportFileRepository;

    @Autowired
    private ManagedByRepository managedByRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ReportRepository reportRepository;

    @BeforeEach
    public void beforeEach() {
        registerConstraintRepository.deleteAll();
        gradeRepository.deleteAll();
        reportRepository.deleteAll();
        reportFileRepository.deleteAll();
        managedByRepository.deleteAll();
        registerToRepository.deleteAll();
        applicationUserRepository.deleteAll();
        competitionRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        gradingSystemRepository.deleteAll();
        judgeRepository.deleteAll();
    }

    @AfterEach
    public void afterEach() {
        registerConstraintRepository.deleteAll();
        gradeRepository.deleteAll();
        reportRepository.deleteAll();
        reportFileRepository.deleteAll();
        managedByRepository.deleteAll();
        registerToRepository.deleteAll();
        applicationUserRepository.deleteAll();
        competitionRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        gradingSystemRepository.deleteAll();
        judgeRepository.deleteAll();
    }

    @Test
    public void givenNotLoggedInUser_whenCreatingCompetition_expectForbidden403() throws Exception {
        CompetitionDetailDto competitionDetailDto = getValidCompetitionDetailDto();
        String body = objectMapper.writeValueAsString(competitionDetailDto);

        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenPostInvalid_then422() throws Exception {
        setUpCompetitionUser();
        CompetitionDetailDto competitionDetailDto = new CompetitionDetailDto();
        competitionDetailDto.setName(null)
            .setPhone(null)
            .setBeginOfRegistration(null)
            .setEndOfRegistration(null)
            .setEmail(null)
            .setBeginOfCompetition(null)
            .setEndOfCompetition(null)
            .setDraft(false)
            .setPublic(false);

        String body = objectMapper.writeValueAsString(competitionDetailDto);

        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        TEST_USER_COMPETITION_MANAGER_EMAIL,
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        ErrorListRestDto errorListRestDto = objectMapper.readerFor(ErrorListRestDto.class)
            .readValue(response.getContentAsByteArray());

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
        assertNotNull(errorListRestDto);
        assertThat(errorListRestDto.errors().size())
            .isGreaterThan(0);
        assertEquals("Validation failed", errorListRestDto.message());
    }

    @Test
    public void givenValidCompetition_whenCreating_201() throws Exception {
        setUpCompetitionUser();
        CompetitionDetailDto competitionDetailDto = getValidCompetitionDetailDto();

        String body = objectMapper.writeValueAsString(competitionDetailDto);

        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        TEST_USER_COMPETITION_MANAGER_EMAIL,
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        CompetitionDetailDto result = objectMapper.readerFor(CompetitionDetailDto.class)
            .readValue(response.getContentAsByteArray());

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(competitionDetailDto.getName(), result.getName());
        assertEquals(competitionDetailDto.getEmail(), result.getEmail());
        assertEquals(competitionDetailDto.getPhone(), result.getPhone());
        assertEquals(competitionDetailDto.getBeginOfCompetition(), result.getBeginOfCompetition());
        assertEquals(competitionDetailDto.getEndOfCompetition(), result.getEndOfCompetition());
        assertEquals(competitionDetailDto.getBeginOfRegistration(), result.getBeginOfRegistration());
        assertEquals(competitionDetailDto.getEndOfRegistration(), result.getEndOfRegistration());
        assertEquals(competitionDetailDto.isPublic(), result.isPublic());
        assertEquals(competitionDetailDto.isDraft(), result.isDraft());
    }

    @Test
    public void getParticipants_forExistingCompetition200() throws Exception {
        setUpCompetitionUser();

        Competition competition = createCompetitionEntity(
            applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true,
            false
        );

        MvcResult mvcResult = this.mockMvc.perform(
                get(COMPETITION_BASE_URI + "/{id}/participants",
                    competition.getId())
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(
                            TEST_USER_COMPETITION_MANAGER_EMAIL,
                            List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                        ))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    public void getParticipants_forNotExistingCompetition404() throws Exception {
        setUpCompetitionUser();

        Competition competition = createCompetitionEntity(
            applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true,
            false
        );

        MvcResult mvcResult = this.mockMvc.perform(
                get(COMPETITION_BASE_URI + "/{id}/participants",
                    -1)
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(
                            TEST_USER_COMPETITION_MANAGER_EMAIL,
                            List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                        ))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void getParticipants_givenNotLoggedInUser403() throws Exception {
        Competition competition = createCompetitionEntity(
            applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true,
            false
        );

        MvcResult mvcResult = this.mockMvc.perform(
                get(COMPETITION_BASE_URI + "/{id}/participants",
                    competition.getId())
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    public void getGradingGroups_expect200() throws Exception {
        setUpCompetitionUser();
        Competition c1 = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");
        GradingGroup g2 = new GradingGroup("G2");
        c1.setGradingGroups(Set.of(g1, g2));
        g1.setCompetitions(c1);
        g2.setCompetitions(c1);
        Competition cc1 = competitionRepository.save(c1);
        GradingGroup gg1 = gradingGroupRepository.save(g1);
        GradingGroup gg2 = gradingGroupRepository.save(g2);

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI + "/" + cc1.getId() + "/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        TEST_USER_COMPETITION_MANAGER_EMAIL,
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    )))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        List<SimpleGradingGroupDto> simpleGradingGroupDto = objectMapper
            .readValue(response.getContentAsByteArray(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, SimpleGradingGroupDto.class));

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(2, simpleGradingGroupDto.size());
        List<SimpleGradingGroupDto> sG = simpleGradingGroupDto.stream().filter(g -> g.getTitle().equals(g1.getTitle())).toList();
        assertEquals(1, sG.size());
        assertEquals("G1", sG.get(0).getTitle());
        List<SimpleGradingGroupDto> sG2 = simpleGradingGroupDto.stream().filter(g -> g.getTitle().equals(g2.getTitle())).toList();
        assertEquals(1, sG2.size());
        assertEquals("G2", sG2.get(0).getTitle());
    }

    @Test
    public void getGradingGroups_invalidCompetition_expect404() throws Exception {
        setUpCompetitionUser();
        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI + "/" + 100 + "/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        TEST_USER_COMPETITION_MANAGER_EMAIL,
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    )))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void getRegParticipantsManagement_whenNotLoggedIn_expect403() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(
                get(COMPETITION_BASE_URI + "/{id}/participants/registrations",
                    1L)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @Transactional
    public void searchRegisteredParticipants_withDetailedInformation_expect200() throws Exception {
        setUpCompetitionUser();
        ApplicationUser creator = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = new CompetitionBuilder(
            applicationUserRepository,
            competitionRepository,
            gradingGroupRepository,
            registerToRepository,
            gradingSystemRepository
        )
            .withCreator(creator)
            .withParticipantsPerGroup(25)
            .withGradingGroups(Set.of("T1"))
            .create();

        MvcResult mvcResult = this.mockMvc.perform(
                get(COMPETITION_BASE_URI + "/{id}/participants/registrations?page=0",
                    c.getId())
                    .header(
                        securityProperties.getAuthHeader(),
                        jwtTokenizer.getAuthToken(
                            TEST_USER_COMPETITION_MANAGER_EMAIL,
                            List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                        ))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }


    //@Transactional
    @Test
    public void searchCompetitionList() throws Exception {
        CompetitionSearchDto competitionSearchDto = new CompetitionSearchDto("name test", LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now(),LocalDateTime.now());
        for (int i = 0; i < 30; i++) {
            competitionRepository.save(createCompetition());
        }

        MvcResult mvcResult = mockMvc.perform(get(COMPETITION_BASE_URI + "/search")
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .queryParam("begin", competitionSearchDto.getBeginDate().toString())
                .queryParam("end", competitionSearchDto.getEndDate().toString())
                .queryParam("beginRegistration", competitionSearchDto.getBeginRegistrationDate().toString())
                .queryParam("endRegistration", competitionSearchDto.getEndRegistrationDate().toString())
                .queryParam("name", competitionSearchDto.getName())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print()).andReturn();

        List<CompetitionListDto> response = Arrays.asList(objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CompetitionListDto[].class));
        List<Competition> allByBeginOfCompetitionAfterAndNameStartingWithAndDescriptionContainingIgnoreCase =
            competitionRepository.findAllByBeginOfCompetitionAfterAndEndOfCompetitionAfterAndBeginOfRegistrationAfterAndEndOfRegistrationAfterAndNameContainingIgnoreCaseAndIsPublicIsTrue(
                competitionSearchDto.getBeginDate(),
                competitionSearchDto.getEndDate(),
                competitionSearchDto.getBeginRegistrationDate(),
                competitionSearchDto.getEndRegistrationDate(),
                competitionSearchDto.getName());

        assertEquals(allByBeginOfCompetitionAfterAndNameStartingWithAndDescriptionContainingIgnoreCase.size(), response.size());
        List<Competition> compList = (List<Competition>) competitionRepository.findAll();
        assertEquals(compList.size(),30);
        assertEquals(30,allByBeginOfCompetitionAfterAndNameStartingWithAndDescriptionContainingIgnoreCase.size());
    }

    @Test
    public void searchCompetitionsAdvance() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(COMPETITION_BASE_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                .queryParam("name", "Haus")
                .queryParam("isPublic", "TRUE")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(print()).andReturn();
    }

    @Test
    public void testFindDetail_competitionNotFound_shouldThrowNotFoundException_404() throws Exception {
        setUpCompetitionUser();
        Long id = -1L;

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI + "/{id}/detail", id)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        TEST_USER_COMPETITION_MANAGER_EMAIL,
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void testFindDetail_validInput_shouldReturnCompetitionDetailDto_200() throws Exception {
        setUpCompetitionUser();

        CompetitionDetailDto expectedCompetition = getValidCompetitionDetailDto();
        expectedCompetition = competitionService.create(expectedCompetition);
        Long id = expectedCompetition.getId();

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI + "/{id}/detail", id)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        TEST_USER_COMPETITION_MANAGER_EMAIL,
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        CompetitionDetailDto actualCompetitionDetailDto = objectMapper.readerFor(CompetitionDetailDto.class)
            .readValue(response.getContentAsByteArray());

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedCompetition.getName(), actualCompetitionDetailDto.getName());
        assertEquals(expectedCompetition.getBeginOfCompetition(), actualCompetitionDetailDto.getBeginOfCompetition());
        assertEquals(expectedCompetition.getEndOfCompetition(), actualCompetitionDetailDto.getEndOfCompetition());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void testFindDetail_sessionUserDoesNotHaveEditingAccess_shouldThrowForbiddenException_403() throws Exception {
        setUpCompetitionUser();

        CompetitionDetailDto expectedCompetition = getValidCompetitionDetailDto();
        expectedCompetition = competitionService.create(expectedCompetition);
        Long id = expectedCompetition.getId();

        String email = "notcreator@notcreator.com";
        setUpCompetitionUserWithEMail(email);

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI + "/{id}/detail", id)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        email,
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void testFindDetail_unauthorizedUser_shouldThrowForbiddenException_403() throws Exception {
        setUpCompetitionUser();

        CompetitionDetailDto expectedCompetition = getValidCompetitionDetailDto();
        expectedCompetition = competitionService.create(expectedCompetition);
        Long id = expectedCompetition.getId();

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI + "/{id}/detail", id)
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    public Competition createCompetition() {
        compId++;
        return new Competition("name test" + compId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(compId),
            LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4),
            "test desc" + compId,
            null, true,
            false, null, null);
    }



    @Test
    public void getCurrentUserReportDownloadInclusionRuleOptions_withIncorrectCompetitionId_shouldThrowUnauthenticatedException() throws Exception {
        var compEntity = beforeEachReportTest();

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI
                + "/22222222222/report/download-inclusion-rule-options")
                    .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "participant3@report.test",
                        List.of("ROLE_" + ApplicationUser.Role.PARTICIPANT)
                    ))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value());

        assertTrue(response.getContentAsString().contains("No such competition"));
    }

    @Test
    public void getCurrentUserReportDownloadInclusionRuleOptions_asRegisteredParticipant_shouldReturnSelfTrueAndTeamFalse() throws Exception {
        var compEntity = beforeEachReportTest();

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI
                + "/{competitionId}/report/download-inclusion-rule-options", compEntity.getId())
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "participant3@report.test",
                        List.of("ROLE_" + ApplicationUser.Role.PARTICIPANT)
                    ))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertEquals(response.getStatus(), HttpStatus.OK.value());

        ReportDownloadInclusionRuleOptionsDto optionsDto = objectMapper.readerFor(ReportDownloadInclusionRuleOptionsDto.class)
            .readValue(response.getContentAsByteArray());

        assertFalse(optionsDto.getCanGenerateReportForTeam());
        assertTrue(optionsDto.getCanGenerateReportForSelf());
    }

    @Test
    public void getCurrentUserReportDownloadInclusionRuleOptions_asClubManagerWithManagedRegisteredParticipants_shouldReturnSelfFalseAndTeamTrue() throws Exception {
        var compEntity = beforeEachReportTest();

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI
                + "/{competitionId}/report/download-inclusion-rule-options", compEntity.getId())
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "club_manager2@report.test",
                        List.of("ROLE_" + ApplicationUser.Role.CLUB_MANAGER)
                    ))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertEquals(response.getStatus(), HttpStatus.OK.value());

        ReportDownloadInclusionRuleOptionsDto optionsDto = objectMapper.readerFor(ReportDownloadInclusionRuleOptionsDto.class)
            .readValue(response.getContentAsByteArray());

        assertTrue(optionsDto.getCanGenerateReportForTeam());
        assertFalse(optionsDto.getCanGenerateReportForSelf());
    }

    @Test
    public void getCurrentUserReportDownloadInclusionRuleOptions_asNonRegisteredParticipant_shouldReturnSelfFalseAndTeamFalse() throws Exception {
        var compEntity = beforeEachReportTest();

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI
                + "/{competitionId}/report/download-inclusion-rule-options", compEntity.getId())
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "participant5@report.test",
                        List.of("ROLE_" + ApplicationUser.Role.PARTICIPANT)
                    ))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertEquals(response.getStatus(), HttpStatus.OK.value());

        ReportDownloadInclusionRuleOptionsDto dto = objectMapper.readerFor(ReportDownloadInclusionRuleOptionsDto.class)
            .readValue(response.getContentAsByteArray());

        assertFalse(dto.getCanGenerateReportForSelf());
        assertFalse(dto.getCanGenerateReportForTeam());
    }

    @Test
    public void downloadExcelReport_whenReportsAreNotReady_shouldThrowConflictException() throws Exception {
        var objectMapper = new ObjectMapper();
        var compEntity = beforeEachReportTest();
        reportRepository.deleteAll();

        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_BASE_URI
                + "/{competitionId}/report/download", compEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "participant2@report.test",
                        List.of("ROLE_" + ApplicationUser.Role.PARTICIPANT)
                    ))
                .content(objectMapper.writeValueAsString(new ExcelReportGenerationRequestDto(
                    0L,
                    Set.of(),
                    ExcelReportGenerationRequestInclusionRule.ALL_PARTICIPANTS
                )))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertEquals(response.getStatus(), HttpStatus.CONFLICT.value());

        assertTrue(response.getContentAsString().contains("Reports are not downloadable yet"));
    }

    @Test
    public void downloadExcelReport_whithIncorrectCompetitionId_shouldThrowNotFoundException() throws Exception {
        var objectMapper = new ObjectMapper();
        var compEntity = beforeEachReportTest();
        reportRepository.deleteAll();

        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_BASE_URI
                + "/{competitionId}/report/download", 22222222L)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "participant2@report.test",
                        List.of("ROLE_" + ApplicationUser.Role.PARTICIPANT)
                    ))
                .content(objectMapper.writeValueAsString(new ExcelReportGenerationRequestDto(
                    0L,
                    Set.of(),
                    ExcelReportGenerationRequestInclusionRule.ALL_PARTICIPANTS
                )))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value());

        assertTrue(response.getContentAsString().contains("Such competition was not found"));
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    public void downloadExcelReport_shouldReturnValidFilename() throws Exception {
        var objectMapper = new ObjectMapper();
        var compEntity = beforeEachReportTest();

        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_BASE_URI
                + "/{competitionId}/report/download", compEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "participant2@report.test",
                        List.of("ROLE_" + ApplicationUser.Role.PARTICIPANT)
                    ))
                .content(objectMapper.writeValueAsString(new ExcelReportGenerationRequestDto(
                    0L,
                    compEntity.getGradingGroups().stream().map(GradingGroup::getId).collect(Collectors.toSet()),
                    ExcelReportGenerationRequestInclusionRule.ALL_PARTICIPANTS
                )))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertEquals(response.getStatus(), HttpStatus.OK.value());

        ExcelReportDownloadResponseDto responseDto = objectMapper.readerFor(ExcelReportDownloadResponseDto.class)
            .readValue(response.getContentAsString());

        String filenameWithNoFormat = compEntity.getName() + "__results__" + "all-participants";
        String filename = filenameWithNoFormat + ".xlsx";
        assertEquals(responseDto.getName(), compEntity.getName() + "__results__" + "all-participants.xlsx");

        assertEquals(reportFileRepository.findAll().get(0).getName(), filename);

        assertEquals(mvcResult.getResponse().getStatus(), HttpStatus.OK.value());
    }

    @Test
    public void calculateResultsOfCompetition_shouldGenerateReportsForAllGradingGroups() throws Exception {
        var objectMapper = new ObjectMapper();
        var compEntity = beforeEachReportTest();

        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_BASE_URI
                + "/{competitionId}/report", compEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "comp_manager1@report.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .content(objectMapper.writeValueAsString(new ExcelReportGenerationRequestDto(
                    0L,
                    compEntity.getGradingGroups().stream().map(GradingGroup::getId).collect(Collectors.toSet()),
                    ExcelReportGenerationRequestInclusionRule.ALL_PARTICIPANTS
                )))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertEquals(response.getStatus(), HttpStatus.OK.value());

        compEntity.getGradingGroups().forEach(
            gg -> {
                var foundReportOpt = reportRepository.findByGradingGroupIs(gg);
                assertTrue(foundReportOpt.isPresent());
            }
        );
    }

    @Test
    public void calculateResultsOfCompetition_asNonCompetitionManager_shouldThrowForbiddenException() throws Exception {
        var objectMapper = new ObjectMapper();
        var compEntity = beforeEachReportTest();

        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_BASE_URI
                + "/{competitionId}/report", compEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "participant1@report.test",
                        List.of("ROLE_" + ApplicationUser.Role.PARTICIPANT)
                    ))
                .content(objectMapper.writeValueAsString(new ExcelReportGenerationRequestDto(
                    0L,
                    compEntity.getGradingGroups().stream().map(GradingGroup::getId).collect(Collectors.toSet()),
                    ExcelReportGenerationRequestInclusionRule.ALL_PARTICIPANTS
                )))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertEquals(response.getStatus(), HttpStatus.FORBIDDEN.value());

        // TODO: FIX OR DELETE
        // assertTrue(response.getContentAsString().contains("No permissions to do this"));
    }


    @Test
    public void calculateResultsOfCompetition_asCompetitionManagerButNotCreator_shouldThrowForbiddenException() throws Exception {
        var objectMapper = new ObjectMapper();
        var compEntity = beforeEachReportTest();

        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_BASE_URI
                + "/{competitionId}/report", compEntity.getId())
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "comp_manager2@report.test",
                        List.of("ROLE_" + ApplicationUser.Role.PARTICIPANT)
                    ))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertEquals(response.getStatus(), HttpStatus.FORBIDDEN.value());

        // TODO: FIX OR DELETE
        // assertTrue(response.getContentAsString().contains("No permissions to do this"));
    }

    @Test
    public void checkIfReportsAreDownloadable_withIncorrectCompetitionId_shouldThrowNotFoundException() throws Exception {
        var compEntity = beforeEachReportTest();

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI
                + "/{competitionId}/report/downloadable", 2222222222L)
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "comp_manager1@report.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertEquals(response.getStatus(), HttpStatus.NOT_FOUND.value());

        assertTrue(response.getContentAsString().contains("Such competition was not found"));
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    public void checkIfReportsAreDownloadable_shouldReturnTrue() throws Exception {
        var compEntity = beforeEachReportTest();

        this.mockMvc.perform(post(COMPETITION_BASE_URI
                + "/{competitionId}/report", compEntity.getId())
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "comp_manager1@report.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_BASE_URI
                + "/{competitionId}/report/downloadable", compEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "comp_manager1@report.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertEquals(response.getStatus(), HttpStatus.OK.value());

        ReportIsDownloadableDto dto = objectMapper.readerFor(ReportIsDownloadableDto.class)
            .readValue(response.getContentAsByteArray());

        assertTrue(dto.isDownloadable());
    }
}
