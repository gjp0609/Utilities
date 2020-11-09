package com.onysakura.utilities.db.sqlite;

import com.onysakura.utilities.utils.CustomLogger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class SQLite {
    private static final CustomLogger.Log LOG = CustomLogger.getLogger(SQLite.class);

    private static Connection c;
    private static Statement stmt;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            Path path = Paths.get("src", "main", "resources", "sqlite.db");
            c = DriverManager.getConnection("jdbc:sqlite:" + path.toFile().getAbsolutePath());
            LOG.debug("open database successfully");
            stmt = c.createStatement();
        } catch (Exception e) {
            LOG.warn(e, "open database fail, msg: " + e.getMessage());
        }
    }

    public static ResultSet executeQuery(String sql) {
        LOG.debug("executeQuery: " + sql);
        try {
            c.setAutoCommit(false);
            ResultSet resultSet = stmt.executeQuery(sql);
            c.commit();
            LOG.debug("executeQuery has resultSet: " + (resultSet != null));
            return resultSet;
        } catch (SQLException e) {
            LOG.warn(e, "executeUpdate sql fail, message: " + e.getErrorCode() + ", sql: " + sql);
            return null;
        }
    }

    public static int executeUpdate(String sql) {
        LOG.debug("executeUpdate: " + sql);
        try {
            c.setAutoCommit(false);
            int execute = stmt.executeUpdate(sql);
            c.commit();
            LOG.debug("executeUpdate result: " + execute);
            return execute;
        } catch (SQLException e) {
            LOG.warn(e, "executeUpdate sql fail, message: " + e.getErrorCode() + ", sql: " + sql);
            return -1;
        }
    }

    public static boolean execute(String sql) {
        LOG.debug("execute: " + sql);
        boolean success = false;
        try {
            c.setAutoCommit(false);
            boolean execute = stmt.execute(sql);
            if (execute) {
                success = true;
                c.commit();
            }
            LOG.debug("execute result: " + execute);
        } catch (SQLException e) {
            LOG.warn(e, "execute sql fail, message: " + e.getErrorCode() + ", sql: " + sql);
        }
        return success;
    }

    public static String escape(String keyWord) {
        keyWord = keyWord.replace("'", "''");
        return keyWord;
    }

    public enum Sort {
        DESC,
        ASC;
    }
}

