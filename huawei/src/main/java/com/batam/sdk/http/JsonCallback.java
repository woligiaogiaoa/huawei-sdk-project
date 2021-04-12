/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.batam.sdk.http;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.request.base.Request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.Headers;
import okhttp3.Response;


public abstract class JsonCallback<T> extends AbsCallback<T> {

    public static final String KEY_HTTP_DEVICE_HEADER = "Info";

    public static final String KEY_HTTP_ACCEPT_HEADER = "Accept";

    public static final String KEY_HTTP_AUTH_HEADER = "Authorization";

    private Type type;
    private Class<T> clazz;

    public JsonCallback() {
    }

    public JsonCallback(Type type) {
        this.type = type;
    }

    public JsonCallback(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        // 主要用于在所有请求之前添加公共的请求头或请求参数
        // 例如登录授权的 token
        // 使用的设备信息
        // 可以随意添加,也可以什么都不传
        // 还可以在这里对所有的参数进行加密，均在这里实现
        request.headers(KEY_HTTP_ACCEPT_HEADER, "application/json");
        //request.headers(KEY_HTTP_DEVICE_HEADER, MyApp.Companion.getApp().getDeviceInfo());
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(Response response) throws Throwable {

        //获取服务端返回的header信息
        Headers headers = response.headers();
        if (headers != null) {
            String userAuth = headers.get(KEY_HTTP_AUTH_HEADER);
            OkGo.getInstance().addCommonHeaders(
                    new HttpHeaders(KEY_HTTP_AUTH_HEADER, userAuth)
            );
        }
        if (type == null) {
            if (clazz == null) {
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            } else {
                JsonConvert<T> convert = new JsonConvert<>(clazz);
                return convert.convertResponse(response);
            }
        }

        JsonConvert<T> convert = new JsonConvert<>(type);
        return convert.convertResponse(response);
    }

    @Override
    public void onError(com.lzy.okgo.model.Response<T> response) {
        Throwable exception = response.getException();
        if (exception != null) {
            String msg = checkFail(exception);
            if (msg == null) {
                return;
            }
            String[] msgAndCode = msg.split(IllegalResponseException.SPLIT_SEPARATOR);
            int code = -Integer.MAX_VALUE;
            if (msgAndCode.length > 1) {
                try {
                    code = Integer.parseInt(msgAndCode[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                onError(msgAndCode[0], code);
            } else {
                onError(msg, code);
            }
        } else {
            super.onError(response);
        }
    }

    private String checkFail(Throwable t) {
        Throwable cause = t.getCause();
        if (cause == null) {
            cause = t;
        }
        cause.printStackTrace();
        if (cause instanceof SocketTimeoutException) {
            return "网络连接超时，请稍候再试试吧";
        } else if (cause instanceof UnknownHostException) {
            return "网络异常，请检查网络设置";
        } else {
            return cause.getMessage();
        }
    }


    public void onError(String errorMsg, int code) {
    }

}
