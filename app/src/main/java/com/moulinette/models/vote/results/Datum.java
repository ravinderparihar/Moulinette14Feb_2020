
package com.moulinette.models.vote.results;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("contest_id")
    @Expose
    private String contestId;
    @SerializedName("vote_title")
    @Expose
    private String voteTitle;
    @SerializedName("number_winners")
    @Expose
    private String numberWinners;
    @SerializedName("result_date")
    @Expose
    private String resultDate;
    @SerializedName("no_of_voters")
    @Expose
    private String noOfVoters;
    @SerializedName("details")
    @Expose
    private List<Detail> details = null;

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public String getVoteTitle() {
        return voteTitle;
    }

    public void setVoteTitle(String voteTitle) {
        this.voteTitle = voteTitle;
    }

    public String getNumberWinners() {
        return numberWinners;
    }

    public void setNumberWinners(String numberWinners) {
        this.numberWinners = numberWinners;
    }

    public String getResultDate() {
        return resultDate;
    }

    public void setResultDate(String resultDate) {
        this.resultDate = resultDate;
    }

    public String getNoOfVoters() {
        return noOfVoters;
    }

    public void setNoOfVoters(String noOfVoters) {
        this.noOfVoters = noOfVoters;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

}
