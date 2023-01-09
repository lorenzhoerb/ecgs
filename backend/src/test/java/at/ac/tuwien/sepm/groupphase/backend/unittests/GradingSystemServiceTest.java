package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingSystemDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @BeforeEach
    public void beforeEach() {
        gradingSystemRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        applicationUserRepository.deleteAll();
        setUpCompetitionUser();
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
        GradingSystemDetailDto result =
            gradingSystemService.createGradingSystem(getValidGradingSystemDetailDto());
        assertNotNull(result);
        assertNotNull(result.name());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenInvalidNameToLong() throws JsonProcessingException {
        GradingSystemDetailDto gradingSystemDetailDto1 = new GradingSystemDetailDto(
            "A".repeat(257),
            "desc",
            true,
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
}
