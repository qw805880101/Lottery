package com.psylife.wrmvplibrary.utils.sex;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by tc on 2017/6/13.
 */

public class XMLConverterFactory extends Converter.Factory {

    private static String beanRoot; //xml 最外层标签

    //方法为网络调用后 使用
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        //如果出类型为Bitmap 那么就处理用UserResponseConverter 类处理
        // 我们稍后再看这个类
        //如果不为这个处理类那么返回空交给另一个转化器处理
        if (type == String.class)
            return new UserResponseConverter(type, beanRoot);
        return null;
    }

    private static XMLConverterFactory xmlConverterFactory;

    public static XMLConverterFactory create(String bean) {

        beanRoot = bean;

        if (xmlConverterFactory == null) {
            xmlConverterFactory = new XMLConverterFactory();
        }
        return xmlConverterFactory;
    }

    private XMLConverterFactory() {

    }

}
