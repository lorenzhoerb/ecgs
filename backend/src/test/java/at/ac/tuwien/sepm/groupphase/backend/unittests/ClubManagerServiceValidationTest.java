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
import at.ac.tuwien.sepm.groupphase.backend.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClubManagerServiceValidationTest {
    private final UserService userService;
    private final ApplicationUserRepository userRepository;
    private final DataCleaner cleaner;
    private final ClubManagerTeamImportDataGenerator cmGenerator;

    @Autowired
    public ClubManagerServiceValidationTest(UserService userService, ApplicationUserRepository userRepository, DataCleaner cleaner, ClubManagerTeamImportDataGenerator cmGenerator) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.cleaner = cleaner;
        this.cmGenerator = cmGenerator;
    }

    @BeforeEach
    public void refreshTestDate() {
        cleaner.clear();
        cmGenerator.setup();
    }

    @Test
    public void teamWithInvalidTeamName() {
        ValidationException ve_exception = assertThrows(ValidationException.class, () -> {
                userService.importTeam(
                    ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                    null
                );
            }
        );

        assertThat(ve_exception.getMessage()).contains("Team is empty");

        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                new ClubManagerTeamImportDto(
                    null,
                    ClubManagerTeamImportGeneratorHelper.testTeams.get(0).teamMembers()
                ));
        });

        assertThat(exception.getMessage()).contains("Team name is blank");

        exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                new ClubManagerTeamImportDto(
                    "",
                    ClubManagerTeamImportGeneratorHelper.testTeams.get(0).teamMembers()
                ));
        });

        assertThat(exception.getMessage()).contains("Team name is blank");


        exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                new ClubManagerTeamImportDto(
                    "A".repeat(256),
                    ClubManagerTeamImportGeneratorHelper.testTeams.get(0).teamMembers()
                ));
        });

        assertThat(exception.getMessage()).contains("Team name is too long");
    }

    @Test
    public void teamWithNullMembers() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    null
                ));
        });

        assertThat(exception.errors().get(0)).contains("Team members are empty");
    }

    @Test
    public void teamWithOkTeamName() {
        var results = userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                new ClubManagerTeamImportDto(
                    "A".repeat(254),
                    ClubManagerTeamImportGeneratorHelper.testTeams.get(0).teamMembers()
                )
        );

        assertThat(results.newParticipantsCount()).isEqualTo(4);
    }

    @Test
    public void teamMemberWithInvalidFirstName() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                null, "lastname", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            ));
                        }
                    }
                ));
        });

        assertThat(exception.errors().get(0)).contains("First name must not be blank");


        exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "", "lastname", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            ));
                        }
                    }
                ));
        });

        assertThat(exception.errors().get(0)).contains("First name must not be blank");

        exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "A".repeat(256), "lastname", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            ));
                        }
                    }
                ));
        });

        assertThat(exception.errors().get(0)).contains("First name must be shorter");
    }

    @Test
    public void teamMemberWithInvalidLastName() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", null, ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            ));
                        }
                    }
                ));
        });

        assertThat(exception.errors().get(0)).contains("Last name must not be blank");


        exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "", ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            ));
                        }
                    }
                ));
        });

        assertThat(exception.errors().get(0)).contains("Last name must not be blank");

        exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "A".repeat(256), ApplicationUser.Gender.MALE, new Date(10000000L), "valid@valid.com"
                            ));
                        }
                    }
                ));
        });

        assertThat(exception.errors().get(0)).contains("Last name must be shorter");
    }

    @Test
    public void teamMemberWithInvalidDateOfBirth() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "lastname", ApplicationUser.Gender.MALE, new Date(-1647533357000L), "valid@valid.com"
                            ));
                        }
                    }
                ));
        });

        assertThat(exception.getMessage()).contains("Date of birth must be after");

        ValidationListException exception2 = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
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
    public void teamMemberWithInvalidGender() {
        ValidationListException exception = assertThrows(ValidationListException.class, () -> {
            userService.importTeam(
                ClubManagerTeamImportGeneratorHelper.generatedClubManagers.get(0),
                new ClubManagerTeamImportDto(
                    "A".repeat(252),
                    new ArrayList<>() {
                        {
                            add(new ClubManagerTeamMemberImportDto(
                                "firstname", "lastname", null, new Date(0), "valid@valid.com"
                            ));
                        }
                    }
                ));
        });

        assertThat(exception.getMessage()).contains("Gender field is blank");
    }
}
