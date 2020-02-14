package com.moulinette.sms;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.moulinette.R;
import com.moulinette.adapters.SMSAdapter;
import com.moulinette.databinding.SmsListActivityBinding;
import com.moulinette.sms.sms_db.DatabaseClient;
import com.moulinette.sms.sms_db.pojo.SMS;

import java.util.List;

public class SMSListActivity extends AppCompatActivity {

    SmsListActivityBinding binding;
    List<SMS> SMSList;
    SMSAdapter smsAdapter;
    LinearLayoutManager layoutManager;
    RecyclerView smsRecycler;
    Activity ac;
    MyClickHandler handlers;

    @RequiresApi ( api = Build.VERSION_CODES.KITKAT )
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

//        if ( android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ){
//            if ( PreferenceManager.getDefaultSharedPreferences(this)
//                    .getBoolean("request_permissions", true) &&
//                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
//                startActivity(new Intent(this, PermissionActivity.class));
//                return;
//            }
//        }

        binding = DataBindingUtil.setContentView(this, R.layout.sms_list_activity);

        ac = this;


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
                    // check if all permissions are granted


                }
                @Override
                public void onPermissionRationaleShouldBeShown (List <PermissionRequest> permissions, PermissionToken token){
                    token.continuePermissionRequest();
                }
            }).check();
        }



        smsRecycler = binding.recyclePlayed;
        layoutManager = new LinearLayoutManager(ac, LinearLayoutManager.VERTICAL, false);
        smsRecycler.setLayoutManager(layoutManager);

        getTasks();

        handlers = new MyClickHandler(this);
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


    }

    private void getTasks() {
        class GetTasks extends AsyncTask <Void, Void, List <SMS>> {

            @Override
            protected List<SMS> doInBackground(Void... voids) {
                SMSList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .getSms();
                return SMSList;
            }

            @RequiresApi ( api = Build.VERSION_CODES.KITKAT )
            @Override
            protected void onPostExecute(List<SMS> tasks) {
                super.onPostExecute(tasks);

                for (int i = 0; i < SMSList.size(); i++) {
                    System.out.println("Message Body: "+SMSList.get(i).getBody());
                    System.out.println("Message Number: "+SMSList.get(i).getNumber());
                }
                getAllSms(ac);

            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }



    @RequiresApi ( api = Build.VERSION_CODES.KITKAT )
    public void getAllSms(Context context){


        try {
            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
            int totalSMS = 0;
            if ( c != null ){
                totalSMS = c.getCount();
                if ( c.moveToFirst() ){
                    for (int j = 0; j < totalSMS; j++) {
                        String smsId = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.Inbox.THREAD_ID));
                        String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                        String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                        String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
//                    Date dateFormat= new Date(Long.valueOf(smsDate));
                        String type;

                        switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                            case Telephony.Sms.MESSAGE_TYPE_INBOX:
                                type = "inbox";
                                break;
                            case Telephony.Sms.MESSAGE_TYPE_SENT:
                                type = "sent";
                                break;
                            case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                                type = "outbox";
                                break;
                            default:
                                type = "inbox";
                                break;
                        }

                        if ( type.equals("inbox") ){

                            SMS sms = new SMS();
                            sms.setNumber(number);
                            sms.setBody(body);
                            sms.setTime(smsDate);
                            sms.setBody(body);
                            SMSList.add(sms);


                        }
                        c.moveToNext();
                    }
                }

                c.close();
                System.out.println("SMS Size: " + SMSList.size());

                smsAdapter = new SMSAdapter(ac, SMSList);
                smsRecycler.setAdapter(smsAdapter);

            } else {
                System.out.println("Moulinette: No message to show!");
            }
        }catch ( Exception e){}

    }

}
