package com.moulinette.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.moulinette.R;
import com.moulinette.databinding.ActivityLoginBinding;
import com.moulinette.databinding.ActivitySignupBinding;
import com.moulinette.dialog.ProgDialog;
import com.moulinette.models.signup.SignupRequest;
import com.moulinette.models.signup.SignupResponse;
import com.moulinette.models.signup.SignupResponseErroe;
import com.moulinette.utilities.MainApplication;
import com.moulinette.utilities.MessageDialog;
import com.moulinette.utilities.Restart;
import com.moulinette.utilities.TinyDB;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.ilhasoft.support.validation.Validator;
import render.animations.Render;
import render.animations.Zoom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.moulinette.utilities.ConstandFunctions.hideKeyboardFrom;

public class Signup extends Activity {

    ActivitySignupBinding binding;
    Validator validator;
    MyClickHandler handlers;
    Activity ac;
    TinyDB tinyDB;

    private String refreshedToken = "";
    private String deviceId = "";
    Validator validatorSignUp;

    ProgDialog prog = new ProgDialog();
    Render render;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        validator = new Validator(binding);

        ac = this;
        tinyDB = new TinyDB(ac);
        render = new Render( ac );
        Handler handler = new Handler ( );
        handler.postDelayed ( new Runnable ( ) {
            @Override
            public void run ( ) {
                binding.logo.setVisibility(View.VISIBLE);
                render.setAnimation ( Zoom.In ( binding.logo ) );
                render.start ( );
            }
        } , 100 );

        handlers = new MyClickHandler(this);
        binding.setClick(handlers);
        validatorSignUp = new Validator(binding);
    }
    public class MyClickHandler {

        Activity context;
        public MyClickHandler(Activity ac) {
            this.context = ac;
        }

        public void signupClick(View view){

                if (validatorSignUp.validate(binding.name)  && validatorSignUp.validate(binding.userName)&& validatorSignUp.validate(binding.email) &&  validatorSignUp.validate(binding.password)&&  validatorSignUp.validate(binding.conPassword)) {

                    if (binding.password.getText().toString().equals(binding.conPassword.getText().toString())) {
                        hideKeyboardFrom(ac, view);
                        signUpRequest(binding.name.getText().toString(), binding.userName.getText().toString(), binding.email.getText().toString(), binding.password.getText().toString());
                    }else{
                        binding.conPassword.setError(ac.getString(R.string.con_and_pass_mismtch));
                    }
                }
        }
    }
    private void signUpRequest(final String name,final String uname, final String email, final String password) {
        if (MainApplication.isNetworkAvailable( ac )) {

            tinyDB.clear();
            refreshedToken = FirebaseInstanceId.getInstance().getToken();
            deviceId = FirebaseInstanceId.getInstance().getId();
            System.out.println("Firebase Tocken:-   "+refreshedToken);

            SignupRequest request = new SignupRequest();
            request.setEmail(email);
            request.setName(name);
            request.setUserName(uname);
            request.setPassword(password);
            request.setDeviceId(deviceId);
            request.setDeviceToken(refreshedToken);
            request.setDeviceType("android");
            request.setAddress("");
            request.setGender("");
            request.setDob("");

            prog.progDialog( ac );
            MainApplication.getApiService().signUp(tinyDB.getString("Lang_UI"), request ).enqueue(new Callback<SignupResponse>() {
                @Override
                public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                    prog.hideProg();

                    if (response.code() == 200) {

                        tinyDB.putString("user_id", String.valueOf(response.body().getData().get(0).getId()));
                        tinyDB.putString( "FName", name);
                        tinyDB.putString( "UName", uname);
                        tinyDB.putString( "user_email", email);
                        tinyDB.putString( "user_password", password);

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ac, R.style.MyDialogTheme);
                        builder1.setTitle(getResources().getString(R.string.successfully_registered));
                        builder1.setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        finish();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    } else {
                        String message = "";
                        try {
                            JSONObject obj = new JSONObject(response.errorBody().string());
                                message = obj.getString("message");
                                MessageDialog.showAlert(ac, message, getResources().getString(R.string.ok));
                            } catch (Exception e) {
                            MessageDialog.showAlert(ac, getString(R.string.other_error), getResources().getString(R.string.ok));
                        }
                    }
                }

                @Override
                public void onFailure(Call<SignupResponse> call, Throwable t) {
                    prog.hideProg();
                    t.printStackTrace();
                }
            });

        } else {
            MessageDialog.showAlert(ac, getString(R.string.no_internet), getResources().getString(R.string.ok));
        }
    }

}
