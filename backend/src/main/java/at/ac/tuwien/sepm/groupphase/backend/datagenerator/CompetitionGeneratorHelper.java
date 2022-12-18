package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;


public interface CompetitionGeneratorHelper {
    List<Competition> testCompetitions = new ArrayList<>() {
        {
            add(new Competition(
                "name1",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(2),
                "desc1",
                "path1",
                true,
                false,
                "example1@example1.com",
                "1123456789"));
            add(new Competition(
                "name2",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(10),
                LocalDateTime.now().plusDays(15),
                "desc2",
                "path2",
                true,
                false,
                "example2@example2.com",
                "2123456789"));
            add(new Competition(
                "name3",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now(),
                "desc3",
                "path3",
                true,
                false,
                "example3@example3.com",
                "3123456789"));
            add(new Competition(
                "name4",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(20),
                LocalDateTime.now().plusDays(1),
                "desc4",
                "path4",
                true,
                false,
                "example4@example4.com",
                "4123456789"));
            add(new Competition(
                "name5",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(9),
                LocalDateTime.now().plusDays(20),
                "desc5",
                "path5",
                true,
                false,
                "example5@example5.com",
                "5123456789"));
            add(new Competition(
                "name6",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(3),
                "desc6",
                "path6",
                true,
                false,
                "example6@example6.com",
                "6123456789"));
            add(new Competition(
                "name7AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusWeeks(4),
                LocalDateTime.now().plusWeeks(4),
                "desc7",
                "path7",
                true,
                false,
                "example7@example6.com",
                "6123456789"));
            add(new Competition(
                "name8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusWeeks(5).plusDays(1),
                LocalDateTime.now().plusWeeks(5).plusDays(1),
                "desc7",
                "path7",
                true,
                false,
                "example7@example6.com",
                "6123456789"));
            add(new Competition(
                "name9AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusWeeks(5).minusDays(1),
                LocalDateTime.now().plusWeeks(5).minusDays(1),
                "desc7",
                "path7",
                true,
                false,
                "example7@example6.com",
                "6123456789"));
        }};
    List<ApplicationUser> testCompetitionManagers = new ArrayList<>() {
        {
            var au1 = new ApplicationUser(
                ApplicationUser.Role.TOURNAMENT_MANAGER,
                "fn_one",
                "ln_one",
                ApplicationUser.Gender.MALE,
                new Date(),
                "/hello/first");
            add(au1);

            var au2 = new ApplicationUser(
                ApplicationUser.Role.TOURNAMENT_MANAGER,
                "fs_two",
                "ln_two",
                ApplicationUser.Gender.MALE,
                new Date(),
                "/hello/second");
            add(au2);

            var au3 = (new ApplicationUser(
                ApplicationUser.Role.TOURNAMENT_MANAGER,
                "fs_three",
                "ln_three",
                ApplicationUser.Gender.MALE,
                new Date(),
                "/hello/third"));
            add(au3);
        }
    };
    List<SecurityUser> testCompetitionManagersManagersSecUsers = new ArrayList<>() {
        {
            add(new SecurityUser(
                "test@test.test",
                "$2a$10$dgJCPt2/G6wpP3lAZPw9oOuC9zufbyJLE6RfoCXvI/Ai0z9UXMmV." //rootroot
            ));
            add(new SecurityUser(
                "test2@test.test",
                "$2a$10$dgJCPt2/G6wpP3lAZPw9oOuC9zufbyJLE6RfoCXvI/Ai0z9UXMmV."
            ));
            add(new SecurityUser(
                "test3@test.test",
                "$2a$10$dgJCPt2/G6wpP3lAZPw9oOuC9zufbyJLE6RfoCXvI/Ai0z9UXMmV."
            ));
        }
    };

    List<Competition> generatedCompetitions = new ArrayList<>();
    List<ApplicationUser> generatedCompetitionManagers = new ArrayList<>();
    List<SecurityUser> generatedCompetitionManagersSecUsers = new ArrayList<>();
}
