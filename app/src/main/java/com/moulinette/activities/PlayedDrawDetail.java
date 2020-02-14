package com.moulinette.activities;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.moulinette.R;
import com.moulinette.databinding.ActivityPlayedDrawDetailsBinding;
import com.moulinette.models.draws.Datum;
import com.moulinette.utilities.TinyDB;

import java.text.ParseException;
import java.util.Date;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static com.moulinette.utilities.Config.Image_URL;

public class PlayedDrawDetail extends Activity {

    ActivityPlayedDrawDetailsBinding binding;
    Activity ac;
    MyClickHandler handlers;
    String title;
    Datum datum;
    TinyDB tinyDb;

    @RequiresApi ( api = Build.VERSION_CODES.N )
    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        binding = DataBindingUtil.setContentView ( this, R.layout.activity_played_draw_details );
        getWindow ( ).getDecorView ( ).setSystemUiVisibility ( SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );

        ac = this;
        this.tinyDb = new TinyDB(ac);

        title = getIntent ( ).getStringExtra ( "Title" );
        binding.header.setText ( title );

        datum = (Datum) tinyDb.getObject("QuizResult", Datum.class);
        binding.setDraw(datum);


        System.out.println("hddfhdfh   "+Image_URL+datum.getVideoThumb());
        try {

            binding.image.setImageURI(Image_URL+datum.getVideoThumb());
        } catch ( Exception e ) {}

//        binding.image.setImageURI();
//
//
//        binding.videoView1.setUp(datum.getVideoLink(),  "");
//        binding.videoView1.thumbImageView.setImageURI(Uri.parse("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640"));



//        binding.videoView1.startVideo();

//        binding.numberOpPart.setText(datum.getCount());
//        binding.quizPlayDate.setText(datum.getPlayOn());
//        binding.quizResultDate.setText(datum.getResultDate());
//
//        try {
//            if (!isValidDate(datum.getResultDate())) {
//
//                binding.correctAnsLay.setVisibility(View.VISIBLE);
//                binding.correctAns.setText(datum.getNumberCorrectAns());
//                binding.yourRankLay.setVisibility(View.VISIBLE);
//                binding.yourRank.setText(datum.getRank());
//                binding.numberOpPartHed.setText(ac.getString(R.string.user_participate));
//                binding.numberOpPartLay.setVisibility(View.VISIBLE);
//                binding.numberOpPart.setText(datum.getRank());
//
//                if (!datum.getLink().isEmpty()) {
//                    binding.raffleDrawLay.setVisibility(View.VISIBLE);
//                    binding.videoView1.setUp(datum.getLink(),  "");
////                    binding.videoView1.startVideo();
//                }else{
//                    binding.raffleDrawLay.setVisibility(View.GONE);
//                }
//                Display display = getWindowManager().getDefaultDisplay();
//                binding.viewKonfetti.build()
//                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
//                        .setDirection(10.0, 359.0)
//                        .setSpeed(1f, 5f)
//                        .setFadeOutEnabled(true)
//                        .setTimeToLive(5000L)
//                        .setPosition(display.getWidth()/2, 0)
//                        .addShapes(Shape.RECT, Shape.CIRCLE)
//                        .addSizes(new Size(12, 5))
////                .setPosition(-50f, binding.viewKonfetti.getWidth() + 50f, -50f, -50f)
//                        .streamFor(100, 50000L);
//
//            }else{
//                binding.numberOpPartHed.setText(ac.getString(R.string.till_prtcipted));
//            }
//        } catch ( ParseException e) {
//            e.printStackTrace();
//        }


        handlers = new MyClickHandler ( ac );
        binding.setClick ( handlers );

    }

    public class MyClickHandler {
        Activity context;
        public MyClickHandler ( Activity ac ) {
            this.context = ac;
        }
        public void backClick ( View view ) {
            onBackPressed ( );
        }

        public void playClick ( View view ) {
            Intent intent = new Intent ( ac, VideoPlayer.class );
            intent.putExtra("VideoLink", datum.getVideoLink());
            ac.startActivity ( intent );
            ac.overridePendingTransition ( R.anim.enter_from_right, R.anim.exit_to_left );
        }
    }

    @RequiresApi (api = Build.VERSION_CODES.N)
    public static boolean isValidDate(String pDateString) throws ParseException {
        Date date = new SimpleDateFormat("dd MMMM, yyyy").parse(pDateString);
        return new Date().before(date);
    }
}
