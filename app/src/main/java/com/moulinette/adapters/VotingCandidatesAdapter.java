package com.moulinette.adapters;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.moulinette.R;
import com.moulinette.activities.VoteCast;
import com.moulinette.databinding.RowVotingCandidateBinding;
import com.moulinette.models.vote.contest.Datum;
import com.moulinette.utilities.TinyDB;
import com.moulinette.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

public class VotingCandidatesAdapter extends RecyclerView.Adapter<VotingCandidatesAdapter.MyViewHolder> {

    Activity ac;
    List<Datum> songList = new ArrayList<>();
    private final static int IMAGE_VIEW = 0;
    TinyDB tinyDb;

    private static final int ANIMATED_ITEMS_COUNT = 4;
    private int lastAnimatedPosition = -1;
    private boolean animateItems = true;
    float normalX, normalY;
    boolean click = false;

    RowVotingCandidateBinding upcomingDrawsBinding;

    public VotingCandidatesAdapter(Activity c, List<Datum> eventListt) {
        ac = c;
        this.songList = eventListt;
        this.tinyDb = new TinyDB(ac);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        upcomingDrawsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_voting_candidate, parent, false);
        return new MyViewHolder(upcomingDrawsBinding.getRoot(), viewType);
    }

    public void setEmployeeList(ArrayList<Datum> employees) {
        this.songList = employees;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return IMAGE_VIEW;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        MyViewHolder viewHolder = (MyViewHolder) holder;
        holder.mDataPosition = position;

        Datum currentFeed = songList.get(position);
        upcomingDrawsBinding.setData(currentFeed);
        runEnterAnimation(viewHolder.itemView, position);

        if (!click) {
            normalX = holder.mainLay.getScaleX();
            normalY = holder.mainLay.getScaleY();
        }

        if (songList.get(position).isSelected()){
            holder.mainLay.setBackground(ac.getResources().getDrawable(R.drawable.selected_bg));
            holder.name.setTextColor(ac.getResources().getColor(R.color.colorWhite));
            holder.description.setTextColor(ac.getResources().getColor(R.color.colorWhite));

        }else{
            holder.mainLay.setBackgroundColor(ac.getResources().getColor(R.color.colorWhite));
            holder.name.setTextColor(ac.getResources().getColor(R.color.colorBlack));
            holder.description.setTextColor(ac.getResources().getColor(R.color.colorGray));
            float x = holder.mainLay.getScaleX();
            float y = holder.mainLay.getScaleY();

            if (x> normalX){
                holder.mainLay.setElevation(10);
                holder.mainLay.setScaleX(x - 0.20f);
                holder.mainLay.setScaleY(y - 0.20f);
            }
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
        RelativeLayout mainLay;
        TextView name, description;

        public MyViewHolder(View itemView, int viewType) {
            super(itemView);

            mainLay = itemView.findViewById(R.id.main_lay);
            name = itemView.findViewById(R.id.name);
            description = itemView.findViewById(R.id.description);


            upcomingDrawsBinding.mainLay.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View view) {
                    click = true;

                    for (int i = 0; i < songList.size(); i++) {
                        songList.get(i).setSelected(false);
                    }
                    VoteCast.selectedContestent = songList.get(mDataPosition).getId();
                    songList.get(mDataPosition).setSelected(true);
                    notifyDataSetChanged();

                    float x = mainLay.getScaleX();
                    float y = mainLay.getScaleY();

                    if (x == normalX) {
                        mainLay.setElevation(20);
                        mainLay.setScaleX(x + 0.20f);
                        mainLay.setScaleY(y + 0.20f);
                    }
                }
            });
        }

    }

}


