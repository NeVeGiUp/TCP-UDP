package com.itc.smartbroadcast.channels.http;

import android.text.TextUtils;

import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.util.ConfigUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @ 作者: 李观鸿
 * @ 创建于: 2016-12-18 09:19
 * @ 描述 : 初始化retrofit对象
 */


public class HttpNetWork {

    private static volatile HttpNetWork mInstance;
    private ApiService mApi;

    private HttpNetWork() {

    }

    public static HttpNetWork getInstance() {
        if (mInstance == null) {
            synchronized (HttpNetWork.class) {
                if (mInstance == null) {
                    mInstance = new HttpNetWork();
                }
            }
        }
        return mInstance;
    }

    private static OkHttpClient client = new OkHttpClient.Builder()
            //超时处理
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            //添加拦截器,添加请求头
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    String cloudLogined = AppDataCache.getInstance().getString("cloudLogined");
                    String cloudAuthorization = AppDataCache.getInstance().getString("cloudAuthorization");
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Token", cloudLogined)
                            .header("Authorization", "Bearer " + cloudAuthorization)
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                }
            })
            .build();

    public ApiService getApi() {
        if (mApi == null) {
            synchronized (HttpNetWork.class) {
                if (mApi == null) {
                    Retrofit retrofit = new Retrofit.Builder()
                            //使用自定义的mGsonConverterFactory
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(client)
                            .baseUrl(ConfigUtils.BASEURL)
                            .build();
                    mApi = retrofit.create(ApiService.class);
                }
            }
        }
        return mApi;
    }


}
