package com.moulinette.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.moulinette.R;

public class ProgDialog {


    android.app.AlertDialog alertDialog;

    public  void progDialog(Context ac) {


        LayoutInflater li = LayoutInflater.from(ac);
        View confirmDialog = li.inflate( R.layout.dialog_progress, null );

        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder( ac, R.style.AlertDialogCustom );
        alert.setView( confirmDialog );
        alertDialog = alert.create();
        alertDialog.show();

    }

    public void hideProg(){
        alertDialog.dismiss();
    }


}
