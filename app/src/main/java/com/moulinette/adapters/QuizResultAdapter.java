package com.moulinette.adapters;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.moulinette.R;
import com.moulinette.activities.PlayedDrawDetail;
import com.moulinette.activities.PlayedQuizDetail;
import com.moulinette.databinding.RowQuizResultBinding;
import com.moulinette.models.quiz.results.Datum;
import com.moulinette.utilities.TinyDB;
import com.moulinette.utilities.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.moulinette.utilities.DateValidater.isValidDate;


public class QuizResultAdapter extends RecyclerView.Adapter<QuizResultAdapter.MyViewHolder> {

    Activity ac;
    List<Datum> resultList = new ArrayList<>();
    private final static int IMAGE_VIEW = 0;
    TinyDB tinyDb;

    private static final int ANIMATED_ITEMS_COUNT = 4;
    private int lastAnimatedPosition = -1;
    private boolean animateItems = true;

    RowQuizResultBinding binding;
    MyViewHolder.MyClickHandler myClickHandler;


    public QuizResultAdapter(Activity c, List<Datum> eventListt) {
        ac = c;
        this.resultList = eventListt;
        this.tinyDb = new TinyDB(ac);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_quiz_result, parent, false);
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

        Datum currentFeed = resultList.get(position);
        binding.setDraw(currentFeed);

        try {
            if (isValidDate(currentFeed.getResultDate())){
                binding.participationHad.setText(ac.getResources().getString(R.string.till_now));
                binding.participation.setText(currentFeed.getCount());

                binding.rankHad.setText(ac.getResources().getString(R.string.res_date));
                binding.rank.setText(currentFeed.getResultDate());
                binding.rank.setTextColor(ac.getResources().getColor(R.color.colorRed));
            }else{
                binding.participationHad.setText(ac.getResources().getString(R.string.usr_parti));
                binding.participation.setText(currentFeed.getCount());

                binding.rankHad.setText(ac.getResources().getString(R.string.your_rnk));
                binding.rank.setText(currentFeed.getRank());
                binding.rank.setTextColor(ac.getResources().getColor(R.color.colorGreen));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        runEnterAnimation(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {

        return resultList.size();
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

            public void detailClick(View view) {

                try {

                    tinyDb.putObject("QuizResult", resultList.get(mDataPosition));

                        Intent intent = new Intent(ac, PlayedQuizDetail.class);
                        intent.putExtra("Title", "Played Quiz Detail");
                        ac.startActivity(intent);
                        ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

                } catch (Exception e) {
                }
            }
        }
    }


}
