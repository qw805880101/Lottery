/*
 * Copyright (C) 2015 Square, Inc.
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
package com.psylife.wrmvplibrary.utils.sex;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.psylife.wrmvplibrary.BaseApplication;
import com.psylife.wrmvplibrary.bean.BaseBeanInfo;
import com.psylife.wrmvplibrary.utils.LogUtil;
import com.psylife.wrmvplibrary.utils.SignUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

final class DeCodeGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    DeCodeGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        try {
//            Map<String, Object> map = JSON.parseObject(response, Map.class);

//            if (map.get("code").toString().equals("0000")) {
//                response = Utils.deCode(map.get("result").toString());
//                Map<String, Object> dataMap = JSON.parseObject(response, Map.class);
//                map.remove("result");
//                map.put("result", dataMap);
//                response = JSON.toJSONString(map);
//            }
            LogUtil.d("response", response);
            BaseBeanInfo mBaseBeanInfo = JSON.parseObject(response, BaseBeanInfo.class);
            try {
//                HashMap<String, Object> linkedHashMap = JSON.parseObject(response, LinkedHashMap.class,
//                        Feature.OrderedField);
                Gson gs = new GsonBuilder()
                        .disableHtmlEscaping()
                        .create();
                HashMap linkedHashMap = gs.fromJson(response, LinkedHashMap.class);

                String test = SignUtil.sign(gs.toJson(linkedHashMap.get("responseData")), BaseApplication.PRIVATE_KEY);

                LogUtil.d("responseData", gs.toJson(linkedHashMap.get("responseData")));
                LogUtil.d("sign", gs.toJson(linkedHashMap.get("sign")));
                LogUtil.d("test", test);

                boolean checkStatus = SignUtil.checkSign(gs.toJson(linkedHashMap.get("responseData")), linkedHashMap.get("sign").toString());

                if (!checkStatus) {

                    mBaseBeanInfo.setRespCode("9999");
                    mBaseBeanInfo.setRespDesc("返回参数验签失败");
                    response = JSON.toJSONString(mBaseBeanInfo);
                } else {
                    response = JSON.toJSONString(mBaseBeanInfo.getResponseData());
                }
            } catch (Exception e) {
                e.printStackTrace();
                mBaseBeanInfo.setRespCode("9999");
                mBaseBeanInfo.setRespDesc("返回参数校验失败 ");
                response = JSON.toJSONString(mBaseBeanInfo);
            }

            value = ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), response);
            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}
