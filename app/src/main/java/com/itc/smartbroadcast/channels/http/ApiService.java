package com.itc.smartbroadcast.channels.http;

import com.itc.smartbroadcast.bean.CloudAddProjectInfo;
import com.itc.smartbroadcast.bean.CloudAuthorizationInfo;
import com.itc.smartbroadcast.bean.CloudLoginInfo;
import com.itc.smartbroadcast.bean.CloudProjectListInfo;
import com.itc.smartbroadcast.bean.CloudRegisterCodeInfo;
import com.itc.smartbroadcast.bean.CloudRegisterInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.util.ConfigUtils;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @ 作者: 李观鸿
 * @ 创建于: 2016-12-18 09:25
 * @ 描述 : 封装每个http请求
 */


public interface ApiService {

    /**
     * 登录
     *
     * @param mobile   用户手机号码
     * @param password 用户密码
     * @return CloudLoginInfo对象
     */
    @GET("api/user/login")
    Call<CloudLoginInfo> getLoginMsg(@Query("mobile") String mobile, @Query("password") String password);


    /**
     * 系统授权
     *
     * @param clientId 终端id
     * @param secret   密钥
     * @return AuthorizationInfo对象
     */
    @GET("accessToken")
    Call<CloudAuthorizationInfo> getAuthorization(@Query("client_id") String clientId, @Query("secret") String secret);


    /**
     * 获取注册码
     *
     * @param mobile 用户手机号
     * @return GetRegisterCodeInfo对象
     */
    @FormUrlEncoded
    @POST("api/user/regcode")
    Call<CloudRegisterCodeInfo> postRegisterCode(@Field("mobile") String mobile);


    /**
     * 注册
     *
     * @param map 包含请求所有参数字段的map集合
     * @return RegisterInfo对象
     */
    @FormUrlEncoded
    @POST("api/user/register")
    Call<CloudRegisterInfo> postRegister(@FieldMap Map<String, String> map);


    /**
     * 添加项目
     *
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST("api/project/single")
    Call<CloudAddProjectInfo> postAddProject(@FieldMap Map<String, String> map);


    /**
     * 用户绑定的项目列表
     *
     * @return CloudProjectListInfo对象
     */
//    @Headers({"Token:" + ConfigUtils.CLOUD_AUTHORIZATION})
    @GET("api/machine/appUserBindMachine")
    Call<CloudProjectListInfo> getProjectList();

    /**
     *
     * 找回密码
     */
    @FormUrlEncoded
    @POST("/api/user/fndpass")
    Call<CloudRegisterInfo> postForgotPass(@FieldMap Map<String,String> map);

    /**
     *
     * 找回密码验证码
     */
    @FormUrlEncoded
    @POST("/api/user/fndpasscode")
    Call<CloudRegisterInfo> postForgotPassCode(@Field("mobile") String mobile);

}
