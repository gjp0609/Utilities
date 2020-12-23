package com.onysakura.utilities.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.*;
import java.util.regex.Matcher;

public class CustomLogger {

    private static final boolean IS_SAVE_LOG_FILE = true;
    private static final String LOG_PATH = "/Files/yj/logs/Utilities";
    private static final Level LOG_FILE_LEVEL = Level.INFO;
    private static final Level LOG_CONSOLE_LEVEL = Level.INFO;
    private static final int CLASS_NAME_LENGTH_LIMIT = 30;

    private static final Formatter formatter;
    private static FileHandler fileHandler = null;

    static {
        formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                String clearColor = "\033[0m";
                String levelColor = getColor(record.getLevel());
                String classColor = "\033[36m";
                return DateUtils.format(new Date(record.getMillis()), DateUtils.YYYY_MM_DD_HH_MM_SS) + " "
                        + levelColor + "[" + String.format("%5s", getLevel(record.getLevel())) + "] " + clearColor
                        + classColor + getShortClassName(record.getLoggerName()) + clearColor + ": "
                        + record.getMessage() + "\n";
            }
        };
        if (IS_SAVE_LOG_FILE) {
            try {
                String filePath = LOG_PATH + "/" + DateUtils.format(new Date(), DateUtils.YYYYMMDDHHMMSS) + ".log";
                File file = new File(LOG_PATH);
                if (!file.exists()) {
                    boolean success = file.mkdirs();
                    if (success) {
                        file = new File(filePath);
                        success = file.createNewFile();
                        if (!success) {
                            throw new RuntimeException("create file fail: " + filePath);
                        }
                    } else {
                        throw new RuntimeException("mkdir fail: " + LOG_PATH);
                    }
                }
                fileHandler = new FileHandler(filePath);
                fileHandler.setFormatter(formatter);
            } catch (RuntimeException | IOException e) {
                Log logger = getLogger(CustomLogger.class);
                logger.warn(e, "save log file fail");
            }
        }
    }

    public static Log getLogger(Class<?> loggerName) {
        Logger logger = Logger.getLogger(loggerName.getName());
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.ALL);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        consoleHandler.setLevel(LOG_CONSOLE_LEVEL);
        logger.addHandler(consoleHandler);
        if (IS_SAVE_LOG_FILE && fileHandler != null) {
            fileHandler.setLevel(LOG_FILE_LEVEL);
            logger.addHandler(fileHandler);
        }
        return new Log(logger);
    }

    public static class Log {
        private final Logger logger;

        public Log(Logger logger) {
            this.logger = logger;
        }

        public void debug(Object msg) {
            logger.fine(String.valueOf(msg));
        }

        public void debug(String msg, Object... args) {
            msg = msg(msg, args);
            logger.fine(msg);
        }

        public void info(Object msg) {
            logger.info(String.valueOf(msg));
        }

        public void info(String msg, Object... args) {
            msg = msg(msg, args);
            logger.info(String.valueOf(msg));
        }

        public void warn(Object msg) {
            logger.warning(String.valueOf(msg));
        }

        public void warn(String msg, Object... args) {
            msg = msg(msg, args);
            logger.warning(String.valueOf(msg));
        }

        public void warn(Throwable t, Object msg) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            t.printStackTrace(writer);
            logger.warning(msg + "\n" + stringWriter.toString());
        }

        public void warn(Throwable t, String msg, Object... args) {
            msg = msg(msg, args);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            t.printStackTrace(writer);
            logger.warning(msg + "\n" + stringWriter.toString());
        }

        public void error(Object msg) {
            logger.severe(String.valueOf(msg));
        }

        public void error(String msg, Object... args) {
            msg = msg(msg, args);
            logger.severe(String.valueOf(msg));
        }

        public void error(Throwable t, Object msg) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            t.printStackTrace(writer);
            logger.severe(msg + "\n" + stringWriter.toString());
        }

        public void error(Throwable t, String msg, Object... args) {
            msg = msg(msg, args);
            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            t.printStackTrace(writer);
            logger.severe(msg + "\n" + stringWriter.toString());
        }

        public String msg(String msg, Object[] args) {
            for (Object arg : args) {
                String str = Matcher.quoteReplacement(String.valueOf(arg));
                msg = msg.replaceFirst("\\{}", str);
            }
            return msg;
        }
    }

    private static String getLevel(Level level) {
        switch (level.getName()) {
            case "SEVERE":
                return "ERROR";
            case "WARNING":
                return "WARN";
            case "INFO":
                return "INFO";
            case "FINE":
            case "FINER":
            case "FINEST":
            default:
                return "DEBUG";
        }
    }

    private static String getShortClassName(String className) {
        if (className.length() <= CustomLogger.CLASS_NAME_LENGTH_LIMIT) {
            return String.format("%" + CustomLogger.CLASS_NAME_LENGTH_LIMIT + "s", className);
        }
        return String.format("%" + CustomLogger.CLASS_NAME_LENGTH_LIMIT + "s", className.substring(className.lastIndexOf(".") + 1));
    }

    private static String getColor(Level level) {
        switch (level.getName()) {
            case "SEVERE":
                return "\033[31m";
            case "WARNING":
                return "\033[33m";
            case "INFO":
                return "\033[0m";
            case "FINE":
            case "FINER":
            case "FINEST":
            default:
                return "\033[37m";
        }
    }
}
