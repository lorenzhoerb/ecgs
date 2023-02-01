package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestDataProvider;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.*;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.UserMapper;

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
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ForbiddenException;
import at.ac.tuwien.sepm.groupphase.backend.exception.NotFoundException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.CompetitionRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingGroupRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.GradingSystemRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.JudgeRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterConstraintRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.RegisterToRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReportFileRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ReportRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SecurityUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.CompetitionService;
import at.ac.tuwien.sepm.groupphase.backend.service.ReportService;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CompetitionServiceTest extends TestDataProvider {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private RegisterConstraintRepository registerConstraintRepository;

    @Autowired
    private GradingSystemRepository gradingSystemRepository;

    @Autowired
    private JudgeRepository judgeRepository;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private GradingGroupRepository gradingGroupRepository;

    @Autowired
    private RegisterToRepository registerToRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportFileRepository reportFileRepository;

    @Autowired
    private ManagedByRepository managedByRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    SessionUtils sessionUtils;

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
        registerConstraintRepository.deleteAll();
        gradeRepository.deleteAll();
        reportRepository.deleteAll();
        reportFileRepository.deleteAll();
        managedByRepository.deleteAll();
        registerToRepository.deleteAll();
        applicationUserRepository.deleteAll();
        competitionRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        gradingSystemRepository.deleteAll();
        judgeRepository.deleteAll();
        setUpCompetitionUser();
        setUpParticipantUser();
    }

    @AfterEach
    public void afterEach() {
        registerConstraintRepository.deleteAll();
        gradeRepository.deleteAll();
        reportRepository.deleteAll();
        reportFileRepository.deleteAll();
        managedByRepository.deleteAll();
        registerToRepository.deleteAll();
        applicationUserRepository.deleteAll();
        competitionRepository.deleteAll();
        gradingGroupRepository.deleteAll();
        gradingSystemRepository.deleteAll();
        judgeRepository.deleteAll();
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

        Page<UserDetailDto> participants =
            competitionService.getParticipants(competition.getId(), null);
        assertNotNull(participants);
        assertEquals(participants.getTotalElements(), 1);

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

        Page<UserDetailDto> participants =
            competitionService.getParticipants(competition.getId(), null);
        assertNotNull(participants);
        assertEquals(participants.getTotalElements(), 0);
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
            competitionService.getParticipants(-1L, null);
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
            competitionService.getParticipants(-1L, null);
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
            competitionService.getParticipants(competition.getId(), null);
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
            gradingSystemRepository,
            gradeRepository
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
            gradingSystemRepository,
            gradeRepository
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
            gradingSystemRepository,
            gradeRepository
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
            gradingSystemRepository,
            gradeRepository
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
                0L,
                null
            ),0,10));

        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getPageable().getPageNumber());
    }
    @Test
    public void advanceSearchCompetitions_withNoSearchParams_expectPageList() {
        Competition c1 = getValidCompetitionEntity();
        Competition c2 = getValidCompetitionEntity();
        Competition c3 = getValidCompetitionEntity();
        Competition cs1 = competitionRepository.save(c1);
        Competition cs2 = competitionRepository.save(c2);
        Competition cs3 = competitionRepository.save(c3);

        Page<CompetitionListDto> competitions = competitionService.searchCompetitionsAdvanced(null);
        assertNotNull(competitions);
        assertEquals(3, competitions.getTotalElements());
        assertEquals(0, competitions.getPageable().getPageNumber());
        List<CompetitionListDto> compList = competitions.getContent();
        assertEquals(3, compList.size());

        assertEquals(
            1,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs1.getId())).toList().size()
        );

        assertEquals(
            1,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs2.getId())).toList().size()
        );

        assertEquals(
            1,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs3.getId())).toList().size()
        );
    }

    @Test
    public void advanceSearchCompetitions_withNameLike_expectList() {
        Competition c1 = getValidCompetitionEntity();
        c1.setName("Peter");
        Competition c2 = getValidCompetitionEntity();
        c2.setName("Competition Haus");
        Competition c3 = getValidCompetitionEntity();
        c3.setName("haus");
        Competition cs1 = competitionRepository.save(c1);
        Competition cs2 = competitionRepository.save(c2);
        Competition cs3 = competitionRepository.save(c3);

        AdvanceCompetitionSearchDto searchParams = new AdvanceCompetitionSearchDto();
        searchParams.setName("Haus");

        Page<CompetitionListDto> competitions = this.competitionService.searchCompetitionsAdvanced(searchParams);

        assertNotNull(competitions);
        assertEquals(2, competitions.getTotalElements());
        assertEquals(0, competitions.getPageable().getPageNumber());
        List<CompetitionListDto> compList = competitions.getContent();
        assertEquals(2, compList.size());

        assertEquals(
            0,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs1.getId())).toList().size()
        );

        assertEquals(
            1,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs2.getId())).toList().size()
        );

        assertEquals(
            1,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs3.getId())).toList().size()
        );
    }

    @Test
    public void advanceSearchCompetitions_isRegistrationOpen() {
        LocalDateTime now = LocalDateTime.now();

        //open
        Competition c1 = getValidCompetitionEntity();
        c1.setBeginOfRegistration(now.minusDays(2));
        c1.setEndOfRegistration(now.plusDays(2));

        //closed
        Competition c2 = getValidCompetitionEntity();
        c2.setBeginOfRegistration(now.plusDays(2));
        c2.setEndOfRegistration(now.plusDays(4));

        //closed
        Competition c3 = getValidCompetitionEntity();
        c3.setBeginOfRegistration(now.minusDays(2));
        c3.setEndOfRegistration(now.minusDays(1));

        Competition cs1 = competitionRepository.save(c1);
        Competition cs2 = competitionRepository.save(c2);
        Competition cs3 = competitionRepository.save(c3);

        AdvanceCompetitionSearchDto searchParams = new AdvanceCompetitionSearchDto();
        searchParams.setIsRegistrationOpen(true);

        Page<CompetitionListDto> competitions = this.competitionService.searchCompetitionsAdvanced(searchParams);

        assertNotNull(competitions);
        assertEquals(1, competitions.getTotalElements());
        assertEquals(0, competitions.getPageable().getPageNumber());
        List<CompetitionListDto> compList = competitions.getContent();
        assertEquals(1, compList.size());

        assertEquals(
            1,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs1.getId())).toList().size()
        );

        assertEquals(
            0,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs2.getId())).toList().size()
        );

        assertEquals(
            0,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs3.getId())).toList().size()
        );
    }

    @Test
    public void advanceSearchCompetitions_isPublic() {
        Competition c1 = getValidCompetitionEntity();
        c1.setPublic(true);

        Competition c2 = getValidCompetitionEntity();
        c2.setPublic(false);

        Competition c3 = getValidCompetitionEntity();
        c3.setPublic(false);

        Competition cs1 = competitionRepository.save(c1);
        Competition cs2 = competitionRepository.save(c2);
        Competition cs3 = competitionRepository.save(c3);

        AdvanceCompetitionSearchDto searchParams = new AdvanceCompetitionSearchDto();
        searchParams.setIsPublic(true);

        Page<CompetitionListDto> competitions = this.competitionService.searchCompetitionsAdvanced(searchParams);

        assertNotNull(competitions);
        assertEquals(1, competitions.getTotalElements());
        assertEquals(0, competitions.getPageable().getPageNumber());
        List<CompetitionListDto> compList = competitions.getContent();
        assertEquals(1, compList.size());

        assertEquals(
            1,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs1.getId())).toList().size()
        );

        assertEquals(
            0,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs2.getId())).toList().size()
        );

        assertEquals(
            0,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs3.getId())).toList().size()
        );
    }

    @Test
    public void advanceSearchCompetitions_setEndOfCompetitionAfter() {
        LocalDateTime now = LocalDateTime.now();

        Competition c1 = getValidCompetitionEntity();
        c1.setEndOfCompetition(now.plusDays(3));

        Competition c2 = getValidCompetitionEntity();
        c2.setEndOfCompetition(now.minusDays(2));

        Competition c3 = getValidCompetitionEntity();
        c3.setEndOfCompetition(now.plusSeconds(30));

        Competition cs1 = competitionRepository.save(c1);
        Competition cs2 = competitionRepository.save(c2);
        Competition cs3 = competitionRepository.save(c3);

        AdvanceCompetitionSearchDto searchParams = new AdvanceCompetitionSearchDto();
        searchParams.setEndOfCompetitionAfter(now);

        Page<CompetitionListDto> competitions = this.competitionService.searchCompetitionsAdvanced(searchParams);

        assertNotNull(competitions);
        assertEquals(2, competitions.getTotalElements());
        assertEquals(0, competitions.getPageable().getPageNumber());
        List<CompetitionListDto> compList = competitions.getContent();
        assertEquals(2, compList.size());

        assertEquals(
            1,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs1.getId())).toList().size()
        );

        assertEquals(
            0,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs2.getId())).toList().size()
        );

        assertEquals(
            1,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs3.getId())).toList().size()
        );
    }

    @Test
    public void advanceSearchCompetitions_filterMultiple() {
        Competition c1 = getValidCompetitionEntity();
        c1.setName("Haus ist groß");
        c1.setPublic(true);

        Competition c2 = getValidCompetitionEntity();
        c2.setName("Das ist ein Haus");
        c2.setPublic(false);

        Competition c3 = getValidCompetitionEntity();
        c3.setName("Kevin");
        c3.setPublic(false);

        Competition cs1 = competitionRepository.save(c1);
        Competition cs2 = competitionRepository.save(c2);
        Competition cs3 = competitionRepository.save(c3);

        AdvanceCompetitionSearchDto searchParams = new AdvanceCompetitionSearchDto();
        searchParams.setIsPublic(true);
        searchParams.setName("haus");

        Page<CompetitionListDto> competitions = this.competitionService.searchCompetitionsAdvanced(searchParams);

        assertNotNull(competitions);
        assertEquals(1, competitions.getTotalElements());
        assertEquals(0, competitions.getPageable().getPageNumber());
        List<CompetitionListDto> compList = competitions.getContent();
        assertEquals(1, compList.size());

        assertEquals(
            1,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs1.getId())).toList().size()
        );

        assertEquals(
            0,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs2.getId())).toList().size()
        );

        assertEquals(
            0,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs3.getId())).toList().size()
        );
    }

    @Test
    public void advanceSearchCompetitions_checkExcludeDraft() {
        Competition c1 = getValidCompetitionEntity();
        c1.setPublic(true);
        c1.setName("Haus der Weisen");

        Competition c2 = getValidCompetitionEntity();
        c2.setPublic(false);
        c2.setName("Das ist ein kleines Haus");

        Competition c3 = getValidCompetitionEntity();
        c3.setPublic(false);
        c3.setName("Die Wohnung ist hässlich");

        Competition c4 = getValidCompetitionEntity();
        c4.setPublic(true);
        c4.setName("XXL Haus");
        c4.setDraft(true);

        Competition cs1 = competitionRepository.save(c1);
        Competition cs2 = competitionRepository.save(c2);
        Competition cs3 = competitionRepository.save(c3);
        Competition cs4 = competitionRepository.save(c4);

        AdvanceCompetitionSearchDto searchParams = new AdvanceCompetitionSearchDto();
        searchParams.setIsPublic(true);
        searchParams.setName("haus");

        Page<CompetitionListDto> competitions = this.competitionService.searchCompetitionsAdvanced(searchParams);

        assertNotNull(competitions);
        assertEquals(1, competitions.getTotalElements());
        assertEquals(0, competitions.getPageable().getPageNumber());
        List<CompetitionListDto> compList = competitions.getContent();
        assertEquals(1, compList.size());

        assertEquals(
            1,
            compList.stream().filter(c -> Objects.equals(c.getId(), cs1.getId())).toList().size()
        );
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void addFlagsForUsers_addFlagForNullUsers_shouldThrowValidationException() {
        SimpleFlagDto flag = new SimpleFlagDto(-1L, "new");
        Competition competition = getValidCompetitionEntity();
        competition = competitionRepository.save(competition);
        Long id = competition.getId();

        UserDetailSetFlagDto dto = new UserDetailSetFlagDto();
        dto.setFlag(flag);
        dto.setUsers(null);

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            competitionService.addFlagsForUsers(id, dto);
        });
        assertThat(e.getMessage()).contains("users must be set");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void addFlagsForUsers_addFlagForUsersWithNullObjects_shouldThrowValidationException() {
        SimpleFlagDto flag = new SimpleFlagDto(-1L, "new");

        HashSet<UserDetailDto> set = new HashSet<>();
        set.add(null);

        UserDetailSetFlagDto dto = new UserDetailSetFlagDto();
        dto.setFlag(flag);
        dto.setUsers(set);

        Competition competition = getValidCompetitionEntity();
        competition = competitionRepository.save(competition);
        Long id = competition.getId();

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            competitionService.addFlagsForUsers(id, dto);
        });
        assertThat(e.getMessage()).contains("User was null");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void addFlagsForUsers_addFlagForUsersWithNullIds() {
        SimpleFlagDto flag = new SimpleFlagDto(-1L, "new");

        Competition competition = getValidCompetitionEntity();
        competition = competitionRepository.save(competition);
        Long id = competition.getId();

        HashSet<UserDetailDto> set = new HashSet<>();
        set.add(new UserDetailDto(null, "", "", ApplicationUser.Gender.FEMALE, new Date(), ""));

        UserDetailSetFlagDto dto = new UserDetailSetFlagDto();
        dto.setFlag(flag);
        dto.setUsers(set);

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            competitionService.addFlagsForUsers(id, dto);
        });
        assertThat(e.getMessage()).contains("User id was null");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void addFlagsForUsers_addFlagForNullName_shouldThrowValidationException() {
        SimpleFlagDto flag = new SimpleFlagDto(-1l, null);

        Competition competition = getValidCompetitionEntity();
        competition = competitionRepository.save(competition);
        Long id = competition.getId();

        UserDetailSetFlagDto dto = new UserDetailSetFlagDto();
        dto.setFlag(flag);
        dto.setUsers(new HashSet<>());

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            competitionService.addFlagsForUsers(id, dto);
        });
        assertThat(e.getMessage()).contains("Flag must be specified");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void addFlagsForUsers_addFlagForEmptyName_shouldThrowValidationException() {
        SimpleFlagDto flag = new SimpleFlagDto(-1L, "");

        Competition competition = getValidCompetitionEntity();
        competition = competitionRepository.save(competition);
        Long id = competition.getId();

        UserDetailSetFlagDto dto = new UserDetailSetFlagDto();
        dto.setFlag(flag);
        dto.setUsers(new HashSet<>());

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
           competitionService.addFlagsForUsers(id, dto);
        });
        assertThat(e.getMessage()).contains("Flag must be specified");
    }

    @Test
    @WithMockUser(username = TEST_USER_COMPETITION_MANAGER_EMAIL)
    public void addFlagsForUsers_addFlagForName256_shouldThrowValidationException() {
        SimpleFlagDto flag = new SimpleFlagDto(-1L, "A".repeat(256));

        Competition competition = getValidCompetitionEntity();
        competition = competitionRepository.save(competition);
        Long id = competition.getId();

        UserDetailSetFlagDto dto = new UserDetailSetFlagDto();
        dto.setFlag(flag);
        dto.setUsers(new HashSet<>());

        ValidationListException e = assertThrows(ValidationListException.class, () -> {
            competitionService.addFlagsForUsers(id, dto);
        });
        assertThat(e.getMessage()).contains("Flag is too long");
    }

    @Test
    @WithMockUser(value = "participant5@report.test")
    public void getCurrentUserReportDownloadInclusionRuleOptions_asNonRegisteredParticipant_shouldReturnFalseForBothOptions() throws Exception {
        var compEntity = beforeEachReportTest();
        var res = competitionService.getCurrentUserReportDownloadInclusionRuleOptions(compEntity.getId());

        assertFalse(res.getCanGenerateReportForSelf());
        assertFalse(res.getCanGenerateReportForTeam());
    }

    @Test
    @WithMockUser(value = "club_manager1@report.test")
    public void getCurrentUserReportDownloadInclusionRuleOptions_asClubManagerWithRegisteredParticipants_shouldReturnFalseForSelfTrueForTeamOptions() throws Exception {
        var compEntity = beforeEachReportTest();
        var res = competitionService.getCurrentUserReportDownloadInclusionRuleOptions(compEntity.getId());

        assertFalse(res.getCanGenerateReportForSelf());
        assertTrue(res.getCanGenerateReportForTeam());
    }

    @Test
    @WithMockUser(value = "club_manager2@report.test")
    public void getCurrentUserReportDownloadInclusionRuleOptions_asClubManagerWithNoRegisteredParticipants_shouldReturnFalseForSelfTrueForTeamOptions() throws Exception {
        var compEntity = beforeEachReportTest();
        var res = competitionService.getCurrentUserReportDownloadInclusionRuleOptions(compEntity.getId());

        assertFalse(res.getCanGenerateReportForSelf());
        assertTrue(res.getCanGenerateReportForTeam());
    }

    @Test
    @WithMockUser(value = "club_manager3@report.test")
    public void getCurrentUserReportDownloadInclusionRuleOptions_asClubManagerWithOneRegisteredParticipantsAndHimSelf_shouldReturnTrueForSelfTrueForTeamOptions() throws Exception {
        var compEntity = beforeEachReportTest();
        var res = competitionService.getCurrentUserReportDownloadInclusionRuleOptions(compEntity.getId());

        assertTrue(res.getCanGenerateReportForSelf());
        assertFalse(res.getCanGenerateReportForTeam());
    }
}
