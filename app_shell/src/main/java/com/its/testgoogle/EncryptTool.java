package com.its.testgoogle;

import android.content.Context;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptTool {

    public static String sha256(String txt) {
        byte[] bts = sha256Bts(txt);
        return bts == null ? null : fillMd5(bts);
    }

    public static byte[] sha256Bts(String txt) {
        if (txt == null) {
            return null;
        }

        byte[] bts = txt.getBytes(StandardCharsets.UTF_8);
        return sha256Bts(bts);
    }

    public static byte[] sha256Bts(byte[] bts) {
        if (bts == null) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(bts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String md5Str(String txt) {
        byte[] bts = md5Bts(txt);
        return bts == null ? null : fillMd5(bts);
    }

    private static String fillMd5(byte[] bts) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bts) {
            int c = b & 0xFF;
            if (c < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(c));
        }
        return sb.toString();
    }

    /**
     * 文件的 md5 值
     */
    public static String md5File(Context context, String path) {
        byte[] bts = FileTool.readFileBts(context, path);
        if (bts == null) {
            return null;
        }

        byte[] newbts = md5Bts(bts);
        return newbts == null ? null : fillMd5(newbts);
    }

    public static byte[] md5Bts(String txt) {
        if (txt == null) {
            return null;
        }

        byte[] bts = txt.getBytes(StandardCharsets.UTF_8);
        return md5Bts(bts);
    }

    public static byte[] md5Bts(byte[] bts) {
        if (bts == null) {
            return null;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return digest.digest(bts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ------------------ aes
    public static byte[] aesEncryptBase64(byte[] dataByteArray, String CryptoKey) {
        if (dataByteArray == null) {
            return null;
        }

        try {
            byte[] key = sha256Bts(CryptoKey);
            byte[] iv = md5Bts(CryptoKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivps = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivps);
            return cipher.doFinal(dataByteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] aesDecryptBase64(byte[] dataByteArray, String CryptoKey) {
        if (dataByteArray == null) {
            return null;
        }

        try {
            byte[] key = sha256Bts(CryptoKey);
            byte[] iv = md5Bts(CryptoKey); // 只能是 16, 32 字节, 所以用 md5Bts
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivps = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivps);
            return cipher.doFinal(dataByteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
