package at.ac.tuwien.sepm.groupphase.backend.util;

import java.security.SecureRandom;

public class PasswordGenerator {
    public static final String RANDOM_PASSWORD_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateRandomPassword(int length) {
        SecureRandom s = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int randomChoice = s.nextInt(0, RANDOM_PASSWORD_ALPHABET.length());
            password.append(RANDOM_PASSWORD_ALPHABET.charAt(randomChoice));
        }

        return password.toString();
    }
}
