package com.moulinette.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moulinette.R;
import com.moulinette.adapters.VoteContestResultAdapter;
import com.moulinette.adapters.VoteContestTypeAdapter;
import com.moulinette.databinding.ActivityVoteSectionBinding;
import com.moulinette.dialog.ProgDialog;
import com.moulinette.models.quiz.play_board.QuizSubmitionRequest;
import com.moulinette.models.vote.VoteTypeResponce;
import com.moulinette.models.vote.results.VoteResultsReseponce;
import com.moulinette.utilities.MainApplication;
import com.moulinette.utilities.MessageDialog;
import com.moulinette.utilities.TinyDB;

import java.util.ArrayList;
import java.util.List;

import render.animations.Render;
import render.animations.Slide;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.moulinette.utilities.ConstandFunctions.hideKeyboardFrom;


public class VoteSection extends Activity {

    ActivityVoteSectionBinding binding;
    MyClickHandler handlers;
    RecyclerView playedList, upcomingContests;

    boolean played = false;
    boolean upcoming = false;

    Activity ac;
    ProgDialog prog = new ProgDialog();
    TinyDB tinyDB;

    List<com.moulinette.models.vote.Datum> listcontestType = new ArrayList<>();
    List<com.moulinette.models.vote.Datum> searchcontestType = new ArrayList<>();
//    private MutableLiveData<List<com.moulinette.models.vote.Datum>> mutableLiveData = new MutableLiveData<>();


    List<com.moulinette.models.vote.results.Datum> listVotingResults = new ArrayList<>();
    List<com.moulinette.models.vote.results.Datum> searchVotingResults = new ArrayList<>();
    VoteContestTypeAdapter contestTypeAdapter;

    VoteContestResultAdapter contestResultAdapter;

    LinearLayoutManager layoutManager;
    LinearLayoutManager layoutManager1;

    // Search
    boolean search = false;
    Render render;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vote_section);
//        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ac = this;
        tinyDB = new TinyDB(ac);

        playedList = binding.recyclePlayed;
        upcomingContests = binding.recycleUpcoming;
        layoutManager = new LinearLayoutManager(ac, LinearLayoutManager.VERTICAL, false);
        layoutManager1 = new LinearLayoutManager(ac, LinearLayoutManager.VERTICAL, false);

        upcomingContests.setLayoutManager(layoutManager);
        contestTypeAdapter = new VoteContestTypeAdapter(ac, listcontestType);
        upcomingContests.setAdapter(contestTypeAdapter);

        playedList.setLayoutManager(layoutManager1);
        contestResultAdapter = new VoteContestResultAdapter(ac, listVotingResults);
        playedList.setAdapter(contestResultAdapter);

        getVoteContestType();
        binding.search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {

                    listcontestType = new ArrayList <>();
                    listVotingResults = new ArrayList <>();

                    String searchTxt = binding.search.getText().toString();
                    if (searchcontestType.size()>0 ){
                        for (int i = 0; i < searchcontestType.size(); i++) {
                            if ( searchcontestType.get(i).getVoteTitle().toLowerCase().contains(searchTxt) ){
                                listcontestType.add(searchcontestType.get(i));
                            }
                        }
                        contestTypeAdapter = new VoteContestTypeAdapter(ac, listcontestType);
                        upcomingContests.setAdapter(contestTypeAdapter);
                    }

                    if (searchVotingResults.size()>0 ){
                        for (int i = 0; i < searchVotingResults.size(); i++) {
                            if ( searchVotingResults.get(i).getVoteTitle().toLowerCase().contains(searchTxt) ){
                                listVotingResults.add(searchVotingResults.get(i));
                            }
                        }
                        contestResultAdapter = new VoteContestResultAdapter(ac, listVotingResults);
                        playedList.setAdapter(contestResultAdapter);
                    }

                }else{
                    listcontestType = new ArrayList <>();
                    for (int i = 0; i < searchcontestType.size(); i++) {
                        listcontestType.add(searchcontestType.get(i));
                        contestTypeAdapter = new VoteContestTypeAdapter(ac, listcontestType);
                        upcomingContests.setAdapter(contestTypeAdapter);
                    }
                    listVotingResults = new ArrayList <>();
                    for (int i = 0; i < searchVotingResults.size(); i++) {
                        listVotingResults.add(searchVotingResults.get(i));
                        contestResultAdapter = new VoteContestResultAdapter(ac, listVotingResults);
                        playedList.setAdapter(contestResultAdapter);
                    }
                }
            }});

        // Create Render Class
        render = new Render( ac );
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
            if ( !played ){
                played = true;
                binding.playedtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                binding.playedContestLay.setVisibility(View.GONE);
            }else{
                played = false;
                binding.playedtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                binding.playedContestLay.setVisibility(View.VISIBLE);
            }
        }
        public void upcomingClick(View view){
            if ( !upcoming ){
                upcoming = true;
                binding.upcomingTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                binding.upComingContestLay.setVisibility(View.GONE);
            }else{
                upcoming = false;
                binding.upcomingTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                binding.upComingContestLay.setVisibility(View.VISIBLE);
            }
        }
        // Search icon click method to hide and show the search bar
        public void searchIconClick ( View view ) {

            if ( !search ){
                search = true;
                binding.searchIcon.setImageResource(R.drawable.ic_close);
                // Set Animation
                render.setAnimation ( Slide.InRight ( binding.search) );
                render.start ( );
                binding.search.setVisibility(View.VISIBLE);
                binding.search.requestFocus();
                binding.search.postDelayed(new Runnable(){ @Override public void run(){
                   InputMethodManager keyboard=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                   keyboard.showSoftInput(binding.search,0);
                                               }
                                           } ,200);
                    }else{
                search = false;
                binding.searchIcon.setImageResource(R.drawable.ic_search_black);

                // Set Animation
                render.setAnimation ( Slide.OutRight( binding.search) );
                render.start ( );

                binding.search.setText("");
                Handler handler = new Handler ( );
                handler.postDelayed ( new Runnable ( ) {
                    @Override
                    public void run ( ) {
                        binding.search.setVisibility(View.GONE);
                    }
                } , 800 );

                listVotingResults = new ArrayList <>();
                for (int i = 0; i < searchVotingResults.size(); i++) {
                    listVotingResults.add(searchVotingResults.get(i));
                    contestResultAdapter.setEmployeeList((ArrayList<com.moulinette.models.vote.results.Datum>) listVotingResults);
                }

                listcontestType = new ArrayList <>();
                for (int i = 0; i < searchcontestType.size(); i++) {
                    listcontestType.add(searchcontestType.get(i));
                    contestTypeAdapter.setEmployeeList((ArrayList<com.moulinette.models.vote.Datum>) listcontestType);
                }
                hideKeyboardFrom(ac, view);
            }
        }

    }
    public void getVoteContestType() {

        if (MainApplication.isNetworkAvailable(ac)) {

            prog.progDialog(ac);
            QuizSubmitionRequest quizTypeRequest = new QuizSubmitionRequest();
            quizTypeRequest.setUserId(tinyDB.getString("user_id"));

            MainApplication.getApiService().getVoteContestTypes(quizTypeRequest).enqueue(new Callback<VoteTypeResponce>() {
                @Override
                public void onResponse(Call<VoteTypeResponce> call, Response<VoteTypeResponce> response) {

                    prog.hideProg();
                    getVoteResults();
                    if (response.code() == 200) {
                        if (response.body().getData().size() != 0) {
                            listcontestType = response.body().getData();
                            searchcontestType = response.body().getData();
                            contestTypeAdapter.setEmployeeList((ArrayList<com.moulinette.models.vote.Datum>) listcontestType);
                        }else{
                            upcoming = true;
                            binding.upcomingTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                            binding.upComingContestLay.setVisibility(View.GONE);
                        }
                    } else {
                        binding.noRecFound.setVisibility(View.VISIBLE);
                        playedList.setVisibility(View.GONE);
                        Toast.makeText(ac, ac.getResources().getString(R.string.no_quiz_found), Toast.LENGTH_SHORT).show();
                        upcoming = true;
                        binding.upcomingTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                        binding.upComingContestLay.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onFailure(Call<VoteTypeResponce> call, Throwable t) {
                    getVoteResults();
                }
            });
        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }
    }

    public void getVoteResults() {

        if (MainApplication.isNetworkAvailable(ac)) {

            prog.progDialog(ac);
            QuizSubmitionRequest quizTypeRequest = new QuizSubmitionRequest();
            quizTypeRequest.setUserId(tinyDB.getString("user_id"));

            MainApplication.getApiService().getVoteResults(quizTypeRequest).enqueue(new Callback<VoteResultsReseponce>() {
                @Override
                public void onResponse(Call<VoteResultsReseponce> call, Response<VoteResultsReseponce> response) {

                    prog.hideProg();
                    if (response.code() == 200) {

                        if (response.body().getData().size() != 0) {
                            listVotingResults = response.body().getData();
                            searchVotingResults = response.body().getData();
                            contestResultAdapter.setEmployeeList((ArrayList<com.moulinette.models.vote.results.Datum>) listVotingResults);
                        }else{
                            played = true;
                            binding.playedtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                            binding.playedContestLay.setVisibility(View.GONE);
                        }
                    } else {
                        binding.noRecFound1.setVisibility(View.VISIBLE);
                        playedList.setVisibility(View.GONE);
                        Toast.makeText(ac, ac.getResources().getString(R.string.no_played_contest), Toast.LENGTH_SHORT).show();
                        played = true;
                        binding.playedtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                        binding.playedContestLay.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<VoteResultsReseponce> call, Throwable t) {}
            });
        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }

    }

}
