package com.moulinette.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moulinette.R;
import com.moulinette.adapters.ContestDrawsAdapter;
import com.moulinette.adapters.UpcomingDrawsAdapter;
import com.moulinette.databinding.ActivityRaffleDrawBinding;
import com.moulinette.dialog.ProgDialog;
import com.moulinette.models.draws.ContestResponce;
import com.moulinette.models.draws.Datum;
import com.moulinette.models.quiz.play_board.QuizSubmitionRequest;
import com.moulinette.utilities.MainApplication;
import com.moulinette.utilities.MessageDialog;
import com.moulinette.utilities.TinyDB;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RaffleDrawSection extends Activity {

    ActivityRaffleDrawBinding binding;
    MyClickHandler handlers;
    RecyclerView contestDraws, recycleUpcoming;
    ProgDialog prog = new ProgDialog();
    TinyDB tinyDB;
    Activity ac;

    boolean contest = false;
    boolean quiz_vote = false;

    List<com.moulinette.models.draws.Datum> list_contest = new ArrayList<>();
    List<com.moulinette.models.draws.Datum> search_contest_list = new ArrayList<>();
    ContestDrawsAdapter playedDrawsAdapter;
    UpcomingDrawsAdapter upcomingDrawsAdapter;
    LinearLayoutManager layoutManager;
    LinearLayoutManager layoutManager1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_raffle_draw);
//        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ac = this;
        tinyDB = new TinyDB(ac);

        contestDraws = binding.recycleContests;
        recycleUpcoming = binding.recycleQuizVoteDraws;
        layoutManager = new LinearLayoutManager(ac, LinearLayoutManager.HORIZONTAL, false);
        layoutManager1 = new LinearLayoutManager(ac, LinearLayoutManager.HORIZONTAL, false);

                contestDraws.setLayoutManager(layoutManager);
                playedDrawsAdapter = new ContestDrawsAdapter(ac, list_contest);
                contestDraws.setAdapter(playedDrawsAdapter);

        getContestRaffles();

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
        public void playedClick(View view){
            if ( !contest ){
                contest = true;
                binding.playedtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                binding.playedContestLay.setVisibility(View.GONE);
            }else{
                contest = false;
                binding.playedtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                binding.playedContestLay.setVisibility(View.VISIBLE);
            }
        }

        public void upcomingClick(View view){
            if ( !quiz_vote ){
                quiz_vote = true;
                binding.upcomingTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                binding.upComingContestLay.setVisibility(View.GONE);
            }else{
                quiz_vote = false;
                binding.upcomingTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                binding.upComingContestLay.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getContestRaffles() {

        if ( MainApplication.isNetworkAvailable(ac)) {

            prog.progDialog(ac);
            QuizSubmitionRequest quizTypeRequest = new QuizSubmitionRequest();
            quizTypeRequest.setUserId(tinyDB.getString("user_id"));

            MainApplication.getApiService().getContestResults(quizTypeRequest).enqueue(new Callback <ContestResponce>() {
                @Override
                public void onResponse(Call <ContestResponce> call, Response <ContestResponce> response) {

                    prog.hideProg();
                    if (response.code() == 200) {

                        if (response.body().getData().size() != 0) {
                            list_contest = response.body().getData();
                            search_contest_list = response.body().getData();
                            playedDrawsAdapter.setEmployeeList((ArrayList<Datum>) list_contest);
                        }else{
//                            played = true;
                            binding.playedtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                            binding.playedContestLay.setVisibility(View.GONE);
                        }
                    } else {

                        binding.noRecFound1.setVisibility(View.VISIBLE);
                        contestDraws.setVisibility(View.GONE);
                        Toast.makeText(ac, ac.getResources().getString(R.string.no_played_contest), Toast.LENGTH_SHORT).show();
//                        played = true;
                        binding.playedtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                        binding.playedContestLay.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onFailure(Call<ContestResponce> call, Throwable t) {}
            });
        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }

    }


}
