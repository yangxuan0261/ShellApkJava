package com.its.testgoogle;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static java.lang.String.format;

public class FileTool {
    private static final String TAG = "--- FileTool";

    // ------------------ file

    /**
     * 适配 安卓 7.0 以上 ExternalFiles Uri 获取
     */
    public static Uri getUri(Context context, String filePath) {
        File dstFile = getFile(context, filePath);
        return getUri(context, dstFile);
    }

    /**
     * 适配 安卓 7.0 以上 ExternalFiles Uri 获取
     */
    public static Uri getUri(Context context, File dstFile) {
        Uri dstUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+ 需要用 fileprovider 来获得 uri
            dstUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", dstFile);
        } else {
            dstUri = Uri.fromFile(dstFile);
        }
        return dstUri;
    }

    /**
     * 获取 ExternalFiles
     */
    public static File getFile(Context context, String filePath) {
        return new File(getFilePath(context, filePath));
    }

    public static String getFilePath(Context context, String filePath) {
        return context.getExternalFilesDir("").getAbsolutePath() + "/" + filePath;
    }

    /**
     * 写文件到 ExternalFiles
     */
    public static Exception writeFile(Context context, String fileName, String content) {
        if (content == null) {
            return new Exception("content is null");
        }
        byte[] bts = content.getBytes(StandardCharsets.UTF_8);
        return writeFile(context, fileName, bts);
    }

    public static Exception writeFile(Context context, String fileName, byte[] bts) {
        File file = getFile(context, fileName);
        return writeFile(file, bts);
    }

    public static Exception writeFile(File file, byte[] bts) {
        if (file == null) {
            return new Exception("file is null");
        }
        if (bts == null) {
            return new Exception("bytes is null");
        }

        FileOutputStream fos = null;
        Exception ex = null;
        try {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            fos = new FileOutputStream(file);
            fos.write(bts);
        } catch (Exception e) {
            e.printStackTrace();
            ex = e;
        } finally {
            close(fos);
        }
        return ex;
    }

    public static Exception writeFileEncrypt(Context context, String fileName, String content) {
        if (content == null) {
            return new Exception("writeFileEncrypt content is null");
        }
        byte[] bts = content.getBytes(StandardCharsets.UTF_8);
        bts = EncryptTool.aesEncryptBase64(bts, Define.AesKey);
        return writeFile(context, fileName, bts);
    }

    public static Exception writeFileEncrypt(File file, String content) {
        if (file == null) {
            return new Exception("writeFileEncrypt file is null");
        }
        byte[] bts = content.getBytes(StandardCharsets.UTF_8);
        return writeFileEncrypt(file, bts);
    }

    public static Exception writeFileEncrypt(Context context, String fileName, byte[] bts) {
        if (bts == null) {
            return new Exception("writeFileEncrypt bytes is null");
        }
        bts = EncryptTool.aesEncryptBase64(bts, Define.AesKey);
        return writeFile(context, fileName, bts);
    }

    public static Exception writeFileEncrypt(File file, byte[] bts) {
        if (file == null) {
            return new Exception("writeFileEncrypt file is null");
        }
        bts = EncryptTool.aesEncryptBase64(bts, Define.AesKey);
        return writeFile(file, bts);
    }

    public static String readFile(Context context, String fileName) {
        byte[] bts = readFileBts(context, fileName);
        return bts == null ? null : new String(bts, StandardCharsets.UTF_8);
    }

    public static String readFile(File file) {
        byte[] bts = readFileBts(file);
        return bts == null ? null : new String(bts, StandardCharsets.UTF_8);
    }

    public static byte[] readFileBts(Context context, String fileName) {
        File file = getFile(context, fileName);
        return readFileBts(file);
    }

    public static byte[] readFileBts(File file) {
        if (file == null || !file.exists()) {
            return null;
        }

        InputStream inputStream = null;
        try {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            inputStream = new FileInputStream(file);
            if (inputStream == null) {
                throw new IOException(format("--- FileInputStream fail, path: %s", file.getAbsolutePath()));
            }

            int length = inputStream.available();
            byte[] buffer = new byte[length];
            inputStream.read(buffer);
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(inputStream);
        }
        return null;
    }

    public static String readFileEncrypt(Context context, String fileName) {
        byte[] bts = readFileEncryptBts(context, fileName);
        return bts == null ? null : new String(bts, StandardCharsets.UTF_8);
    }

    public static String readFileEncrypt(File file) {
        byte[] bts = readFileEncryptBts(file);
        return bts == null ? null : new String(bts, StandardCharsets.UTF_8);
    }

    public static byte[] readFileEncryptBts(Context context, String fileName) {
        byte[] bts = readFileBts(context, fileName);
        return bts == null ? null : EncryptTool.aesDecryptBase64(bts, Define.AesKey);
    }

    public static byte[] readFileEncryptBts(File file) {
        byte[] bts = readFileBts(file);
        return bts == null ? null : EncryptTool.aesDecryptBase64(bts, Define.AesKey);
    }

    public static Exception copy(Context context, Uri srcUri, File dstFile) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        Exception e = null;
        try {
            File dir = dstFile.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) {
                throw new IOException(format("--- openInputStream fail, srcUri: %s", srcUri.getPath()));
            }
            outputStream = new FileOutputStream(dstFile);
            e = copyFile(inputStream, outputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
            e = ex;
        } finally {
            close(inputStream);
            close(outputStream);
        }
        return e;
    }

    // 据说更高效的拷贝: https://blog.csdn.net/mvpstevenlin/article/details/54090722
    public static Exception copyFile(InputStream inputStream, OutputStream outputStream) {
        int BUFFER_SIZE = 1024 * 2;
        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(inputStream, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(outputStream, BUFFER_SIZE);
        int n;
        Exception e = null;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
            }
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
            e = ex;
        } finally {
            close(out);
            close(in);
        }
        return e;
    }

    public static <T extends java.io.Closeable> void close(T t) {
        try {
            if (t != null) {
                t.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getAssetsFileStr(Context context, final String path) {
        byte[] bts = getAssetsFileBts(context, path);
        return bts == null ? null : new String(bts, StandardCharsets.UTF_8);
    }

    public static String getAssetsFileStrEncrypt(Context context, final String path) {
        byte[] bts = getAssetsFileBts(context, path);
        if (bts == null) {
            return null;
        }
        bts = EncryptTool.aesDecryptBase64(bts, Define.AesKey);
        return bts == null ? null : new String(bts, StandardCharsets.UTF_8);
    }

    public static byte[] getAssetsFileBts(Context context, final String path) {
        InputStream is = null;
        try {
            is = context.getAssets().open(path);
            int lenght = is.available();
            byte[] bts = new byte[lenght];
            is.read(bts);
            return bts;
        } catch (Exception e) {
            LogUtil.TE(TAG, "--- getAssetsFileStr, no file:" + path);
        } finally {
            FileTool.close(is);
        }
        return null;
    }
}
