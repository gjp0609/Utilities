package com.onysakura.utilities.utils.httpclient;

import com.alibaba.fastjson.JSON;
import com.onysakura.utilities.utils.CustomLogger;
import com.onysakura.utilities.utils.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class HttpClientUtils {

    private static final CustomLogger.Log LOG = CustomLogger.getLogger(HttpClientUtils.class);

    public static ResponseResult get(String httpUrl) throws IOException {
        return get(httpUrl, null);
    }

    public static ResponseResult get(String httpUrl, GetParam getParam) throws IOException {
        LOG.info("---------- GET: {}", httpUrl);
        LOG.info("params: {}", JSON.toJSONString(getParam));
        long startTime = System.currentTimeMillis();
        if (StringUtils.isBlank(httpUrl)) {
            throw new RuntimeException("url must not be null");
        }
        if (getParam == null) {
            getParam = new GetParam();
        }
        ResponseResult responseResult = new ResponseResult();
        httpUrl = setUrlParams(httpUrl, getParam);
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(getParam.getConnectTimeout());
            connection.setReadTimeout(getParam.getReadTimeout());
            connection.connect();
            responseResult.setResponseCode(connection.getResponseCode());
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            responseResult.setHeaderFields(headerFields);
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                result = br.lines().collect(Collectors.joining("\n"));
                responseResult.setResult(result);
            }
        } finally {
            close(br);
            close(is);
            close(connection);
        }
        LOG.info("Time usage: {}ms, Result: {}", System.currentTimeMillis() - startTime, result);
        return responseResult;
    }

    public static ResponseResult post(String httpUrl) throws Exception {
        return post(httpUrl, null);
    }

    public static ResponseResult post(String httpUrl, PostParam postParam) throws Exception {
        LOG.info("---------- POST: {}", httpUrl);
        LOG.info("params: {}", JSON.toJSONString(postParam));
        long startTime = System.currentTimeMillis();
        if (postParam == null) {
            postParam = new PostParam();
        }
        if (StringUtils.isBlank(httpUrl)) {
            throw new RuntimeException("url must not be null");
        }
        httpUrl = setUrlParams(httpUrl, postParam);
        Charset charset = postParam.getCharset();
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        Constants.ContentType contentType = postParam.getContentType();
        if (contentType == null) {
            contentType = Constants.ContentType.APPLICATION_JSON;
        }
        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        DataOutputStream ds = null;
        BufferedReader br = null;
        String body = null;
        ResponseResult responseResult = new ResponseResult();
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(postParam.getConnectTimeout());
            connection.setReadTimeout(postParam.getReadTimeout());
            // 默认值为：false，当向远程服务器传送数据/写数据时，需要设置为true
            connection.setDoOutput(true);
            // 默认值为：true，当前向远程服务读取数据时，设置为true，该参数可有可无
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", contentType.getValueWithCharset(charset.name()));
            Map<String, String> requestProperty = postParam.getRequestProperty();
            if (requestProperty != null) {
                for (String key : requestProperty.keySet()) {
                    connection.setRequestProperty(key, requestProperty.get(key));
                }
            }
            switch (contentType) {
                case APPLICATION_XML:
                    body = StringUtils.mapToXml(postParam.getBody());
                    break;
                case MULTIPART_FORM_DATA:
                    String end = "\r\n";
                    String twoHyphens = "--";
                    String boundary = "----WebKitFormBoundary" + StringUtils.randomStr(10);
                    connection.setRequestProperty("Content-Type", contentType.getValue() + "; boundary=" + boundary);
                    ds = new DataOutputStream(connection.getOutputStream());
                    Map<String, String> postParamBody = postParam.getBody();
                    if (postParamBody != null) {
                        for (String key : postParamBody.keySet()) {
                            ds.writeBytes(twoHyphens + boundary);
                            ds.writeBytes(end);
                            ds.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"");
                            ds.writeBytes(end);
                            ds.writeBytes("Content-Type: text/plain");
                            ds.writeBytes(end);
                            ds.writeBytes(end);
                            ds.writeBytes(postParamBody.get(key));
                            ds.writeBytes(end);
                        }
                    }
                    List<String> uploadFilePaths = postParam.getUploadFilePaths();
                    List<String> uploadFileNames = postParam.getUploadFileNames();
                    if (uploadFilePaths != null && uploadFilePaths.size() > 0) {
                        for (int i = 0; i < uploadFilePaths.size(); i++) {
                            String uploadFile = uploadFilePaths.get(i);
                            String filename = uploadFile.substring(uploadFile.lastIndexOf("/") + 1);
                            ds.writeBytes(twoHyphens + boundary);
                            ds.writeBytes(end);
                            String uploadFileName = "file" + i;
                            if (uploadFileNames != null) {
                                uploadFileName = uploadFileNames.get(i);
                            }
                            ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + uploadFileName + "\"; filename=\"" + filename + "\"");
                            ds.writeBytes(end);
                            // ds.writeBytes("Content-Type: image/jpeg");
                            // ds.writeBytes(end);
                            ds.writeBytes(end);
                            FileInputStream fis = new FileInputStream(uploadFile);
                            int bufferSize = 1024;
                            byte[] buffer = new byte[bufferSize];
                            int length;
                            while ((length = fis.read(buffer)) != -1) {
                                ds.write(buffer, 0, length);
                            }
                            ds.writeBytes(end);
                            fis.close();
                        }
                        ds.writeBytes(twoHyphens + boundary + twoHyphens);
                        ds.writeBytes(end);
                    }
                    ds.flush();
                    break;
                case APPLICATION_FORM_URLENCODED:
                    StringBuilder postData = new StringBuilder();
                    for (Map.Entry<String, String> param : postParam.getBody().entrySet()) {
                        if (postData.length() != 0) postData.append('&');
                        postData.append(URLEncoder.encode(param.getKey(), charset.name()));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(param.getValue()), charset.name()));
                    }
                    body = postData.toString();
                    break;
                case APPLICATION_JSON:
                default:
                    body = JSON.toJSONString(postParam.getBody());
                    break;
            }
            if (ds == null) {
                os = connection.getOutputStream();
                os.write(body.getBytes());
                os.flush();
            }
            responseResult.setResponseCode(connection.getResponseCode());
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            responseResult.setHeaderFields(headerFields);
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String result = br.lines().collect(Collectors.joining("\n"));
                responseResult.setResult(result);
            }
        } finally {
            close(br);
            close(os);
            close(is);
            close(connection);
        }
        LOG.info("Time usage: {}ms, Result: {}", System.currentTimeMillis() - startTime, JSON.toJSONString(responseResult));
        return responseResult;
    }

    private static String setUrlParams(String httpUrl, GetParam getParam) {
        Map<String, String> params = getParam.getParams();
        if (params != null) {
            ArrayList<String> list = new ArrayList<>();
            for (String key : params.keySet()) {
                String value = params.get(key);
                list.add(key + "=" + value);
            }
            if (httpUrl.contains("?")) {
                httpUrl = httpUrl + "&";
            } else {
                httpUrl = httpUrl + "?";
            }
            httpUrl = httpUrl + String.join("&", list);
        }
        return httpUrl;
    }

    private static void close(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }

    private static void close(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                LOG.warn(e, "close stream fail");
            }
        }
    }
}
