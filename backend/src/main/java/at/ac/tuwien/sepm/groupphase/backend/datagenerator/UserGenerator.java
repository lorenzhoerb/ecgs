package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UserRegisterDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.service.impl.CustomUserDetailService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Profile("generateData")
@Component
public class UserGenerator {

    private final CustomUserDetailService customUserDetailService;

    public UserGenerator(CustomUserDetailService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
    }


    @PostConstruct
    private void generateUsers() {
        customUserDetailService.registerUser(new UserRegisterDto(
            "tm@email.com",
            "12345678",
            "Franz",
            "Fischer",
            ApplicationUser.Gender.MALE,
            new Date(99, 1, 1),
            ApplicationUser.Role.TOURNAMENT_MANAGER
        ));

        customUserDetailService.registerUser(new UserRegisterDto(
            "cm@email.com",
            "12345678",
            "Andrea",
            "Schilling",
            ApplicationUser.Gender.FEMALE,
            new Date(70, 1, 1),
            ApplicationUser.Role.CLUB_MANAGER
        ));

        customUserDetailService.registerUser(new UserRegisterDto(
            "pa@email.com",
            "12345678",
            "Kevin",
            "Klein",
            ApplicationUser.Gender.FEMALE,
            new Date(70, 1, 1),
            ApplicationUser.Role.PARTICIPANT
        ));
    }
}
