package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public interface CompetitionGeneratorHelper {
    LocalDateTime baseDate = LocalDateTime.of(2022, 9, 20, 0, 0);
    List<Competition> testCompetitions = new ArrayList<>() {
        {
            add(new Competition(
                "name1",
                baseDate,
                baseDate,
                baseDate.minusDays(2),
                baseDate.plusDays(2),
                "desc1",
                "path1",
                true,
                false,
                "example1@example1.com",
                "1123456789"));
            add(new Competition(
                "name2",
                baseDate,
                baseDate,
                baseDate.plusDays(10),
                baseDate.plusDays(15),
                "desc2",
                "path2",
                true,
                false,
                "example2@example2.com",
                "2123456789"));
            add(new Competition(
                "name3",
                baseDate,
                baseDate,
                baseDate.minusDays(10),
                baseDate,
                "desc3",
                "path3",
                true,
                false,
                "example3@example3.com",
                "3123456789"));
            add(new Competition(
                "name4",
                baseDate,
                baseDate,
                baseDate.minusDays(20),
                baseDate.plusDays(1),
                "desc4",
                "path4",
                true,
                false,
                "example4@example4.com",
                "4123456789"));
            add(new Competition(
                "name5",
                baseDate,
                baseDate,
                baseDate.plusDays(9),
                baseDate.plusDays(20),
                "desc5",
                "path5",
                true,
                false,
                "example5@example5.com",
                "5123456789"));
            add(new Competition(
                "name6",
                baseDate,
                baseDate,
                baseDate.minusDays(3),
                baseDate.plusDays(3),
                "desc6",
                "path6",
                true,
                false,
                "example6@example6.com",
                "6123456789"));
            add(new Competition(
                "name7AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                baseDate,
                baseDate,
                baseDate.plusWeeks(4),
                baseDate.plusWeeks(4),
                "desc7",
                "path7",
                true,
                false,
                "example7@example6.com",
                "6123456789"));
            add(new Competition(
                "name8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                baseDate,
                baseDate,
                baseDate.plusWeeks(5).plusDays(1),
                baseDate.plusWeeks(5).plusDays(1),
                "desc7",
                "path7",
                true,
                false,
                "example7@example6.com",
                "6123456789"));
            add(new Competition(
                "name9AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                baseDate,
                baseDate,
                baseDate.plusWeeks(5).minusDays(1),
                baseDate.plusWeeks(5).minusDays(1),
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
                "$2a$10$dgJCPt2/G6wpP3lAZPw9oOuC9zufbyJLE6RfoCXvI/Ai0z9UXMmV." //rootroot
            ));
            add(new SecurityUser(
                "test3@test.test",
                "$2a$10$dgJCPt2/G6wpP3lAZPw9oOuC9zufbyJLE6RfoCXvI/Ai0z9UXMmV." //rootroot
            ));
        }
    };

    List<Competition> generatedCompetitions = new ArrayList<>();
    List<ApplicationUser> generatedCompetitionManagers = new ArrayList<>();
    List<SecurityUser> generatedCompetitionManagersSecUsers = new ArrayList<>();
}
