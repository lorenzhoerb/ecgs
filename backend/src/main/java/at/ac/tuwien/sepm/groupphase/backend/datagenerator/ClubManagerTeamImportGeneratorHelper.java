package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamImportDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ClubManagerTeamMemberImportDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.SecurityUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface ClubManagerTeamImportGeneratorHelper {
    List<ApplicationUser> testClubManagers = new ArrayList<>() {
        {
            add(new ApplicationUser(
                ApplicationUser.Role.CLUB_MANAGER,
                "fnclub",
                "lnmanager",
                ApplicationUser.Gender.MALE,
                new Date(946762043000L), // 2000-01-01
                "/test/test")
            );
        }
    };

    List<SecurityUser> testClubManagersSecUsers = new ArrayList<>() {
        {
            add(new SecurityUser(
                    "cm_test@test.test",
                    "$2a$10$dgJCPt2/G6wpP3lAZPw9oOuC9zufbyJLE6RfoCXvI/Ai0z9UXMmV." // rootroot
                )
            );
        }
    };

    List<ClubManagerTeamImportDto> testTeams = new ArrayList<>() {
        {
            add(new ClubManagerTeamImportDto(
                "testteam_1",
                new ArrayList<>() {
                    {
                        add(new ClubManagerTeamMemberImportDto(
                            "testteammemberonefn",
                            "testteammemberoneln",
                            ApplicationUser.Gender.FEMALE,
                            new Date(1005920443000L),
                            "testteam_member1@test.test"));
                        add(new ClubManagerTeamMemberImportDto(
                            "testteammembertwofn",
                            "testteammembertwoln",
                            ApplicationUser.Gender.FEMALE,
                            new Date(1006920443000L),
                            "testteam_member2@test.test"));
                        add(new ClubManagerTeamMemberImportDto(
                            "testteammemberthreefn",
                            "testteammemberthreeln",
                            ApplicationUser.Gender.MALE,
                            new Date(1007920443000L),
                            "testteam_member3@test.test"));
                        add(new ClubManagerTeamMemberImportDto(
                            "testteammemberfourfn",
                            "testteammemberfourln",
                            ApplicationUser.Gender.OTHER,
                            new Date(1008920443000L),
                            "testteam_member4@test.test"));
                    }
                }
            ));
            add(new ClubManagerTeamImportDto(
                "testteam_2",
                new ArrayList<>() {
                    {
                        add(new ClubManagerTeamMemberImportDto(
                            "testteammemberfivefn",
                            "testteammemberfiveln",
                            ApplicationUser.Gender.FEMALE,
                            new Date(1004920443000L),
                            "testteam_member2@test.test"));
                        add(new ClubManagerTeamMemberImportDto(
                            "testteammembersixfn",
                            "testteammembersixln",
                            ApplicationUser.Gender.FEMALE,
                            new Date(1003920443000L),
                            "testteam_member6@test.test"));
                    }
                }
            ));
        }
    };

    List<ClubManagerTeamImportDto> testTeams_withInvalidMembers = new ArrayList<>() {
        {
            add(new ClubManagerTeamImportDto(
                "testteam_3",
                new ArrayList<>() {
                    {
                        add(new ClubManagerTeamMemberImportDto(
                            null,
                            "lnn",
                            ApplicationUser.Gender.MALE,
                            new Date(1039459726000L),
                            "test_invalid1@test.test"
                        ));
                        add(new ClubManagerTeamMemberImportDto(
                            "fnn",
                            "lnnn",
                            ApplicationUser.Gender.FEMALE,
                            new Date(939459726000L),
                            "test_invalid2@test.test"
                        ));
                    }
                }
            ));
        }
    };

    List<ClubManagerTeamMemberImportDto> testTeamMembers_valid = new ArrayList<>() {
        {
            add(new ClubManagerTeamMemberImportDto(
                "firstname",
                "lastname",
                ApplicationUser.Gender.MALE,
                new Date(977074643000L),
                "valid@valid.valid"
            ));
            add(new ClubManagerTeamMemberImportDto(
                "firstnametwo",
                "lastnamethree",
                ApplicationUser.Gender.FEMALE,
                new Date(987074643000L),
                "valid2@valid.valid"
            ));
            add(new ClubManagerTeamMemberImportDto(
                "firstnametrhee",
                "lastnamethreee",
                ApplicationUser.Gender.OTHER,
                new Date(997074643000L),
                "valid3@valid.valid"
            ));
        }
    };

    List<ClubManagerTeamImportDto> testTeams_withInvalidTeamName = new ArrayList<>() {
        {
            add(new ClubManagerTeamImportDto(
                null,
                new ArrayList<>() {
                    {
                        add(new ClubManagerTeamMemberImportDto(
                            "fnnn",
                            "lnnn",
                            ApplicationUser.Gender.OTHER,
                            new Date(1055559726000L),
                            "test_invalid3@test.test"
                        ));
                        add(new ClubManagerTeamMemberImportDto(
                            "testfnnn",
                            "testlnn",
                            ApplicationUser.Gender.MALE,
                            new Date(999559726000L),
                            "test_invalid4@test.test"
                        ));
                    }
                }
            ));
        }
    };

    List<ApplicationUser> generatedClubManagers = new ArrayList<>();
}
