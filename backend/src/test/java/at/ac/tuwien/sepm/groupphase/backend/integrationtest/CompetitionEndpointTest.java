package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder.CompetitionBuilder;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ErrorListRestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantResultDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.Judge;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.JudgeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.With;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.TestData.ADMIN_USER;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CompetitionEndpointTest extends TestDataProvider {
    static int compId = 0;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private GradingGroupRepository gradingGroupRepository;

    @Autowired
    private RegisterToRepository registerToRepository;

    @Autowired
    private JudgeRepository judgeRepository;

    @Autowired
    private GradingSystemRepository gradingSystemRepository;

    @Autowired
    private SecurityUserRepository securityUserRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void beforeEach() {
        competitionRepository.deleteAll();
        applicationUserRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        registerToRepository.deleteAll();
        gradingSystemRepository.deleteAll();
        securityUserRepository.deleteAll();
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

        Set<UserDetailDto> resp = objectMapper.readValue(response.getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(Set.class, UserDetailDto.class));

        assertNotNull(resp);
        assertEquals(resp.size(), 1);

        UserDetailDto participant = resp.iterator().next();
        assertEquals(participant.firstName(), "first");
        assertEquals(participant.lastName(), "last");
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

    /*
    //TODO delete oder fix test

    @Test
    @Transactional
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenValidJudgeForCompetitionGrading_thenSendGrades_ShouldSucceed() throws Exception {
        assertEquals(1, Stream.of(judgeRepository.findAll()).toList().size());
        setUpCompetitionUser();
        Competition competition = createCompetitionEntity(
            applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true,
            false
        );

        Optional<ApplicationUser> optApp = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL);
        ApplicationUser optional = null;
        if (optApp.isPresent()) {
            optional = optApp.get();
        }
        Competition competitionLoaded = competitionRepository.findById(competition.getId()).get();
        Judge toCreate = new Judge();
        toCreate.setCompetition(competitionLoaded);
        toCreate.setParticipant(optional);
        Judge createdJudge = judgeRepository.save(toCreate);
        List<ParticipantResultDto> resultDtoList = new ArrayList<>();
        List<RegisterTo> registerTo = registerToRepository.findByGradingGroupCompetitionId(competitionLoaded.getId());
        ParticipantResultDto resultDto = new ParticipantResultDto(registerTo.get(0).getParticipant().getId(),
            "[{\"gradingGroupId\":5,\"stations\":[{\"stationId\":1,\"variables\":[{\"variableId\":1,\"value\":\"321\"},{\"variableId\":2,\"value\":\"321\"}]}]}]");
        resultDtoList.add(resultDto);

        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_BASE_URI + "/" + competitionLoaded.getId() + "/group-registrations")
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        TEST_USER_COMPETITION_MANAGER_EMAIL,
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resultDtoList)))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(2, Stream.of(judgeRepository.findAll()).toList().size());
        createdJudge = judgeRepository.findById(createdJudge.getId()).get();
        assertEquals(resultDtoList.size(), createdJudge.getGrades().size());
    }
    */
}
