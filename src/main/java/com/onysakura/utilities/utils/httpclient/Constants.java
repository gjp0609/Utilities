package com.onysakura.utilities.utils.httpclient;

public interface Constants {

    enum ContentType {
        APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),
        APPLICATION_JSON("application/json"),
        APPLICATION_XML("application/xml"),
        MULTIPART_FORM_DATA("multipart/form-data")
        ;

        ContentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String getValueWithCharset() {
            return this.getValueWithCharset(null);
        }

        public String getValueWithCharset(String charset) {
            if (charset == null) {
                charset = "UTF-8";
            }
            return this.getValue() + ";charset=" + charset;
        }

        private final String value;
    }

}
