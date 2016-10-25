import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.mindrot.jbcrypt.BCrypt;

public class LoginHashTest {
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    
    public static void main(String[] args) throws NoSuchAlgorithmException {
        // String salt = BCrypt.gensalt(10);
        String salt = "$2a$10$WIPcBKSa4vaBhmA3dMbwR.";
        String password = "secret";
        String challenge_seed = "seed";
        String challenge = sha256(challenge_seed);
        System.out.println("salt = " + salt);
        System.out.println("password = " + password);
        System.out.println("challenge = " + challenge);
        String hs = ENCODER.encodeToString(BCrypt.hashpw(password, salt).getBytes());
        System.out.println("[SECRET] hs = Hs(password, salt) = " + hs);
        String hash = sha256(hs);
        System.out.println("hash = H(Hs(password, salt)) = " + hash);
        String response = stringXOR(sha256(hash + challenge), hs);
        System.out.println("response = H(H(Hs(password, salt)), challenge) xor Hs(password, salt) = " + response);
        String server = sha256(stringXOR(sha256(hash + challenge), response));
        System.out.println("server = H(H(hash, challenge) xor response) = " + server);
        System.out.println("server == hash = " + (server.compareTo(hash) == 0));
    }
    
    public static String sha256(String s) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }
    
    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    private static String stringXOR(String s1, String s2) {
        byte[] b1 = DECODER.decode(s1.getBytes());
        byte[] b2 = DECODER.decode(s2.getBytes());
        byte[] res = new byte[b2.length];
        for(int i = 0; i < b2.length; i++) {
            if(i >= b1.length) {
                res[i] = (byte) (0 ^ b2[i]);
            } else {
                res[i] = (byte) (b1[i] ^ b2[i]);
            }
        }
        return ENCODER.encodeToString(res);
    }
}
