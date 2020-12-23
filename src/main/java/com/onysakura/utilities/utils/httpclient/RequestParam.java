package com.onysakura.utilities.utils.httpclient;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class RequestParam {
    /**
     * 读取主机服务器返回数据超时时间，默认：60000 毫秒
     */
    private int readTimeout = 60000;
    /**
     * 连接主机服务器超时时间，默认：15000 毫秒
     */
    private int connectTimeout = 15000;

    public int getReadTimeout() {
        return readTimeout;
    }

    public RequestParam setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public RequestParam setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }
}
