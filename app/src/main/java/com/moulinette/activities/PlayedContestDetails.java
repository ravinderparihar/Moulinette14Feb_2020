package com.moulinette.activities;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.moulinette.R;
import com.moulinette.databinding.ActivityPlayesContestDetailsBinding;
import com.moulinette.models.vote.results.Datum;
import com.moulinette.utilities.TinyDB;

import java.text.ParseException;

import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static com.moulinette.utilities.Config.Image_URL;
import static com.moulinette.utilities.DateValidater.isValidDate;

public class PlayedContestDetails extends Activity {

    ActivityPlayesContestDetailsBinding binding;
    Activity ac;
    TinyDB tinyDb;
    MyClickHandler handlers;
    Datum selectedContest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_playes_contest_details);
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ac = this;
        tinyDb = new TinyDB(ac);

       selectedContest = (Datum) tinyDb.getObject("SelectedContest", Datum.class);
       binding.setData(selectedContest);

        try {
            if (isValidDate(selectedContest.getResultDate())){

                binding.winners.setText(ac.getResources().getString(R.string.result_date));
                binding.date.setText(": "+ selectedContest.getResultDate());
                binding.particepent.setText(ac.getResources().getString(R.string.vote_casted_till));
                binding.winrHedLay.setVisibility(View.VISIBLE);
                binding.winrsLst.setVisibility(View.GONE);
                binding.winrsNo.setText(selectedContest.getNumberWinners());
            }else{
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

                for (int i = 0; i < selectedContest.getDetails().size(); i++) {
                    LinearLayout layout = new LinearLayout(ac);
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.setBackgroundColor(ac.getResources().getColor(R.color.colorWhite));
                    layout.setElevation(10);
                    layout.setPadding(15, 15, 15, 15);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(15, 5, 15, 5);
                    layout.setGravity(Gravity.CENTER_VERTICAL);
                    layout.setLayoutParams(params);

                    SimpleDraweeView image = new SimpleDraweeView(ac);
                    loadImageCircle(Image_URL+selectedContest.getDetails().get(i).getImage(), image);
                    image.setLayoutParams(new LinearLayout.LayoutParams(100,100));
                    layout.addView(image);

                    LinearLayout layout1 = new LinearLayout(ac);
                    layout1.setOrientation(LinearLayout.VERTICAL);

                    layout1.setGravity(Gravity.CENTER_VERTICAL);
                    layout1.setLayoutParams(params);

                    TextView title = new TextView(ac);
                    title.setTextSize(15);
                    title.setTextColor(ac.getResources().getColor(R.color.colorBlack));
                    title.setText(selectedContest.getDetails().get(i).getName());
                    title.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    layout1.addView(title);

                    TextView desc = new TextView(ac);
                    desc.setTextSize(12);
                    desc.setTextColor(ac.getResources().getColor(R.color.colorGray));
                    desc.setText(selectedContest.getDetails().get(i).getDescription());
                    desc.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    layout1.addView(desc);

                    TextView winby = new TextView(ac);
                    winby.setTextSize(12);
                    winby.setTextColor(ac.getResources().getColor(R.color.colorBlack));
                    winby.setText(getString(R.string.vido_ernd)+selectedContest.getDetails().get(i).getNumberOfVote());
                    winby.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    layout1.addView(winby);

                    TextView position = new TextView(ac);
                    position.setTextSize(22);
                    position.setTextColor(ac.getResources().getColor(R.color.colorGreen));
                    if ( i == 0 ){
                        position.setText("1st");
                    }else if ( i == 1 ){
                        position.setText("2nd");
                    }else if ( i == 2 ){
                        position.setText("3rd");
                    }else{
                        position.setText((i+1)+"th");
                    }
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    param.gravity = Gravity.RIGHT;
                    position.setLayoutParams(param);
                    layout1.addView(position);

                    layout.addView(layout1);
                    binding.mainViewLay.addView(layout);
                }

            }
        } catch ( ParseException e ) {
            e.printStackTrace();
        }

//        if (!selectedContest.getLink().isEmpty()) {
//            binding.raffleDrawLay.setVisibility(View.VISIBLE);
//            binding.videoView1.setUp(selectedContest.getLink(),  "");
////                    binding.videoView1.startVideo();
//
//        }else{
//            binding.raffleDrawLay.setVisibility(View.GONE);
//        }

        handlers = new MyClickHandler(ac);
        binding.setClick(handlers);
    }

    public static void loadImageCircle(String url, SimpleDraweeView targetView){
        ImageRequest request =
                ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                        .setProgressiveRenderingEnabled(false)
                        .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setAutoPlayAnimations(false)
                .build();

        RoundingParams roundingParams = new RoundingParams();
        roundingParams.setRoundAsCircle(true);
        targetView.getHierarchy().setRoundingParams(roundingParams);
        targetView.setController(controller);
    }

    public class MyClickHandler {

        Activity context;
        public MyClickHandler(Activity ac) {
            this.context = ac;
        }

        public void backClick(View view){
            finish();
        }

    }
}
