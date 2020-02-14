
package com.moulinette.models.sms.short_codes;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetShortCodeResponce {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("short_code")
    @Expose
    private List<ShortCode> shortCode = null;
    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<ShortCode> getShortCode() {
        return shortCode;
    }

    public void setShortCode(List<ShortCode> shortCode) {
        this.shortCode = shortCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
