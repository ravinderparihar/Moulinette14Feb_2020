
package com.moulinette.models.fb_login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FBLoginRequest {

    @SerializedName("access_token")
    @Expose
    private String access_token;

    @SerializedName("device_id")
    @Expose
    private String device_id;

    @SerializedName("device_token")
    @Expose
    private String deviceToken;

    @SerializedName("device_type")
    @Expose
    private String deviceType;


    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getAccess_token ( ){
        return access_token;
    }

    public void setAccess_token (String access_token){
        this.access_token = access_token;
    }
}
