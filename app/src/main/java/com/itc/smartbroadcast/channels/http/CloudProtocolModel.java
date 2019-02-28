package com.itc.smartbroadcast.channels.http;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.base.BaseModel;
import com.itc.smartbroadcast.bean.CloudAddProjectInfo;
import com.itc.smartbroadcast.bean.CloudAuthorizationInfo;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.CloudLoginInfo;
import com.itc.smartbroadcast.bean.CloudProjectListInfo;
import com.itc.smartbroadcast.bean.CloudRegisterCodeInfo;
import com.itc.smartbroadcast.bean.CloudRegisterInfo;
import com.itc.smartbroadcast.cache.AppDataCache;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @ 作者  : 李观鸿
 * @ 创建于: 2016-12-18 09:30
 * @ 描述 : 封装每个请求数据回调,因项目http接口不多,统一由CloudProtocolModel处理回调
 */


public class CloudProtocolModel {

    /**
     * 登录数据回调
     *
     * @param mobile
     * @param password
     */
    public static void getCloudLoginedMsg(String mobile, String password, final Context context) {
        ApiService api = HttpNetWork.getInstance().getApi();
        Call<CloudLoginInfo> news = api.getLoginMsg(mobile, password);
        news.enqueue(new Callback<CloudLoginInfo>() {
            @Override
            public void onResponse(Call<CloudLoginInfo> call, Response<CloudLoginInfo> response) {
                parseData(response, context.getString(R.string.cloud_login), context);
            }

            @Override
            public void onFailure(Call<CloudLoginInfo> call, Throwable t) {
                parseData(context.getString(R.string.cloud_login), context);
            }

        });
    }


    /**
     * 系统授权回调
     *
     * @param clientId 客户端id
     * @param secret   密钥
     */
    public static void getCloudAuthorization(String clientId, String secret, final Context context) {
        ApiService api = HttpNetWork.getInstance().getApi();
        Call<CloudAuthorizationInfo> news = api.getAuthorization(clientId, secret);
        news.enqueue(new Callback<CloudAuthorizationInfo>() {
            @Override
            public void onResponse(Call<CloudAuthorizationInfo> call, Response<CloudAuthorizationInfo> response) {
//                parseData(response, context.getString(R.string.cloud_authorization), context);
                if (response == null)
                    return;
                CloudAuthorizationInfo body = response.body();
                AppDataCache.getInstance().putString("cloudAuthorization",body.getAccess_token());
            }

            @Override
            public void onFailure(Call<CloudAuthorizationInfo> call, Throwable t) {
                parseData(context.getString(R.string.cloud_authorization), context);
            }
        });
    }


    /**
     * 获取注册验证码
     *
     * @param mobile
     */
    public static void postCloudRegisterCode(String mobile, final Context context) {
        ApiService api = HttpNetWork.getInstance().getApi();
        Call<CloudRegisterCodeInfo> news = api.postRegisterCode(mobile);
        news.enqueue(new Callback<CloudRegisterCodeInfo>() {
            @Override
            public void onResponse(Call<CloudRegisterCodeInfo> call, Response<CloudRegisterCodeInfo> response) {
                parseData(response, context.getString(R.string.cloud_register_code), context);
            }

            @Override
            public void onFailure(Call<CloudRegisterCodeInfo> call, Throwable t) {
                parseData(context.getString(R.string.cloud_register_code), context);
            }
        });
    }


    /**
     * 云注册
     *
     * @param map     携带请求参数键值对
     * @param context
     */
    public static void postCloudRegister(Map<String, String> map, final Context context) {
        ApiService api = HttpNetWork.getInstance().getApi();
        Call<CloudRegisterInfo> news = api.postRegister(map);
        news.enqueue(new Callback<CloudRegisterInfo>() {
            @Override
            public void onResponse(Call<CloudRegisterInfo> call, Response<CloudRegisterInfo> response) {
                parseData(response, context.getString(R.string.cloud_register), context);
            }

            @Override
            public void onFailure(Call<CloudRegisterInfo> call, Throwable t) {
                parseData(context.getString(R.string.cloud_register), context);
            }
        });
    }


    /**
     * 添加项目
     *
     * @param map
     * @param context
     */
    public static void postCloudAddProject(Map<String, String> map, final Context context) {
        ApiService api = HttpNetWork.getInstance().getApi();
        Call<CloudAddProjectInfo> news = api.postAddProject(map);
        news.enqueue(new Callback<CloudAddProjectInfo>() {
            @Override
            public void onResponse(Call<CloudAddProjectInfo> call, Response<CloudAddProjectInfo> response) {
                parseData(response, context.getString(R.string.cloud_add_project), context);
            }

            @Override
            public void onFailure(Call<CloudAddProjectInfo> call, Throwable t) {
                parseData(context.getString(R.string.cloud_add_project), context);
            }
        });
    }

    /**
     * 找回密码
     *
     */
    public static void postForgotPassword(Map<String,String> map ,final Context context){
        ApiService api = HttpNetWork.getInstance().getApi();
        Call<CloudRegisterInfo> news = api.postForgotPass(map);
        news.enqueue(new Callback<CloudRegisterInfo>() {
            @Override
            public void onResponse(Call<CloudRegisterInfo> call, Response<CloudRegisterInfo> response) {
                parseData(response, context.getString(R.string.cloud_forget_pass), context);
            }

            @Override
            public void onFailure(Call<CloudRegisterInfo> call, Throwable throwable) {
                parseData(context.getString(R.string.cloud_forget_pass), context);
            }
        });

    }

    /**
     * 找回密码验证码
     *
     */
    public static void postForgotPasswordCode(String mobile, final Context context){
        ApiService api = HttpNetWork.getInstance().getApi();
        Call<CloudRegisterInfo> news = api.postForgotPassCode(mobile);
        news.enqueue(new Callback<CloudRegisterInfo>() {
            @Override
            public void onResponse(Call<CloudRegisterInfo> call, Response<CloudRegisterInfo> response) {
                parseData(response, context.getString(R.string.cloud_forgot_pass_code), context);
            }

            @Override
            public void onFailure(Call<CloudRegisterInfo> call, Throwable throwable) {
                parseData(context.getString(R.string.cloud_forgot_pass_code), context);
            }
        });

    }


    /**
     * 获取用户项目列表
     *
     * @param context
     */
    public static void getCloudProjectList(final Context context) {
        ApiService api = HttpNetWork.getInstance().getApi();
        Call<CloudProjectListInfo> news = api.getProjectList();
        news.enqueue(new Callback<CloudProjectListInfo>() {
            @Override
            public void onResponse(Call<CloudProjectListInfo> call, Response<CloudProjectListInfo> response) {
                parseData(response, context.getString(R.string.cloud_project_list), context);
            }

            @Override
            public void onFailure(Call<CloudProjectListInfo> call, Throwable t) {
                parseData(context.getString(R.string.cloud_project_list), context);
            }
        });
    }


    /**
     * 解析成功回调数据
     *
     * @param response 数据体
     * @param type     请求类型
     */
    private static void parseData(Response<? extends BaseModel> response, String type, Context context) {
        if (response.body() == null)
            return;
        BaseModel body = response.body();
        String data = JSONObject.toJSONString(body);
        //封装第一层数据,添加成功标志
        BaseBean innerData = new BaseBean();
        innerData.setType(context.getString(R.string.cloud_success));
        innerData.setData(data);
        String innerDataJsonStr = JSONObject.toJSONString(innerData);
        //封装第二层数据,添加请求类型
        BaseBean baseBean = new BaseBean();
        baseBean.setType(type);
        baseBean.setData(innerDataJsonStr);


        //最终json数据
        String json = JSONObject.toJSONString(baseBean);
        EventBus.getDefault().post(json);
    }


    /**
     * 处理失败回调数据
     *
     * @param type 请求类型
     */
    private static void parseData(String type, Context context) {
        //封装第一层数据,添加失败标志
        BaseBean innerData = new BaseBean();
        innerData.setType(context.getString(R.string.cloud_failed));
        String innerDataJsonStr = JSONObject.toJSONString(innerData);
        //封装第二层数据,添加请求类型
        BaseBean baseBean = new BaseBean();
        baseBean.setType(type);
        baseBean.setData(innerDataJsonStr);
        //最终json数据
        String json = JSONObject.toJSONString(baseBean);
        EventBus.getDefault().post(json);
    }

}
