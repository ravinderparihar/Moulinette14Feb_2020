/*
 * Copyright 2014 Jacob Klinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moulinette.sms.sms_receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.moulinette.R;
import com.moulinette.sms.sms_db.DatabaseClient;
import com.moulinette.sms.sms_db.pojo.SMS;


public class SmsReceiver extends BroadcastReceiver {

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public SMS message;
    Context context1;

    public void onReceive (Context context, Intent intent){

        context1 = context;

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if ( bundle != null ){

                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message1 = currentMessage.getDisplayMessageBody();
                    String timestampMillis = String.valueOf(currentMessage.getTimestampMillis());

                    System.out.println("SmsReceiver   senderNum: " + senderNum + "; message: " + message1);


//                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//                    builder
//                            .setContentTitle(phoneNumber)
//                            .setContentText(message1)
//                            .setSmallIcon(R.drawable.app_logo)
//                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);//to show content in lock screen


                    message = new SMS();
                    message.setNumber(phoneNumber);
                    message.setBody(message1);
                    message.setTime(timestampMillis);





                    SaveTask st = new SaveTask();
                    st.execute();


                } // end for loop
            } // bundle is null

        } catch ( Exception e ) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }


    class SaveTask extends AsyncTask <Void, Void, Void> {

        @Override
        protected Void doInBackground (Void... voids){

            //adding to database
            DatabaseClient.getInstance(context1).getAppDatabase()
                    .taskDao()
                    .insertsms(message);
            return null;
        }

        @Override
        protected void onPostExecute (Void aVoid){
            super.onPostExecute(aVoid);

            Toast.makeText(context1, "Saved", Toast.LENGTH_LONG).show();
        }
    }

}




//        extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent){
//        Object[] smsExtra = (Object[]) intent.getExtras().get("pdus");
//        String body = "";
//
//        for (int i = 0; i < smsExtra.length; ++ i) {
//            SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
//            body += sms.getMessageBody();
//        }
//
//        System.out.println("SMS Received---------------------------" + body);
//
//        Notification notification = new Notification.Builder(context)
//                .setContentText(body)
//                .setContentTitle("New Message")
//                .setSmallIcon(R.drawable.app_logo)
//                .setStyle(new Notification.BigTextStyle().bigText(body))
//                .build();
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//        notificationManagerCompat.notify(1, notification);
//
//    }
//
//
//}
