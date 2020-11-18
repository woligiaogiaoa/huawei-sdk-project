/*
 * Copyright 2018 Zhenjie Yan.
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
package com.batam.inception.http;

import android.content.Context;
import android.util.Log;

import com.batam.inception.R;
import com.elephant.library.fastjson.JSON;
import com.elephant.library.kalle.Response;
import com.elephant.library.kalle.simple.Converter;
import com.elephant.library.kalle.simple.SimpleResponse;

import java.lang.reflect.Type;

/**
 * Created by Zhenjie Yan on 2018/3/26.
 */
public class JsonConverter implements Converter {

    private Context mContext;

    public JsonConverter(Context context) {
        this.mContext = context;
    }

    @Override
    public <S, F> SimpleResponse<S, F> convert(Type succeed, Type failed, Response response, boolean fromCache)
            throws Exception {
        S succeedData = null; // The data when the business successful.
        F failedData = null; // The data when the business failed.

        int code = response.code();
        String serverJson = response.body().string();
        Log.i("Server Data: ", JSON.parse(serverJson).toString());
        HttpEntity httpEntity;
        try {
            httpEntity = JSON.parseObject(serverJson, HttpEntity.class);
        } catch (Exception e) {
            httpEntity = new HttpEntity();
            httpEntity.setCode(-1);
            httpEntity.setMessage(mContext.getString(R.string.inception_http_server_data_format_error));
        }
        if (code >= 200 && code < 500) { // Http is successful.
            if (httpEntity.isSucceed()) { // The server successfully processed the business.
                // 如果是登录接口，返回的Header-Authorization保存起来
                /*if (succeed == LoginResult.class) {
                    if (response.headers() != null && response.headers().get(Constants.KEY_AUTHORIZATION) != null &&
                            !response.headers().get(Constants.KEY_AUTHORIZATION).isEmpty()) {
                        Pref.get(((Application) mContext.getApplicationContext())).setStringCommit(Constants.KEY_AUTHORIZATION,
                                response.headers().getFirst(Constants.KEY_AUTHORIZATION));
                        HttpManager.updateAuthorizationHeader();
                    }
                }*/
                try {
                    if (succeed == String.class || succeed == Integer.class ||
                            succeed == Float.class || succeed == Double.class ||
                            succeed == Short.class || succeed == Long.class ||
                            succeed == Boolean.class || succeed == Byte.class ||
                            succeed == Character.class
                    ) {
                        //noinspection unchecked
                        succeedData = (S) httpEntity.getData();
                    } else {
                        succeedData = JSON.parseObject(httpEntity.getData(), succeed);
                    }
                } catch (Exception e) {
                    //noinspection unchecked
                    failedData = (F) mContext.getString(R.string.inception_http_server_data_format_error);
                }
            } else {
                // The server failed to read the wrong information.
                //noinspection unchecked
                failedData = (F) httpEntity.getMessage();
            }
        } else if (code >= 500) {
            //noinspection unchecked
            failedData = (F) mContext.getString(R.string.inception_http_server_error);
        }


        return SimpleResponse.<S, F>newBuilder().code(response.code())
                .headers(response.headers())
                .fromCache(fromCache)
                .succeed(succeedData)
                .failed(failedData)
                .build();
    }
}