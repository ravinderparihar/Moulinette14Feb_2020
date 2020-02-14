package com.moulinette.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.moulinette.R;
import com.moulinette.activities.PlayedDrawDetail;
import com.moulinette.databinding.RowUpcomingDrawsBinding;
import com.moulinette.models.draws.Datum;
import com.moulinette.utilities.TinyDB;
import com.moulinette.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

public class UpcomingDrawsAdapter extends RecyclerView.Adapter<UpcomingDrawsAdapter.MyViewHolder> {

    Activity ac;
    List<Datum> songList = new ArrayList<>();
    private final static int IMAGE_VIEW = 0;
    TinyDB tinyDb;

    private static final int ANIMATED_ITEMS_COUNT = 4;
    private int lastAnimatedPosition = -1;
    private boolean animateItems = true;

    RowUpcomingDrawsBinding rowPlayedDrawsBinding;
    MyViewHolder.MyClickHandler myClickHandler;

    public UpcomingDrawsAdapter(Activity c, List<Datum> eventListt) {
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
        rowPlayedDrawsBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_upcoming_draws, parent, false);
        return new MyViewHolder(rowPlayedDrawsBinding.getRoot(), viewType);
    }

    @Override
    public int getItemViewType(int position) {
        return IMAGE_VIEW;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        MyViewHolder viewHolder = (MyViewHolder) holder;
        holder.mDataPosition = position;
        Datum currentFeed = songList.get(position);
        rowPlayedDrawsBinding.setDraw(currentFeed);
        runEnterAnimation(viewHolder.itemView, position);
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
            rowPlayedDrawsBinding.setClick(myClickHandler);
        }

        public class MyClickHandler {

            Activity context;
            public MyClickHandler(Activity ac) {
                this.context = ac;
            }

            public void detailsClick(View view) {
                Intent intent = new Intent(ac, PlayedDrawDetail.class);
                intent.putExtra("Title", "Played Draws Detail");
                ac.startActivity(intent);
                ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        }
    }


}
