package com.moulinette.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import com.balsikandar.crashreporter.CrashReporter;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.moulinette.R;
import com.moulinette.databinding.ActivityLoginBinding;
import com.moulinette.databinding.DialogForgetPasswordBinding;
import com.moulinette.dialog.ProgDialog;
import com.moulinette.models.fb_login.FBLoginRequest;
import com.moulinette.models.fb_login.FBLoginReseponce;
import com.moulinette.models.forget_password.ForgotpasswdRequest;
import com.moulinette.models.forget_password.ForgotpasswdResponse;
import com.moulinette.models.login.LoginRequest;
import com.moulinette.models.login.LoginResponse;
import com.moulinette.utilities.MainApplication;
import com.moulinette.utilities.MessageDialog;
import com.moulinette.utilities.PermissionExternalStorageActivity;
import com.moulinette.utilities.Restart;
import com.moulinette.utilities.TinyDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import br.com.ilhasoft.support.validation.Validator;
import render.animations.Render;
import render.animations.Zoom;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.moulinette.utilities.ConstandFunctions.hideKeyboardFrom;

public class Login extends Activity {

    ActivityLoginBinding binding;
    Validator validatorLogin;
    MyClickHandler handlers;

    CallbackManager callbackManager;
    LoginButton loginButton;
    String accessTocken;
    TinyDB tinyDB;
    Activity ac;
    boolean rememberMe = true;

    Render render;

    private String refreshedToken = "";
    private String deviceId = "";
    ProgDialog prog = new ProgDialog ( );

    //-------------------ForgetPassword  Popup Variables------------------------
    Validator validatorForgetPassword;
    DialogForgetPasswordBinding bindingForgetPassword;
    ForgotpasswdRequest forgotpasswdRequest;
    AlertDialog alertDialog;
    //-------------------Forget Password Popup Variables------------------------

    int REQUEST_ID_MULTIPLE_PERMISSIONS = 0;


    // onCreate method for login screen this will called every time initially login screen open
    @Override
    protected void onCreate ( @Nullable Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );

            if ( PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean("request_permissions", true) &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startActivity(new Intent(this, PermissionExternalStorageActivity.class));
//                finish();
                return;
            }

        // Setting the view for the login screen and binding the with the current class
        binding = DataBindingUtil.setContentView ( this , R.layout.activity_login );
        // Below is the validator class which will validate the edit text values for specified conditions
        validatorLogin = new Validator ( binding );

        // current class refrence
        ac = this;
        // storage class initialization
        tinyDB = new TinyDB ( ac );
        render = new Render( ac );

        if ( checkAndRequestPermissions() ){
            CrashReporter.initialize(this, getFilePath());
        }

        Handler handler = new Handler ( );
        handler.postDelayed ( new Runnable ( ) {
            @Override
            public void run ( ) {
                binding.logo.setVisibility(View.VISIBLE);
                // Set Animation
                render.setAnimation ( Zoom.In ( binding.logo ) );
                render.start ();
            }
        } , 100 );

        fbLogin ( );

        handlers = new MyClickHandler ( this );
        binding.setClick ( handlers );

    }

    public static String getFilePath() {
        File dir = new File(Environment.getExternalStorageDirectory()+File.separator+"Moulinette"+File.separator);
        if(!dir.exists()) {
            dir.mkdir();
        }
        dir = new File(Environment.getExternalStorageDirectory()+File.separator+"Moulinette"+File.separator+"CrashReports"+File.separator);
        if(!dir.exists()) {
            dir.mkdir();
        }

        return dir.getAbsolutePath();
    }

    private boolean checkAndRequestPermissions()
    {
        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (read != PackageManager.PERMISSION_GRANTED || write != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }else{
            return true;
        }

    }

    // Click handler class
    public class MyClickHandler {

        Activity context;
        public MyClickHandler ( Activity ac ) {
            this.context = ac;
        }

        // Login click method
        public void loginClick ( View view ) {
            if ( validatorLogin.validate ( binding.email ) && validatorLogin.validate ( binding.password ) ) {
                hideKeyboardFrom ( ac , view );
                loginRequest ( binding.email.getText ( ).toString ( ) , binding.password.getText ( ).toString ( ) );
            }
        }
        // Signup click method
        public void signupClick ( View view ) {
            Intent mainIntent = new Intent ( getApplicationContext ( ) , Signup.class );
            startActivity ( mainIntent );
        }

        // Facebook login click
        public void fbLogin ( View view ) {
            try {
                LoginManager.getInstance().logOut();
            } catch (Exception e) {}
            loginButton.performClick ( );
        }

        // Remember me check changed listioner
        public void onCheckedChangedLogin ( boolean isCheck ) {
            rememberMe = isCheck;
        }

        // Forget password click method
        public void forgetPasswordClick ( View view ) {
            popupForgetPassword ( );
        }

        // Cancel Forget password popup
        public void cancelForgetPassClick ( View view ) {
            alertDialog.dismiss ( );
        }

        // Confirm email in forget password popup
        public void confirmForgetPassClick ( View view ) {

            // validate email on popup
            if ( validatorForgetPassword.validate ( bindingForgetPassword.editTextOtp ) ) {
                // hide keyboard
                hideKeyboardFrom ( ac , view );
                // creating request for forget password
                ForgotpasswdRequest request = new ForgotpasswdRequest ( );
                // getting email from edittext
                request.setEmail ( bindingForgetPassword.editTextOtp.getText ( ).toString ( ) );
                // checking internet connection availability
                if ( MainApplication.isNetworkAvailable ( ac ) ) {

                    // call to progress dialog, to show progress dialog while getting data from server
                    prog.progDialog ( ac );
                    // Forget password request responce method
                    MainApplication.getApiService ( ).forgotPassword ( request ).enqueue ( new Callback < ForgotpasswdResponse > ( ) {
                        @Override
                        public void onResponse ( Call < ForgotpasswdResponse > call , Response < ForgotpasswdResponse > response ) {
                            // hideing progres after getting responce from server
                            prog.hideProg ( );

                            // if responce code from server is 2000 then status == ok and the email sent to the user emsail address
                            if ( response.code ( ) == 200 ) {
                                // dismis the forget password dialog
                                alertDialog.dismiss ( );
                                // show sucesss message dialog
                                MessageDialog.showAlert ( ac , response.body ( ).getMessage ( ) , getResources ( ).getString ( R.string.ok ) );
                            } else {

                                String message = "";
                                try {
                                    JSONObject obj = new JSONObject ( response.errorBody ( ).string ( ) );
                                    try {
                                        message = obj.getString ( "email" );
                                    } catch ( JSONException e3 ) {
                                        message = "";
                                    }

                                } catch ( Exception e ) {
                                    e.printStackTrace ( );
                                }
                                try {
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder ( ac , R.style.MyDialogTheme );
                                    builder1.setTitle ( getResources ( ).getString ( R.string.app_name ) );
                                    builder1.setMessage ( message );
                                    builder1.setPositiveButton ( getResources ( ).getString ( R.string.ok ) ,
                                            new DialogInterface.OnClickListener ( ) {
                                                public void onClick ( DialogInterface dialog , int id ) {
                                                    dialog.cancel ( );
                                                    new Restart ( ac );
                                                }
                                            } );

                                    AlertDialog alert11 = builder1.create ( );
                                    alert11.show ( );
                                } catch ( Exception e ) {
                                    MessageDialog.showAlert ( ac , getString ( R.string.other_error ) , getResources ( ).getString ( R.string.ok ) );
                                }
                            }
                        }

                        @Override
                        public void onFailure ( Call < ForgotpasswdResponse > call , Throwable t ) {
                            prog.hideProg ( );
                        }
                    } );

                } else {
                    MessageDialog.showAlert ( ac , getString ( R.string.no_internet ) , getResources ( ).getString ( R.string.ok ) );
                }
            }
        }

    }

    // Alert for Forget password
    private void popupForgetPassword () {
        // Binding the layout for the dialog
        bindingForgetPassword = DataBindingUtil.inflate ( LayoutInflater.from ( ac ) , R.layout.dialog_forget_password , null , true );
        validatorForgetPassword = new Validator ( bindingForgetPassword );
        bindingForgetPassword.setClick ( handlers );
        forgotpasswdRequest = new ForgotpasswdRequest ( );
        bindingForgetPassword.setData ( forgotpasswdRequest );

        AlertDialog.Builder alert = new AlertDialog.Builder ( this , R.style.AlertDialogCustom );
        alert.setView ( bindingForgetPassword.getRoot () );
        alertDialog = alert.create ();
        alertDialog.show ();
    }


    // Facebook login method
    private void fbLogin () {
        callbackManager = CallbackManager.Factory.create ( );
        loginButton = binding.loginButton;
        List < String > permissionNeeds = Arrays.asList ( "email" , "public_profile" );
        loginButton.setPermissions ( permissionNeeds );
        callbackManager = CallbackManager.Factory.create ( );

        // Callback registration
        loginButton.registerCallback ( callbackManager , new FacebookCallback < LoginResult > () {

            @Override
            public void onSuccess ( LoginResult loginResult ) {
//                System.out.println("FB login Sucess");
                accessTocken = loginResult.getAccessToken ().getToken ();
                facebookLogin(accessTocken);
            }

            @Override
            public void onCancel () {}
            @Override
            public void onError ( FacebookException exception ) {
                exception.printStackTrace ();
                Toast.makeText ( ac , getString ( R.string.facebook_error ) , Toast.LENGTH_SHORT ).show ( );
            }
        } );
    }
/*
Java script is the programming language by which we can make html live,
JAVA basic
 -Inheritence
 -Polymophisiom
 -Abustraction
 -Robast
 Java is not pure object oriented language, so meny features are left in this language for more testing so to improve the programmer fecilityies for the future for the refrence
*/

    // Normal login method
    private void loginRequest ( String emaila , final String passworda ) {

        // Check for internet connection avaivality
        if ( MainApplication.isNetworkAvailable (ac) ) {
            // getting tocken from Firebase
            refreshedToken = FirebaseInstanceId.getInstance ().getToken ();
            // Getting device id from Firebase
            deviceId = FirebaseInstanceId.getInstance ().getId ();
            System.out.println ("Firebase Tocken:-   " + refreshedToken);
            // Creating login request to validate user from server
            LoginRequest loginRequest = new LoginRequest ( );
            loginRequest.setEmail (emaila);
            loginRequest.setPassword (passworda);
            loginRequest.setDevice_id (deviceId );
            loginRequest.setDeviceToken (refreshedToken);
            loginRequest.setDeviceType ("android");

            // Clear all stored share prefrences before login
            tinyDB.clear ( );
            // Progress dialod show method
            prog.progDialog ( ac );
            // Sending login request to the server and getting the responce
            MainApplication.getApiService ().login ( "application/json" , loginRequest ).enqueue ( new Callback < JsonElement > () {
                @Override
                public void onResponse ( Call < JsonElement > call , Response < JsonElement > response ) {
                    prog.hideProg ();
                    if ( response.code () == 200 ) {

                        // Converting responce to Gson
                        Gson gson = new Gson ();
                        LoginResponse responcee = gson.fromJson (String.valueOf (response.body()) , LoginResponse.class );

                        // Storing data to prefrences
                        tinyDB.putString ( "user_id" , responcee.getData().get (0).getId());
                        tinyDB.putString ( "user_level" , responcee.getData().get (0).getUserLevel());
                        try {
                            tinyDB.putLong ( "Frequency" , Long.parseLong(responcee.getData ( ).get(0).getFrequency()));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        tinyDB.putString ("user_email" , responcee.getData().get (0).getEmail());
                        tinyDB.putString ("user_name" , responcee.getData().get (0).getUserName());
                        tinyDB.putString ("user_gender" , responcee.getData().get (0).getGender());
                        tinyDB.putString ("user_dob" , responcee.getData().get (0).getDob());
                        tinyDB.putString ("user_address" , responcee.getData ().get (0).getAddress());
                        tinyDB.putString ("user_password" , passworda);

                        if ( responcee.getData ( ).get (0).getFirstName () != null ) {
                            tinyDB.putString ( "FName" , responcee.getData ().get (0).getFirstName ());
                        } else {
                            tinyDB.putString ( "FName" , "");
                        }

                        if ( responcee.getData ().get (0).getLastName () != null) {
                            tinyDB.putString ( "LName" , responcee.getData().get (0).getLastName());
                        } else {
                            tinyDB.putString ( "LName" , "" );
                        }
                        if ( responcee.getData ().get (0).getPicture() != null ) {
                            tinyDB.putString ( "user_img" , responcee.getData().get (0).getPicture());
                        } else {
                            tinyDB.putString ("user_img" , "");
                        }

                        if ( rememberMe ) {
                            tinyDB.putBoolean ("RememberMe" , true);
                        } else {
                            tinyDB.putBoolean ("RememberMe" , false);
                        }
                        // After all the task move to Dashboard activity
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                    } else {
                        String message = "";
                        try {
                            JSONObject obj = new JSONObject ( response.errorBody().string() );
                            message = obj.getString ("message");
                            MessageDialog.showAlert (ac , message , getResources ( ).getString ( R.string.ok ));
                        } catch (Exception e) {
                            MessageDialog.showAlert (ac , getString ( R.string.other_error ) , getResources ( ).getString ( R.string.ok ));
                        }
                    }
                }

                @Override
                public void onFailure (Call < JsonElement > call , Throwable t) {
                    prog.hideProg ();
                    t.printStackTrace ();
                    MessageDialog.showAlert ( ac , getString ( R.string.other_error ) , getResources ( ).getString ( R.string.ok ) );
                }
            } );
        } else {
            MessageDialog.showAlert ( ac , getString ( R.string.no_internet ) , getResources ( ).getString ( R.string.ok ) );
        }
    }

    public void facebookLogin(String accessTocken) {

        if (MainApplication.isNetworkAvailable( ac )) {

            refreshedToken = FirebaseInstanceId.getInstance().getToken();
            deviceId = FirebaseInstanceId.getInstance().getId();

            FBLoginRequest loginRequest = new FBLoginRequest();
            loginRequest.setAccess_token(accessTocken);
            loginRequest.setDeviceToken(refreshedToken );
            loginRequest.setDevice_id(deviceId );
            loginRequest.setDeviceType("android");

            prog.progDialog( ac );
            MainApplication.getApiService().fblogin( "application/json", loginRequest ).enqueue( new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    prog.hideProg();
                    if (response.code()== 200) {

                    tinyDB.putBoolean("facebookLogin", true);

                        Gson gson = new Gson ();
                        FBLoginReseponce responcee = gson.fromJson ( String.valueOf ( response.body ()) , FBLoginReseponce.class );

                        tinyDB.putString ( "user_id" , responcee.getData().getId ( ) );
                        tinyDB.putString ( "user_email" , responcee.getData ( ).getEmail ( ) );
                        tinyDB.putString ( "user_name" , responcee.getData ( ).getUserName ( ) );
                        tinyDB.putString ( "user_level" , responcee.getData().getUserLevel());
                        try {
                            tinyDB.putLong ( "Frequency" , Long.parseLong(responcee.getData ().getFrequency()));
                        } catch ( NumberFormatException e ) {
                            e.printStackTrace();
                        }
                        try {
                            tinyDB.putString ( "user_gender" , responcee.getData ().getGender ());
                        } catch ( Exception e ) {
                            tinyDB.putString ( "user_gender" , "m");
                        }
                        tinyDB.putString ( "user_dob" , responcee.getData ().getDob () );
                        tinyDB.putString ( "user_address" , responcee.getData ().getAddress ());
                        tinyDB.putString ( "user_password" , "" );

                        if ( responcee.getData ().getFirstName () != null ) {
                            tinyDB.putString ( "FName" , responcee.getData ().getFirstName ());
                        } else {
                            tinyDB.putString ( "FName" , "" );
                        }

                        if ( responcee.getData ().getLastName () != null ) {
                            tinyDB.putString ( "LName" , responcee.getData ().getLastName ());
                        } else {
                            tinyDB.putString ( "LName" , "" );
                        }

                        if ( responcee.getData ().getPicture () != null ) {
                            tinyDB.putString ( "user_img" , responcee.getData ().getPicture ());
                        } else {
                            tinyDB.putString ( "user_img" , "" );
                        }

                        if ( rememberMe ) {
                            tinyDB.putBoolean ( "RememberMe" , true );
                        } else {
                            tinyDB.putBoolean ( "RememberMe" , false );
                        }
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();

                    } else {
                        String message = "";
                        try {
                            JSONObject obj = new JSONObject(response.errorBody().string());
                            try {
                                message = obj.getString("email");
                            } catch (JSONException e0) {}
                        }catch (Exception e){}

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ac, R.style.MyDialogTheme);
                        builder1.setTitle(getString(R.string.oops));
                        builder1.setMessage(message);
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                ac.getResources().getString(R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }});
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable t) {
                    prog.hideProg();
                    t.printStackTrace();
                    MessageDialog.showAlert(ac, getString(R.string.other_error), getResources().getString(R.string.ok));
                }});
        } else {
            MessageDialog.showAlert(ac, getString(R.string.no_internet), getResources().getString(R.string.ok));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
//        Toast.makeText(ac, "Result", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
