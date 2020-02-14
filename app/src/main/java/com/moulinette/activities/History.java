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
import com.moulinette.adapters.QuizResultAdapter;
import com.moulinette.databinding.ActivityHistoryBinding;
import com.moulinette.dialog.ProgDialog;
import com.moulinette.models.quiz.play_board.QuizSubmitionRequest;
import com.moulinette.models.quiz.results.QuizResultResponce;
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

// All played draws, quizs, and contest listing
public class History extends Activity {

    ActivityHistoryBinding binding;
    MyClickHandler handlers;

    ProgDialog prog = new ProgDialog();
    Activity ac;
    TinyDB tinyDB;

    boolean myQuizs = false;
    boolean myVotes = false;
    boolean myRafels = false;

    RecyclerView myDrawsList, myQuizList, myVoteList;
    QuizResultAdapter quizResultAdapter;
    List<com.moulinette.models.quiz.results.Datum> quizResultList = new ArrayList<>();
    List<com.moulinette.models.quiz.results.Datum> quizSearchList = new ArrayList<>();

    LinearLayoutManager layoutManager, layoutManager1, layoutManager2;

    List<com.moulinette.models.vote.results.Datum> listVotingResults = new ArrayList<>();
    List<com.moulinette.models.vote.results.Datum> voteSearchResults = new ArrayList<>();
    VoteContestResultAdapter contestResultAdapter;

    boolean search = false;
//    boolean searchCheck = false;
    Render render;

    // on create method of the Activity, initially called when activity called
    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        // setting the content view for the activit
        binding = DataBindingUtil.setContentView (this , R.layout.activity_history);

        // current activity refrence
        ac = this;
        tinyDB = new TinyDB(ac);

        // setting the layout managers for the recyclerviews
        layoutManager = new LinearLayoutManager ( ac , LinearLayoutManager.HORIZONTAL , false );
        layoutManager1 = new LinearLayoutManager ( ac , LinearLayoutManager.VERTICAL , false );
        layoutManager2 = new LinearLayoutManager ( ac , LinearLayoutManager.VERTICAL , false );

        //++++++++++++++++++++++++++Draws++++++++++++++++++++++++++++++++++++++++++++++++++++

        // setting the draws section closped
        myRafels = true;
        binding.myDraws.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
        binding.drawsLay.setVisibility(View.GONE);
        binding.noRecFound.setVisibility(View.VISIBLE);
//        myDrawsList = binding.recycleRaffle;
//        myDrawsList.setLayoutManager ( layoutManager );
//        playedDrawsAdapter = new PlayedDrawsAdapter ( ac , listDraws );
//        myDrawsList.setAdapter ( playedDrawsAdapter );
        //++++++++++++++++++++++++++Draws++++++++++++++++++++++++++++++++++++++++++++++++++++

        //++++++++++++++++++++++++++Quizes++++++++++++++++++++++++++++++++++++++++++++++++++++

        // Getting the result from server for quiz which user played
        myQuizList = binding.recycleQuiz;
        getQuizResults();

        //++++++++++++++++++++++++++Quizes++++++++++++++++++++++++++++++++++++++++++++++++++++

        // ++++++++++++++++++++++++++Votes++++++++++++++++++++++++++++++++++++++++++++++++++++

        // contest results for which user played
        myVoteList = binding.recycleVotes;
        myVoteList.setLayoutManager(layoutManager2);
        contestResultAdapter = new VoteContestResultAdapter(ac, listVotingResults);
        myVoteList.setAdapter(contestResultAdapter);
        // ++++++++++++++++++++++++++Votes++++++++++++++++++++++++++++++++++++++++++++++++++++

        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {

                    quizResultList = new ArrayList <>();
                    listVotingResults = new ArrayList <>();

                    String searchTxt = binding.search.getText().toString();
                    if (quizSearchList.size()>0 ){
                        for (int i = 0; i < quizSearchList.size(); i++) {
                            if ( quizSearchList.get(i).getQuizTitle().toLowerCase().contains(searchTxt) ){
                                quizResultList.add(quizSearchList.get(i));
                            }
                        }
                        quizResultAdapter = new QuizResultAdapter(ac, quizResultList);
                        myQuizList.setAdapter(quizResultAdapter);
                    }

                    if (voteSearchResults.size()>0 ){
                        for (int i = 0; i < voteSearchResults.size(); i++) {
                            if ( voteSearchResults.get(i).getVoteTitle().toLowerCase().contains(searchTxt) ){
                                listVotingResults.add(voteSearchResults.get(i));
                            }
                            contestResultAdapter = new VoteContestResultAdapter(ac, listVotingResults);
                            myVoteList.setAdapter(contestResultAdapter);
                        }
                    }

                }else{
                    quizResultList = new ArrayList <>();
                    listVotingResults = new ArrayList <>();

                        for (int i = 0; i < quizSearchList.size(); i++) {
                            quizResultList.add(quizSearchList.get(i));
                            quizResultAdapter = new QuizResultAdapter(ac, quizResultList);
                            myQuizList.setAdapter(quizResultAdapter);
                        }
                    listVotingResults = new ArrayList <>();
                    for (int i = 0; i < voteSearchResults.size(); i++) {
                        listVotingResults.add(voteSearchResults.get(i));
                        contestResultAdapter = new VoteContestResultAdapter(ac, listVotingResults);
                        myVoteList.setAdapter(contestResultAdapter);
                    }
                }
            }
        });

        // Create Render Class
        render = new Render( ac );
        //setting click handlers
        handlers = new MyClickHandler ( this );
        binding.setClick ( handlers );
    }

    // click handler class, by which clicks are handled
    public class MyClickHandler {

        Activity context;
        public MyClickHandler ( Activity ac ) {
            this.context = ac;
        }
        public void backClick ( View view ) {
            onBackPressed ( );
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
                binding.search.postDelayed(new Runnable(){
                        @Override public void run(){
                InputMethodManager keyboard=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                 keyboard.showSoftInput(binding.search,0);
                     }
                        }
                   ,200);

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

                    quizResultList = new ArrayList <>();
                    for (int i = 0; i < quizSearchList.size(); i++) {
                        quizResultList.add(quizSearchList.get(i));
                        quizResultAdapter = new QuizResultAdapter(ac, quizResultList);
                        myQuizList.setAdapter(quizResultAdapter);
                    }
                listVotingResults = new ArrayList <>();
                for (int i = 0; i < voteSearchResults.size(); i++) {
                    listVotingResults.add(voteSearchResults.get(i));
                    contestResultAdapter = new VoteContestResultAdapter(ac, listVotingResults);
                    myVoteList.setAdapter(contestResultAdapter);
                }
                hideKeyboardFrom(ac, view);
            }
        }

        //Quiz heder click to expend or collesp the recycler view
        public void myQuizsClick ( View view ) {
           if ( !myQuizs ){
               myQuizs = true;
               binding.myQuizs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
               binding.quizLay.setVisibility(View.GONE);
           }else{
               myQuizs = false;
               binding.myQuizs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
               binding.quizLay.setVisibility(View.VISIBLE);
           }
        }

        //Vote heder click to expend or collesp the recycler view
        public void myVoteClick ( View view ) {
            if ( !myVotes ){
                myVotes = true;
                binding.myVotes.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                binding.voteLay.setVisibility(View.GONE);
            }else{
                myVotes = false;
                binding.myVotes.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                binding.voteLay.setVisibility(View.VISIBLE);
            }
        }

        //Raffle heder click to expend or collesp the recycler view
        public void myRafelsClick ( View view ) {
            if ( !myRafels ){
                myRafels = true;
                binding.myDraws.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                binding.drawsLay.setVisibility(View.GONE);
            }else{
                myRafels = false;
                binding.myDraws.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                binding.drawsLay.setVisibility(View.VISIBLE);
            }
        }
    }

    // Getting the quiz results
    public void getQuizResults() {

        // showing the progress bar
        prog.progDialog(ac);
        // checking the internet connection avalibality
        if ( MainApplication.isNetworkAvailable(ac)) {
            // sending the user id to get played quizs results
            QuizSubmitionRequest quizTypeRequest = new QuizSubmitionRequest();
            quizTypeRequest.setUserId(tinyDB.getString("user_id"));
            // retrofit method for sending request and getting the responce from server
            MainApplication.getApiService().getQuizResults(quizTypeRequest).enqueue(new Callback <QuizResultResponce>() {
                @Override
                public void onResponse(Call <QuizResultResponce> call, Response <QuizResultResponce> response) {

                    getVoteResults();
                    //checking responce code for valied code which is 200
                    if (response.code() == 200) {
                        if (response.body().getData().size() != 0) {

                            myQuizList.setVisibility(View.VISIBLE);
                            quizResultList = response.body().getData();
                            quizSearchList = response.body().getData();

                            myQuizList.setLayoutManager(layoutManager1);
                            quizResultAdapter = new QuizResultAdapter(ac, quizResultList);
                            myQuizList.setAdapter(quizResultAdapter);
                            myQuizs = false;
                            binding.myQuizs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                            binding.quizLay.setVisibility(View.VISIBLE);

                        }else{
                            binding.noRecFound1.setVisibility(View.VISIBLE);
                            myQuizList.setVisibility(View.GONE);
                            Toast.makeText(ac, ac.getResources().getString(R.string.no_qz_rslt_fd), Toast.LENGTH_SHORT).show();
                            myQuizs = true;
                            binding.myQuizs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                            binding.quizLay.setVisibility(View.GONE);
                        }
                    } else {
                        binding.noRecFound1.setVisibility(View.VISIBLE);
                        myQuizList.setVisibility(View.GONE);
                        Toast.makeText(ac, ac.getResources().getString(R.string.no_qz_rslt_fd), Toast.LENGTH_SHORT).show();
                        myQuizs = true;
                        binding.myQuizs.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                        binding.quizLay.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onFailure(Call<QuizResultResponce> call, Throwable t) {
                    getVoteResults();
                    System.out.println("GetResult Exception occers:  "+t.getMessage());
                }
            });
        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }
    }

   /*

    */

    // getting vote result method
    public void getVoteResults() {

        // internet checking method
        if (MainApplication.isNetworkAvailable(ac)) {

            QuizSubmitionRequest quizTypeRequest = new QuizSubmitionRequest();
            quizTypeRequest.setUserId(tinyDB.getString("user_id"));
            MainApplication.getApiService().getVoteResults(quizTypeRequest).enqueue(new Callback<VoteResultsReseponce>() {
                @Override
                public void onResponse(Call<VoteResultsReseponce> call, Response<VoteResultsReseponce> response) {

                    prog.hideProg();
                    if (response.code() == 200) {
                        if (response.body().getData().size() != 0) {
                            listVotingResults = response.body().getData();
                            voteSearchResults = response.body().getData();
                            contestResultAdapter.setEmployeeList((ArrayList<com.moulinette.models.vote.results.Datum>) listVotingResults);
                            myVotes = false;
                            binding.myVotes.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                            binding.voteLay.setVisibility(View.VISIBLE);
                        }else{
                            myVotes = true;
                            binding.myVotes.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                            binding.voteLay.setVisibility(View.GONE);
                        }

                    } else {
                        binding.noRecFound2.setVisibility(View.VISIBLE);
                        myVoteList.setVisibility(View.GONE);
                        Toast.makeText(ac, ac.getResources().getString(R.string.no_quiz_found), Toast.LENGTH_SHORT).show();
                        myVotes = true;
                        binding.myVotes.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                        binding.voteLay.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<VoteResultsReseponce> call, Throwable t) {
                    prog.hideProg();
                }
            });
        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }

    }

}
