package com.moulinette.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.moulinette.R;
import com.moulinette.databinding.ActivityPlayedDrawDetailsBinding;
import com.moulinette.databinding.ActivityPlayedQuizDetailsBinding;
import com.moulinette.models.quiz.results.Datum;
import com.moulinette.utilities.TinyDB;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

public class PlayedQuizDetail extends Activity {

    ActivityPlayedQuizDetailsBinding binding;
    Activity ac;
    TinyDB tinyDB;
    MyClickHandler handlers;
    Datum quizResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_played_quiz_details);
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ac = this;
        tinyDB = new TinyDB(ac);
        quizResult = (Datum) tinyDB.getObject("QuizResult", Datum.class);

        binding.title.setText(quizResult.getQuizTitle());
        binding.numberOpPart.setText(quizResult.getCount());
        binding.quizPlayDate.setText(quizResult.getPlayOn());
        binding.quizResultDate.setText(quizResult.getResultDate());

        try {
            if (!isValidDate(quizResult.getResultDate())) {

                binding.correctAnsLay.setVisibility(View.VISIBLE);
                binding.correctAns.setText(quizResult.getNumberCorrectAns());
                binding.yourRankLay.setVisibility(View.VISIBLE);
                binding.yourRank.setText(quizResult.getRank());
                binding.numberOpPartHed.setText(ac.getString(R.string.user_participate));
                binding.numberOpPartLay.setVisibility(View.VISIBLE);
                binding.numberOpPart.setText(quizResult.getCount());

                if (!quizResult.getLink().isEmpty()) {
                    binding.raffleDrawLay.setVisibility(View.VISIBLE);
                    binding.videoView1.setUp(quizResult.getLink(),  "");
//                    binding.videoView1.startVideo();
                }else{
                    binding.raffleDrawLay.setVisibility(View.GONE);
                }
                Display display = getWindowManager().getDefaultDisplay();
                binding.viewKonfetti.build()
                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                        .setDirection(10.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(5000L)
                        .setPosition(display.getWidth()/2, 0)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(12, 5))
//                .setPosition(-50f, binding.viewKonfetti.getWidth() + 50f, -50f, -50f)
                        .streamFor(100, 50000L);

            }else{
                binding.numberOpPartHed.setText(ac.getString(R.string.till_prtcipted));
            }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        handlers = new MyClickHandler(ac);
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

        public void videoPlayerClick(View view){
            Intent intent = new Intent(ac, VideoPlayer.class);
            ac.startActivity(intent);
            ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
        }
    }

    public static boolean isValidDate(String pDateString) throws ParseException {
        Date date = new SimpleDateFormat("dd MMMM, yyyy").parse(pDateString);
        return new Date().before(date);
    }
}
