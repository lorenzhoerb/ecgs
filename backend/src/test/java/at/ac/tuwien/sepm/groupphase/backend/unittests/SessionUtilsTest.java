package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.util.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class SessionUtilsTest {

    private final SessionUtils sessionUtils;

    public SessionUtilsTest(SessionUtils sessionUtils) {
        this.sessionUtils = sessionUtils;
    }
}
