package com.psylife.wrmvplibrary.data.net;

import com.psylife.wrmvplibrary.BaseApplication;
import com.psylife.wrmvplibrary.BuildConfig;
import com.psylife.wrmvplibrary.utils.LogUtil;
import com.psylife.wrmvplibrary.utils.sex.DeCodeGsonConverterFactory;
import com.psylife.wrmvplibrary.utils.sex.StringConverterFactory;
import com.psylife.wrmvplibrary.utils.sex.XMLConverterFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hpw on 16/11/2.
 */

public class RxService {
    private static final int TIMEOUT_READ = 6000;
    private static final int TIMEOUT_CONNECTION = 6000;
    private static Map<String, String> map;
    private static final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static CacheInterceptor cacheInterceptor = new CacheInterceptor();
    private static Builder builder = new OkHttpClient().newBuilder();
    private static OkHttpClient okHttpClient = builder
            //SSL证书
            .sslSocketFactory(TrustManager.getUnsafeOkHttpClient())
            .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
            //打印日志
            .addInterceptor(getLoggingInterceptor())
            //设置Cache
            .addNetworkInterceptor(cacheInterceptor)//缓存方面需要加入这个拦截器
            .addInterceptor(cacheInterceptor)
            .addNetworkInterceptor(new RequestInterceptor())
            .cache(HttpCache.getCache())
            //time out
            .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
            //失败重连
            .retryOnConnectionFailure(true)
            .build();

    public static <T> T createApi(Class<T> clazz, String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(clazz);
    }

    public static <T> T createApiString(Class<T> clazz, String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(StringConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }

    public static <T> T createApiXml(Class<T> clazz, String url, String beanRoot) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(XMLConverterFactory.create(beanRoot))
                .build();

        return retrofit.create(clazz);
    }

    public static <T> T createApiDecode(Class<T> clazz, String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(DeCodeGsonConverterFactory.create())
                .build();

        return retrofit.create(clazz);
    }

    /**
     * 请求拦截器，修改请求header
     */
    public static class RequestInterceptor implements Interceptor {

        public RequestInterceptor() {

        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request.Builder mBuild = original.newBuilder();
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    mBuild.header(entry.getKey(), entry.getValue());
                }
            }
            Request request = mBuild.build();
            LogUtil.d("request:" + request.toString());
            LogUtil.d("request headers:" + request.headers().toString());
            return chain.proceed(request);
        }
    }

    public static void setHeaders(Map<String, String> headMap) {
        map = headMap;
    }

    public static HttpLoggingInterceptor getLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        if (false) {
            httpLoggingInterceptor.setLevel(Level.BODY);
        } else {
            httpLoggingInterceptor.setLevel(Level.NONE);
        }
        return httpLoggingInterceptor;
    }
}

