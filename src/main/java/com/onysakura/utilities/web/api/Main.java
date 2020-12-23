package com.onysakura.utilities.web.api;

import com.onysakura.utilities.utils.httpclient.Constants;
import com.onysakura.utilities.utils.httpclient.GetParam;
import com.onysakura.utilities.utils.httpclient.HttpClientUtils;
import com.onysakura.utilities.utils.httpclient.PostParam;

public class Main {

    private static final String URL = "https://httpbin.org/";

    public static void main(String[] args) throws Exception {
//        get();
//        post();
//        postJson();
//        postForm();
        postFile();
    }

    public static void get() throws Exception {
        HttpClientUtils.get(URL + "get", new GetParam().addParam("k", "v").addParam("k2", "v2"));
    }

    public static void post() throws Exception {
        HttpClientUtils.post(URL + "post",
                new PostParam()
                        .addParam("k1", "v1")
                        .addParam("k2", "v2")
                        .addRequestProperty("Authorization", "AuthorizationValue")
        );
    }

    public static void postJson() throws Exception {
        HttpClientUtils.post(URL + "post",
                new PostParam()
                        .setContentType(Constants.ContentType.APPLICATION_JSON)
                        .addBody("k1", "v1")
                        .addBody("k2", "v2")
        );
    }

    public static void postForm() throws Exception {
        HttpClientUtils.post(URL + "post",
                new PostParam()
                        .setContentType(Constants.ContentType.APPLICATION_FORM_URLENCODED)
                        .addBody("k1", "v1")
                        .addBody("k2", "v2")
        );
    }

    public static void postFile() throws Exception {
        HttpClientUtils.post(URL + "post",
                new PostParam()
                        .setContentType(Constants.ContentType.MULTIPART_FORM_DATA)
                        .addBody("k1", "v1")
                        .addBody("k2", "v2")
                        .addUploadFilePath("/Files/Temp/Types/text.txt")
                        .addUploadFileName("text")
                        .addUploadFilePath("/Files/Temp/Types/image.jpg")
                        .addUploadFileName("data")
        );
    }
}
