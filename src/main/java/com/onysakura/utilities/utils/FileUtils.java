package com.onysakura.utilities.utils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
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
}
