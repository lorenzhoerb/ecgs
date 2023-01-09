package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ResponseParticipantRegistrationDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.GradingGroup;
import at.ac.tuwien.sepm.groupphase.backend.entity.RegisterTo;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionRegistrationService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class CompetitionRegistrationServiceTest extends TestDataProvider {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private GradingGroupRepository gradingGroupRepository;

    @Autowired
    private RegisterToRepository registerToRepository;

    @Autowired
    private CompetitionRegistrationService competitionRegistrationService;

    @Autowired
    private SessionUtils sessionUtils;

    @BeforeEach
    public void beforeEach() {
        competitionRepository.deleteAll();
        applicationUserRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        registerToRepository.deleteAll();
        setUpCompetitionUser();
        setUpParticipantUser();
    }

    @Test
    public void selfRegistration_whenNotAuthenticated_expectForbidden() {
        ForbiddenException ex = assertThrows(ForbiddenException.class, () ->
            competitionRegistrationService.selfRegisterParticipant(1L, null));
        assertEquals("No permissions to register to competitions. Authentication required.", ex.getMessage());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void selfRegistration_whenUnknownCompetitionId_expectNotFound() {
        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            competitionRegistrationService.selfRegisterParticipant(1L, null);
        });
        assertEquals("Unknown competition 1", ex.getMessage());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void selfRegistration_whenCompetitionIsNull_expectValidationException() {
        ValidationListException ex = assertThrows(ValidationListException.class, () -> {
            competitionRegistrationService.selfRegisterParticipant(null, null);
        });
        assertEquals("Competition id must be given", ex.errors().get(0));
    }

    @Test
    @WithMockUser(username = TEST_USER_PARTICIPANT_EMAIL)
    public void selfRegistration_whenCompetitionIsNotPublic_expectForbidden() {
        Competition c = getValidCompetitionEntity();
        c.setPublic(false);
        c.setDraft(false);
        Competition created = competitionRepository.save(c);
        assertNotNull(created);
        assertNotNull(created.getId());
        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> {
            competitionRegistrationService.selfRegisterParticipant(created.getId(), null);
        });
        assertEquals("Registration forbidden.", ex.getMessage());
    }

    @Test
    @WithMockUser(username = TEST_USER_PARTICIPANT_EMAIL)
    public void selfRegistration_whenCompetitionIsDraft_expectForbidden() {
        Competition c = getValidCompetitionEntity();
        c.setPublic(true);
        c.setDraft(true);
        Competition created = competitionRepository.save(c);
        assertNotNull(created);
        assertNotNull(created.getId());
        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> {
            competitionRegistrationService.selfRegisterParticipant(created.getId(), null);
        });
        assertEquals("Registration forbidden.", ex.getMessage());
    }

    @Test
    @WithMockUser(username = TEST_USER_PARTICIPANT_EMAIL)
    public void selfRegistration_whenOutOfRegistrationDate_expectForbidden() {
        Competition c = getValidCompetitionEntity();
        c.setPublic(true);
        c.setDraft(false);
        c.setBeginOfRegistration(LocalDateTime.now().plusDays(2));
        c.setEndOfRegistration(LocalDateTime.now().plusDays(4));
        Competition created = competitionRepository.save(c);
        assertNotNull(created);
        assertNotNull(created.getId());
        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> {
            competitionRegistrationService.selfRegisterParticipant(created.getId(), null);
        });
        assertEquals("Registration for competition " + created.getId() + " is not closed.", ex.getMessage());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void selfRegistration_whenCompetitionHasNoGradingGroup_expectNotFound() {
        Competition c = getValidCompetitionEntity();
        c.setPublic(true);
        c.setDraft(false);
        c.setBeginOfRegistration(LocalDateTime.now().minusDays(2));
        c.setEndOfRegistration(LocalDateTime.now().plusDays(4));
        Competition created = competitionRepository.save(c);
        assertNotNull(created);
        assertNotNull(created.getId());
        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            competitionRegistrationService.selfRegisterParticipant(created.getId(), null);
        });
        assertEquals("Registration failed. Could not assign to grading group.", ex.getMessage());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void selfRegistrationToDefault_whenValidInput_expectCreated() {
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

        assertNotNull(gc1);
        assertNotNull(gc2);
        assertNotNull(cc);

        ApplicationUser sessionUser = sessionUtils.getSessionUser();
        assertNotNull(sessionUser);

        Long defaultGroup = gc1.getId() < gc2.getId() ? gc1.getId() : gc2.getId();

        ResponseParticipantRegistrationDto reg = competitionRegistrationService
            .selfRegisterParticipant(cc.getId(), null);
        assertNotNull(reg);
        assertEquals(cc.getId(), reg.getCompetitionId());
        assertEquals(sessionUser.getId(), reg.getUserId());
        assertEquals(defaultGroup, reg.getGroupPreference());

        Optional<RegisterTo> oRegTo = registerToRepository
            .findByGradingGroupCompetitionIdAndParticipantId(cc.getId(), sessionUser.getId());
        assertTrue(oRegTo.isPresent());
        RegisterTo regTo = oRegTo.get();
        assertFalse(regTo.getAccepted());
        assertEquals(defaultGroup, regTo.getGradingGroup().getId());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void selfRegistrationWithGroupPreference_whenValidInput_expectCreated() {
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

        assertNotNull(gc1);
        assertNotNull(gc2);
        assertNotNull(cc);

        ApplicationUser sessionUser = sessionUtils.getSessionUser();
        assertNotNull(sessionUser);

        Long notDefaultGroup = gc1.getId() > gc2.getId() ? gc1.getId() : gc2.getId();

        ResponseParticipantRegistrationDto reg = competitionRegistrationService
            .selfRegisterParticipant(cc.getId(), notDefaultGroup);
        assertNotNull(reg);
        assertEquals(cc.getId(), reg.getCompetitionId());
        assertEquals(sessionUser.getId(), reg.getUserId());
        assertEquals(notDefaultGroup, reg.getGroupPreference());

        Optional<RegisterTo> oRegTo = registerToRepository
            .findByGradingGroupCompetitionIdAndParticipantId(cc.getId(), sessionUser.getId());
        assertTrue(oRegTo.isPresent());
        RegisterTo regTo = oRegTo.get();
        assertFalse(regTo.getAccepted());
        assertEquals(notDefaultGroup, regTo.getGradingGroup().getId());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void selfRegistrationWithInvalidGroupPreference_whenValidInput_NotFound() {
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

        assertNotNull(gc1);
        assertNotNull(gc2);
        assertNotNull(cc);

        Long invalidGroupForComp = gc1.getId() + gc2.getId();

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            competitionRegistrationService
                .selfRegisterParticipant(cc.getId(), invalidGroupForComp);
        });
        assertEquals("Registration failed. Could not assign to grading group.", ex.getMessage());
    }

    @Test
    public void checkRegisteredTo_whenNotLoggedIn_expectUnauthorized() {
        assertThrows(ForbiddenException.class, () -> competitionRegistrationService.isRegisteredTo(2L));
    }

    @Test
    @WithMockUser(username = TEST_USER_PARTICIPANT_EMAIL)
    public void checkRegisteredTo_whenLoggedInWithNoCompetitionId_expectValidation() {
        assertThrows(ValidationListException.class, () -> competitionRegistrationService.isRegisteredTo(null));
    }

    @Test
    @WithMockUser(username = TEST_USER_PARTICIPANT_EMAIL)
    public void checkRegisteredTo_whenLoggedIn_whenCompetitionDoesNotExists() {
        assertFalse(competitionRegistrationService.isRegisteredTo(1L));
    }

    @Test
    @WithMockUser(username = TEST_USER_PARTICIPANT_EMAIL)
    public void checkRegisteredTo_whenLoggedIn_whenCompetitionExists_andNotRegistered_expectFalse() {
        Competition c = competitionRepository.save(getValidCompetitionEntity());
        assertFalse(competitionRegistrationService.isRegisteredTo(c.getId()));
    }

    @Test
    @WithMockUser(username = TEST_USER_PARTICIPANT_EMAIL)
    public void checkRegisteredTo_whenRegistered_expectTrue() {
        Competition c = getValidCompetitionEntity();
        GradingGroup g1 = new GradingGroup("G1");
        g1.setCompetitions(c);
        c.setGradingGroups(Set.of(g1));
        Competition cc = competitionRepository.save(c);
        gradingGroupRepository.save(g1);
        registerToRepository.save(new RegisterTo(sessionUtils.getSessionUser(), g1, false));
        assertTrue(competitionRegistrationService.isRegisteredTo(cc.getId()));
    }

}
