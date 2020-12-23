package com.onysakura.utilities.utils.httpclient;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class GetParam extends RequestParam {

    /**
     * 请求内容
     */
    private Map<String, String> params;

    public Map<String, String> getParams() {
        return params;
    }

    public GetParam setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public GetParam addParams(Map<String, String> params) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.putAll(params);
        return this;
    }

    public GetParam addParam(String key, String value) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(key, value);
        return this;
    }

    @Override
    public GetParam setReadTimeout(int readTimeout) {
        super.setReadTimeout(readTimeout);
        return this;
    }

    @Override
    public GetParam setConnectTimeout(int connectTimeout) {
        super.setConnectTimeout(connectTimeout);
        return this;
    }
}
