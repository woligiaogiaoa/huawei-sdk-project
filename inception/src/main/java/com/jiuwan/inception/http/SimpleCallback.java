/*
 * Copyright © 2018 Zhenjie Yan.
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
package com.jiuwan.inception.http;

import android.content.Context;
import android.util.Log;

import com.jiuwan.inception.R;
import com.elephant.library.kalle.exception.ConnectTimeoutError;
import com.elephant.library.kalle.exception.HostError;
import com.elephant.library.kalle.exception.NetworkError;
import com.elephant.library.kalle.exception.ReadTimeoutError;
import com.elephant.library.kalle.exception.URLError;
import com.elephant.library.kalle.exception.WriteException;
import com.elephant.library.kalle.simple.Callback;
import com.elephant.library.kalle.simple.SimpleResponse;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by Zhenjie Yan on 2018/3/26.
 */
public abstract class SimpleCallback<S> extends Callback<S, String> {

    private Context mContext;

    public SimpleCallback(Context context) {
        this.mContext = context;
    }

    @Override
    public Type getSucceed() {
        Type superClass = getClass().getGenericSuperclass();
        return ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    @Override
    public Type getFailed() {
        return String.class;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onException(Exception e) {
        String message;
        if (e instanceof NetworkError) {
            message = mContext.getString(R.string.inception_http_exception_network);
        } else if (e instanceof URLError) {
            message = mContext.getString(R.string.inception_http_exception_url);
        } else if (e instanceof HostError) {
            message = mContext.getString(R.string.inception_http_exception_host);
        } else if (e instanceof ConnectTimeoutError) {
            message = mContext.getString(R.string.inception_http_exception_connect_timeout);
        } else if (e instanceof WriteException) {
            message = mContext.getString(R.string.inception_http_exception_write);
        } else if (e instanceof ReadTimeoutError) {
            message = mContext.getString(R.string.inception_http_exception_read_timeout);
        } else {
            message = mContext.getString(R.string.inception_http_exception_unknown_error) + e.getCause() == null ? "" : e.getCause().toString();
        }

        Log.e("SimpleCallback", "onException: " + e.getMessage());
        onResponse(SimpleResponse.<S, String>newBuilder().failed(message).build());
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onEnd() {
    }
}