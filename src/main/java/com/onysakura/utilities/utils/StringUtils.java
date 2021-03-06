package com.onysakura.utilities.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StringUtils {

    private static final CustomLogger.Log LOG = CustomLogger.getLogger(StringUtils.class);
    private static final SnowflakeIdWorker SNOWFLAKE_ID_WORKER = new SnowflakeIdWorker(0, 0);

    /**
     * 获取 snowflake id
     */
    public static String getNextId() {
        long nextId = SNOWFLAKE_ID_WORKER.nextId();
        return String.valueOf(nextId);
    }

    public static List<String> splitString(String src, char split) {
        List<String> list = new ArrayList<>();
        if (isEmpty(src)) {
            return list;
        }
        StringBuilder temp = new StringBuilder();
        for (char c : src.toCharArray()) {
            if (split == c) {
                list.add(temp.toString());
                temp = new StringBuilder();
            } else {
                temp.append(c);
            }
        }
        list.add(temp.toString());
        return list;
    }

    public static boolean isEmpty(CharSequence sequence) {
        return sequence == null || sequence.toString().isEmpty();
    }

    public static boolean isBlank(CharSequence sequence) {
        return sequence == null || sequence.toString().trim().isEmpty();
    }

    /**
     * 驼峰转下划线
     */
    public static String humpToUnderline(String para) {
        StringBuilder sb = new StringBuilder(para);
        int temp = 0;
        if (!para.contains("_")) {
            for (int i = 0; i < para.length(); i++) {
                if (Character.isUpperCase(para.charAt(i))) {
                    sb.insert(i + temp, "_");
                    temp += 1;
                }
            }
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 下划线转驼峰
     */
    public static String underlineToHump(String para) {
        StringBuilder result = new StringBuilder();
        String[] a = para.split("_");
        for (String s : a) {
            if (!para.contains("_")) {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
                continue;
            }
            if (result.length() == 0) {
                result.append(s.toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    /**
     * 计算两个字符串的相似度
     */
    public static int levenshtein(String str1, String str2) {
        //计算两个字符串的长度。
        int len1 = str1.length();
        int len2 = str2.length();
        //建立上面说的数组，比字符长度大一个空间
        int[][] dif = new int[len1 + 1][len2 + 1];
        for (int a = 0; a <= len1; a++) {
            dif[a][0] = a;
        }
        for (int a = 0; a <= len2; a++) {
            dif[0][a] = a;
        }
        //计算两个字符是否一样，计算左上的值
        int temp;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                //取三个值中最小的
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,
                        dif[i - 1][j] + 1);
            }
        }
        LOG.debug("字符串\"" + str1 + "\"与\"" + str2 + "\"的比较");
        //取数组右下角的值，同样不同位置代表不同字符串的比较
        LOG.debug("差异步骤：" + dif[len1][len2]);
        float v = 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
        LOG.debug("相似度：" + v);
        //计算相似度
        return (int) (v * 100);
    }

    /**
     * 得到最小值
     */
    private static int min(int... is) {
        int min = Integer.MAX_VALUE;
        for (int i : is) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }

    private static final String NUMBER = "0123456789";
    private static final String ABC = "abcdefghijklmnopqrstuvwxyz";

    public static synchronized String randomNum(int length) {
        Random random = new Random();
        String source = NUMBER;
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append(source.charAt(random.nextInt(source.length())));
        }
        return s.toString();
    }

    public static synchronized String randomStr(int length) {
        Random random = new Random();
        String source = NUMBER + ABC + ABC.toUpperCase();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append(source.charAt(random.nextInt(source.length())));
        }
        return s.toString();
    }
}
