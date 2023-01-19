package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface TestData {

    static final String INTEGRATION_TEST_USER_COMPETITION_MANAGER_EMAIL = "comp.manager@email.com";

    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestMessageText";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    String BASE_URI = "/api/v1";
    String ACCOUNT_REGISTER_URI = BASE_URI + "/registration";

    String ACCOUNT_REQUEST_PASSWORD_RESET_URI = BASE_URI + "/forgot";

    String ACCOUNT_RESET_PASSWORD_URI = BASE_URI + "/reset";

    String ACCOUNT_CHANGE_PASSWORD_URI = BASE_URI + "/changePassword";

    String ACCOUNT_LOGIN_URI = BASE_URI + "/authentication";


    String ADMIN_USER = "admin@email.com";
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_TOURNAMENT_MANAGER");
        }
    };

    String TEST_ADMIN_USERNAME = "tadmin";
    String TEST_USER_USERNAME = "ttestuser";

    String TEST_DEFAULT_PASSWORD = "tpassword";
    List<String> ACCOUNT_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
            add("ROLE_PARTICIPANT");
        }
    };



}
