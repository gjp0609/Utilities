package com.onysakura.utilities.utils.sec;

import com.onysakura.utilities.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class ParamsUtils {

    private static final String EQUAL = "=";
    private static final String DELIMITER = "&";

    public static String join(Map<String, String> map) {
        return join(map, false);
    }

    public static String join(Map<String, String> map, boolean ignoreBlankValue) {
        ArrayList<String> list = new ArrayList<>();
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (ignoreBlankValue) {
                if (StringUtils.isBlank(value)) {
                    continue;
                }
            }
            String s = key + EQUAL + value;
            list.add(s);
        }
        Collections.sort(list);
        return String.join(DELIMITER, list);
    }
}
