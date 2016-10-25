package sg.edu.nus.comp.cs3205.common.utils;

import org.bouncycastle.util.encoders.Base64;

public class XorUtils {

    private static final java.util.Base64.Decoder DECODER = java.util.Base64.getDecoder();
    private static final java.util.Base64.Encoder ENCODER = java.util.Base64.getEncoder();

    // http://stackoverflow.com/questions/14243922/java-xor-over-two-arrays/14244006#14244006
    public static byte[] xorByteArrays(byte[] array1, byte[] array2) {
        // assumes arrays have the same length
        byte[] array3 = new byte[array1.length];
        int i = 0;
        for (byte b : array1) {
            array3[i] = (byte) (b ^ array2[i++]);
        }
        return array3;
    }

    public static String stringXOR(String s1, String s2) {
        byte[] b1 = DECODER.decode(s1.getBytes());
        byte[] b2 = DECODER.decode(s2.getBytes());
        byte[] result = new byte[b2.length];
        for(int i = 0; i < b2.length; i++) {
            if(i >= b1.length) {
                result[i] = b2[i];
            } else {
                result[i] = (byte) (b1[i] ^ b2[i]);
            }
        }
        return ENCODER.encodeToString(result);
    }

}
