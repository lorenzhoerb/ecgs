package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CompetitionServiceImpl;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ActiveProfiles("test")
public class CompetitionServiceTest extends TestDataProvider {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private GradingGroupRepository gradingGroupRepository;

    @Autowired
    private RegisterToRepository registerToRepository;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    // Test Competition for findOne
    private final Competition competition = new Competition(
        "Test Competition",
        LocalDateTime.of(2022, 11, 9, 8, 0),
        LocalDateTime.of(2022, 11, 10, 23, 55),
        LocalDateTime.of(2022, 11, 11, 14, 0),
        LocalDateTime.of(2022, 11, 11, 8, 0),
        "This is a test competition",
        "",
        true,
        false,
        "test@mail.com",
        "+436666660666"
    );

    @BeforeEach
    public void beforeEach() {
        competitionRepository.deleteAll();
        applicationUserRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        registerToRepository.deleteAll();
        setUpCompetitionUser();
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenValidCompetition_createValidCompetition() {
        CompetitionDetailDto result = competitionService.create(getValidCompetitionDetailDto());
        assertNotNull(result);
        assertNotNull(result.getId());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenEmptyOrNullTitle_whenCreatingCompetition_thenValidationException() {
        CompetitionDetailDto competitionDetailDto = getValidCompetitionDetailDto();
        assertThrows(ValidationListException.class, () -> {
            competitionDetailDto.setName("");
            competitionService.create(competitionDetailDto);
        });

        assertThrows(ValidationListException.class, () -> {
            competitionDetailDto.setName(null);
            competitionService.create(competitionDetailDto);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenPastTodayRegistration_whenCreatingCompetition_thenValidationException() {
        CompetitionDetailDto competitionDetailDto = getValidCompetitionDetailDto();
        assertThrows(ValidationListException.class, () -> {
            competitionDetailDto.setBeginOfRegistration(LocalDateTime.now().minusDays(1));
            competitionService.create(competitionDetailDto);
        });

        assertThrows(ValidationListException.class, () -> {
            competitionDetailDto.setName(null);
            competitionService.create(competitionDetailDto);
        });
    }

    @Test
    public void givenNotLoggedInUser_whenCreatingCompetition_thenForbiddenException() {
       assertThrows(ForbiddenException.class, () -> {
           competitionService.create(getValidCompetitionDetailDto());
       });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void getParticipants_forExistingCompetition() throws Exception {
        Competition competition = createCompetitionEntity(
            applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true,
           false
        );

        Set<UserDetailDto> participants =
            competitionService.getParticipants(competition.getId());
        assertNotNull(participants);
        assertEquals(participants.size(), 1);

        UserDetailDto participant = participants.iterator().next();
        assertEquals(participant.firstName(), "first");
        assertEquals(participant.lastName(), "last");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void getParticipants_forNotAcceptedParticipant() throws Exception {
        Competition competition = createCompetitionEntity(
            applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            false,
            false
        );

        Set<UserDetailDto> participants =
            competitionService.getParticipants(competition.getId());
        assertNotNull(participants);
        assertEquals(participants.size(), 0);
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void getParticipants_forCompetitionInDraft() throws Exception {
        Competition competition = createCompetitionEntity(
            applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true,
            true
        );

        assertThrows(NotFoundException.class, () -> {
            competitionService.getParticipants(-1L);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void getParticipants_forNotExistingCompetition() throws Exception {
        Competition competition = createCompetitionEntity(
            applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true,
            false
        );

        assertThrows(NotFoundException.class, () -> {
            competitionService.getParticipants(-1L);
        });
    }

    @Test
    public void getParticipants_givenNotLoggedInUser() throws Exception {
        Competition competition = createCompetitionEntity(
            applicationUserRepository,
            registerToRepository,
            gradingGroupRepository,
            competitionRepository,
            true,
            false
        );

        assertThrows(ForbiddenException.class, () -> {
            competitionService.getParticipants(competition.getId());
        });
    }

    public void findExistingCompetitionById() {
        competitionRepository.save(competition);
        Competition result = competitionService.findOne(competition.getId());

        assertNotNull(result);
        assertNotNull(result.getId());
    }

    @Test
    public void throwNotFound_searchingForNonExistingUser() {
        assertThrows(NotFoundException.class, () -> {
            competitionService.findOne(-1L);
        });
    }
}
