package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.BasicRegisterConstraintDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterConstraint;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterConstraintRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingGroupService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class GradingGroupEndpointTest extends TestDataProvider {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private GradingGroupRepository gradingGroupRepository;

    @Autowired
    private RegisterConstraintRepository registerConstraintRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private SessionUtils sessionUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GradingGroupService gradingGroupService;

    @Autowired
    private SecurityUserRepository securityUserRepository;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @BeforeEach
    public void beforeEach() {
        competitionRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        registerConstraintRepository.deleteAll();
        applicationUserRepository.deleteAll();
        securityUserRepository.deleteAll();
        setUpCompetitionUser();
    }

    @Test
    public void getGradingGroupDetails_expect200() throws Exception {
        ApplicationUser a = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");
        RegisterConstraint rc = new RegisterConstraint();

        rc.setGradingGroup(g1);
        rc.setType(RegisterConstraint.ConstraintType.AGE);
        rc.setOperator(RegisterConstraint.Operator.EQUALS);
        rc.setConstraintValue("1");

        c.setCreator(a);
        c.setGradingGroups(Set.of(g1));

        g1.setCompetitions(c);
        g1.setRegisterConstraints(List.of(rc));

        competitionRepository.save(c);
        GradingGroup savedG = gradingGroupRepository.save(g1);

        MvcResult mvcResult = this.mockMvc.perform(get(GRADING_GROUPS_BASE_URI + "/{id}",
                savedG.getId())
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

        DetailedGradingGroupDto resp = objectMapper.readValue(response.getContentAsString(),
            DetailedGradingGroupDto.class);
        assertEquals(g1.getId(), resp.getId());
        assertEquals(g1.getTitle(), resp.getTitle());
    }

    @Test
    public void getGradingGroupDetails_withNotExistingGradingGroup_expect404() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(GRADING_GROUPS_BASE_URI + "/{id}",
                1L)
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
    public void getGradingGroupDetails_whenNotAuthenticated_expect403() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(GRADING_GROUPS_BASE_URI + "/{id}",
                1L))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    public void setGradingGroupRegisterConstraint_expect_201() throws Exception {
        ApplicationUser a = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");

        RegisterConstraint rc = new RegisterConstraint();
        rc.setGradingGroup(g1);
        rc.setType(RegisterConstraint.ConstraintType.AGE);
        rc.setOperator(RegisterConstraint.Operator.EQUALS);
        rc.setConstraintValue("1");

        c.setCreator(a);
        c.setGradingGroups(Set.of(g1));

        g1.setCompetitions(c);
        g1.setRegisterConstraints(List.of(rc));
        competitionRepository.save(c);
        GradingGroup savedG = gradingGroupRepository.save(g1);

        List<BasicRegisterConstraintDto> constraints = List.of(
            BasicRegisterConstraintDto.builder()
                .operator(RegisterConstraint.Operator.EQUALS)
                .type(RegisterConstraint.ConstraintType.AGE)
                .value("1")
                .build()
        );

        String body = objectMapper.writeValueAsString(constraints);

        MvcResult mvcResult = this.mockMvc.perform(post(GRADING_GROUPS_BASE_URI + "/{id}/constraints",
                savedG.getId())
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        TEST_USER_COMPETITION_MANAGER_EMAIL,
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    public void setGradingGroupConstraints_whenNotAuthenticated_expect403() throws Exception {
        List<BasicRegisterConstraintDto> constraints = List.of(
            BasicRegisterConstraintDto.builder()
                .operator(RegisterConstraint.Operator.EQUALS)
                .type(RegisterConstraint.ConstraintType.AGE)
                .value("1")
                .build()
        );

        String body = objectMapper.writeValueAsString(constraints);

        MvcResult mvcResult = this.mockMvc.perform(post(GRADING_GROUPS_BASE_URI + "/{id}/constraints",
                1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
            )
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    public void setGradingGroupConstraints_forNonExistingGradingGroup_expect404() throws Exception {
        List<BasicRegisterConstraintDto> constraints = List.of(
            BasicRegisterConstraintDto.builder()
                .operator(RegisterConstraint.Operator.EQUALS)
                .type(RegisterConstraint.ConstraintType.AGE)
                .value("1")
                .build()
        );

        String body = objectMapper.writeValueAsString(constraints);

        MvcResult mvcResult = this.mockMvc.perform(post(GRADING_GROUPS_BASE_URI + "/{id}/constraints",
                1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
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
}
