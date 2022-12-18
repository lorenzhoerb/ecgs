package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import aj.org.objectweb.asm.TypeReference;
import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ErrorListRestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CompetitionEndpointTest extends TestDataProvider {

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
                        TEST_USER_TOURNAMENT_MANAGER_EMAIL,
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
                        TEST_USER_TOURNAMENT_MANAGER_EMAIL,
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
                        TEST_USER_TOURNAMENT_MANAGER_EMAIL,
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
                            TEST_USER_TOURNAMENT_MANAGER_EMAIL,
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
}
