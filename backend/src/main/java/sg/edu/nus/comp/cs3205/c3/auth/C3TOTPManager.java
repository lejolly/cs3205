package sg.edu.nus.comp.cs3205.c3.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.comp.cs3205.common.core.AbstractManager;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

// Calculate OTP as per RFC6238
// https://en.wikipedia.org/wiki/Time-based_One-time_Password_Algorithm#Implementation
public class C3TOTPManager extends AbstractManager {

    private static final Logger logger = LoggerFactory.getLogger(C3TOTPManager.class.getSimpleName());

    private static final int INTERVAL = 30;

    private static final String KEY = "Test";

    public C3TOTPManager() {

    }

    public String getOTP() {
        long currTime = System.currentTimeMillis();
        long C = currTime / 1000 / INTERVAL;
        String otp = "ERROR";
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret = new SecretKeySpec(KEY.getBytes(), mac.getAlgorithm());
            mac.init(secret);
            byte[] hash = mac.doFinal(longToBytes(C));
            logger.debug("Time Tick: " + String.valueOf(C));
            logger.debug("Data Bytes: " + bytesToString(longToBytes(C)));
            logger.debug("Raw Hash: " + bytesToString(hash));
            int offset = hash[hash.length - 1] & 0xF;
            logger.debug("Offset: " + String.valueOf(offset));
            logger.debug("BinHash: " + bytesToBinaryString(hash));
            long truncatedHash = getTruncatedHash(bytesToBinaryString(hash), offset);
            logger.debug("THash: " + String.valueOf(truncatedHash) + " " + longToBinaryString(truncatedHash));
            long pinValue = truncatedHash % 1000000;
            logger.debug("OTP: " + String.valueOf(pinValue));
            otp = String.valueOf(pinValue);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Failed to do SHA1 HMAC", e);
        }
        return otp;
    }

    private String bytesToString(byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for(byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private byte[] longToBytes(long l) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(l);
        return buffer.array();
    }

    private String bytesToBinaryString(byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for(byte b : bytes) {
            sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return sb.toString();
    }

    private String longToBinaryString(long l) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(l);
        return bytesToBinaryString(buffer.array());
    }

    private long getTruncatedHash(String hash, int offset) {
        int bitOffset = offset * 4 + 1;
        String truncatedHashString = hash.substring(bitOffset, bitOffset + 32);
        logger.debug("THashString: " + truncatedHashString);
        return Long.parseLong(truncatedHashString, 2);
    }

}
