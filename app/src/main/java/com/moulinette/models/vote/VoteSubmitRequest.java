
package com.moulinette.models.vote;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VoteSubmitRequest {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("contest_id")
    @Expose
    private String contestId;
    @SerializedName("contestant_id")
    @Expose
    private String contestantId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public String getContestantId() {
        return contestantId;
    }

    public void setContestantId(String contestantId) {
        this.contestantId = contestantId;
    }

}
