package sg.edu.nus.comp.cs3205.common.utils;

import org.mindrot.jbcrypt.BCrypt;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class HashUtils {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(getSha256HashFromString("123456"));
        String salt = BCrypt.gensalt();
        System.out.println(salt);
        System.out.println(getBcryptHash("pass", salt));
    }

    // https://www.mkyong.com/java/java-sha-hashing-example/
    public static String getSha256HashFromString(String input) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(input.getBytes(StandardCharsets.UTF_8));
        return getStringFromBytes(messageDigest.digest());
    }

    public static String getSha256HashFromBytes(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(bytes);
        return getStringFromBytes(messageDigest.digest());
    }

    public static String getBcryptHash(String input, String salt) {
        return BCrypt.hashpw(input, salt);
    }

    public static String getShaNonce() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return getSha256HashFromBytes(bytes);
    }

    public static String getStringFromBytes(byte[] bytes) {
        BigInteger bigInt = new BigInteger(1, bytes);
        String hash = bigInt.toString(16);
        // padding for sha-256, length 64
        while (hash.length() < 64) {
            hash = "0" + hash;
        }
        return hash;
    }

}
