package sg.edu.nus.comp.cs3205.common.utils;

import org.mindrot.jbcrypt.BCrypt;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HashUtils {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(getMD5Hash("hello"));
        System.out.println(getBcryptHash("pass", BCrypt.gensalt()));
    }

    // http://stackoverflow.com/questions/415953/how-can-i-generate-an-md5-hash/421696#421696
    public static String getMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(input.getBytes());
        return getStringFromBytes(md5.digest());
    }

    public static String getBcryptHash(String input, String salt) {
        return BCrypt.hashpw(input, salt);
    }

    public static String get32CharNonce() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return getStringFromBytes(bytes);
    }

    private static String getStringFromBytes(byte[] bytes) {
        BigInteger bigInt = new BigInteger(1, bytes);
        String hash = bigInt.toString(16);
        // padding
        while (hash.length() < 32) {
            hash = "0" + hash;
        }
        return hash;
    }

}
