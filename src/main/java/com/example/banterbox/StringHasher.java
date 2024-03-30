package com.example.banterbox;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class StringHasher {
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 512;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final Pattern PATTERN = Pattern.compile(":");

    public static String createHash(String s) throws NoSuchAlgorithmException, InvalidKeySpecException {
        char[] chars = s.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = skf.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyString(String providedString, String storedString)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = PATTERN.split(storedString);
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] storedHash = Base64.getDecoder().decode(parts[1]);

        PBEKeySpec spec = new PBEKeySpec(providedString.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = storedHash.length ^ testHash.length;
        for (int i = 0; i < storedHash.length && i < testHash.length; i++) {
            diff |= storedHash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    private static byte[] getSalt() {
        // Implement salt generation logic here
        return new byte[16]; // For example, generating a random salt of length 16 bytes
    }
}
