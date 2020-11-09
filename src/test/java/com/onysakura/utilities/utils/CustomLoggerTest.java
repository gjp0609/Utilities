package com.onysakura.utilities.utils;

public class CustomLoggerTest {

    public static void main(String[] args) {
        CustomLogger.Log logger = CustomLogger.getLogger(CustomLoggerTest.class);
        logger.debug("asd");
        logger.debug("asd: {}, {}", "a", "b");

        logger.info("asd");
        logger.info("asd: {}, {}", "a", "b");

        logger.warn("asd");
        logger.warn("asd: {}, {}", "a", "b");
        try {
            String s = null;
            s.isEmpty();
        } catch (NullPointerException e) {
            logger.warn(e, "asd: {}, {}", "a", "b");
        }

        logger.error("asd");
        logger.error("asd: {}, {}", "a", "b");
        try {
            String s = null;
            s.isEmpty();
        } catch (NullPointerException e) {
            logger.error(e, "asd: {}, {}", "a", "b");
        }
    }
}
