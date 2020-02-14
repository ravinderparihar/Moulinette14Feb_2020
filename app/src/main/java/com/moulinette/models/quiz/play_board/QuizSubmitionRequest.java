
package com.moulinette.models.quiz.play_board;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuizSubmitionRequest {

    @SerializedName("quiz_id")
    @Expose
    private String quizId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("number_correct_ans")
    @Expose
    private String numberCorrectAns;

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
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

}
