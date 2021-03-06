
package com.moulinette.models.sms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SM {

    @SerializedName("mob_number")
    @Expose
    private String mobNumber;

    @SerializedName("short_code")
    @Expose
    private String shortCode;

    @SerializedName("thread_id")
    @Expose
    private String threadId;





    public String getThreadId ( ){
        return threadId;
    }

    public void setThreadId (String threadId){
        this.threadId = threadId;
    }

    public String getMobNumber() {
        return mobNumber;
    }

    public void setMobNumber(String mobNumber) {
        this.mobNumber = mobNumber;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }


}
