package at.ac.tuwien.sepm.groupphase.backend.basetest;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CompetitionDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@SpringBootTest
@ActiveProfiles("test")
public abstract class TestDataProvider {

    @Autowired
    private CustomUserDetailService customUserDetailService;

    protected static final String BASE_URI = "/api/v1";
    protected static final String COMPETITION_URI = "/competitions";
    protected static final String COMPETITION_BASE_URI = BASE_URI + COMPETITION_URI;

    protected static final String TEST_USER_BASIC_EMAIL = "basic@email.com";
    protected static final String TEST_USER_COMPETITION_MANAGER_EMAIL = "comp.manager@email.com";

    protected UserRegisterDto getValidRegistrationDtoForCompetitionManager() {
        return new UserRegisterDto(
            TEST_USER_COMPETITION_MANAGER_EMAIL,
            "12345678",
            "firstNameTest",
            "lastNameTest",
            ApplicationUser.Gender.FEMALE,
            new Date(),
            ApplicationUser.Role.TOURNAMENT_MANAGER);
    }

    protected CompetitionDetailDto getValidCompetitionDetailDto() {
        return new CompetitionDetailDto()
            .setEmail("competition@gmx.at")
            .setName("TestTitle")
            .setDescription("Test Description")
            .setBeginOfCompetition(LocalDateTime.of(LocalDate.now().plusDays(20), LocalTime.of(9, 0)))
            .setEndOfCompetition(LocalDateTime.of(LocalDate.now().plusDays(20), LocalTime.of(18, 0)))
            .setBeginOfRegistration(LocalDateTime.now().plusDays(1))
            .setEndOfRegistration(LocalDateTime.now().plusDays(5))
            .setPublic(true)
            .setDraft(true);
    }

    protected void setUpCompetitionUser() {
       customUserDetailService.registerUser(getValidRegistrationDtoForCompetitionManager());
    }
}
