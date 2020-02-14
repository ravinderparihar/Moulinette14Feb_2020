package com.moulinette.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;


import com.moulinette.R;
import com.moulinette.activities.Splash;

import org.json.JSONException;

public class LogoutPopup {
    private static final LogoutPopup ourInstance = new LogoutPopup();
    private static AppCompatButton buttonConfirm, buttonCancel;
    private static Context ac;

    public static LogoutPopup getInstance() {
        return ourInstance;
    }

    private LogoutPopup() {}

    @SuppressLint("ResourceAsColor")
    public static void logout_Popup(final Activity ac) throws JSONException {

        LayoutInflater li = LayoutInflater.from( ac );
        View confirmDialog = li.inflate( R.layout.dialog_logout, null );

        buttonConfirm = confirmDialog.findViewById( R.id.buttonConfirm );
        buttonCancel =  confirmDialog.findViewById( R.id.buttonCancel );

        TextView titlepop = (TextView) confirmDialog.findViewById( R.id.titlepop );
        titlepop.setText( R.string.do_you_want_to_logout);
        buttonCancel.setText( R.string.no);
        buttonConfirm.setText( R.string.yes );
        AlertDialog.Builder alert = new AlertDialog.Builder( ac );
        alert.setView( confirmDialog );
        final AlertDialog alertDialog = alert.create();
        alertDialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        alertDialog.show();
        buttonCancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        } );
        buttonConfirm.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TinyDB tinyDb = new TinyDB( ac );

                tinyDb.clear();
                Intent mainIntent = new Intent(ac, Splash.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ac.startActivity(mainIntent);
                ac.finish();

            }


        } );
    }
}
