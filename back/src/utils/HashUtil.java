package utils;

import java.security.MessageDigest;

public class HashUtil {
    public static String jinxHash(String password) {
        int length = password.length();
        int pos1 = length / 3;
        int pos2 = 2 * length / 3;

        String modifiedPassword = password.substring(0, pos1) + "get" + 
                                  password.substring(pos1, pos2) + "jinxed" + 
                                  password.substring(pos2) + "!";

        return sha256(modifiedPassword);
    }

    private static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
