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
import com.moulinette.adapters.QuizResultAdapter;
import com.moulinette.adapters.QuizTypeAdapter;
import com.moulinette.databinding.ActivityQuizSectionBinding;
import com.moulinette.dialog.ProgDialog;
import com.moulinette.models.quiz.Datum;
import com.moulinette.models.quiz.QuizTypeRequest;
import com.moulinette.models.quiz.QuizTypeResponce;
import com.moulinette.models.quiz.play_board.QuizSubmitionRequest;
import com.moulinette.models.quiz.results.QuizResultResponce;
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


public class QuizSection extends Activity {

    ActivityQuizSectionBinding binding;
    MyClickHandler handlers;
    RecyclerView upcomingQuizRecycler, recycleResultRecycler;

    boolean played = false;
    boolean upcoming = false;

    Activity ac;
    TinyDB tinyDB;
    QuizTypeAdapter quizTypeAdapter;
    LinearLayoutManager layoutManager;
    ProgDialog prog = new ProgDialog();

    List<Datum> quizTypeList = new ArrayList<>();
    List<Datum> searchquizTypeList = new ArrayList<>();

    QuizResultAdapter quizResultAdapter;
    LinearLayoutManager layoutManager1;
    List<com.moulinette.models.quiz.results.Datum> quizResultList = new ArrayList<>();
    List<com.moulinette.models.quiz.results.Datum> searchquizResultList = new ArrayList<>();

    // Search
    boolean search = false;
    Render render;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_section);
//        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ac = this;
        tinyDB = new TinyDB(ac);

        upcomingQuizRecycler = binding.recycleQuizType;
        recycleResultRecycler = binding.recycleResult;
        layoutManager = new LinearLayoutManager(ac, LinearLayoutManager.VERTICAL, false);
        layoutManager1 = new LinearLayoutManager(ac, LinearLayoutManager.VERTICAL, false);

        upcomingQuizRecycler.setLayoutManager(layoutManager);
        recycleResultRecycler.setLayoutManager(layoutManager1);

        quizTypeAdapter = new QuizTypeAdapter(ac, quizTypeList);
        upcomingQuizRecycler.setAdapter(quizTypeAdapter);

        getQuizType();

        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {

                    quizTypeList = new ArrayList <>();
                    quizResultList = new ArrayList <>();

                    String searchTxt = binding.search.getText().toString();
                    if (searchquizTypeList.size()>0 ){
                        for (int i = 0; i < searchquizTypeList.size(); i++) {
                            if ( searchquizTypeList.get(i).getQuizTitle().toLowerCase().contains(searchTxt) ){
                                quizTypeList.add(searchquizTypeList.get(i));
                            }
                        }
                        quizTypeAdapter = new QuizTypeAdapter(ac, quizTypeList);
                        upcomingQuizRecycler.setAdapter(quizTypeAdapter);
                    }

                    if (searchquizResultList.size()>0 ){
                        for (int i = 0; i < searchquizResultList.size(); i++) {
                            if ( searchquizResultList.get(i).getQuizTitle().toLowerCase().contains(searchTxt) ){
                                quizResultList.add(searchquizResultList.get(i));
                            }
                        }
                        quizResultAdapter = new QuizResultAdapter(ac, quizResultList);
                        recycleResultRecycler.setAdapter(quizResultAdapter);
                    }

                }else{
                    quizTypeList = new ArrayList <>();
                    for (int i = 0; i < searchquizTypeList.size(); i++) {
                        quizTypeList.add(searchquizTypeList.get(i));
                        quizTypeAdapter = new QuizTypeAdapter(ac, quizTypeList);
                        upcomingQuizRecycler.setAdapter(quizTypeAdapter);
                    }

                    quizResultList = new ArrayList <>();
                    for (int i = 0; i < searchquizResultList.size(); i++) {
                        quizResultList.add(searchquizResultList.get(i));
                        quizResultAdapter = new QuizResultAdapter(ac, quizResultList);
                        recycleResultRecycler.setAdapter(quizResultAdapter);
                    }
                }
            }
        });

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
            onBackPressed();
        }

        public void playedClick(View view){
            if ( !played ){
                played = true;
                binding.playedQuizText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                binding.playedQuizLay.setVisibility(View.GONE);
            }else{
                played = false;
                binding.playedQuizText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                binding.playedQuizLay.setVisibility(View.VISIBLE);
            }
        }

        public void upcomingQuizClick(View view){
            if ( !upcoming ){
                upcoming = true;
                binding.upcomingQuizTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                binding.upComingQuizLay.setVisibility(View.GONE);
            }else{
                upcoming = false;
                binding.upcomingQuizTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_black, 0);
                binding.upComingQuizLay.setVisibility(View.VISIBLE);
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
                for (int i = 0; i < quizResultList.size(); i++) {
                    quizResultList.add(searchquizResultList.get(i));
                    quizResultAdapter = new QuizResultAdapter(ac, quizResultList);
                    recycleResultRecycler.setAdapter(quizResultAdapter);
                }

                quizTypeList = new ArrayList <>();
                for (int i = 0; i < searchquizTypeList.size(); i++) {
                    quizTypeList.add(searchquizTypeList.get(i));
                    quizTypeAdapter = new QuizTypeAdapter(ac, quizTypeList);
                    upcomingQuizRecycler.setAdapter(quizTypeAdapter);
                }

                hideKeyboardFrom(ac, view);
            }
        }
    }

    public void getQuizType() {

        if (MainApplication.isNetworkAvailable(ac)) {

            prog.progDialog(ac);
            QuizTypeRequest quizTypeRequest = new QuizTypeRequest();
            quizTypeRequest.setId(tinyDB.getString("user_id"));
            MainApplication.getApiService().getQuizTypes(quizTypeRequest).enqueue(new Callback<QuizTypeResponce>() {
                @Override
                public void onResponse(Call<QuizTypeResponce> call, Response<QuizTypeResponce> response) {

                    getQuizResults();
                    if (response.code() == 200) {

                        if (response.body().getData().size() != 0) {
                            quizTypeList = response.body().getData();
                            searchquizTypeList = response.body().getData();
                            quizTypeAdapter.setEmployeeList((ArrayList<Datum>) quizTypeList);
                        }else{
                            upcoming = true;
                            binding.upcomingQuizTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                            binding.upComingQuizLay.setVisibility(View.GONE);
                        }
                    } else {
                        binding.noRecFound.setVisibility(View.VISIBLE);
                        upcomingQuizRecycler.setVisibility(View.GONE);
                        Toast.makeText(ac, ac.getResources().getString(R.string.no_quiz_found), Toast.LENGTH_SHORT).show();
                        upcoming = true;
                        binding.upcomingQuizTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                        binding.upComingQuizLay.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<QuizTypeResponce> call, Throwable t) {
                    getQuizResults();
                }
            });
        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }
    }

    public void getQuizResults() {

        if (MainApplication.isNetworkAvailable(ac)) {
            QuizSubmitionRequest quizTypeRequest = new QuizSubmitionRequest();
            quizTypeRequest.setUserId(tinyDB.getString("user_id"));
            MainApplication.getApiService().getQuizResults(quizTypeRequest).enqueue(new Callback<QuizResultResponce>() {
                @Override
                public void onResponse(Call<QuizResultResponce> call, Response<QuizResultResponce> response) {

                    prog.hideProg();
                    if (response.code() == 200) {
                        if (response.body().getData().size() != 0) {
                            recycleResultRecycler.setVisibility(View.VISIBLE);
                            quizResultList = response.body().getData();
                            searchquizResultList = response.body().getData();
                            quizResultAdapter = new QuizResultAdapter(ac, quizResultList);
                            recycleResultRecycler.setAdapter(quizResultAdapter);

                        }else{
                            binding.noRecFound1.setVisibility(View.VISIBLE);
                            recycleResultRecycler.setVisibility(View.GONE);
                            Toast.makeText(ac, ac.getResources().getString(R.string.no_qz_rslt_fd), Toast.LENGTH_SHORT).show();
                            played = true;
                            binding.playedQuizText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                            binding.playedQuizLay.setVisibility(View.GONE);
                        }
                    } else {
                        binding.noRecFound1.setVisibility(View.VISIBLE);
                        recycleResultRecycler.setVisibility(View.GONE);
                        Toast.makeText(ac, ac.getResources().getString(R.string.no_qz_rslt_fd), Toast.LENGTH_SHORT).show();
                        played = true;
                        binding.playedQuizText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_black, 0);
                        binding.playedQuizLay.setVisibility(View.GONE);
                    }
                }
                @Override
                public void onFailure(Call<QuizResultResponce> call, Throwable t) {
                    System.out.println("GetResult Exception occers:  "+t.getMessage());
                }
            });
        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }

    }

}
