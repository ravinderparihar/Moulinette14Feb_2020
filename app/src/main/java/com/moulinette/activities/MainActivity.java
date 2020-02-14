package com.moulinette.activities;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.provider.Telephony;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.evernote.android.job.JobRequest;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.moulinette.R;
import com.moulinette.databinding.ActivityMainBinding;
import com.moulinette.fragments.Home;
import com.moulinette.sms.DemoSyncJob;
import com.moulinette.sms.SMSListActivity;
import com.moulinette.sms.sms_db.AppDatabase;
import com.moulinette.sms.sms_db.pojo.SMS;
import com.moulinette.sms.sms_receiver.PermissionActivity;
import com.moulinette.utilities.LeftMenu;
import com.moulinette.utilities.TinyDB;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.moulinette.utilities.ConstandFunctions.addFragment;
import static com.moulinette.utilities.LeftMenu.drawer;

// Landing activity of the app on this left menu and Home fragment call to display
public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    MyClickHandler handlers;
    Fragment fragment;
    static FragmentActivity ac;
    TinyDB tinyDB;

    long smsFrq = 15;

//    final int min = 0;
//    final int max = 100;


//    //SMS Receive
//    int REQUEST_ID_MULTIPLE_PERMISSIONS = 3;
//
//   public static AppDatabase appDatabase;
//
    private int mLastJobId;
//
//    List<SMS> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tinyDB = new TinyDB(getApplicationContext());
        if ( tinyDB.getString("user_level").equals("1") ){

            if ( PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("request_permissions", true) &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startActivity(new Intent(this, PermissionActivity.class));
//                finish();
                return;
            }
        }
        // Binding the view with activity
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ac = this;
//        tinyDB = new TinyDB(ac);

        // Left menu initialization
        LeftMenu leftMenu = new LeftMenu();
        // passing the views, button to toggle and drawer layout to the app
        leftMenu.init(this, binding.drawerLayoutHome, binding.contactussLeftDrawer, binding.toggle);

        //calling home fragment on dashboard
        fragment = new Home();
        addFragment(ac, fragment);

        // Check for user name, if empty
        if (!tinyDB.getString("user_name").isEmpty()){
            binding.name.setText(tinyDB.getString("user_name"));
        }else{
            binding.name.setText(tinyDB.getString("FName"));
        }

        // Setting address under the user namein the left menu
        binding.address.setText(tinyDB.getString("user_address"));
        //Show image in the icon to the user
        binding.profileImage.setImageURI(tinyDB.getString("user_img"));

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {kjhbvt
//                new ParticleSystem(ac, 50, R.drawable.fl_7, 1000)
//                        .setSpeedRange(0.1f, 0.25f)
//                        .emit(binding.middle, 100);
//
//            }
//        }, 1000);
//
         // calling click handler
        handlers = new MyClickHandler(ac);
        binding.setClick(handlers);

//        deleteSms("");

//        deleteSMS("+918360243433");


        //delete all call logs
//        Uri callLog = Uri.parse("content://call_log/calls");
//        int rs1 = getContentResolver().delete(callLog, null, null);

//        ac.getContentResolver().delete(Uri.parse("content://sms/inbox" +
//                "/"), null, null);

//                startActivity(new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT).putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, "com.moulinette"));


        //          delete all sms
//        Uri inboxUri = Uri.parse("content://sms/");
//        getContentResolver().delete(inboxUri, Telephony.Sms._ID + "!=?", new String[]{"0"});


        if ( tinyDB.getString("user_level").equals("1") ){

           smsFrq =  tinyDB.getLong("Frequency", 15);
            binding.message.setVisibility(View.VISIBLE);
            setDefaultSmsApp();

            if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ){
//            Permission check and ask for required permission
                Dexter.withActivity(ac)
                        .withPermissions(
                                Manifest.permission.READ_SMS,
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.RECEIVE_MMS,
                                Manifest.permission.CHANGE_NETWORK_STATE,
                                Manifest.permission.RECEIVE_SMS
                        ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked (MultiplePermissionsReport report){

                        mLastJobId = new JobRequest.Builder(DemoSyncJob.TAG)
//                .setPeriodic(JobRequest.MIN_INTERVAL, JobRequest.MIN_FLEX)
                                .setPeriodic(TimeUnit.MINUTES.toMillis(smsFrq))
                                .setRequiredNetworkType(JobRequest.NetworkType.values()[0])
                                .setUpdateCurrent(true)
                                .build()
                                .schedule();
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown (List <PermissionRequest> permissions, PermissionToken token){
                        token.continuePermissionRequest();
                    }
                }).check();
            }
        }

        binding.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
//                sendNotification();
                Intent intent = new Intent(getApplicationContext(), SMSListActivity.class);
                startActivity(intent);
            }
        });
    }
    //    public void sendNotification() {
//
//        //Get an instance of NotificationManager//
//
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.app_logo)
//                        .setContentTitle("My notification")
//                        .setContentText("Hello World!");
//
//
//        // Gets an instance of the NotificationManager service//
//
//        NotificationManager mNotificationManager =
//
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // When you issue multiple notifications about the same type of event,
//        // it’s best practice for your app to try to update an existing notification
//        // with this new information, rather than immediately creating a new notification.
//        // If you want to update this notification at a later date, you need to assign it an ID.
//        // You can then use this ID whenever you issue a subsequent notification.
//        // If the previous notification is still visible, the system will update this existing notification,
//        // rather than create a new one. In this example, the notification’s ID is 001//
//
//
//         int random = new Random().nextInt((max - min) + 1) + min;
//
//        System.out.println("Randum Number: "+random);
//
//        mNotificationManager.notify(random, mBuilder.build());
////        mNotificationManager.notify();
//    }



//    public  void callMethod(String phoneNumber, String message,  String time){
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            final String myPackageName = getPackageName();
//            if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
//
//                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
//                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
//                startActivityForResult(intent, 1);
//            }else {
//                saveSms(phoneNumber, message, "0", time, "inbox");
//            }
//        }else {
//            saveSms(phoneNumber, message, "0", time, "inbox");
//        }
//    }
//
//
//    public  boolean saveSms(String phoneNumber, String message, String readState, String time, String folderName) {
//        boolean ret = false;
//        try {
//            ContentValues values = new ContentValues();
//            values.put("address", phoneNumber);
//            values.put("body", message);
//            values.put("read", readState); //"0" for have not read sms and "1" for have read sms
//            values.put("date", time);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                Uri uri = Telephony.Sms.Sent.CONTENT_URI;
//                if(folderName.equals("inbox")){
//                    uri = Telephony.Sms.Inbox.CONTENT_URI;
//                }
//                getApplicationContext().getContentResolver().insert(uri, values);
//            }
//            else {
//                /* folderName  could be inbox or sent */
//                getApplicationContext().getContentResolver().insert(Uri.parse("content://sms/" + folderName), values);
//            }
//
//            ret = true;
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            ret = false;
//        }
//        return ret;
//    }

    private void setDefaultSmsApp() {
        Intent intent =
                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                getPackageName());
        startActivity(intent);
    }

//    public void delete_thread(String thread)
//    {
//        Cursor c = getApplicationContext().getContentResolver().query(
//                Uri.parse("content://sms/"),new String[] {
//                        "_id", "thread_id", "address", "person", "date","body" }, null, null, null);
//
//        try {
//            while (c.moveToNext())
//            {
//                int id = c.getInt(0);
//                String address = c.getString(2);
//                if (address.equals(thread))
//                {
//                    getApplicationContext().getContentResolver().delete(
//                            Uri.parse("content://sms/" + id), null, null);
//                }
//
//            }
//        } catch (Exception e) {
//
//        }
//    }


//    public void delete_thread(String thread)
//    {
//        Cursor c = getApplicationContext().getContentResolver().query(
//                Uri.parse("content://sms/"),new String[] {
//                        "_id", "thread_id", "address", "person", "date","body" }, null, null, null);
//
//        try {
//            while (c.moveToNext())
//            {
//                int id = c.getInt(0);
//                String address = c.getString(2);
//                if (address.equals(thread))
//                {
//                    getApplicationContext().getContentResolver().delete(
//                            Uri.parse("content://sms/" + id), null, null);
//                }
//
//            }
//        } catch (Exception e) {
//
//        }
//    }

//    public void deleteSMS(String number) {
//        try {
//            Uri uriSms = Uri.parse("content://sms/inbox");
//
//            // if read=0 then unread messages else read messages will come
//            Cursor c = getContentResolver().query(
//                    uriSms,
//                    new String[] { "_id", "thread_id", "address", "person",
//                            "date", "body" }, "read=1", null, null);
//
//            System.out.println("hhhhhhhh  "+c.getCount());
//            if (c != null && c.moveToFirst()) {
//                do {
//                    long id = c.getLong(0);
//                    long threadId = c.getLong(1);
//                    String address = c.getString(2);
//                    String body = c.getString(5);
//                    String date = c.getString(3);
//                    Log.e("log>>>",
//                            "0>" + c.getString(0) + "1>" + c.getString(1)
//                                    + "2>" + c.getString(2) + "<-1>"
//                                    + c.getString(3) + "4>" + c.getString(4)
//                                    + "5>" + c.getString(5));
//                    Log.e("log>>>", "date" + c.getString(0));
//
//                    delete_thread("370");
//
//                    System.out.println(id+"rumbaaaaaaaaa"+threadId);
//                    if (address.trim().equals(number.trim())) {
//
//                        System.out.println(address+"  hhhhhhhhh    "+number);
//                        // mLogger.logInfo("Deleting SMS with id: " + threadId);
//                        getContentResolver().delete(
//                                Uri.parse("content://sms/" + id), "date=?",
//                                new String[] { c.getString(4) });
//                        Log.e("log>>>", "Delete success.........");
//                    }
//                } while (c.moveToNext());
//            }
//        } catch (Exception e) {
//            System.out.println("hhhhhhh "+e.toString());
//        }
//    }
//
//    public boolean deleteSms(String smsId) {
//        boolean isSmsDeleted = false;
//        try {
//            getContentResolver().delete(
//                    Uri.parse("content://sms/" + smsId), null, null);
//            isSmsDeleted = true;
//
//        } catch (Exception ex) {
//            isSmsDeleted = false;
//        }
//        return isSmsDeleted;
//
////        Uri uri = Uri.parse("content://sms");
////
////        ContentResolver contentResolver = getContentResolver();
////
////        Cursor cursor = contentResolver.query(uri, null, null, null,
////                null);
////
////
////
////        while (cursor.moveToNext()) {
////
////            long thread_id = cursor.getLong(1);
////            Uri thread = Uri.parse("content://sms/conversations/"
////                    + thread_id);
////            getContentResolver().delete(thread, null, null);
////        }
////
////        return true;
//    }






    @Override
    protected void onResume ( ){
        super.onResume();
        // Check for user name, if empty
        try {
            if (!tinyDB.getString("user_name").isEmpty()){
                binding.name.setText(tinyDB.getString("user_name"));
            }else{
                binding.name.setText(tinyDB.getString("FName"));
            }
        } catch ( Exception e ) {}

        // Setting address under the user namein the left menu
        try {
            binding.address.setText(tinyDB.getString("user_address"));
        } catch ( Exception e ) {}
        //Show image in the icon to the user
        try {
            binding.profileImage.setImageURI(tinyDB.getString("user_img"));
        } catch ( Exception e ) {}
    }

    public class MyClickHandler {

        Activity context;
        public MyClickHandler(Activity ac) {
            this.context = ac;
        }

        //calling profile activity on user icon, name, or address click
        public void profileClick(View view) {
            //profile screen intent call to the activity
            Intent intent = new Intent(ac, Profile.class);
            ac.startActivity(intent);
            ac.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            drawer.closeDrawer(Gravity.LEFT);
        }
    }
}
