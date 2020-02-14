
package com.moulinette.models.sms;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SMSPostRequest {

    @SerializedName("SMS")
    @Expose
    private List<SM> sMS = null;

    public List<SM> getSMS() {
        return sMS;
    }

    public void setSMS(List<SM> sMS) {
        this.sMS = sMS;
    }

}
