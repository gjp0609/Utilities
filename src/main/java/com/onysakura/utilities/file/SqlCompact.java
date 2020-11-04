package com.onysakura.utilities.file;

import java.io.*;

public class SqlCompact {

    /**
     * 每个表留下的数据行数
     */
    private static final int COUNT = 10;

    public static void main(String[] args) throws Exception {
        String sqlPath = "/Files/Workspaces/Mine/tzkbapp.sql";
        File file = new File(sqlPath);
        File newFile = new File(sqlPath.replace(".sql", ".new.sql"));
        BufferedReader reader = new BufferedReader(new FileReader(file));
        BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
        String s = reader.readLine();
        String prefix = "INSERT INTO ";
        int count = 0;
        String countTable = "";
        while (s != null) {
            if (s.startsWith(prefix)) {
                String tableName = s.substring("INSERT INTO ".length(), s.indexOf(" VALUES "));
                if (!countTable.equals(tableName)) {
                    countTable = tableName;
                    count = 0;
                }
                count++;
                if (count > COUNT) {
                    s = reader.readLine();
                    continue;
                }
            }
            writer.write(s);
            writer.newLine();
            s = reader.readLine();
        }
        reader.close();
        writer.flush();
        writer.close();
    }
}
