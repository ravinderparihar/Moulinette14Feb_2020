
package com.moulinette.models.quiz.results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("quizs_id")
    @Expose
    private String quizsId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("number_correct_ans")
    @Expose
    private String numberCorrectAns;
    @SerializedName("play_on")
    @Expose
    private String playOn;
    @SerializedName("question_id")
    @Expose
    private String questionId;
    @SerializedName("quiz_title")
    @Expose
    private String quizTitle;
    @SerializedName("quiz_type")
    @Expose
    private String quizType;
    @SerializedName("number_winners")
    @Expose
    private String numberWinners;
    @SerializedName("result_date")
    @Expose
    private String resultDate;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("modified_at")
    @Expose
    private String modifiedAt;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("count")
    @Expose
    private String count;
    @SerializedName("rank")
    @Expose
    private String rank;

    @SerializedName("link")
    @Expose
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuizsId() {
        return quizsId;
    }

    public void setQuizsId(String quizsId) {
        this.quizsId = quizsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNumberCorrectAns() {
        return numberCorrectAns;
    }

    public void setNumberCorrectAns(String numberCorrectAns) {
        this.numberCorrectAns = numberCorrectAns;
    }

    public String getPlayOn() {
        return playOn;
    }

    public void setPlayOn(String playOn) {
        this.playOn = playOn;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getQuizType() {
        return quizType;
    }

    public void setQuizType(String quizType) {
        this.quizType = quizType;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(String modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

}
