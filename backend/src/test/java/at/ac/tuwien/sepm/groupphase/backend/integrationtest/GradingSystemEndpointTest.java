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
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ViewEditGradingSystemDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingSystemMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.help.GradingSystemProjectIdAndNameAndIsPublicAndEditableAsClass;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.projections.GradingSystemProjectIdAndNameAndIsPublicAndEditable;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingGroupService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.TestData.ADMIN_ROLES;
import static at.ac.tuwien.sepm.groupphase.backend.integrationtest.TestData.ADMIN_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class GradingSystemEndpointTest extends TestDataProvider {
    static int compId = 0;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private GradingSystemMapper gradingSystemMapper;

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

    @Test
    public void getDraftGradingSystemById_withValidId_expectToFindCorrectGradingSystem() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var gs_test_mapped = gradingSystemMapper.gradingSystemToViewEditGradingSystemDto(gs_test);
        var user = userService.findApplicationUserByEmail("gs_test_1@test.test");

        MvcResult mvcResult = this.mockMvc.perform(get(String.format("%s/drafts/%d", GRADING_GROUP_BASE_URI, gs_test.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        user.getUser().getEmail(),
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    )))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        ViewEditGradingSystemDto result = objectMapper.readerFor(ViewEditGradingSystemDto.class)
            .readValue(response.getContentAsByteArray());

        assertThat(result).isEqualTo(gs_test_mapped);
    }

    @Test
    public void getDraftGradingSystemById_withIncorrectId_expectToFindIncorrectGradingSystem() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var gs_test_mapped = gradingSystemMapper.gradingSystemToViewEditGradingSystemDto(gs_test);
        var user = userService.findApplicationUserByEmail("gs_test_1@test.test");

        MvcResult mvcResult = this.mockMvc.perform(get(String.format("%s/drafts/%d", GRADING_GROUP_BASE_URI, gss.get(1).getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        user.getUser().getEmail(),
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    )))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        ViewEditGradingSystemDto result = objectMapper.readerFor(ViewEditGradingSystemDto.class)
            .readValue(response.getContentAsByteArray());

        assertThat(result).isNotEqualTo(gs_test_mapped);
    }

    @Test
    public void getDraftGradingSystemById_withInvalidId_expectToGetNotFoundException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var gs_test_mapped = gradingSystemMapper.gradingSystemToViewEditGradingSystemDto(gs_test);
        var user = userService.findApplicationUserByEmail("gs_test_1@test.test");

        MvcResult mvcResult = this.mockMvc.perform(get(String.format("%s/drafts/%d", GRADING_GROUP_BASE_URI, 3333333L))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        user.getUser().getEmail(),
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    )))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

        assertThat(response.getContentAsString()).contains("No such grading system found");
    }

    @Test
    public void getDraftGradingSystemById_withValidIdButOfDifferentUserWhenNonPublicAndIsDraft_expectToGetNotFoundException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(1);

        MvcResult mvcResult = this.mockMvc.perform(get(String.format("%s/drafts/%d", GRADING_GROUP_BASE_URI, gs_test.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_2@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    )))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

        assertThat(response.getContentAsString()).contains("No such grading system found");
    }

    @Test
    public void getDraftGradingSystemById_withValidIdButOfDifferentUserWhenPublicAndIsDraft_expectToGetCorrectGradingSystem() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var gs_test_mapped = gradingSystemMapper.gradingSystemToViewEditGradingSystemDto(gs_test);

        MvcResult mvcResult = this.mockMvc.perform(get(String.format("%s/drafts/%d", GRADING_GROUP_BASE_URI, gs_test.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_2@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    )))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        ViewEditGradingSystemDto result = objectMapper.readerFor(ViewEditGradingSystemDto.class)
            .readValue(response.getContentAsByteArray());

        assertThat(result).isEqualTo(gs_test_mapped);
    }

    @Test
    public void getSimpleDraftGradingSystem_expectToGetCorrectGradingSystems() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var gs_test_mapped = gradingSystemMapper.gradingSystemToViewEditGradingSystemDto(gs_test);

        MvcResult mvcResult = this.mockMvc.perform(get(String.format("%s/drafts/simple", GRADING_GROUP_BASE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    )))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        var found = objectMapper.readValue(response.getContentAsString(), GradingSystemProjectIdAndNameAndIsPublicAndEditableAsClass[].class);
        assertThat(found.length).isEqualTo(3);
    }

    @Test
    public void updateDraftGradingSystem_expectToGetCorrectGradingSystems() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var testVEGS = new ViewEditGradingSystemDto(
            gs_test.getId(),
            "CHANGED_NAME1",
            "CHANGED_DESC1",
            !gs_test.getPublic(),
            "{KEK:\"KEK\"}"
        );

        var testVEGSAsJson = objectMapper.writeValueAsString(testVEGS);
        MvcResult mvcResult = this.mockMvc.perform(put(String.format("%s/drafts", GRADING_GROUP_BASE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testVEGSAsJson))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

        ViewEditGradingSystemDto found = objectMapper.readValue(response.getContentAsString(), ViewEditGradingSystemDto.class);
        assertThat(found).isEqualTo(testVEGS);
    }

    @Test
    public void updateDraftGradingSystem_withTooLongName_expectToThrowValidationException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var testVEGS = new ViewEditGradingSystemDto(
            gs_test.getId(),
            "C".repeat(256),
            "CHANGED_DESC1",
            !gs_test.getPublic(),
            "{KEK:\"KEK\"}"
        );

        var testVEGSAsJson = objectMapper.writeValueAsString(testVEGS);
        MvcResult mvcResult = this.mockMvc.perform(put(String.format("%s/drafts", GRADING_GROUP_BASE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testVEGSAsJson))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        ErrorListRestDto errorListRestDto = objectMapper.readValue(response.getContentAsString(),
            ErrorListRestDto.class);

        assertThat(errorListRestDto.errors()).contains("name to long");
    }

    @Test
    public void updateDraftGradingSystem_withEmptyName_expectToThrowValidationException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var testVEGS = new ViewEditGradingSystemDto(
            gs_test.getId(),
            "",
            "CHANGED_DESC1",
            !gs_test.getPublic(),
            "{KEK:\"KEK\"}"
        );

        var testVEGSAsJson = objectMapper.writeValueAsString(testVEGS);
        var mvcResult = this.mockMvc.perform(put(String.format("%s/drafts", GRADING_GROUP_BASE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testVEGSAsJson))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        var errorListRestDto = objectMapper.readValue(response.getContentAsString(),
            ErrorListRestDto.class);

        assertThat(errorListRestDto.errors()).contains("name can't be empty");
    }

    @Test
    public void updateDraftGradingSystem_withNullName_expectToThrowValidationException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var testVEGS = new ViewEditGradingSystemDto(
            gs_test.getId(),
            null,
            "CHANGED_DESC1",
            !gs_test.getPublic(),
            "{KEK:\"KEK\"}"
        );

        var testVEGSAsJson = objectMapper.writeValueAsString(testVEGS);
        var mvcResult = this.mockMvc.perform(put(String.format("%s/drafts", GRADING_GROUP_BASE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testVEGSAsJson))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        var errorListRestDto = objectMapper.readValue(response.getContentAsString(),
            ErrorListRestDto.class);

        assertThat(errorListRestDto.errors()).contains("name can't be empty");
    }

    @Test
    public void updateDraftGradingSystem_withTooLongDescription_expectToThrowValidationException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var testVEGS = new ViewEditGradingSystemDto(
            gs_test.getId(),
            "CHANGED_NAME1",
            "C".repeat(4096),
            !gs_test.getPublic(),
            "{KEK:\"KEK\"}"
        );

        var testVEGSAsJson = objectMapper.writeValueAsString(testVEGS);
        MvcResult mvcResult = this.mockMvc.perform(put(String.format("%s/drafts", GRADING_GROUP_BASE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testVEGSAsJson))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        ErrorListRestDto errorListRestDto = objectMapper.readValue(response.getContentAsString(),
            ErrorListRestDto.class);

        assertThat(errorListRestDto.errors()).contains("description to long");
    }

    @Test
    public void updateDraftGradingSystem_withEmptyDescription_expectToThrowValidationException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var testVEGS = new ViewEditGradingSystemDto(
            gs_test.getId(),
            "CHANGED_DESC1",
            "",
            !gs_test.getPublic(),
            "{KEK:\"KEK\"}"
        );

        var testVEGSAsJson = objectMapper.writeValueAsString(testVEGS);
        var mvcResult = this.mockMvc.perform(put(String.format("%s/drafts", GRADING_GROUP_BASE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testVEGSAsJson))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        var errorListRestDto = objectMapper.readValue(response.getContentAsString(),
            ErrorListRestDto.class);

        assertThat(errorListRestDto.errors()).contains("description can't be empty");
    }

    @Test
    public void updateDraftGradingSystem_withNullDescription_expectToThrowValidationException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var testVEGS = new ViewEditGradingSystemDto(
            gs_test.getId(),
            "CHANGED_DESC1",
            null,
            !gs_test.getPublic(),
            "{KEK:\"KEK\"}"
        );

        var testVEGSAsJson = objectMapper.writeValueAsString(testVEGS);
        var mvcResult = this.mockMvc.perform(put(String.format("%s/drafts", GRADING_GROUP_BASE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testVEGSAsJson))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        var errorListRestDto = objectMapper.readValue(response.getContentAsString(),
            ErrorListRestDto.class);

        assertThat(errorListRestDto.errors()).contains("description can't be empty");
    }

    @Test
    public void updateDraftGradingSystem_withNullIsPublic_expectToThrowValidationException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var testVEGS = new ViewEditGradingSystemDto(
            gs_test.getId(),
            "CHANGED_NAME1",
            "CHANGED_DESC1",
            null,
            "{KEK:\"KEK\"}"
        );

        var testVEGSAsJson = objectMapper.writeValueAsString(testVEGS);
        var mvcResult = this.mockMvc.perform(put(String.format("%s/drafts", GRADING_GROUP_BASE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testVEGSAsJson))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        var errorListRestDto = objectMapper.readValue(response.getContentAsString(),
            ErrorListRestDto.class);

        assertThat(errorListRestDto.errors()).contains("isPublic must be given");
    }

    @Test
    public void updateDraftGradingSystem_withTooLongFormula_expectToThrowValidationException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var testVEGS = new ViewEditGradingSystemDto(
            gs_test.getId(),
            "CHANGED_NAME1",
            "CHANGED_DESC1",
            !gs_test.getPublic(),
            "f".repeat(65536)
        );

        var testVEGSAsJson = objectMapper.writeValueAsString(testVEGS);
        MvcResult mvcResult = this.mockMvc.perform(put(String.format("%s/drafts", GRADING_GROUP_BASE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testVEGSAsJson))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        ErrorListRestDto errorListRestDto = objectMapper.readValue(response.getContentAsString(),
            ErrorListRestDto.class);

        assertThat(errorListRestDto.errors()).contains("formula to long");
    }

    @Test
    public void updateDraftGradingSystem_withNullFormula_expectToThrowValidationException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var testVEGS = new ViewEditGradingSystemDto(
            gs_test.getId(),
            "CHANGED_NAME1",
            "CHANGED_DESC1",
            !gs_test.getPublic(),
            null
        );

        var testVEGSAsJson = objectMapper.writeValueAsString(testVEGS);
        var mvcResult = this.mockMvc.perform(put(String.format("%s/drafts", GRADING_GROUP_BASE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testVEGSAsJson))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());

        var errorListRestDto = objectMapper.readValue(response.getContentAsString(),
            ErrorListRestDto.class);

        assertThat(errorListRestDto.errors()).contains("formula must be given");
    }

    @Test
    public void updateDraftGradingSystem_withIdBelongingToOtherUserAndIsPrivateTemplate_expectToThrowNotFoundException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(5);
        var testVEGS = new ViewEditGradingSystemDto(
            gs_test.getId(),
            "CHANGED_NAME1",
            "CHANGED_DESC1",
            !gs_test.getPublic(),
            "{}"
        );

        var testVEGSAsJson = objectMapper.writeValueAsString(testVEGS);
        var mvcResult = this.mockMvc.perform(put(String.format("%s/drafts", GRADING_GROUP_BASE_URI))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    ))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testVEGSAsJson))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());

        assertThat(response.getContentAsString()).contains("No such grading system found");
    }

    @Test
    public void deleteDraftGradingSystem_withCorrectId_expectToSucceed() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);

        var mvcResult = this.mockMvc.perform(delete(String.format("%s/drafts/%d", GRADING_GROUP_BASE_URI, gs_test.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    )))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void deleteDraftGradingSystem_withIncorrectId_expectToThrowNotFoundException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(5);

        var mvcResult = this.mockMvc.perform(delete(String.format("%s/drafts/%d", GRADING_GROUP_BASE_URI, gs_test.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    )))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("No such grading system found");
    }

    @Test
    public void deleteDraftGradingSystem_withInvalidId_expectToThrowNotFoundException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(5);

        var mvcResult = this.mockMvc.perform(delete(String.format("%s/drafts/%d", GRADING_GROUP_BASE_URI, 222222222))
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        "gs_test_1@test.test",
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    )))
            .andDo(print())
            .andReturn();

        var response = mvcResult.getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).contains("No such grading system found");
    }
}
