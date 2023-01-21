package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.UserProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantManageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantSelfRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseMultiParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.SimpleGradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CompetitionRegistrationEndpointTest extends TestDataProvider {
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
    private SessionUtils sessionUtils;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @BeforeEach
    public void beforeEach() {
        competitionRepository.deleteAll();
        applicationUserRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        registerToRepository.deleteAll();
        setUpCompetitionUser();
        setUpParticipantUser();
        setUpClubManagerUser();
    }

    @Test
    public void selfRegistration_whenNotLoggedIn_expect403() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_SELF_REGISTRATION_BASE_URI + 1))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    public void selfRegistrationToDefaultComp_withValidData_expect201() throws Exception {
        Competition c = getValidCompetitionEntity();
        c.setPublic(true);
        c.setDraft(false);
        c.setBeginOfRegistration(LocalDateTime.now().minusDays(2));
        c.setEndOfRegistration(LocalDateTime.now().plusDays(4));
        GradingGroup g1 = new GradingGroup("G1");
        GradingGroup g2 = new GradingGroup("G2");
        g1.setCompetitions(c);
        g2.setCompetitions(c);
        c.setGradingGroups(Set.of(g1, g2));

        Competition cc = competitionRepository.save(c);
        GradingGroup gc1 = gradingGroupRepository.save(g1);
        GradingGroup gc2 = gradingGroupRepository.save(g2);

        Long defaultGroup = gc1.getId() < gc2.getId() ? gc1.getId() : gc2.getId();

        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_SELF_REGISTRATION_BASE_URI + cc.getId())
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        TEST_USER_COMPETITION_MANAGER_EMAIL,
                        List.of("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER)
                    )))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        ResponseParticipantRegistrationDto result = objectMapper
            .readerFor(ResponseParticipantRegistrationDto.class)
            .readValue(response.getContentAsByteArray());


        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(defaultGroup, result.getGroupPreference());
        assertEquals(cc.getId(), result.getCompetitionId());
    }

    @Test
    public void selfRegistrationToPreferenceGroup_withValidData_expect201() throws Exception {
        Competition c = getValidCompetitionEntity();
        c.setPublic(true);
        c.setDraft(false);
        c.setBeginOfRegistration(LocalDateTime.now().minusDays(2));
        c.setEndOfRegistration(LocalDateTime.now().plusDays(4));
        GradingGroup g1 = new GradingGroup("G1");
        GradingGroup g2 = new GradingGroup("G2");
        g1.setCompetitions(c);
        g2.setCompetitions(c);
        c.setGradingGroups(Set.of(g1, g2));

        Competition cc = competitionRepository.save(c);
        GradingGroup gc1 = gradingGroupRepository.save(g1);
        GradingGroup gc2 = gradingGroupRepository.save(g2);

        Long notDefault = gc1.getId() > gc2.getId() ? gc1.getId() : gc2.getId();

        ParticipantSelfRegistrationDto registrationDto = new ParticipantSelfRegistrationDto(notDefault);
        String body = objectMapper.writeValueAsString(registrationDto);


        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_SELF_REGISTRATION_BASE_URI + cc.getId())
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
        ResponseParticipantRegistrationDto result = objectMapper
            .readerFor(ResponseParticipantRegistrationDto.class)
            .readValue(response.getContentAsByteArray());

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(notDefault, result.getGroupPreference());
        assertEquals(cc.getId(), result.getCompetitionId());
    }

    @Test
    public void checkRegisteredTo_expect204() throws Exception {
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");
        g1.setCompetitions(c);
        c.setGradingGroups(Set.of(g1));
        Competition cc = competitionRepository.save(c);
        gradingGroupRepository.save(g1);
        registerToRepository
            .save(new RegisterTo(
                applicationUserRepository
                    .findApplicationUserByUserEmail(TEST_USER_PARTICIPANT_EMAIL).get(),
                g1, false));


        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_SELF_REGISTRATION_BASE_URI + cc.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        TEST_USER_PARTICIPANT_EMAIL,
                        List.of("ROLE_" + ApplicationUser.Role.PARTICIPANT)
                    )))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    public void checkRegisteredTo_whenNoAuthenticated_expect401() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_SELF_REGISTRATION_BASE_URI + 4)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    @WithMockUser(username = TEST_USER_PARTICIPANT_EMAIL)
    public void checkRegisteredTo_whenNoNotRegistered_expect404() throws Exception {
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");
        g1.setCompetitions(c);
        c.setGradingGroups(Set.of(g1));
        Competition cc = competitionRepository.save(c);
        gradingGroupRepository.save(g1);

        MvcResult mvcResult = this.mockMvc.perform(get(COMPETITION_SELF_REGISTRATION_BASE_URI + cc.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        TEST_USER_PARTICIPANT_EMAIL,
                        List.of("ROLE_" + ApplicationUser.Role.PARTICIPANT)
                    )))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void registration_whenNotLoggedIn_expect403() throws Exception {
        List<ParticipantRegistrationDto> registrationDto = new ArrayList<>();
        registrationDto.add(new ParticipantRegistrationDto(1L, null));
        String body = objectMapper.writeValueAsString(registrationDto);

        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_BASE_URI + "/" + 1 + "/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andDo(print())
            .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
    }

    @Test
    public void registration_expect201() throws Exception {
        final int participantsToRegister = 3;
        Competition c = getValidCompetitionEntity();
        c.setPublic(true);
        c.setDraft(false);
        c.setBeginOfRegistration(LocalDateTime.now().minusDays(2));
        c.setEndOfRegistration(LocalDateTime.now().plusDays(4));
        GradingGroup g1 = new GradingGroup("G1");
        GradingGroup g2 = new GradingGroup("G2");
        g1.setCompetitions(c);
        g2.setCompetitions(c);
        c.setGradingGroups(Set.of(g1, g2));

        Competition cc = competitionRepository.save(c);
        GradingGroup gc1 = gradingGroupRepository.save(g1);
        GradingGroup gc2 = gradingGroupRepository.save(g2);

        GradingGroup defaultGroup = gradingGroupRepository.findFirstByCompetitionIdOrderByIdAsc(cc.getId()).get();

        clubManagerWithManagedUsers(participantsToRegister);
        ApplicationUser clubManager = applicationUserRepository
            .findApplicationUserByUserEmail(TEST_USER_CLUB_MANAGER_EMAIL).get();
        List<ParticipantRegistrationDto> registrations = getParticipantRegistrationDto(clubManager.getId());
        String body = objectMapper.writeValueAsString(registrations);

        MvcResult mvcResult = this.mockMvc.perform(post(COMPETITION_BASE_URI + "/" + cc.getId() + "/participants")
                .contentType(MediaType.APPLICATION_JSON)
                .header(
                    securityProperties.getAuthHeader(),
                    jwtTokenizer.getAuthToken(
                        TEST_USER_CLUB_MANAGER_EMAIL,
                        List.of("ROLE_" + ApplicationUser.Role.CLUB_MANAGER)
                    ))
                .content(body))
            .andDo(print())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        ResponseMultiParticipantRegistrationDto result = objectMapper
            .readerFor(ResponseMultiParticipantRegistrationDto.class)
            .readValue(response.getContentAsByteArray());

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());

        assertEquals(cc.getId(), result.getCompetitionId());

        List<ParticipantRegistrationDto> registeredParticipants = result.getRegisteredParticipants();
        for (int i = 0; i < participantsToRegister; i++) {
            assertEquals(registrations.get(i).getUserId(), registeredParticipants.get(i).getUserId());
            assertEquals(defaultGroup.getId(), registeredParticipants.get(i).getGroupPreference());
        }
    }

    @Test
    public void participantUpdate_expect200() throws Exception {
        ApplicationUser competitionManager = applicationUserRepository
            .findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = getValidCompetitionEntity();
        c.setPublic(true);
        c.setDraft(false);
        c.setCreator(competitionManager);
        c.setBeginOfRegistration(LocalDateTime.now().minusDays(2));
        c.setEndOfRegistration(LocalDateTime.now().plusDays(4));
        GradingGroup g1 = new GradingGroup("G1");
        GradingGroup g2 = new GradingGroup("G2");
        g1.setCompetitions(c);
        g2.setCompetitions(c);
        c.setGradingGroups(Set.of(g1, g2));

        Competition cc = competitionRepository.save(c);
        GradingGroup gc1 = gradingGroupRepository.save(g1);
        GradingGroup gc2 = gradingGroupRepository.save(g2);

        List<ApplicationUser> participants = UserProvider.getParticipants(2);
        List<ApplicationUser> savedParticipants = applicationUserRepository.saveAll(participants);

        registerToRepository.save(new RegisterTo(
            savedParticipants.get(0),
            gc1,
            false
        ));

        registerToRepository.save(new RegisterTo(
            savedParticipants.get(1),
            gc2,
            true
        ));

        List<ParticipantManageDto> updateDto = new ArrayList<>();
        updateDto.add(new ParticipantManageDto(participants.get(0).getId(), null, true));
        updateDto.add(new ParticipantManageDto(participants.get(1).getId(), gc1.getId(), false));

        String body = objectMapper.writeValueAsString(updateDto);

        MvcResult mvcResult = this.mockMvc.perform(patch(COMPETITION_BASE_URI + "/" + cc.getId() + "/participants")
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

        List<ParticipantManageDto> updatedParticipants = objectMapper
            .readValue(response.getContentAsByteArray(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, ParticipantManageDto.class));

        assertEquals(HttpStatus.OK.value(), response.getStatus());


        assertEquals(participants.get(0).getId(), updatedParticipants.get(0).getUserId());
        assertEquals(gc1.getId(), updatedParticipants.get(0).getGroupId());
        assertEquals(true, updatedParticipants.get(0).getAccepted());

        assertEquals(participants.get(1).getId(), updatedParticipants.get(1).getUserId());
        assertEquals(gc1.getId(), updatedParticipants.get(1).getGroupId());
        assertEquals(false, updatedParticipants.get(1).getAccepted());
    }
}
