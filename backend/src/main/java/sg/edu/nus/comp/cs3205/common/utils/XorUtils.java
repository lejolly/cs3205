package sg.edu.nus.comp.cs3205.common.utils;

import org.bouncycastle.util.encoders.Base64;

public class XorUtils {

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

    public static String xorBase64ByteArrays(byte[] array1, byte[] array2) {
        // assumes that string1 is shorter than string2
        String s1 = new String(Base64.decode(array1));
        String s2 = new String(Base64.decode(array2));
        System.out.println(s1);
        System.out.println(s1.length());
        System.out.println(s2);
        System.out.println(s2.length());
        String result = "";
        for (int i = 0; i < s1.length(); i++) {
            result += String.valueOf(((byte) s1.charAt(i)) ^ ((byte) s2.charAt(i)));
        }
        System.out.println(new String(Base64.encode(result.getBytes())));
        return new String(Base64.encode(result.getBytes()));
    }

}
