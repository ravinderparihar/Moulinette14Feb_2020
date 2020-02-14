package com.moulinette.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.moulinette.R;
import com.moulinette.databinding.ActivitySettingsBinding;
import com.moulinette.dialog.ChangePasswordDialog;
import com.moulinette.dialog.ProgDialog;
import com.moulinette.models.quiz.QuizTypeRequest;
import com.moulinette.utilities.MainApplication;
import com.moulinette.utilities.MessageDialog;
import com.moulinette.utilities.TinyDB;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Settings extends Activity {

    ActivitySettingsBinding binding;
    MyClickHandler handlers;
    Activity ac;
    ProgDialog prog = new ProgDialog();
    TinyDB tinyDB;
    boolean lang_change = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
//        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ac = this;
        tinyDB = new TinyDB(ac);

        if ( tinyDB.getString("Lang").equals("fr") ){
            binding.uiLangSpiner.setSelection(1);
        }

        binding.uiLangSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected (AdapterView <?> adapterView, View view, int i, long l){

                if ( lang_change ){
                    lang_change = false;
                    if ( i == 0 ){
                        tinyDB.putString("Lang", "en");
                    }else{
                        tinyDB.putString("Lang", "fr");
                    }
                    Intent mainIntent = new Intent(ac, Splash.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ac.startActivity(mainIntent);
                    ac.finish();
                }
            }
            @Override
            public void onNothingSelected (AdapterView <?> adapterView){}
        });
        handlers = new MyClickHandler(ac);
        binding.setClick(handlers);
    }
    public class MyClickHandler {

        Activity context;
        public MyClickHandler(Activity ac) {
            this.context = ac;
        }
        public void changePasswordClick(View view) {

            try {
                ChangePasswordDialog.changePassword_Popup(context, binding.mainLay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void changeLangClick(View view) {
            lang_change = true;
            binding.uiLangSpiner.performClick();
        }

        public void deleteAcClick(View view) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(ac, R.style.MyDialogTheme);
            builder1.setTitle(ac.getString(R.string.are_you_sure));
            builder1.setMessage(ac.getString(R.string.want_delete_account));
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    ac.getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            deleteAccount();
                        }
                    });
            builder1.setNegativeButton(
                    ac.getString(R.string.no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        public void backClick(View view) {
            finish();
        }
    }

    public void deleteAccount() {

        if (MainApplication.isNetworkAvailable(ac)) {
            prog.progDialog(ac);
            QuizTypeRequest quizTypeRequest = new QuizTypeRequest();
            quizTypeRequest.setId(tinyDB.getString("user_id"));

            MainApplication.getApiService().deleteAccount(quizTypeRequest).enqueue( new Callback<QuizTypeRequest>() {
                @Override
                public void onResponse(Call<QuizTypeRequest> call, Response<QuizTypeRequest> response) {

                    prog.hideProg();
                    if (response.code() == 200) {

                        TinyDB tinyDb = new TinyDB( ac );
                        tinyDb.clear();
                        Intent mainIntent = new Intent(ac, Splash.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        ac.startActivity(mainIntent);
                        ac.finish();
                    } else {
                        Toast.makeText(ac, ac.getString(R.string.no_ac_found), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<QuizTypeRequest> call, Throwable t) {
                    prog.hideProg();
                    t.printStackTrace();
                    MessageDialog.showAlert(ac, getString(R.string.other_error), getResources().getString(R.string.ok));
                }
            } );
        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }

    }
}
//
