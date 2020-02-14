package com.moulinette.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.moulinette.R;
import com.moulinette.activities.PlayedContestDetails;
import com.moulinette.databinding.RowContestResultBinding;
import com.moulinette.models.vote.results.Datum;
import com.moulinette.utilities.TinyDB;
import com.moulinette.utilities.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.moulinette.utilities.Config.Image_URL;
import static com.moulinette.utilities.DateValidater.isValidDate;

public class VoteContestResultAdapter extends RecyclerView.Adapter<VoteContestResultAdapter.MyViewHolder> {

    Activity ac;
    List<com.moulinette.models.vote.results.Datum> songList = new ArrayList<>();
    private final static int IMAGE_VIEW = 0;
    TinyDB tinyDb;

    private static final int ANIMATED_ITEMS_COUNT = 4;
    private int lastAnimatedPosition = -1;
    private boolean animateItems = true;

    RowContestResultBinding binding;
    MyViewHolder.MyClickHandler myClickHandler;


    public VoteContestResultAdapter (Activity c, List<com.moulinette.models.vote.results.Datum> eventListt) {
        ac = c;
        this.songList = eventListt;
        this.tinyDb = new TinyDB(ac);
    }

    public void setEmployeeList(ArrayList<Datum> employees) {
        this.songList = employees;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_contest_result, parent, false);
        return new MyViewHolder(binding.getRoot(), viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return IMAGE_VIEW;
    }



    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        MyViewHolder viewHolder = (MyViewHolder) holder;
        holder.mDataPosition = position;

        Datum contestDt = songList.get(position);
        binding.setData(contestDt);
        runEnterAnimation(viewHolder.itemView, position);

        System.out.println();
        try {
            if (isValidDate(contestDt.getResultDate())){

                binding.winners.setText(ac.getResources().getString(R.string.result_date));
                binding.date.setText(": "+ contestDt.getResultDate());
                binding.particepent.setText(ac.getResources().getString(R.string.vote_casted_till));
                binding.winrHedLay.setVisibility(View.VISIBLE);
                binding.winrsNo.setText(contestDt.getNumberWinners());

            }else{

        for (int i = 0; i < contestDt.getDetails().size(); i++) {
            LinearLayout layout = new LinearLayout(ac);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setBackgroundColor(ac.getResources().getColor(R.color.colorWhite));
            layout.setElevation(10);
            layout.setPadding(15, 15, 15, 15);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(15, 5, 15, 5);
            layout.setGravity(Gravity.CENTER);
            layout.setLayoutParams(params);

            SimpleDraweeView image = new SimpleDraweeView(ac);
            loadImageCircle(Image_URL+contestDt.getDetails().get(i).getImage(), image);
            image.setLayoutParams(new LinearLayout.LayoutParams(
                    100,
                    100));
            layout.addView(image);

            TextView title = new TextView(ac);
            title.setTextSize(12);
            title.setTextColor(ac.getResources().getColor(R.color.colorBlack));
            title.setText(contestDt.getDetails().get(i).getName());
            title.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.addView(title);

            TextView winby = new TextView(ac);
            winby.setTextSize(10);
            winby.setTextColor(ac.getResources().getColor(R.color.colorGray));
            winby.setText(contestDt.getDetails().get(i).getDescription());
            winby.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.addView(winby);

            binding.mainViewLay.addView(layout);
        }

            }
        } catch (Exception e ) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {

        return songList.size();
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

            myClickHandler = new MyClickHandler(ac);
            binding.setClick(myClickHandler);
        }

        public class MyClickHandler {

            Activity context;
            public MyClickHandler(Activity ac) {
                this.context = ac;
            }

            public void contestDetailsClick(View view) {

                tinyDb.putObject("SelectedContest", songList.get(mDataPosition));
                Intent intent = new Intent(ac, PlayedContestDetails.class);
                ac.startActivity(intent);
                ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

            }
        }
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



}
