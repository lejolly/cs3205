package sg.edu.nus.comp.cs3205.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

// Calculate OTP as per RFC6238
// https://en.wikipedia.org/wiki/Time-based_One-time_Password_Algorithm#Implementation
public class TotpUtils {

    private static final Logger logger = LoggerFactory.getLogger(TotpUtils.class.getSimpleName());

    private static final int INTERVAL = 30;

    public static void main (String[] args) {
        List<String> otps = getOTPS("Test");
        if (otps.size() == 3) {
            for (String otp : otps) {
                System.out.println(otp);
            }
        } else {
            System.out.println("Unable to get OTPs");
        }
    }

    public static List<String> getOTPS(String key) {
        long currentTime = System.currentTimeMillis();
        long C = currentTime / 1000 / INTERVAL;
        List<String> otps = new ArrayList<>();
        try {
            otps.add(getOTP(key, C - 1));
            otps.add(getOTP(key, C));
            otps.add(getOTP(key, C + 1));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            logger.error("Failed to do SHA1 HMAC", e);
        }
        return otps;
    }

    public static long getTruncatedHash(String hash, int offset) {
        int bitOffset = offset * 4 + 1;
        String truncatedHashString = hash.substring(bitOffset, bitOffset + 32);
        logger.debug("THashString: " + truncatedHashString);
        return Long.parseLong(truncatedHashString, 2);
    }

    private static String getOTP(String key, long C) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), mac.getAlgorithm());
        mac.init(secret);
        byte[] hash = mac.doFinal(ByteUtils.longToBytes(C));
        logger.debug("Time Tick: " + String.valueOf(C));
        logger.debug("Data Bytes: " + ByteUtils.bytesToString(ByteUtils.longToBytes(C)));
        logger.debug("Raw Hash: " + ByteUtils.bytesToString(hash));
        int offset = hash[hash.length - 1] & 0xF;
        logger.debug("Offset: " + String.valueOf(offset));
        logger.debug("BinHash: " + ByteUtils.bytesToBinaryString(hash));
        long truncatedHash = getTruncatedHash(ByteUtils.bytesToBinaryString(hash), offset);
        logger.debug("THash: " + String.valueOf(truncatedHash) + " " + ByteUtils.longToBinaryString(truncatedHash));
        long pinValue = truncatedHash % 1000000;
        logger.debug("OTP: " + String.valueOf(pinValue));
        String otp = String.valueOf(pinValue);
        while (otp.length() < 6) {
            otp = String.valueOf(0) + otp;
        }
        return otp;
    }

}
