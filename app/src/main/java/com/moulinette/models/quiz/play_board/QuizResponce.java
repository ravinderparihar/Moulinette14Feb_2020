
package com.moulinette.models.quiz.play_board;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuizResponce {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("quiz_title")
    @Expose
    private String quizTitle;

    @SerializedName("quizplayed")
    @Expose
    private Boolean quizplayed;


    public Boolean getQuizplayed() {
        return quizplayed;
    }

    public void setQuizplayed(Boolean quizplayed) {
        this.quizplayed = quizplayed;
    }
    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
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
