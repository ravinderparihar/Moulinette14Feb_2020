
package com.moulinette.models.sms.short_codes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShortCode {

    @SerializedName("short_code")
    @Expose
    private String shortCode;

    public String getShortCode ( ){
        return shortCode;
    }

    public void setShortCode (String shortCode){
        this.shortCode = shortCode;
    }
}
