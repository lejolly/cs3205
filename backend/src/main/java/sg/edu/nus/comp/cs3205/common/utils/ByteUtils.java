package sg.edu.nus.comp.cs3205.common.utils;

public class ByteUtils {

    // http://stackoverflow.com/questions/14243922/java-xor-over-two-arrays/14244006#14244006
    public static byte[] xorByteArrays(byte[] array1, byte[] array2) {
        // assumes array1 and array2 have the same length
        byte[] array3 = new byte[array1.length];
        int i = 0;
        for (byte b : array1) {
            array3[i] = (byte) (b ^ array2[i++]);
        }
        return array3;
    }

}
