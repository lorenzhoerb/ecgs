package at.ac.tuwien.sepm.groupphase.backend.integrationtest.userendpoint;


import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.CalendarViewDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.ClubManagerTeamImportDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.ClubManagerTeamImportGeneratorHelper;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.DataCleaner;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamMemberImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ErrorListRestDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlag;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ImportFlagsResultDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.help.CalendarViewHelp;
import at.ac.tuwien.sepm.groupphase.backend.repository.FlagsRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.service.helprecords.ClubManagerTeamImportResults;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.StreamSupport;

import static at.ac.tuwien.sepm.groupphase.backend.help.CalendarViewHelp.CURRENT_WEEK_NUMBER;
import static at.ac.tuwien.sepm.groupphase.backend.help.CalendarViewHelp.CURRENT_YEAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class UserEndpointTest extends TestDataProvider implements TestData {
    private final ObjectMapper objectMapper;
    private final JwtTokenizer jwtTokenizer;
    private final SecurityProperties securityProperties;
    private final MockMvc mockMvc;
    private final DataCleaner cleaner;
    private final CalendarViewDataGenerator cvGenerator;
    private final ClubManagerTeamImportDataGenerator cmGenerator;

    private final FlagsRepository flagsRepository;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    public UserEndpointTest(ObjectMapper objectMapper, JwtTokenizer jwtTokenizer,
                            SecurityProperties securityProperties, MockMvc mockMvc,
                            DataCleaner cleaner, CalendarViewDataGenerator cvGenerator,
                            ClubManagerTeamImportDataGenerator cmGenerator, FlagsRepository flagsRepository) {
        this.objectMapper = objectMapper;
        this.jwtTokenizer = jwtTokenizer;
        this.securityProperties = securityProperties;
        this.mockMvc = mockMvc;
        this.cleaner = cleaner;
        this.cvGenerator = cvGenerator;
        this.cmGenerator = cmGenerator;
        this.flagsRepository = flagsRepository;
    }

    @BeforeEach
    public void refreshTestDate() {
        cleaner.clear();
        cvGenerator.setup();
        cmGenerator.setup();
    }

    @Test
    public void importFlags_whenAllParticipantsArePresent_shouldAddOnlyOneFlag() throws Exception {
        var testFlagsString = objectMapper.writeValueAsString(flagsImport_setupTestFlags());
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_FLAGS_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("cm_1@test.test", CALENDAR_TEST_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFlagsString)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        ImportFlagsResultDto result = objectMapper.readValue(response.getContentAsString(),
            ImportFlagsResultDto.class);
        var resultingFlagNamesNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).count();
        var resultingFlagsNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).map(
            flag -> flag.getClubs().size()
        ).reduce(0, Integer::sum);
        assertThat(result.getNewImportedFlags()).isEqualTo(3);
        assertThat(resultingFlagsNumber).isEqualTo(3);
        assertThat(resultingFlagNamesNumber).isEqualTo(1);

    }

    @Test
    public void importFlags_whenAllParticipantsArePresentImportFlagsTwice_shouldAddOnlyOneFlagOnFirstCall() throws Exception {
        var testFlagsString = objectMapper.writeValueAsString(flagsImport_setupTestFlags());
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_FLAGS_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("cm_1@test.test", CALENDAR_TEST_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFlagsString)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        ImportFlagsResultDto result = objectMapper.readValue(response.getContentAsString(),
            ImportFlagsResultDto.class);
        var resultingFlagNamesNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).count();
        var resultingFlagsNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).map(
            flag -> flag.getClubs().size()
        ).reduce(0, Integer::sum);
        assertThat(result.getNewImportedFlags()).isEqualTo(3);
        assertThat(resultingFlagsNumber).isEqualTo(3);
        assertThat(resultingFlagNamesNumber).isEqualTo(1);

        mvcResult = this.mockMvc.perform(post(BASE_FLAGS_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("cm_1@test.test", CALENDAR_TEST_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFlagsString)
            )
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        result = objectMapper.readValue(response.getContentAsString(),
            ImportFlagsResultDto.class);
        resultingFlagNamesNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).count();
        resultingFlagsNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).map(
            flag -> flag.getClubs().size()
        ).reduce(0, Integer::sum);
        assertThat(result.getNewImportedFlags()).isEqualTo(0);
        assertThat(resultingFlagsNumber).isEqualTo(3);
        assertThat(resultingFlagNamesNumber).isEqualTo(1);
    }

    @Test
    public void importFlags_whenAllParticipantsArePresent_importTwoSetsOfFlagsDifferingInOneValueSubsequently_shouldAddOnlyOneFlagOnFirstCall()
        throws Exception {
        var testFlags = flagsImport_setupTestFlags();
        var testFlagsString = objectMapper.writeValueAsString(testFlags);
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_FLAGS_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("cm_1@test.test", CALENDAR_TEST_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFlagsString)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        var resultingFlagNamesNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).count();
        var resultingFlagsNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).map(
            flag -> flag.getClubs().size()
        ).reduce(0, Integer::sum);
        ImportFlagsResultDto result = objectMapper.readValue(response.getContentAsString(),
            ImportFlagsResultDto.class);
        assertThat(result.getNewImportedFlags()).isEqualTo(3);
        assertThat(resultingFlagsNumber).isEqualTo(3);
        assertThat(resultingFlagNamesNumber).isEqualTo(1);


        testFlags.add(
            new ImportFlag(
                "part4@test.test",
                "cool"
            )
        );
        testFlagsString = objectMapper.writeValueAsString(testFlags);
        mvcResult = this.mockMvc.perform(post(BASE_FLAGS_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("cm_1@test.test", CALENDAR_TEST_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFlagsString)
            )
            .andDo(print())
            .andReturn();
        response = mvcResult.getResponse();
        resultingFlagNamesNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).count();
        resultingFlagsNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).map(
            flag -> flag.getClubs().size()
        ).reduce(0, Integer::sum);
        result = objectMapper.readValue(response.getContentAsString(),
            ImportFlagsResultDto.class);
        assertThat(result.getNewImportedFlags()).isEqualTo(1);
        assertThat(resultingFlagsNumber).isEqualTo(4);
        assertThat(resultingFlagNamesNumber).isEqualTo(1);
    }

    @Test
    public void importFlags_whenSomeParticipantsAreNotPresent_shouldThrowValidationExceptionAndNothingMore() throws Exception {
        var testFlags = flagsImport_setupTestFlags();
        testFlags.add(
            new ImportFlag(
                "unknown@test.test",
                "cool"
            )
        );
        var testFlagsString = objectMapper.writeValueAsString(testFlags);

        MvcResult mvcResult = this.mockMvc.perform(post(BASE_FLAGS_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("cm_1@test.test", CALENDAR_TEST_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFlagsString)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        var responseString = response.getContentAsString();
        var resultingFlagNamesNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).count();
        var resultingFlagsNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).map(
            flag -> flag.getClubs().size()
        ).reduce(0, Integer::sum);
        assertThat(responseString)
            .contains("Some emails are not managed by you")
            .contains("#4 - unknown@test.test");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(resultingFlagsNumber).isEqualTo(0);
        assertThat(resultingFlagNamesNumber).isEqualTo(0);
    }

    @Test
    public void importFlags_whenSomeParticipantsArePresentAndRequestHasDuplicates_shouldDiscardDuplicatesInResponse()
        throws Exception {
        var testFlags = flagsImport_setupTestFlags();
        testFlags.add(
            new ImportFlag(
                "part3@test.test",
                "cool"
            )
        );
        var testFlagsString = objectMapper.writeValueAsString(testFlags);

        MvcResult mvcResult = this.mockMvc.perform(post(BASE_FLAGS_URI)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("cm_1@test.test", CALENDAR_TEST_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFlagsString)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        var resultingFlagNamesNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).count();
        var resultingFlagsNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).map(
            flag -> flag.getClubs().size()
        ).reduce(0, Integer::sum);
        ImportFlagsResultDto result = objectMapper.readValue(response.getContentAsString(),
            ImportFlagsResultDto.class);
        assertThat(result.getNewImportedFlags()).isEqualTo(3);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(resultingFlagsNumber).isEqualTo(3);
        assertThat(resultingFlagNamesNumber).isEqualTo(1);
    }

    @Test
    public void importFlags_whenSomeParticipantsHaveInvalidEmail_shouldThrowValidationExceptionAndNothingMore() throws Exception {
        var testFlags = flagsImport_setupTestFlags();
        testFlags.add(
            new ImportFlag(
                "unknown",
                "cool"
            )
        );
        var testFlagsString = objectMapper.writeValueAsString(testFlags);

        MvcResult mvcResult = this.mockMvc.perform(post(BASE_FLAGS_URI, testFlags)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("cm_1@test.test", CALENDAR_TEST_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFlagsString)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        var responseString = response.getContentAsString();
        var resultingFlagNamesNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).count();
        var resultingFlagsNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).map(
            flag -> flag.getClubs().size()
        ).reduce(0, Integer::sum);
        assertThat(responseString)
            .contains("Validation failure")
            .contains("#4")
            .contains("Not valid email specified");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(resultingFlagsNumber).isEqualTo(0);
        assertThat(resultingFlagNamesNumber).isEqualTo(0);
    }

    @Test
    public void importFlags_whenSomeParticipantsHaveBlankFlag_shouldThrowValidationExceptionAndNothingMore() throws Exception {
        var testFlags = flagsImport_setupTestFlags();
        testFlags.add(
            new ImportFlag(
                "unknown@asd.com",
                ""
            )
        );
        var testFlagsString = objectMapper.writeValueAsString(testFlags);
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_FLAGS_URI, testFlags)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("cm_1@test.test", CALENDAR_TEST_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFlagsString)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        var responseString = response.getContentAsString();
        var resultingFlagNamesNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).count();
        var resultingFlagsNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).map(
            flag -> flag.getClubs().size()
        ).reduce(0, Integer::sum);
        assertThat(responseString)
            .contains("Validation failure")
            .contains("#4")
            .contains("Flag must be specified");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(resultingFlagsNumber).isEqualTo(0);
        assertThat(resultingFlagNamesNumber).isEqualTo(0);
    }

    @Test
    public void importFlags_whenSomeParticipantsHaveTooLongFlag_shouldThrowValidationExceptionAndNothingMore() throws Exception {
        var testFlags = flagsImport_setupTestFlags();
        testFlags.add(
            new ImportFlag(
                "unknown@asd.com",
                "A".repeat(256)
            )
        );
        var testFlagsString = objectMapper.writeValueAsString(testFlags);
        MvcResult mvcResult = this.mockMvc.perform(post(BASE_FLAGS_URI, testFlags)
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("cm_1@test.test", CALENDAR_TEST_ROLES))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testFlagsString)
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        var responseString = response.getContentAsString();
        var resultingFlagNamesNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).count();
        var resultingFlagsNumber = StreamSupport.stream(flagsRepository.findAll().spliterator(), false).map(
            flag -> flag.getClubs().size()
        ).reduce(0, Integer::sum);
        assertThat(responseString)
            .contains("Validation failure")
            .contains("#4")
            .contains("Flag is too long");
        assertThat(response.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(resultingFlagsNumber).isEqualTo(0);
        assertThat(resultingFlagNamesNumber).isEqualTo(0);
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    public void getCompetitionsForCalendar_expectsAllManagedCompetitionsRetrieved() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(String.format("%s?year=%d&weekNumber=%d", BASE_CALENDAR_URI, 2022, 38))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("test@test.test", CALENDAR_TEST_ROLES))
            )
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        List<Competition> managedCompetitions = Arrays.asList(objectMapper.readValue(response.getContentAsString(),
            Competition[].class));

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
    public void getCompetitionsForCalendar_withInvalidYear_expectsValidationExceptionThrown() throws Exception {
        this.mockMvc.perform(get(String.format("%s?year=%d&weekNumber=%d", BASE_CALENDAR_URI, 1800, CURRENT_WEEK_NUMBER))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("test@test.test", CALENDAR_TEST_ROLES))
            )
            .andExpect(result -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus()))
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationListException))
            .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("Invalid date requested")))
            .andExpect(result -> assertTrue(((ValidationListException) result.getResolvedException()).errors().get(0)
                .contains("Year must be at least 2000")));
    }

    @Test
    public void getCompetitionsForCalendar_withInvalidMonth_expectsValidationExceptionThrown() throws Exception {
        this.mockMvc.perform(get(String.format("%s?year=%d&weekNumber=%d", BASE_CALENDAR_URI, CURRENT_YEAR, 55))
                .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken("test@test.test", CALENDAR_TEST_ROLES))
            )
            .andExpect(result -> assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), result.getResponse().getStatus()))
            .andExpect(result -> assertTrue(result.getResolvedException() instanceof ValidationListException))
            .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("Invalid date requested")))
            .andExpect(result -> assertTrue(((ValidationListException) result.getResolvedException()).errors().get(0)
                .contains("Number of the week must be from 1 up to 52")));
    }

    @Test
    public void importTeam_withValidFields_shouldSucceedAndContainCorrectNumberOfNewMembers() throws Exception {
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

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        ClubManagerTeamImportResults result = objectMapper.readValue(response.getContentAsString(),
            ClubManagerTeamImportResults.class);

        int teamSize = ClubManagerTeamImportGeneratorHelper.testTeams.get(0).teamMembers().size();

        assertEquals(teamSize, result.getNewParticipantsCount());
        assertEquals(0, result.getOldParticipantsCount());
    }

    @Test
    public void importTeam_withInvalidTeamMembers_shouldReturnValidationFailedAnswer() throws Exception {
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
    public void importTeam_withInvalidTeamName_shouldReturnValidationFailedAnswer() throws Exception {
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
    public void importTeam_withFewInvalidMembers_shouldReturnValidationFailedAnswerForFewMembers() throws Exception {
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

    @Test
    public void importTeam_withValidMembersTwoOfWhichHaveFlags_shouldImportAllParticipantsAndAddThoseFlags() throws Exception {
        String body = objectMapper.writeValueAsString(new ClubManagerTeamImportDto(
            "teamnamee",
            new ArrayList<>() {
                {
                    add(new ClubManagerTeamMemberImportDto(
                            "first", "second", ApplicationUser.Gender.MALE, new Date(946681200L), "ceck@ceck.com"
                        )
                    );
                    add(new ClubManagerTeamMemberImportDto(
                            "first", "asdasd", ApplicationUser.Gender.MALE, new Date(946681200L), "ceck2@ceck.com", "flag1"
                        )
                    );
                    add(new ClubManagerTeamMemberImportDto(
                            "firstf", "lastf", ApplicationUser.Gender.MALE, new Date(946681200L), "ceck23@ceck.com"
                        )
                    );
                    add(new ClubManagerTeamMemberImportDto(
                            "first", "lastg", ApplicationUser.Gender.MALE, new Date(946681200L), "ceck24@ceck.com", "flag2"
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
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        ClubManagerTeamImportResults results = objectMapper.readValue(response.getContentAsString(),
            ClubManagerTeamImportResults.class);

        assertThat(results.getNewParticipantsCount()).isEqualTo(4);
        assertThat(results.getOldParticipantsCount()).isEqualTo(0);
        assertThat(StreamSupport.stream(flagsRepository.findAll().spliterator(), false).count()).isEqualTo(2);
        assertThat(
            flagsRepository.findByName("flag1").get().getClubs().stream().toList().get(0)
                .getMember().getUser().getEmail())
            .isEqualTo("ceck2@ceck.com");
        assertThat(
            flagsRepository.findByName("flag2").get().getClubs().stream().toList().get(0)
                .getMember().getUser().getEmail())
            .isEqualTo("ceck24@ceck.com");
    }

    @Test
    public void uploadUserPicture_whenCorrectFileTypeAndContent_ShouldSucceed() throws Exception {
        setUpCompetitionUser();
        Resource originalSource = resourceLoader.getResource("classpath:/user-pictures/dot.png");
        byte[] originalBytes = originalSource.getInputStream().readAllBytes();
        MockMultipartFile file = new MockMultipartFile("file", "pic.png", MediaType.IMAGE_PNG_VALUE, originalBytes);
        MvcResult mvcResult = mockMvc.perform(multipart(BASE_UPLOAD_PICTURE_URI).file(file).header("Authorization", "Bearer " + jwtTokenizer.getAuthToken(
                TEST_USER_COMPETITION_MANAGER_EMAIL,
                List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
            )).contentType(MediaType.MULTIPART_FORM_DATA))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertEquals(response.getContentAsString(),"Picture successfully stored");
        Path of = Path.of("user-test-pictures/");
        if (Files.exists(of)) {
            FileUtils.deleteDirectory(of.toFile());
        }
    }


}
