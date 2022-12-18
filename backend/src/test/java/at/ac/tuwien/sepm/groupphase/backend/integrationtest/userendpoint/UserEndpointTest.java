package at.ac.tuwien.sepm.groupphase.backend.integrationtest.userendpoint;

import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.CalendarViewDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.ClubManagerTeamImportDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.ClubManagerTeamImportGeneratorHelper;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.DataCleaner;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamMemberImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ErrorListRestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GeneralResponseDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.help.CalendarViewHelp;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static at.ac.tuwien.sepm.groupphase.backend.help.CalendarViewHelp.CURRENT_WEEK_NUMBER;
import static at.ac.tuwien.sepm.groupphase.backend.help.CalendarViewHelp.CURRENT_YEAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserEndpointTest implements TestData {
    private final ObjectMapper objectMapper;
    private final JwtTokenizer jwtTokenizer;
    private final SecurityProperties securityProperties;
    private final MockMvc mockMvc;
    private final DataCleaner cleaner;
    private final CalendarViewDataGenerator cvGenerator;
    private final ClubManagerTeamImportDataGenerator cmGenerator;

    @Autowired
    public UserEndpointTest(ObjectMapper objectMapper, JwtTokenizer jwtTokenizer,
                            SecurityProperties securityProperties, MockMvc mockMvc,
                            DataCleaner cleaner, CalendarViewDataGenerator cvGenerator,
                            ClubManagerTeamImportDataGenerator cmGenerator) {
        this.objectMapper = objectMapper;
        this.jwtTokenizer = jwtTokenizer;
        this.securityProperties = securityProperties;
        this.mockMvc = mockMvc;
        this.cleaner = cleaner;
        this.cvGenerator = cvGenerator;
        this.cmGenerator = cmGenerator;
    }

    @BeforeEach
    public void refreshTestDate() {
        cleaner.clear();
        cvGenerator.setup();
        cmGenerator.setup();
    }

    @Test
    @Order(0)
    public void competitionManagerDefaultCalendar() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(BASE_CALENDAR_URI + String.format("/%d/%d", 2022, 38))
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("test@test.test", CALENDAR_TEST_ROLES))
        )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<Competition> managedCompetitions = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            Competition[].class));

        // @TODO: this test fails for me locally
        assertEquals(2, managedCompetitions.size());
        assertThat(managedCompetitions)
            .map(Competition::getName,
                (t -> t.getBeginOfCompetition().toLocalDate().toString()),
                (t -> t.getEndOfCompetition().toLocalDate().toString()),
                Competition::getDescription, Competition::getPicturePath)
            .contains(CalendarViewHelp.generateTupleOfCalendarViewCompetition(CalendarViewDataGenerator.testCompetitions.get(0)))
            .contains(CalendarViewHelp.generateTupleOfCalendarViewCompetition(CalendarViewDataGenerator.testCompetitions.get(2)));
    }

    @Test
    public void getCalendarForInvalidYear() throws Exception {
        this.mockMvc.perform(get(BASE_CALENDAR_URI + String.format("/1800/%d", CURRENT_WEEK_NUMBER))
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("test@test.test", CALENDAR_TEST_ROLES))
        )
            .andExpect(result -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus()))
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationListException))
            .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("Invalid date requested")))
            .andExpect(result -> assertTrue(((ValidationListException) result.getResolvedException()).errors().get(0)
                .contains("Year must be at least 2000")));
    }

    @Test
    public void getCalendarForInvalidMonth() throws Exception {
        this.mockMvc.perform(get(BASE_CALENDAR_URI + String.format("/%d/55", CURRENT_YEAR))
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("test@test.test", CALENDAR_TEST_ROLES))
        )
            .andExpect(result -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus()))
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationListException))
            .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("Invalid date requested")))
            .andExpect(result -> assertTrue(((ValidationListException) result.getResolvedException()).errors().get(0)
                .contains("Number of the week must be from 1 up to 52")));
    }

    @Test
    @Order(1)
    public void importValidTeam() throws Exception {
        String body = objectMapper.writeValueAsString(ClubManagerTeamImportGeneratorHelper.testTeams.get(0));

        MvcResult mvcResult = this.mockMvc.perform(post(BASE_IMPORT_TEAM_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(
                ClubManagerTeamImportGeneratorHelper.testClubManagersSecUsers.get(0).getEmail(),
                TEAM_IMPORT_TEST_ROLES))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
        )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        GeneralResponseDto generalResponseDto = objectMapper.readValue(response.getContentAsString(),
            GeneralResponseDto.class);

        assertThat(generalResponseDto.message()).contains("4");
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @Order(2)
    public void importSameValidTeam() throws Exception {
        importValidTeam();
    }

    @Test
    @Order(3)
    public void importTeam_withInvalidTeamMembers() throws Exception {
        String body = objectMapper.writeValueAsString(ClubManagerTeamImportGeneratorHelper.testTeams_withInvalidMembers.get(0));

        MvcResult mvcResult = this.mockMvc.perform(post(BASE_IMPORT_TEAM_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(
                ClubManagerTeamImportGeneratorHelper.testClubManagersSecUsers.get(0).getEmail(),
                TEAM_IMPORT_TEST_ROLES))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
        )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        ErrorListRestDto errorListRestDto = objectMapper.readValue(response.getContentAsString(),
            ErrorListRestDto.class);

        assertThat(errorListRestDto.message()).contains("Validation failed");
        assertThat(errorListRestDto.errors().get(0)).contains("First name").contains("User #1 has some issues.");
    }

    @Test
    @Order(4)
    public void importTeam_withInvalidTeamName() throws Exception {
        String body = objectMapper.writeValueAsString(ClubManagerTeamImportGeneratorHelper.testTeams_withInvalidTeamName.get(0));

        MvcResult mvcResult = this.mockMvc.perform(post(BASE_IMPORT_TEAM_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(
                ClubManagerTeamImportGeneratorHelper.testClubManagersSecUsers.get(0).getEmail(),
                TEAM_IMPORT_TEST_ROLES))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
        )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        ErrorListRestDto errorListRestDto = objectMapper.readValue(response.getContentAsString(),
            ErrorListRestDto.class);

        assertThat(errorListRestDto.message()).contains("Validation failed");
        assertThat(errorListRestDto.errors().get(0)).contains("Team name");
    }

    @Test
    @Order(5)
    public void importTeam_withFewInvalidMembers() throws Exception {
        String body = objectMapper.writeValueAsString(new ClubManagerTeamImportDto(
            "teamnamee",
            new ArrayList<>() {
                {
                    add(new ClubManagerTeamMemberImportDto(
                        "first", null, ApplicationUser.Gender.MALE, new Date(946681200L), "ceck@ceck.com"
                        )
                    );
                    add(new ClubManagerTeamMemberImportDto(
                        "first", "asdasd", ApplicationUser.Gender.MALE, new Date(946681200L), "ceck@ceck.com"
                        )
                    );
                    add(new ClubManagerTeamMemberImportDto(
                        null, null, ApplicationUser.Gender.MALE, new Date(946681200L), "ceck.com"
                        )
                    );
                    add(new ClubManagerTeamMemberImportDto(
                        "first", "second", ApplicationUser.Gender.MALE, new Date(-1567533357000L), "ceck@ceck.com"
                        )
                    );
                }
            }
        ));

        MvcResult mvcResult = this.mockMvc.perform(post(BASE_IMPORT_TEAM_URI)
            .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(
                ClubManagerTeamImportGeneratorHelper.testClubManagersSecUsers.get(0).getEmail(),
                TEAM_IMPORT_TEST_ROLES))
            .contentType(MediaType.APPLICATION_JSON)
            .content(body)
        )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        ErrorListRestDto errorListRestDto = objectMapper.readValue(response.getContentAsString(),
            ErrorListRestDto.class);

        assertThat(errorListRestDto.message()).contains("Validation failed");
        assertThat(errorListRestDto.errors().get(0)).contains("User #1 has some issues").contains("Last name must not be blank");
        assertThat(errorListRestDto.errors().get(1))
            .contains("User #3 has some issues")
            .contains("Last name must not be blank")
            .contains("First name must not be blank");
        assertThat(errorListRestDto.errors().get(2))
            .contains("User #4 has some issues")
            .contains("Date of birth must be after the begin of 1920");
    }

}
