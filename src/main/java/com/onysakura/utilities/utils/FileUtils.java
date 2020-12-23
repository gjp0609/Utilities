package com.onysakura.utilities.utils;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class FileUtils {
    private static final CustomLogger.Log LOG = CustomLogger.getLogger(FileUtils.class);

    public static List<File> getFileList(File file) {
        List<File> list = new ArrayList<>();
        if (file != null) {
            File[] files = file.listFiles();
            if (files != null) {
                Collections.addAll(list, files);
            }
        }
        return list;
    }

    public static boolean exists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static String getMD5(File file) {
        try {
            byte[] buffer = new byte[8192];
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileInputStream fis = new FileInputStream(file);
            int len;
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            BigInteger bi = new BigInteger(1, b);
            return bi.toString(16);
        } catch (Exception e) {
            LOG.warn("Get File MD5 Fail, " + e.getMessage());
        }
        return null;
    }

    /**
     * @param imgStr base64 image content
     * @param path   file path and name
     */
    public static void base64ImageToFile(String imgStr, String path) throws IOException {
        if (imgStr != null) {
            byte[] b = Base64.getDecoder().decode(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(path);
            out.write(b);
            out.flush();
            out.close();
        }
    }
}
