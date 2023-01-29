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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


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
    public void importTeam_withInvalidTeamNames_shouldThrowValidationException() {
        ValidationException ve_exception = assertThrows(ValidationException.class, () -> {
                userService.importTeam(
                    null
                );
            }
        );

        assertThat(ve_exception.getMessage()).contains("Team is empty");

        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    null,
                    ClubManagerTeamImportGeneratorHelper.testTeams.get(0).teamMembers()
                ));
        });

        assertThat(exception.getMessage()).contains("Team name is blank");

        exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    "",
                    ClubManagerTeamImportGeneratorHelper.testTeams.get(0).teamMembers()
                ));
        });

        assertThat(exception.getMessage()).contains("Team name is blank");


        exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    "A".repeat(256),
                    ClubManagerTeamImportGeneratorHelper.testTeams.get(0).teamMembers()
                ));
        });

        assertThat(exception.getMessage()).contains("Team name is too long");
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void importTeam_withNullMembers_shouldThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    null
                ));
        });

        assertThat(exception.errors().get(0)).contains("Team members are empty");
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void importTeam_withValidFields_shouldSucceed() {
        var results = userService.importTeam(
                new ClubManagerTeamImportDto(
                    "A".repeat(254),
                    ClubManagerTeamImportGeneratorHelper.testTeams.get(0).teamMembers()
                )
        );

        assertThat(results.getNewParticipantsCount()).isEqualTo(4);
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void importTeam_withTeamMemberWithInvalidFirstName_shouldThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                null, "lastname", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            )); // Thursday, January 1, 1970 2:46:40 AM
                        }
                    }
                ));
        });

        assertThat(exception.errors().get(0)).contains("First name must not be blank");


        exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "", "lastname", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            )); // Thursday, January 1, 1970 2:46:40 AM
                        }
                    }
                ));
        });

        assertThat(exception.errors().get(0)).contains("First name must not be blank");

        exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "A".repeat(256), "lastname", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            )); // Thursday, January 1, 1970 2:46:40 AM
                        }
                    }
                ));
        });

        assertThat(exception.errors().get(0)).contains("First name must be shorter");
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void importTeam_withTeamMemberWithInvalidLastName_shouldThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", null, ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            )); // Thursday, January 1, 1970 2:46:40 AM
                        }
                    }
                ));
        });

        assertThat(exception.errors().get(0)).contains("Last name must not be blank");


        exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            )); // Thursday, January 1, 1970 2:46:40 AM
                        }
                    }
                ));
        });

        assertThat(exception.errors().get(0)).contains("Last name must not be blank");

        exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "A".repeat(256), ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            )); // Thursday, January 1, 1970 2:46:40 AM
                        }
                    }
                ));
        });

        assertThat(exception.errors().get(0)).contains("Last name must be shorter");
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void importTeam_withTeamMemberWithInvalidFlag_shouldThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "lastname", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com", "F".repeat(256)
                            )); // Thursday, January 1, 1970 2:46:40 AM
                        }
                    }
                ));
        });

        assertThat(exception.errors().get(0)).contains("Flag must be shorter");
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void importTeam_withTeamMemberWithInvalidDateOfBirth_shouldThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "lastname", ApplicationUser.Gender.MALE, new Date(-1647533357000L), "valid@valid.com"
                            )); // Wednesday, October 17, 1917 7:50:43 AM
                        }
                    }
                ));
        });

        assertThat(exception.getMessage()).contains("Date of birth must be after");

        ValidationListException exception2 = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "lastname", ApplicationUser.Gender.MALE, null, "valid@valid.com"
                            ));
                        }
                    }
                ));
        });

        assertThat(exception2.errors().get(0)).contains("Date of birth must be given");
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void importTeam_withTeamMemberWithInvalidGender_shouldThrowValidationException() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "lastname", null, new Date(0), "valid@valid.com"
                            )); // Thursday, January 1, 1970 12:00:00 AM
                        }
                    }
                ));
        });

        assertThat(exception.getMessage()).contains("Gender field is blank");
    }

    @Test
    @WithMockUser(username = "cm_test@test.test")
    public void importTeam_withValidTeamAndNonEmptyFlag_shouldSaveBothManagedByAndAddAFlag() {
        userService.importTeam(
            new ClubManagerTeamImportDto(
                "A".repeat(252),
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
        assertThat(testFlag.isPresent()).isTrue();
        assertThat(testFlag.get().getName()).isEqualTo("F".repeat(254));
        var testManagedByes = managedByRepository.findAll();
        assertThat(testManagedByes.size()).isEqualTo(1);
        var testManagedBy = testManagedByes.get(0);
        assertThat(testFlag.get().getClubs().stream().toList().get(0).getId()).isEqualTo(testManagedBy.getId());
        var testIterator = flagsRepository.findAll().iterator();
        assertThat(testIterator.next()).isNotNull();
        assertThat(testIterator.hasNext()).isFalse();
    }
}
