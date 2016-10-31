package sg.edu.nus.comp.cs3205.common.utils;

import java.nio.ByteBuffer;

public class ByteUtils {

    public static String bytesToString(byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for(byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] longToBytes(long l) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(l);
        return buffer.array();
    }

    public static String bytesToBinaryString(byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for(byte b : bytes) {
            sb.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
        }
        return sb.toString();
    }

    public static String longToBinaryString(long l) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(l);
        return bytesToBinaryString(buffer.array());
    }

}
