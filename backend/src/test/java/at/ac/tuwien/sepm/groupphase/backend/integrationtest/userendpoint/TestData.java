package at.ac.tuwien.sepm.groupphase.backend.integrationtest.userendpoint;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.UserEndpoint;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Competition;

import java.time.LocalDateTime;
import java.util.*;

public interface TestData extends at.ac.tuwien.sepm.groupphase.backend.integrationtest.TestData {
    String BASE_CALENDAR_URI = UserEndpoint.BASE_URI + "/calendar";
    String BASE_IMPORT_TEAM_URI = UserEndpoint.BASE_URI + "/import-team";
    List<String> CALENDAR_TEST_ROLES = new ArrayList<>() {
        {
            add("ROLE_" + ApplicationUser.Role.TOURNAMENT_MANAGER.name());
        }
    };
    List<String> TEAM_IMPORT_TEST_ROLES = new ArrayList<>() {
        {
            add("ROLE_" + ApplicationUser.Role.CLUB_MANAGER.name());
        }
    };
}
