package cncs.academy.ess.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static String passwordToHash(String password, byte[] salt) throws Exception{
        int iterations = 10000;
        int keyLength = 256;
        // Hash the password using PBKDF2
        byte[] hashedPassword = hashPassword(password, salt, iterations, keyLength);

        // Convert the hashed password to a string for storage
        String hashedPasswordString = bytesToHex(hashedPassword);

        String saltString =  new String(salt, StandardCharsets.ISO_8859_1);
        return hashedPasswordString + ":" + saltString;
    }

    private static byte[] hashPassword(String password, byte[] salt, int iterations, int keyLength) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
