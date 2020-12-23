package com.onysakura.utilities.utils.httpclient;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PostParam extends GetParam {
    /**
     * 传输方式
     */
    private Constants.ContentType contentType;
    /**
     * 传输编码
     */
    private Charset charset;
    /**
     * 自定义请求参数
     */
    private Map<String, String> requestProperty;
    /**
     * 请求内容
     */
    private Map<String, String> body;
    /**
     * 上传文件路径
     */
    private List<String> uploadFileNames;
    /**
     * 上传文件名称
     */
    private List<String> uploadFilePaths;

    public Constants.ContentType getContentType() {
        return contentType;
    }

    public PostParam setContentType(Constants.ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public Charset getCharset() {
        return charset;
    }

    public PostParam setCharset(Charset charset) {
        this.charset = charset;
        return this;
    }

    public Map<String, String> getRequestProperty() {
        return requestProperty;
    }

    public PostParam setRequestProperty(Map<String, String> requestProperty) {
        this.requestProperty = requestProperty;
        return this;
    }

    public PostParam addRequestProperty(Map<String, String> requestProperty) {
        if (this.requestProperty == null) {
            this.requestProperty = new HashMap<>();
        }
        this.requestProperty.putAll(requestProperty);
        return this;
    }

    public PostParam addRequestProperty(String key, String value) {
        if (this.requestProperty == null) {
            this.requestProperty = new HashMap<>();
        }
        this.requestProperty.put(key, value);
        return this;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public PostParam setBody(Map<String, String> body) {
        this.body = body;
        return this;
    }

    public PostParam addBody(Map<String, String> body) {
        if (this.body == null) {
            this.body = new HashMap<>();
        }
        this.body.putAll(body);
        return this;
    }

    public PostParam addBody(String key, String value) {
        if (this.body == null) {
            this.body = new HashMap<>();
        }
        this.body.put(key, value);
        return this;
    }

    public List<String> getUploadFileNames() {
        return uploadFileNames;
    }

    public PostParam setUploadFileNames(List<String> uploadFileNames) {
        this.uploadFileNames = uploadFileNames;
        return this;
    }

    public PostParam addUploadFileNames(List<String> uploadFileNames) {
        if (uploadFileNames == null) {
            uploadFileNames = new ArrayList<>();
        }
        this.uploadFileNames.addAll(uploadFileNames);
        return this;
    }

    public PostParam addUploadFileName(String uploadFileName) {
        if (uploadFileNames == null) {
            uploadFileNames = new ArrayList<>();
        }
        this.uploadFileNames.add(uploadFileName);
        return this;
    }

    public List<String> getUploadFilePaths() {
        return uploadFilePaths;
    }

    public PostParam setUploadFilePaths(List<String> uploadFilePaths) {
        this.uploadFilePaths = uploadFilePaths;
        return this;
    }

    public PostParam addUploadFilePaths(List<String> uploadFilePaths) {
        if (this.uploadFilePaths == null) {
            this.uploadFilePaths = new ArrayList<>();
        }
        this.uploadFilePaths.addAll(uploadFilePaths);
        return this;
    }

    public PostParam addUploadFilePath(String uploadFilePath) {
        if (this.uploadFilePaths == null) {
            this.uploadFilePaths = new ArrayList<>();
        }
        this.uploadFilePaths.add(uploadFilePath);
        return this;
    }

    @Override
    public PostParam setParams(Map<String, String> params) {
        super.setParams(params);
        return this;
    }

    @Override
    public PostParam addParam(String key, String value) {
        super.addParam(key, value);
        return this;
    }

    @Override
    public PostParam setReadTimeout(int readTimeout) {
        super.setReadTimeout(readTimeout);
        return this;
    }

    @Override
    public PostParam setConnectTimeout(int connectTimeout) {
        super.setConnectTimeout(connectTimeout);
        return this;
    }
}
