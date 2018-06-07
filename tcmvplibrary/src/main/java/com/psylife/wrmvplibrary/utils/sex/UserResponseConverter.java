package com.psylife.wrmvplibrary.utils.sex;

import com.psylife.wrmvplibrary.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by tc on 2017/6/13.
 */

public class UserResponseConverter<T> implements Converter<ResponseBody, T> {

    private Type type;

    private String beanRoot;

    public UserResponseConverter(Type type, String beanRoot) {
        this.type = type;
        this.beanRoot = beanRoot;
    }

    @Override
    public T convert(ResponseBody responseBody) throws IOException {
        try {
            String str = responseBody.string();
            T obj = null;
            try {
//                    System.out.println("Utils.getResposeBody(str) = " + Utils.getResposeBody(str));
                obj = (T) XmlUtils.getBeanListByParseXml(str, beanRoot, getClass());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return obj;
        } finally {
            responseBody.close();
        }
    }

}
