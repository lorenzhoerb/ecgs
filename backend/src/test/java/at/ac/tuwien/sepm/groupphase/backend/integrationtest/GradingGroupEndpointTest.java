package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ErrorListRestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingGroupService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
public class GradingGroupEndpointTest extends TestDataProvider {
    static int compId = 0;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private GradingSystemRepository gradingSystemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private GradingGroupService gradingGroupService;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void beforeEach() {
        applicationUserRepository.deleteAll();
        gradingSystemRepository.deleteAll();
    }

    @Test
    public void givenNotLoggedInUser_whenCreatingGradingSystem_expectForbidden403() throws Exception {
        GradingSystemDetailDto gradingSystemDetailDto = getValidGradingSystemDetailDto();
        String body = objectMapper.writeValueAsString(gradingSystemDetailDto);

        MvcResult mvcResult = this.mockMvc.perform(post(GRADING_GROUP_BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    public void givenNothing_whenPostInvalid_expectUnprocessableEntity422() throws Exception {
        setUpCompetitionUser();
        GradingSystemDetailDto gradingSystemDetailDto = new GradingSystemDetailDto(
            null, null, null, null, null
        );

        String body = objectMapper.writeValueAsString(gradingSystemDetailDto);

        MvcResult mvcResult = this.mockMvc.perform(post(GRADING_GROUP_BASE_URI)
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
    public void givenTwoTemplateGradingSystemsWithSameName_whenCreating_Conflict409() throws Exception {
        setUpCompetitionUser();
        GradingSystemDetailDto gradingSystemDetailDto = getValidGradingSystemDetailDto().withIsTemplate(true);
        String body = objectMapper.writeValueAsString(gradingSystemDetailDto);

        MvcResult mvcResult = this.mockMvc.perform(post(GRADING_GROUP_BASE_URI)
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
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());


        mvcResult = this.mockMvc.perform(post(GRADING_GROUP_BASE_URI)
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

        response = mvcResult.getResponse();
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());

    }

    @Test
    public void givenValidGradingSystem_whenCreating_201() throws Exception {
        setUpCompetitionUser();
        GradingSystemDetailDto gradingSystemDetailDto = getValidGradingSystemDetailDto();
        String body = objectMapper.writeValueAsString(gradingSystemDetailDto);

        MvcResult mvcResult = this.mockMvc.perform(post(GRADING_GROUP_BASE_URI)
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
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        GradingSystemDetailDto result = objectMapper.readerFor(GradingSystemDetailDto.class)
            .readValue(response.getContentAsByteArray());

        assertNotNull(result);
        assertEquals(gradingSystemDetailDto.name(), result.name());
        assertEquals(gradingSystemDetailDto.description(), result.description());
        assertEquals(gradingSystemDetailDto.isTemplate(), result.isTemplate());
        assertEquals(gradingSystemDetailDto.isPublic(), result.isPublic());
        assertEquals(gradingSystemDetailDto.formula(), result.formula());

    }
}
