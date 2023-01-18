package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ErrorListRestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ViewEditGradingSystemDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.GradingSystemMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.StrategyException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Add;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Constant;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Divide;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Mean;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Multiply;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Operation;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.Sub;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.operations.VariableRef;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Equal;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.GradingSystem;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Station;
import at.ac.tuwien.sepm.groupphase.backend.gradingsystem.structural.Variable;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.GradingSystemService;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ActiveProfiles("test")
public class GradingSystemServiceTest extends TestDataProvider {
    @Autowired
    GradingSystemRepository gradingSystemRepository;

    @Autowired
    GradingGroupRepository gradingGroupRepository;

    @Autowired
    GradingSystemService gradingSystemService;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private GradingSystemMapper gradingSystemMapper;

    @Autowired
    private UserService userService;

    private ApplicationUser user;

    @BeforeEach
    public void beforeEach() {
        gradingSystemRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        applicationUserRepository.deleteAll();
        user = setUpCompetitionUser();
    }

    @Test
    public void addOpTest() throws JsonProcessingException {
        String json = getSingleOp2Formula(new Add(new VariableRef(1L), new VariableRef(2L)));
        GradingSystem result = new GradingSystem(json);
        result.bindVariable(1L, 1L, 2.0);
        result.bindVariable(1L, 2L, 7.0);
        assertEquals(result.evaluate(), 9.0, 1e-5);
    }

    @Test
    public void subOpTest() throws JsonProcessingException {
        String json = getSingleOp2Formula(new Sub(new VariableRef(1L), new VariableRef(2L)));
        GradingSystem result = new GradingSystem(json);
        result.bindVariable(1L, 1L, 2.0);
        result.bindVariable(1L, 2L, 7.0);
        assertEquals(result.evaluate(), -5.0, 1e-5);
    }

    @Test
    public void constantOpTest() throws JsonProcessingException {
        String json = getSingleOp0Formula(new Constant(4.0));
        GradingSystem result = new GradingSystem(json);
        assertEquals(result.evaluate(), 4.0, 1e-5);
    }

    @Test
    public void divideOpTest() throws JsonProcessingException {
        String json = getSingleOp2Formula(new Divide(new VariableRef(1L),
                                                     new VariableRef(2L)));
        GradingSystem result = new GradingSystem(json);
        result.bindVariable(1L, 1L, 2.0);
        result.bindVariable(1L, 2L, 7.0);
        assertEquals(result.evaluate(), 2.0/7.0, 1e-5);
    }

    @Test
    public void multiplyOpTest() throws JsonProcessingException {
        String json = getSingleOp2Formula(new Multiply(new VariableRef(1L),
            new VariableRef(2L)));
        GradingSystem result = new GradingSystem(json);
        result.bindVariable(1L, 1L, 2.0);
        result.bindVariable(1L, 2L, 7.0);
        assertEquals(result.evaluate(), 14.0, 1e-5);
    }

    @Test
    public void meanOpTest() throws JsonProcessingException {
        String json = getSingleOp2Formula(new Mean(new Operation[] {
            new VariableRef(1L),
            new VariableRef(2L)
        }));
        GradingSystem result = new GradingSystem(json);
        result.bindVariable(1L, 1L, 2.0);
        result.bindVariable(1L, 2L, 7.0);
        assertEquals(result.evaluate(), (2.0+7.0)/2.0, 1e-5);
    }

    @Test
    public void variableRefOpTest() throws JsonProcessingException {
        String json = getSingleOp2Formula(new VariableRef(1L));
        GradingSystem result = new GradingSystem(json);
        result.bindVariable(1L, 1L, 2.0);
        result.bindVariable(1L, 2L, 7.0);
        assertEquals(result.evaluate(), 2.0, 1e-5);
    }

    @Test
    public void EqualStrategyTest() throws JsonProcessingException {
        String json = getSingleStrategyFormula(new Equal(), 2L);
        GradingSystem result = new GradingSystem(json);

        result.bindVariable(1L, 1L, 2.0);
        assertThrows(StrategyException.class, result::evaluate);

        result.bindVariable(1L, 1L, 2.0);
        assertEquals(result.evaluate(), 2.0, 1e-5);

        result.bindVariable(1L, 1L, 3.0);
        assertThrows(StrategyException.class, result::evaluate);
    }

    @Test
    public void MeanStrategyTest() throws JsonProcessingException {
        String json = getSingleStrategyFormula(
            new at.ac.tuwien.sepm.groupphase.backend.gradingsystem.strategys.Mean(),
            2L);
        GradingSystem result = new GradingSystem(json);

        result.bindVariable(1L, 1L, 2.0);
        assertThrows(StrategyException.class, result::evaluate);

        result.bindVariable(1L, 1L, 4.0);
        assertEquals(result.evaluate(), 3.0, 1e-5);
    }

    @Test
    public void defaultParserTest() throws JsonProcessingException {
        String json = getGradingSystemFormula();
        GradingSystem result = new GradingSystem(json);
        result.bindVariable(1L, 1L, 2.0);
        result.bindVariable(1L, 1L, 4.0);
        result.bindVariable(1L, 2L, 7.0);
        result.bindVariable(2L, 1L, 7.0);
        result.bindVariable(2L, 1L, 8.0);
        result.bindVariable(2L, 1L, 9.0);
        assertEquals(result.evaluate(), 14.0, 1e-5);
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenValidGradingSystem_createGradingSystem() throws JsonProcessingException  {
        GradingSystemDetailDto gradingSystem = getValidGradingSystemDetailDto().withIsTemplate(true);
        GradingSystemDetailDto result =
            gradingSystemService.createGradingSystem(gradingSystem);
        assertNotNull(result);
        assertNotNull(result.name());
        Optional<at.ac.tuwien.sepm.groupphase.backend.entity.GradingSystem>
            entity =  gradingSystemRepository.findFirstByNameAndCreatorAndIsTemplateIsTrue(gradingSystem.name(), user);

        assertTrue(entity.isPresent());
        assertEquals(user.getId(), entity.get().getCreator().getId());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenInvalidNameToLong() throws JsonProcessingException {
        GradingSystemDetailDto gradingSystemDetailDto1 = new GradingSystemDetailDto(
            "A".repeat(257),
            "desc",
            true,
            false,
            getGradingSystemFormula()
        );

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            gradingSystemService.createGradingSystem(gradingSystemDetailDto1);
        });
        assertEquals(e.errors().get(0), "name to long");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenInvalidNameNull() throws JsonProcessingException {
        GradingSystemDetailDto gradingSystemDetailDto1 = new GradingSystemDetailDto(
            null,
            "desc",
            true,
            false,
            getGradingSystemFormula()
        );

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            gradingSystemService.createGradingSystem(gradingSystemDetailDto1);
        });
        assertEquals(e.errors().get(0), "name can't be empty");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenInvalidNameEmpty() throws JsonProcessingException {
        GradingSystemDetailDto gradingSystemDetailDto1 = new GradingSystemDetailDto(
            "",
            "desc",
            true,
            false,
            getGradingSystemFormula()
        );

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            gradingSystemService.createGradingSystem(gradingSystemDetailDto1);
        });
        assertEquals(e.errors().get(0), "name can't be empty");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenInvalidDescriptionToLong() throws JsonProcessingException {
        GradingSystemDetailDto gradingSystemDetailDto2 = new GradingSystemDetailDto(
            "name",
            "A".repeat(4097),
            true,
            false,
            getGradingSystemFormula()
        );

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            gradingSystemService.createGradingSystem(gradingSystemDetailDto2);
        });
        assertEquals(e.errors().get(0), "description to long");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenInvalidDescriptionNull() throws JsonProcessingException {
        GradingSystemDetailDto gradingSystemDetailDto2 = new GradingSystemDetailDto(
            "name",
            null,
            true,
            false,
            getGradingSystemFormula()
        );

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            gradingSystemService.createGradingSystem(gradingSystemDetailDto2);
        });
        assertEquals(e.errors().get(0), "description can't be empty");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenInvalidDescriptionEmpty() throws JsonProcessingException {
        GradingSystemDetailDto gradingSystemDetailDto2 = new GradingSystemDetailDto(
            "name",
            "",
            true,
            false,
            getGradingSystemFormula()
        );

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            gradingSystemService.createGradingSystem(gradingSystemDetailDto2);
        });
        assertEquals(e.errors().get(0), "description can't be empty");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenInvalidFormulaToLong() {
        GradingSystemDetailDto gradingSystemDetailDto2 = new GradingSystemDetailDto(
            "name",
            "desc",
            true,
            false,
            "A".repeat(65537)
        );

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            gradingSystemService.createGradingSystem(gradingSystemDetailDto2);
        });
        assertEquals(e.errors().get(0), "formula to long");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenInvalidFormulaEmpty() {
        GradingSystemDetailDto gradingSystemDetailDto2 = new GradingSystemDetailDto(
            "name",
            "desc",
            true,
            false,
            ""
        );

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            gradingSystemService.createGradingSystem(gradingSystemDetailDto2);
        });
        assertEquals(e.errors().get(0), "parsing formula");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenInvalidFormulaNull() {
        GradingSystemDetailDto gradingSystemDetailDto2 = new GradingSystemDetailDto(
            "name",
            "description",
            true,
           false,
           null
        );

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            gradingSystemService.createGradingSystem(gradingSystemDetailDto2);
        });
        assertEquals(e.errors().get(0), "formula must be given");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenInvalidFormulaIdsNotUniqueOnStationVars() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        GradingSystem system = new GradingSystem();
        system.stations = new Station[] {
            new Station(1L, "Station", new Variable[]{
                new Variable(1L, "Var1", 0L, new Equal()),
                new Variable(1L, "Var1", 0L, new Equal()),
            }, new VariableRef(1L))
        };
        system.formula = new VariableRef(1L);

        GradingSystemDetailDto gradingSystemDetailDto2 = new GradingSystemDetailDto(
            "name",
            "description",
            true,
            false,
            mapper.writeValueAsString(system)
        );

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            gradingSystemService.createGradingSystem(gradingSystemDetailDto2);
        });
        assertEquals(e.errors().get(0), "Station Station has a duplicate variable id");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenInvalidFormulaIdsNotUniqueOnStations() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        GradingSystem system = new GradingSystem();
        system.stations = new Station[] {
            new Station(1L, "Station", new Variable[]{
                new Variable(1L, "Var1", 0L, new Equal()),
            }, new VariableRef(1L)),
            new Station(1L, "Station", new Variable[]{
                new Variable(1L, "Var1", 0L, new Equal()),
            }, new VariableRef(1L))
        };
        system.formula = new VariableRef(1L);

        GradingSystemDetailDto gradingSystemDetailDto2 = new GradingSystemDetailDto(
            "name",
            "description",
            true,
            false,
            mapper.writeValueAsString(system)
        );

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            gradingSystemService.createGradingSystem(gradingSystemDetailDto2);
        });
        assertEquals(e.errors().get(0), "GradingSystem has a duplicate station id");
    }

    @Test
    public void givenNotLoggedInUser_thenForbiddenException() {
        assertThrows(ForbiddenException.class, () -> {
            gradingSystemService.createGradingSystem(getValidGradingSystemDetailDto());
        });
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
    public void getDraftGradingSystemById_withValidId_expectToFindCorrectGradingSystem() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var gs_test_mapped = gradingSystemMapper.gradingSystemToViewEditGradingSystemDto(gs_test);
        var user = userService.findApplicationUserByEmail("gs_test_1@test.test");

        var found = this.gradingSystemService.getDraftGradingSystemById(gs_test.getId());
        assertThat(found).isEqualTo(gs_test_mapped);
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
    public void getDraftGradingSystemById_withIncorrectId_expectToFindIncorrectGradingSystem() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var gs_test_mapped = gradingSystemMapper.gradingSystemToViewEditGradingSystemDto(gs_test);
        var user = userService.findApplicationUserByEmail("gs_test_1@test.test");

        var found = this.gradingSystemService.getDraftGradingSystemById(gss.get(1).getId());
        assertThat(found).isNotEqualTo(gs_test_mapped);
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
    public void getDraftGradingSystemById_withInvalidId_expectToGetNotFoundException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var gs_test_mapped = gradingSystemMapper.gradingSystemToViewEditGradingSystemDto(gs_test);
        var user = userService.findApplicationUserByEmail("gs_test_1@test.test");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            gradingSystemService.getDraftGradingSystemById(-123L);
        });

        assertThat(exception.getMessage()).contains("No such grading system found");
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
    public void getDraftGradingSystemById_withValidIdButOfDifferentUserWhenNonPublicAndIsDraft_expectToGetNotFoundException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var gs_test_mapped = gradingSystemMapper.gradingSystemToViewEditGradingSystemDto(gs_test);
        var user = userService.findApplicationUserByEmail("gs_test_1@test.test");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            gradingSystemService.getDraftGradingSystemById(gss.get(5).getId());
        });

        assertThat(exception.getMessage()).contains("No such grading system found");
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
    public void getDraftGradingSystemById_withValidIdButOfDifferentUserWhenPublicAndIsDraft_expectToGetCorrectGradingSystem() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(4);
        var gs_test_mapped = gradingSystemMapper.gradingSystemToViewEditGradingSystemDto(gs_test);
        var user = userService.findApplicationUserByEmail("gs_test_1@test.test");

        var found = this.gradingSystemService.getDraftGradingSystemById(gs_test.getId());
        assertThat(found).isEqualTo(gs_test_mapped);
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
    public void getSimpleDraftGradingSystemById_expectToGetCorrectGradingSystems() throws Exception {
        var gss = setupGradingSystems();
        var user = userService.findApplicationUserByEmail("gs_test_1@test.test");

        var found = this.gradingSystemService.getSimpleDraftGradingSystem();
        assertThat(found.size()).isEqualTo(3);

        assertThat(found.get(0).getId()).isEqualTo(gss.get(0).getId());
        assertThat(found.get(0).getName()).isEqualTo(gss.get(0).getName());
        assertThat(found.get(0).getPublic()).isEqualTo(gss.get(0).getPublic());
        assertThat(found.get(0).getEditable()).isEqualTo(Objects.equals(gss.get(0).getCreator().getId(), user.getId()));

        assertThat(found.get(1).getId()).isEqualTo(gss.get(1).getId());
        assertThat(found.get(1).getName()).isEqualTo(gss.get(1).getName());
        assertThat(found.get(1).getPublic()).isEqualTo(gss.get(1).getPublic());
        assertThat(found.get(1).getEditable()).isEqualTo(Objects.equals(gss.get(1).getCreator().getId(), user.getId()));

        assertThat(found.get(2).getId()).isEqualTo(gss.get(4).getId());
        assertThat(found.get(2).getName()).isEqualTo(gss.get(4).getName());
        assertThat(found.get(2).getPublic()).isEqualTo(gss.get(4).getPublic());
        assertThat(found.get(2).getEditable()).isEqualTo(Objects.equals(gss.get(4).getCreator().getId(), user.getId()));
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
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

        var found = this.gradingSystemService.updateDraftGradingSystem(testVEGS);
        assertThat(found).isEqualTo(testVEGS);
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
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
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            this.gradingSystemService.updateDraftGradingSystem(testVEGS);
        });

        assertThat(exception.errors()).contains("name to long");
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
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

        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            this.gradingSystemService.updateDraftGradingSystem(testVEGS);
        });

        assertThat(exception.errors()).contains("name can't be empty");
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
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

        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            this.gradingSystemService.updateDraftGradingSystem(testVEGS);
        });

        assertThat(exception.errors()).contains("name can't be empty");
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
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

        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            this.gradingSystemService.updateDraftGradingSystem(testVEGS);
        });

        assertThat(exception.errors()).contains("description to long");
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
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
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            this.gradingSystemService.updateDraftGradingSystem(testVEGS);
        });

        assertThat(exception.errors()).contains("description can't be empty");
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
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

        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            this.gradingSystemService.updateDraftGradingSystem(testVEGS);
        });

        assertThat(exception.errors()).contains("description can't be empty");
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
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

        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            this.gradingSystemService.updateDraftGradingSystem(testVEGS);
        });

        assertThat(exception.errors()).contains("isPublic must be given");
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
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

        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            this.gradingSystemService.updateDraftGradingSystem(testVEGS);
        });


        assertThat(exception.errors()).contains("formula to long");
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
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

        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            this.gradingSystemService.updateDraftGradingSystem(testVEGS);
        });

        assertThat(exception.errors()).contains("formula must be given");
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
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

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            this.gradingSystemService.updateDraftGradingSystem(testVEGS);
        });

        assertThat(exception.getMessage()).contains("No such grading system found");
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
    public void deleteDraftGradingSystem_withCorrectId_expectToSucceed() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(0);
        var toDeleteOpt = gradingSystemRepository.findById(gs_test.getId());
        if (toDeleteOpt.isEmpty()) {
            fail("Did not find grading system, that should have been initialized beforehand");
        }
        assertThat(toDeleteOpt.get().getId()).isEqualTo(gs_test.getId());
        this.gradingSystemService.deleteDraftGradingSystem(gs_test.getId());

        assertThat(gradingSystemRepository.findById(gs_test.getId()).isEmpty()).isTrue();
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
    public void deleteDraftGradingSystem_withIncorrectId_expectToThrowNotFoundException() throws Exception {
        var gss = setupGradingSystems();
        var gs_test = gss.get(5);
        var toDeleteOpt = gradingSystemRepository.findById(gs_test.getId());
        if (toDeleteOpt.isEmpty()) {
            fail("Did not find grading system, that should have been initialized beforehand");
        }
        assertThat(toDeleteOpt.get().getId()).isEqualTo(gs_test.getId());
        NotFoundException e = assertThrows(NotFoundException.class, () -> {
            gradingSystemService.deleteDraftGradingSystem(gs_test.getId());
        });

        assertThat(gradingSystemRepository.findById(gs_test.getId()).isEmpty()).isFalse();
        assertThat(e.getMessage()).contains("No such grading system found");
    }

    @Test
    @WithMockUser("gs_test_1@test.test")
    public void deleteDraftGradingSystem_withInvalidId_expectToThrowNotFoundException() throws Exception {
        var gss = setupGradingSystems();
        NotFoundException e = assertThrows(NotFoundException.class, () -> {
            gradingSystemService.deleteDraftGradingSystem(222222L);
        });
        assertThat(e.getMessage()).contains("No such grading system found");
    }
}
