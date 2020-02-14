package com.moulinette.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moulinette.R;
import com.moulinette.databinding.ActivityQuizPlayBinding;
import com.moulinette.databinding.DialogSubmitQuizBinding;
import com.moulinette.databinding.RowQuizPlayBinding;
import com.moulinette.dialog.ProgDialog;
import com.moulinette.models.quiz.play_board.Datum;
import com.moulinette.models.quiz.play_board.QuizResponce;
import com.moulinette.models.quiz.play_board.QuizSubmitionRequest;
import com.moulinette.models.quiz.play_board.QuizSubmitionResponce;
import com.moulinette.utilities.MainApplication;
import com.moulinette.utilities.MessageDialog;
import com.moulinette.utilities.TinyDB;
import com.moulinette.utilities.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bolts.AppLinks;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class QuizPlay extends Activity {

    ActivityQuizPlayBinding binding;
    MyClickHandler handlers;
    RecyclerView playedList;

    Activity ac;

    List<Datum> questionList = new ArrayList<>();
    List<String> answerList = new ArrayList<>();
    QuizPlayAdapter quizTypeAdapter;
    LinearLayoutManager layoutManager;
    AlertDialog alertDialog;
    String qstionId, title;
    int score = 0;

    ProgDialog prog = new ProgDialog();
    TinyDB tinyDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quiz_play);
//        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ac = this;
        tinyDB = new TinyDB(ac);


        try {
            if ( getIntent().getStringExtra("FromApp").matches("true") ){
                qstionId = getIntent().getStringExtra("Question_ID");
            } else {

                // ATTENTION: This was auto-generated to handle app links.
                Intent appLinkIntent = getIntent();
                String appLinkAction = appLinkIntent.getAction();
                Uri appLinkData = appLinkIntent.getData();



                Intent intent = getIntent();
                Uri data = intent.getData();

                System.out.println("data received:   "+data.toString());

                String url[] = data.toString().split("http://112.196.38.115/quiz/");
                System.out.println("Deep linking Data: " + url[1]);
                qstionId = url[1];
            }
        } catch ( Exception e ) {
            System.out.println("Exception occer: :" + e);
            try {

                Uri targetUrl =
                        AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
                if ( targetUrl != null ){
                    System.out.println("Activity App Link Target URL: " + targetUrl.toString());
                }


                Intent intent = getIntent();
                Uri data = intent.getData();
                System.out.println("Deep linking Data: " + data);
                String url[] = data.toString().split("http://112.196.38.115/quiz/");
                System.out.println("Deep linking Data: " + url[1]);
                qstionId = url[1];
            } catch ( Exception ex ) {
                System.out.println("Exception occers: :" + ex);
            }
        }


        playedList = binding.recyclePlayed;
        layoutManager = new LinearLayoutManager(ac, LinearLayoutManager.VERTICAL, false);


        if ( tinyDB.getString("user_id") != null ){
            getQuestion();
        }else{
            binding.alreadyPlayed.setVisibility(View.VISIBLE);
            binding.text.setText(getResources().getString(R.string.without_ac_quiz));
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

             score = 0;
            answerList = new ArrayList<>();
            for (int i = 0; i < questionList.size(); i++) {
                answerList.add(questionList.get(i).getSelected_option());
                try {
                    if (questionList.get(i).getSelected_option().equals(questionList.get(i).getOptionAnswer())){
                        score++;
                    }
                } catch (Exception e) {}
            }
            answerList.removeAll(Collections.singleton(null));
            if (answerList.size() == questionList.size()){
//                System.out.println("Your score: "+score);
                submitQuiz();
            }else{
                MessageDialog.showAlert(ac, ac.getResources().getString(R.string.missed_qst), ac.getString(R.string.ok));
            }
        }
    }

    public void submitQuiz() {

        if (MainApplication.isNetworkAvailable(ac)) {
            prog.progDialog(ac);
            QuizSubmitionRequest quizTypeRequest = new QuizSubmitionRequest();
            quizTypeRequest.setUserId(tinyDB.getString("user_id"));
            quizTypeRequest.setQuizId(qstionId);
            quizTypeRequest.setNumberCorrectAns(String.valueOf(score));

            MainApplication.getApiService().submitQuiz(quizTypeRequest).enqueue(new Callback<QuizSubmitionResponce>() {
                @Override
                public void onResponse(Call<QuizSubmitionResponce> call, Response<QuizSubmitionResponce> response) {
                    prog.hideProg();
                    if (response.code() == 200) {

                        DialogSubmitQuizBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(ac), R.layout.dialog_submit_quiz, null, true);
                        dialogBinding.setClick(handlers);

                        if (!response.body().getData().getResultDate().isEmpty())
                        dialogBinding.textDate.setText("by "+response.body().getData().getResultDate());
                        AlertDialog.Builder alert = new AlertDialog.Builder(ac, R.style.AlertDialogCustom);
                        alert.setView(dialogBinding.getRoot());
                        alertDialog = alert.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(ac, ac.getResources().getString(R.string.error_ocred), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<QuizSubmitionResponce> call, Throwable t) {
                    prog.hideProg();
                    t.printStackTrace();
                    MessageDialog.showAlert(ac, getString(R.string.other_error), getResources().getString(R.string.ok));
                }
            } );
        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }

    }

    public  void getQuestion(){

        if (MainApplication.isNetworkAvailable(ac)) {
            prog.progDialog(ac);
        MainApplication.getApiService().getQuiz("authentication/questionquiz/"+qstionId+"/"+tinyDB.getString("user_id")).enqueue(new Callback<QuizResponce>() {
            @Override
            public void onResponse(Call<QuizResponce> call, Response<QuizResponce> response) {

                prog.hideProg();
                if (response.code() == 200) {


//                    if ( response.body().getQuizplayed() ){
//                        binding.alreadyPlayed.setVisibility(View.VISIBLE);
//                    }else {

                        try {
                            binding.quizTitle.setText(response.body().getQuizTitle());
                        } catch ( Exception e ) {}

                        if ( response.body().getData().size() != 0 ){
                            questionList = response.body().getData();
                            playedList.setLayoutManager(layoutManager);
                            quizTypeAdapter = new QuizPlayAdapter(ac);
                            playedList.setAdapter(quizTypeAdapter);
                        } else {
                            Toast.makeText(ac, ac.getResources().getString(R.string.no_qst_fd), Toast.LENGTH_SHORT).show();
                        }

//                    }
                } else {

                    binding.alreadyPlayed.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<QuizResponce> call, Throwable t) {
                prog.hideProg();
            }
        });
    } else {
        MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
    }
    }

    class QuizPlayAdapter extends RecyclerView.Adapter<QuizPlayAdapter.MyViewHolder> {

        Activity ac;
        private final static int IMAGE_VIEW = 0;
        private static final int ANIMATED_ITEMS_COUNT = 4;
        private int lastAnimatedPosition = -1;
        private boolean animateItems = true;
        RowQuizPlayBinding rowQuizPlayBinding;

        public QuizPlayAdapter(Activity c) {
            ac = c;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            rowQuizPlayBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_quiz_play, parent, false);
            return new MyViewHolder(rowQuizPlayBinding.getRoot(), viewType);
        }

        @Override
        public int getItemViewType(int position) {
            return IMAGE_VIEW;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            MyViewHolder viewHolder = (MyViewHolder) holder;
            holder.mDataPosition = position;

            Datum currentFeed = questionList.get(position);
            rowQuizPlayBinding.setDraw(currentFeed);
            runEnterAnimation(viewHolder.itemView, position);

            rowQuizPlayBinding.group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int radioButtonID = group.getCheckedRadioButtonId();
                    View radioButton = group.findViewById(radioButtonID);
                    int idx = group.indexOfChild(radioButton);
                    idx = idx+1;
                    questionList.get(position).setSelected_option(String.valueOf(idx));
                }
            });
        }

        @Override
        public int getItemCount() {
            return questionList.size();
        }
        private void runEnterAnimation(View view, int position) {
            if (!animateItems || position >= ANIMATED_ITEMS_COUNT - 1) {
                return;
            }
            if (position > lastAnimatedPosition) {
                lastAnimatedPosition = position;
                view.setTranslationY(Utils.getScreenHeight(ac));
                view.animate()
                        .translationY(0)
                        .setInterpolator(new DecelerateInterpolator(3.f))
                        .setDuration(700)
                        .start();
            }
        }
        public class MyViewHolder extends RecyclerView.ViewHolder {

            int mDataPosition;
            public MyViewHolder(View itemView, int viewType) {
                super(itemView);
            }
        }
    }

}
