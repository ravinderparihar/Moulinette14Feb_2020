package com.moulinette.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moulinette.R;
import com.moulinette.adapters.VotingCandidatesAdapter;
import com.moulinette.databinding.ActivityVoteCastBinding;
import com.moulinette.databinding.DialogSubmitVoteBinding;
import com.moulinette.dialog.ProgDialog;
import com.moulinette.models.quiz.play_board.QuizSubmitionResponce;
import com.moulinette.models.vote.VoteSubmitRequest;
import com.moulinette.models.vote.contest.Datum;
import com.moulinette.models.vote.contest.VoteContestResponce;
import com.moulinette.models.vote.submit.VoteSubmitReseponce;
import com.moulinette.utilities.MainApplication;
import com.moulinette.utilities.MessageDialog;
import com.moulinette.utilities.TinyDB;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VoteCast extends Activity {

    ActivityVoteCastBinding binding;
    MyClickHandler handlers;
    RecyclerView playedList;

    Activity ac;

    List<Datum> contestantList = new ArrayList<>();

    VotingCandidatesAdapter contestTypeAdapter;
    GridLayoutManager layoutManager;

    AlertDialog alertDialog;
    public static  String selectedContestent = "";

    ProgDialog prog = new ProgDialog();
    TinyDB tinyDB;
//    com.moulinette.models.vote.Datum selectedContest;
    String selectedContestId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vote_cast);

        ac = this;
        tinyDB = new TinyDB(ac);

        try {
            if ( getIntent().getStringExtra("FromApp").matches("true") ){
                try {
                    selectedContestId = getIntent().getStringExtra("SelectedContest_id");
                } catch ( Exception e ) {
                }
            } else {

                // ATTENTION: This was auto-generated to handle app links.
                Intent appLinkIntent = getIntent();
                String appLinkAction = appLinkIntent.getAction();
                Uri appLinkData = appLinkIntent.getData();

                Intent intent = getIntent();
                Uri data = intent.getData();
                String url[] = data.toString().split("http://112.196.38.115/vote/");
                System.out.println("Deep linking Data: " + url[1]);
                selectedContestId = url[1];
            }
        } catch ( Exception e ) {
            System.out.println("Exception occer: :" + e);
            try {
                Intent intent = getIntent();
                Uri data = intent.getData();
                System.out.println("Deep linking Data: " + data);
                String url[] = data.toString().split("http://112.196.38.115/vote/");
                System.out.println("Deep linking Data: " + url[1]);
                selectedContestId = url[1];
            } catch ( Exception ex ) {
                System.out.println("Exception occers: :" + ex);
            }
        }


        playedList = binding.recycleConstents;
        layoutManager = new GridLayoutManager(ac, 3);
        playedList.setLayoutManager(layoutManager);
        contestTypeAdapter = new VotingCandidatesAdapter(ac, contestantList);
        playedList.setAdapter(contestTypeAdapter);

        if ( tinyDB.getString("user_id") != null ){
            getContestant();
        }else{
            binding.alreadyPlayed.setVisibility(View.VISIBLE);
            binding.text.setText(getResources().getString(R.string.wuthout_ac_vote_cast));
        }

        handlers = new MyClickHandler(this);
        binding.setClick(handlers);

    }
    public class MyClickHandler {

        Activity context;

        public MyClickHandler(Activity ac) {
            this.context = ac;
        }
        public void backClick(View view){
            finish();
        }
        public void okClick(View view){
            alertDialog.dismiss();

            Intent intent = new Intent(ac, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            ac.startActivity(intent);
            ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }

        public void submitClick(View view){
            if (!selectedContestent.isEmpty()) {
                submitQuiz();
            }else {
                MessageDialog.showAlert(ac, ac.getResources().getString(R.string.please_cast_vote), ac.getString(R.string.ok));
            }
        }





    }

    public void getContestant() {

        if (MainApplication.isNetworkAvailable(ac)) {
            prog.progDialog(ac);
           String url = "Voteapi/contestant/"+selectedContestId+"/"+tinyDB.getString("user_id");
            MainApplication.getApiService().getContestant(url).enqueue(new Callback<VoteContestResponce>() {
                @Override
                public void onResponse(Call<VoteContestResponce> call, Response<VoteContestResponce> response) {

                    prog.hideProg();
                    if (response.code() == 200) {

                        try {
                            binding.numberOfWin.setText(response.body().getNummberWinner());
                            binding.contestHeader.setText(response.body().getTitleVote());
                        } catch ( Exception e ) {}

                        if (response.body().getData().size() != 0) {
                            contestantList = response.body().getData();
                            for (int i = 0; i < response.body().getData().size(); i++) {
                                contestantList.get(i).setSelected(false);
                            }
                            contestTypeAdapter.setEmployeeList((ArrayList<Datum>) contestantList);
                        }
                        binding.cotestentCount.setText(String.valueOf(response.body().getData().size()));



//                        binding.alreadyPlayed.setVisibility(View.VISIBLE);



                    } else {


                        binding.alreadyPlayed.setVisibility(View.VISIBLE);

//                        binding.noRecFound.setVisibility(View.VISIBLE);
//                        playedList.setVisibility(View.GONE);
//                        Toast.makeText(ac, ac.getResources().getString(R.string.no_quiz_found), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<VoteContestResponce> call, Throwable t) {
//                    getQuizResults();
                }
            });
        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }

    }
    public void submitQuiz() {
        if (MainApplication.isNetworkAvailable(ac)) {

            prog.progDialog(ac);
            VoteSubmitRequest quizTypeRequest = new VoteSubmitRequest();
            quizTypeRequest.setUserId(tinyDB.getString("user_id"));
            quizTypeRequest.setContestId(selectedContestId);
            quizTypeRequest.setContestantId(selectedContestent);

            MainApplication.getApiService().submitVote(quizTypeRequest).enqueue(new Callback<VoteSubmitReseponce>() {
                @Override
                public void onResponse(Call<VoteSubmitReseponce> call, Response<VoteSubmitReseponce> response) {

                    prog.hideProg();
                    if (response.code() == 200) {

                        DialogSubmitVoteBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(ac), R.layout.dialog_submit_vote, null, true);
                        dialogBinding.setClick(handlers);
                        dialogBinding.textDate.setText(response.body().getData().getResultDate());
                        AlertDialog.Builder alert = new AlertDialog.Builder(ac, R.style.AlertDialogCustom);
                        alert.setView(dialogBinding.getRoot());
                        alertDialog = alert.create();
                        alertDialog.show();

                    } else {
                        Toast.makeText(ac, ac.getResources().getString(R.string.error_ocred), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<VoteSubmitReseponce> call, Throwable t) {
                    prog.hideProg();
                    t.printStackTrace();
                    MessageDialog.showAlert(ac, getString(R.string.other_error), getResources().getString(R.string.ok));
                }
            } );
        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }
    }

}
