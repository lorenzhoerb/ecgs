package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.builder.CompetitionBuilder;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionListDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionViewDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.GradingGroupDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PageableDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantFilterDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ParticipantRegDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
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
    private GradingSystemRepository gradingSystemRepository;

    @Autowired
    private RegisterToRepository registerToRepository;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private SecurityUserRepository securityUserRepository;

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
        gradingSystemRepository.deleteAll();
        competitionRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        registerToRepository.deleteAll();
        applicationUserRepository.deleteAll();
        applicationUserRepository.flush();
        setUpCompetitionUser();
        setUpParticipantUser();
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

    @Test
    public void findExistingCompetitionById() {
        competitionRepository.save(competition);
        CompetitionViewDto result = competitionService.findOne(competition.getId());

        assertNotNull(result);
        assertNotNull(result.id());
    }

    @Test
    public void throwNotFound_searchingForNonExistingUser() {
        assertThrows(NotFoundException.class, () -> {
            competitionService.findOne(-1L);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenValidCompetitionWithGradingGroups_createValidCompetitionAndGradingGroups()
        throws JsonProcessingException {
        CompetitionDetailDto comp = getValidCompetitionDetailDto();
        comp.setGradingGroups(getValidGradingGroupDtos());

        CompetitionDetailDto result = competitionService.create(comp);

        assertNotNull(result);
        assertNotNull(result.getId());
        GradingGroupDto[] groups = result.getGradingGroups();
        assertNotNull(groups);
        assertEquals(3, groups.length);
        assertNotNull(groups[0].getId());
        assertNotNull(groups[1].getId());
        assertNotNull(groups[2].getId());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenValidCompetitionWithInvalidGradingGroups_thenValidationException() {
        CompetitionDetailDto comp = getValidCompetitionDetailDto();

        assertThrows(ValidationListException.class, () -> {
            comp.setGradingGroups(getGradingGroupDtosWithNameDuplicates());
            competitionService.create(comp);
        });

        assertThrows(ValidationListException.class, () -> {
            comp.setGradingGroups(getGradingGroupDtosWithEmptyNames());
            competitionService.create(comp);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void createAndSearchForCompetitions_ShouldSuccess() {
        for (int i = 0; i < 5; i++) {
            CompetitionDetailDto comp = getValidCompetitionDetailDto();
            if (i % 2 == 0) {
            } else {
                comp.setName("Show this please");
            }
            competitionService.create(comp);
        }
        List<CompetitionListDto> searchList =
            competitionService.searchCompetitions(
                new CompetitionSearchDto(
                    "this",
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    LocalDateTime.now()));

        assertEquals(2, searchList.size());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenValidCompetitionWithDuplicateJudge_thenValidationException() {
        CompetitionDetailDto comp = getValidCompetitionDetailDto();

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            comp.setJudges(getDuplicateJudges(
                applicationUserRepository,
                securityUserRepository
            ));
            competitionService.create(comp);
        });

        assertEquals(e.errors().get(0), "A judge can not be assigned twice to a competition");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenValidCompetitionWithNotExistingJudge_thenValidationException() {
        CompetitionDetailDto comp = getValidCompetitionDetailDto();

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            comp.setJudges(new UserDetailDto[] {
                new UserDetailDto(
                    -1L, "first", "last", ApplicationUser.Gender.MALE,
                    new Date(2000, 1, 1),
                    ""
                )
            });
            competitionService.create(comp);
        });

        assertEquals(e.errors().get(0), "Id of a judge invalid");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenValidCompetitionWithInvalidJudgeId_thenValidationException() {
        CompetitionDetailDto comp = getValidCompetitionDetailDto();

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            comp.setJudges(new UserDetailDto[] {
                new UserDetailDto(
                    null, "first", "last", ApplicationUser.Gender.MALE,
                    new Date(2000, 1, 1),
                    ""
                )
            });
            competitionService.create(comp);
        });

        assertEquals(e.errors().get(0), "Id of a judge invalid");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenValidCompetitionWithvalidJudgeIds_create() {
        CompetitionDetailDto comp = getValidCompetitionDetailDto();

        comp.setJudges(
            getValidJudges(applicationUserRepository, securityUserRepository)
        );

        CompetitionDetailDto result = competitionService.create(comp);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getJudges());
        assertNotNull(result.getJudges()[0]);
        assertNotNull(result.getJudges()[0].id());
        assertNotNull(result.getJudges()[1]);
        assertNotNull(result.getJudges()[1].id());
    }

    @Test
    public void givenUnauthenticatedUser_partRegistrationDetails_participantRegistrationList_expectForbidden() {
        assertThrows(ForbiddenException.class, () -> {
            competitionService.getParticipantsRegistrationDetails(null);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_PARTICIPANT_EMAIL)
    public void givenAuthenticatedParticipant_partRegistrationDetails_expectForbidden() {
        assertThrows(ForbiddenException.class, () -> {
            competitionService.getParticipantsRegistrationDetails(null);
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void givenNotOwnerOfCompetition_partRegistrationDetails_expectNotFound() {
        Competition c = new CompetitionBuilder(
            applicationUserRepository,
            competitionRepository,
            gradingGroupRepository,
            registerToRepository,
            gradingSystemRepository
        ).create();

        assertThrows(NotFoundException.class, () -> {
            competitionService
                .getParticipantsRegistrationDetails(
                    new PageableDto<>(
                        new ParticipantFilterDto(
                            c.getId(),
                            null,
                            null,
                            null,
                            null,
                            null),
                        null,
                        null));
        });
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void partRegistrationDetails_withNoFilters_expectPage() {
        ApplicationUser creator = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = new CompetitionBuilder(
            applicationUserRepository,
            competitionRepository,
            gradingGroupRepository,
            registerToRepository,
            gradingSystemRepository
        )
            .withCreator(creator)
            .withParticipantsPerGroup(25)
            .withGradingGroups(Set.of("T1"))
            .create();

        Page<ParticipantRegDetailDto> result = competitionService.getParticipantsRegistrationDetails(new PageableDto<>(
            new ParticipantFilterDto(
                c.getId(),
                null,
                null,
                null,
                null,
                null
            ),null,null));

        assertEquals(25, result.getTotalElements());
        assertEquals(0, result.getPageable().getPageNumber());
        assertEquals(10, result.getPageable().getPageSize()); // default size
        assertEquals(10, result.getContent().size());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void partRegistrationDetails_withFilter_expectPage() {
        ApplicationUser creator = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = new CompetitionBuilder(
            applicationUserRepository,
            competitionRepository,
            gradingGroupRepository,
            registerToRepository,
            gradingSystemRepository
        )
            .withCreator(creator)
            .withParticipantsPerGroup(25)
            .withGradingGroups(Set.of("T1"))
            .create();

        Page<ParticipantRegDetailDto> result = competitionService.getParticipantsRegistrationDetails(new PageableDto<>(
            new ParticipantFilterDto(
                c.getId(),
                false,
                null,
                null,
                null,
                null
            ),0,10));

        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getPageable().getPageNumber());
        assertEquals(0, result.getContent().size());
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void partRegistrationDetails_withAllFilter_expectPageWithEmptyContent() {
        ApplicationUser creator = applicationUserRepository.findApplicationUserByUserEmail(TEST_USER_COMPETITION_MANAGER_EMAIL).get();
        Competition c = new CompetitionBuilder(
            applicationUserRepository,
            competitionRepository,
            gradingGroupRepository,
            registerToRepository,
            gradingSystemRepository
        )
            .withCreator(creator)
            .withParticipantsPerGroup(25)
            .withGradingGroups(Set.of("T1"))
            .create();

        Page<ParticipantRegDetailDto> result = competitionService.getParticipantsRegistrationDetails(new PageableDto<>(
            new ParticipantFilterDto(
                c.getId(),
                true,
                "xxxx",
                "yyyy",
                ApplicationUser.Gender.MALE,
                0L
            ),0,10));

        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getPageable().getPageNumber());
    }
}
