package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * 系统授权信息
 *
 */

public class CloudAuthorizationInfo extends BaseModel {


    /**
     * error : 0
     * message : success
     * access_token : CozZkiXZKkWrQVRKwmxIXfdw8wV3i8Tk
     * expires : 72000
     */

    private int error;
    private String message;
    private String access_token;
    private int expires;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires() {
        return expires;
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }
}
