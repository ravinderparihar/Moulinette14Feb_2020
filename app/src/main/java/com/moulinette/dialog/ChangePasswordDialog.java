package com.moulinette.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;

import com.google.gson.JsonElement;
import com.moulinette.R;
import com.moulinette.activities.Splash;
import com.moulinette.databinding.DialogChangePasswordBinding;
import com.moulinette.models.change_password.ChangePasswordRequest;
import com.moulinette.models.change_password.ChangePasswordResponce;
import com.moulinette.utilities.MainApplication;
import com.moulinette.utilities.MessageDialog;
import com.moulinette.utilities.TinyDB;

import org.json.JSONException;

import br.com.ilhasoft.support.validation.Validator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.moulinette.utilities.ConstandFunctions.hideKeyboardFrom;

public class ChangePasswordDialog {
    private static final ChangePasswordDialog ourInstance = new ChangePasswordDialog();

    private static TinyDB tinyDB;
    private static ProgDialog prog = new ProgDialog();
    private static Activity ac;
    private static AlertDialog alertDialog;
    private static DialogChangePasswordBinding binding;
    private static Validator validator;
    private static MyClickHandler handlers;

    public static ChangePasswordDialog getInstance() {
        return ourInstance;
    }

    private ChangePasswordDialog() {
    }

    @SuppressLint("ResourceAsColor")
    public static void changePassword_Popup(Activity acc, View mainLay1) throws JSONException {


        ac = acc;
        tinyDB = new TinyDB(ac);

        binding = DataBindingUtil.inflate(LayoutInflater.from(ac), R.layout. dialog_change_password, null, true);
        validator = new Validator(binding);
        handlers = new MyClickHandler(ac);
        binding.setClick(handlers);

        AlertDialog.Builder alert = new AlertDialog.Builder( ac, R.style.AlertDialogCustom );
        alert.setView( binding.getRoot() );
        alertDialog = alert.create();
        alertDialog.show();
    }

    private static void changePassword(final String newPass, String oldPass) {

        if (MainApplication.isNetworkAvailable( ac )) {
            ChangePasswordRequest loginRequest = new ChangePasswordRequest();
            loginRequest.setNewPassword( newPass );
            loginRequest.setId(tinyDB.getString("user_id"));
            loginRequest.setOldPassword( oldPass );

            prog.progDialog( ac );
            MainApplication.getApiService().changePassword(loginRequest ).enqueue( new Callback<ChangePasswordResponce>() {
                @Override
                public void onResponse(Call<ChangePasswordResponce> call, Response<ChangePasswordResponce> response) {
                    prog.hideProg();
                    if (response.code()== 200) {

                        androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(ac);
                        builder1.setMessage(response.body().getMessage());
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        alertDialog.dismiss();
                                        TinyDB tinyDb = new TinyDB( ac );
                                        tinyDb.clear();
                                        Intent mainIntent = new Intent(ac, Splash.class);
                                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        ac.startActivity(mainIntent);
                                        ac.finish();
                                    }
                                });
                        androidx.appcompat.app.AlertDialog alert11 = builder1.create();
                        alert11.show();

                    } else {
                        MessageDialog.showAlert(ac, ac.getString(R.string.other_error), ac.getString(R.string.ok));
                    }
                }
                @Override
                public void onFailure(Call<ChangePasswordResponce> call, Throwable t) {
                    prog.hideProg();
                    t.printStackTrace();
                    MessageDialog.showAlert(ac, ac.getString(R.string.other_error), ac.getString(R.string.ok));
                }
            } );
        } else {
            MessageDialog.showAlert(ac, ac.getString(R.string.no_internet), ac.getString(R.string.ok));
        }
    }

    public static class MyClickHandler {

        Activity context;

        public MyClickHandler(Activity ac) {
            this.context = ac;
        }


        public void cancelClick(View view) {
            alertDialog.dismiss();
        }

        public void confirmClick(View view) {

            if (validator.validate(binding.editOld) && validator.validate(binding.editNew) && validator.validate(binding.editConfirm)) {

                if (!binding.editOld.getText().toString().equals(tinyDB.getString("user_password"))) {
                    MessageDialog.showAlert(ac, ac.getString(R.string.old_passs), ac.getString(R.string.ok));
                }else if (!binding.editNew.getText().toString().equals(binding.editConfirm.getText().toString())) {
                    MessageDialog.showAlert(ac, ac.getString(R.string.new_com_mismatch), ac.getString(R.string.ok));
                }else {
                    hideKeyboardFrom(ac, view);
                    changePassword(binding.editNew.getText().toString(), binding.editOld.getText().toString());
                }
            }

        }
    }
}



