package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.datagenerator.ClubManagerTeamImportDataGenerator;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.ClubManagerTeamImportGeneratorHelper;
import at.ac.tuwien.sepm.groupphase.backend.datagenerator.DataCleaner;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamMemberImportDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationListException;
import at.ac.tuwien.sepm.groupphase.backend.repository.ApplicationUserRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.FlagsRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ManagedByRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class ClubManagerServiceValidationTest {
    private final UserService userService;
    private final ApplicationUserRepository userRepository;
    private final DataCleaner cleaner;
    private final ClubManagerTeamImportDataGenerator cmGenerator;
    private final FlagsRepository flagsRepository;
    private final ManagedByRepository managedByRepository;

    @Autowired
    public ClubManagerServiceValidationTest(UserService userService, ApplicationUserRepository userRepository, DataCleaner cleaner, ClubManagerTeamImportDataGenerator cmGenerator, FlagsRepository flagsRepository, ManagedByRepository managedByRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.cleaner = cleaner;
        this.cmGenerator = cmGenerator;
        this.flagsRepository = flagsRepository;
        this.managedByRepository = managedByRepository;
    }

    @BeforeEach
    public void refreshTestDate() {
        cleaner.clear();
        cmGenerator.setup();
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenNullMembers_whenImportingTeam_expectToThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    null
                ));
        });

        assertTrue(exception.errors().get(0).contains("Team members are empty"));
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenValidTeam_whenImportingTeam_expectToSucceed() {
        var results = userService.importTeam(
            new ClubManagerTeamImportDto(
                ClubManagerTeamImportGeneratorHelper.testTeams.get(0).teamMembers()
            )
        );

        assertEquals(results.getNewParticipantsCount(), 4);
        assertEquals(
            managedByRepository.findAllByManagerIs(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0)
            ).size(),
            5
        );
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenTeamMemberWithFirstNameNull_whenImportingTeam_expectToThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                null, "lastname", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            )); // Thursday, January 1, 1970 2:46:40 AM
                        }
                    }
                ));
        });

        assertTrue(exception.errors().get(0).contains("First name must not be blank"));
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenTeamMemberWithFirstNameEmpty_whenImportingTeam_expectToThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "", "lastname", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            )); // Thursday, January 1, 1970 2:46:40 AM
                        }
                    }
                ));
        });

        assertTrue(exception.errors().get(0).contains("First name must not be blank"));
    }
    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenTeamMemberWithEmptyFirstTooLong_whenImportingTeam_expectToThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "A".repeat(256), "lastname", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            )); // Thursday, January 1, 1970 2:46:40 AM
                        }
                    }
                ));
        });

        assertTrue(exception.errors().get(0).contains("First name must be shorter"));
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenTeamMemberWithLastNameNull_whenImportingTeam_expectToThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", null, ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            )); // Thursday, January 1, 1970 2:46:40 AM
                        }
                    }
                ));
        });

        assertTrue(exception.errors().get(0).contains("Last name must not be blank"));
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenTeamMemberWithLastNameEmpty_whenImportingTeam_expectToThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            )); // Thursday, January 1, 1970 2:46:40 AM
                        }
                    }
                ));
        });

        assertTrue(exception.errors().get(0).contains("Last name must not be blank"));
    }
    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenTeamMemberWithLastNameTooLong_whenImportingTeam_expectToThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "A".repeat(256), ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            )); // Thursday, January 1, 1970 2:46:40 AM
                        }
                    }
                ));
        });

        assertTrue(exception.errors().get(0).contains("Last name must be shorter"));
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenTeamMemberWithDateOfBirthTooFarInThePast_whenImportingTeam_shouldThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "lastname", ApplicationUser.Gender.MALE, new Date(-1647533357000L), "valid@valid.com"
                            )); // Wednesday, October 17, 1917 7:50:43 AM
                        }
                    }
                ));
        });

        assertTrue(exception.getMessage().contains("Date of birth must be after"));
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenTeamMemberWithDateOfBirthNull_whenImportingTeam_shouldThrowValidationException() {
        ValidationListException exception2 = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "lastname", ApplicationUser.Gender.MALE, null, "valid@valid.com"
                            ));
                        }
                    }
                ));
        });

        assertTrue(exception2.errors().get(0).contains("Date of birth must be given"));
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenTeamMemberWithGenderNull_whenImportingTeam_shouldThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "lastname", null, new Date(0), "valid@valid.com"
                            )); // Thursday, January 1, 1970 12:00:00 AM
                        }
                    }
                ));
        });

        assertTrue(exception.getMessage().contains("Gender field is blank"));
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void givenValidTeamAndNonEmptyFlagField_whenImportingTeam_expectToSaveManagedByAndAddAFlag() {
        userService.importTeam(
            new ClubManagerTeamImportDto(
                new ArrayList<>() {
                    {
                        add(new ClubManagerTeamMemberImportDto(
                            "firstname", "lastname", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com", "F".repeat(254)
                        )); // Thursday, January 1, 1970 2:46:40 AM
                    }
                }
            )
        );
        var testFlag = flagsRepository.findByName("F".repeat(254));
        assertTrue(testFlag.isPresent());
        assertEquals(testFlag.get().getName(), "F".repeat(254));

        var testManagedByes = managedByRepository.findAll();
        assertEquals(testManagedByes.size(), 2); // and self

        var testManagedBy = testManagedByes.get(1);
        assertEquals(testFlag.get().getClubs().stream().toList().get(0).getId(), testManagedBy.getId());

        var testIterator = flagsRepository.findAll().iterator();
        assertNotNull(testIterator.next());
        assertFalse(testIterator.hasNext());
    }
}
