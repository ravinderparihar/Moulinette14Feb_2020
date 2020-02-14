package com.moulinette.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.moulinette.R;
import com.moulinette.databinding.RowSmsBinding;
import com.moulinette.sms.sms_db.DatabaseClient;
import com.moulinette.sms.sms_db.pojo.SMS;
import com.moulinette.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.MyViewHolder> {

    Activity ac;
    List<SMS> songList = new ArrayList<>();
    private final static int IMAGE_VIEW = 0;
//    TinyDB tinyDb;

    private static final int ANIMATED_ITEMS_COUNT = 4;
    private int lastAnimatedPosition = -1;
    private boolean animateItems = true;

    RowSmsBinding binding;
    MyViewHolder.MyClickHandler myClickHandler;

    public SMSAdapter (Activity c, List<SMS> eventListt) {
        ac = c;
        this.songList = eventListt;
//        this.tinyDb = new TinyDB(ac);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_sms, parent, false);
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

        System.out.println("SMS Size:  "+songList.size());

        SMS currentFeed = songList.get(position);
        binding.setDraw(currentFeed);

        binding.date.setText(getDate(Long.parseLong(currentFeed.getTime()), "EEEE dd MMM, yyyy"));

        binding.mainLay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick (View view){

                AlertDialog.Builder builder = new AlertDialog.Builder(ac);
                builder.setTitle(ac.getString(R.string.are));
                builder.setMessage(ac.getString(R.string.delete));
                builder.setPositiveButton(ac.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try {
                            deleteTask(currentFeed);
                        } catch ( Exception e ) {
                            e.printStackTrace();
                        }
                        try {
                            delete_thread(currentFeed.getNumber());
                        } catch ( Exception e ) {}

                        try {
                            songList.remove(position);
                        } catch ( Exception e ) {
                            e.printStackTrace();
                        }
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton(ac.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}});

                AlertDialog ad = builder.create();
                ad.show();

                return false;
            }
        });

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
            binding.setClick(myClickHandler);
        }

        public class MyClickHandler {

            Activity context;
            public MyClickHandler(Activity ac) {
                this.context = ac;
            }

            public void smsClick(View view) {
//                Intent intent = new Intent(ac, QuizPlay.class);
//                intent.putExtra("Question_ID", songList.get(mDataPosition).getQuestionId());
//                intent.putExtra("Title", songList.get(mDataPosition).getQuizTitle());
//                ac.startActivity(intent);
//                ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

            }
        }
    }

    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    private void deleteTask(final SMS task) {
        class DeleteTask extends AsyncTask <Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .taskDao()
                        .deletesms(task);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), ac.getResources().getString(R.string.deleted), Toast.LENGTH_LONG).show();

            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();

    }


    public void delete_thread(String thread){
        Cursor c = getApplicationContext().getContentResolver().query(
                Uri.parse("content://sms/"),new String[] {
                        "_id", "thread_id", "address", "person", "date","body" }, null, null, null);
        try {
            while (c.moveToNext())
            {
                int id = c.getInt(0);
                String address = c.getString(2);
                if (address.equals(thread))
                {
                    getApplicationContext().getContentResolver().delete(
                            Uri.parse("content://sms/" + id), null, null);
                }

            }
        } catch (Exception e) {
            System.out.println("SMS Exceprion occred----------------------"+e);
        }
    }

}
