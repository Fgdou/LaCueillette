package serveur;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Common {
    /**
     * Function for hashing password
     * @param input     A string
     * @return          The hashed string
     */
    public static String hash(String input){
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            Log.fatal("Hashing method\n" + e.getMessage());
        }
        return null;
    }
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    public static void sendMail(String mail, String subject, String html){
        System.out.println("Mail for " + mail + ": " + subject + "\n" + html);
    }

    /**
     * @param number    A number
     * @param n         The length to format
     * @return          The number padded with "0"
     * @example         (12, 3) --> 012
     */
    public static String format(int number, int n){
        if(n == 0)
            return "";
        if(number < Math.pow(10, n-1))
            return "0" + format(number, n-1);
        return String.valueOf(number);
    }
}
