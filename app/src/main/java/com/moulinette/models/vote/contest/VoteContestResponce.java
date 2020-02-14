
package com.moulinette.models.vote.contest;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VoteContestResponce {

    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("title_vote")
    @Expose
    private String titleVote;
    @SerializedName("nummber_winner")
    @Expose
    private String nummberWinner;

    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("quizplayed")
    @Expose
    private Boolean quizplayed;


    public Boolean getQuizplayed() {
        return quizplayed;
    }

    public void setQuizplayed(Boolean quizplayed) {
        this.quizplayed = quizplayed;
    }

    public String getTitleVote() {
        return titleVote;
    }

    public void setTitleVote(String titleVote) {
        this.titleVote = titleVote;
    }

    public String getNummberWinner() {
        return nummberWinner;
    }

    public void setNummberWinner(String nummberWinner) {
        this.nummberWinner = nummberWinner;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
