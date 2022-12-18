package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ErrorListRestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.TestData.ADMIN_USER;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @BeforeEach
    public void beforeEach() {
        competitionRepository.deleteAll();
        applicationUserRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        registerToRepository.deleteAll();
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


    public Competition createCompetition() {
        compId++;
        return new Competition("name test" + compId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(compId),
            LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4),
            "test desc" + compId,
            null, true,
            false, null, null);
    }
}
