package com.its.testgoogle;

public class EncrpytUtil {
    private final static byte[] ENCRYPT_VAL = {65, 68, 54, 52, 65, 53, 69, 48, 52, 56, 57, 57, 69, 56, 56, 69, 68, 48, 70, 55, 70, 50, 49, 51, 65, 52, 68, 69, 54, 65, 53, 48};

    private static byte[] decrypt(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (bytes[i] ^ ENCRYPT_VAL[i % ENCRYPT_VAL.length]);
        }
        return bytes;
    }
}
