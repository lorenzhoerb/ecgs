package at.ac.tuwien.sepm.groupphase.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.security.SecureRandom;

public class PasswordGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String RANDOM_PASSWORD_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Generate a random password with given length.
     *
     * @param length length the password should have
     * @return the resulting password in plain text
     */
    public static String generateRandomPassword(int length) {
        LOGGER.debug("generateRandomPassword()");
        SecureRandom s = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randomChoice = s.nextInt(0, RANDOM_PASSWORD_ALPHABET.length());
            password.append(RANDOM_PASSWORD_ALPHABET.charAt(randomChoice));
        }

        return password.toString();
    }
}
