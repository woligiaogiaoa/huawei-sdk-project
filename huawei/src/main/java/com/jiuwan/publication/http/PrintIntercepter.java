package com.jiuwan.publication.http;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * ：
 */
public class PrintIntercepter implements Interceptor {

    private static final String TAG = "AccessTokenInterceptor";

    private static final String JSON_ACCESS_TOKEN = ",\"token\":" + "\"" + "?" + "\"" + ",\"token_type\":1}";

    /**
     * synchronized
     *
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public synchronized Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        // Logs.e(String.format("发送请求 %s on %s%n%s",
        // request.url(), request.headers(), bodyToString(request.body())));
        Request.Builder requestBuilder = request.newBuilder();


        //添加timezone。token，version，user-agent

        Calendar calendar= Calendar.getInstance();

        request = requestBuilder.build();

        Response response = chain.proceed(request);
        //request.newBuilder().removeHeader().addHeader()




        Log.e("dayin",String.format("发送请求 %s on %s%n%s",
                request.url(), request.headers(), bodyToString(request.body())));


        Log.e("dayin",String.format("接收响应: [%s] %n返回json:【%s】 %s",
                response.request().url(),
                response.peekBody(1024 * 1024).string(),
                response.headers()));

        return response;
    }




    /**
     * 将请求提转为String
     *
     * @param request
     * @return
     */
    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "";
        }
    }

    /**
     * 将表单提交的内容转为JSON查看
     *
     * @param post
     * @return
     */
    public static String postStringToJson(String post) {
        String subJson[] = post.split("&");
        String newJson = "{";
        for (int i = 0; i < subJson.length; i++) {
            newJson += "\"" + subJson[i].replace("=", "\":\"") + "\",";
        }
        return newJson.substring(0, newJson.length() - 1) + "}";
    }


    /**
     * 请求是否可以注入参数
     *
     * @param request
     * @return
     */
    private boolean canInjectIntoBody(Request request) {

        if (request == null) {
            return false;
        }

        if (!TextUtils.equals(request.method(), "POST")) {
            return false;
        }

        RequestBody body = request.body();
        if (body == null) {
            return false;
        }

        MediaType mediaType = body.contentType();
        if (mediaType == null) {
            return false;
        }

        //针对 POST 表单 x-www-form-urlencoded 和 POST 实体 json
        if (!TextUtils.equals(mediaType.subtype(), "x-www-form-urlencoded") && !TextUtils.equals(mediaType.subtype(), "json")) {
            return false;
        }
        return true;
    }
}
