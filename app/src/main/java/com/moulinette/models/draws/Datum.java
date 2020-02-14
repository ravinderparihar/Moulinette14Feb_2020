
package com.moulinette.models.draws;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name_challenge")
    @Expose
    private String nameChallenge;
    @SerializedName("video_link")
    @Expose
    private String videoLink;
    @SerializedName("video_thumb")
    @Expose
    private String videoThumb;
    @SerializedName("short_code")
    @Expose
    private String shortCode;
    @SerializedName("result_date")
    @Expose
    private String resultDate;
    @SerializedName("no_of_parti")
    @Expose
    private String noOfParti;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("value")
    @Expose
    private List<Value> value = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameChallenge() {
        return nameChallenge;
    }

    public void setNameChallenge(String nameChallenge) {
        this.nameChallenge = nameChallenge;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(String videoThumb) {
        this.videoThumb = videoThumb;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getResultDate() {
        return resultDate;
    }

    public void setResultDate(String resultDate) {
        this.resultDate = resultDate;
    }

    public String getNoOfParti() {
        return noOfParti;
    }

    public void setNoOfParti(String noOfParti) {
        this.noOfParti = noOfParti;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<Value> getValue() {
        return value;
    }

    public void setValue(List<Value> value) {
        this.value = value;
    }

}
