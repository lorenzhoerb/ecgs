package at.ac.tuwien.sepm.groupphase.backend.endpoint;

import javax.annotation.security.PermitAll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

/**
 * This endpoint is used for kubernetes health checks.
 */
@RestController
@RequestMapping("/health")
public class CustomHealthEndpoint {
    static final String BASE_PATH = "/health";
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ApplicationContext applicationContext;
    private boolean status = true;

    @Autowired
    public CustomHealthEndpoint(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<String> getHealth() {
        LOGGER.info("GET {}", BASE_PATH);
        if (status) {
            return ResponseEntity.ok("OK");
        }
        return ResponseEntity.internalServerError().build();
    }

    /**
      * Before the shutdown of a pod this url will be called. Afterwards the health probes fail. Therefore the pod
      * is removed from the healthy pods which are exposed. This way a zero downtime upgrade is possible.
    */
    @PermitAll
    @GetMapping("/prepareShutdown")
    public void preShutdown() {
        LOGGER.info("GET {}/prepareShutdown", BASE_PATH);
        AvailabilityChangeEvent.publish(applicationContext, LivenessState.BROKEN);
        status = false;
    }
}

