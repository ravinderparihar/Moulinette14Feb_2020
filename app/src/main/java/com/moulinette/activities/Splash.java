package com.moulinette.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.moulinette.R;
import com.moulinette.databinding.ActivitySplashBinding;
import com.moulinette.utilities.TinyDB;

import java.util.Locale;

import render.animations.Render;
import render.animations.Zoom;

public class Splash extends Activity {


    ActivitySplashBinding binding;
    private static int progress;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    TinyDB tinyDB;
    Activity ac;
    String lang;
    Render render;

    //Activty onCreate Method
    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Througn data binding setting content view for splash screen
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        // activity refrence
        ac = this;
        // This is the class to store the data in the share prefrence to use in the app
        tinyDB = new TinyDB(ac);

//        getKeyHash();
        // Getting the selected language from tiny db class, if this is empty then it will set the English as default language
        lang = tinyDB.getString("Lang");
        if ( lang.isEmpty() || lang == null ){
            lang = "en";
            // storing English for further use
            tinyDB.putString("Lang", "en");
        }

        // the below code is used to translate the app is the selected language
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        render = new Render(ac);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run ( ){
                binding.logo.setVisibility(View.VISIBLE);
                // Set Animation
                render.setAnimation(Zoom.In(binding.logo));
                render.start();
            }
        }, 100);

        // method call
        progMethod();
    }


    // Below method is use for wait for 20 second then it will go to new screen
    public void progMethod ( ){

        progress = 0;
        progressBar = binding.spinKit;
        Sprite doubleBounce = new Circle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        new Thread(new Runnable() {
            public void run ( ){
                // checking the progress status
                while (progressStatus < 200) {
                    progressStatus = doSomeWork();
                }

                handler.post(new Runnable() {
                    public void run ( ){
                        System.out.println("Screen check0: " + tinyDB.getBoolean("facebookLogin"));
                        // check if user login by facebook, then below statement get the current access token
                        if ( tinyDB.getBoolean("facebookLogin") ){
                            AccessToken accessToken = AccessToken.getCurrentAccessToken();
                            boolean isLoggedIn = accessToken != null && ! accessToken.isExpired();
                            // if able to find the access token then if will take user to Dashboard else on Login screen
                            if ( isLoggedIn ){
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent mainIntent = new Intent(getApplicationContext(), Login.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        } else {
                            // if any session for Facebook login then logout the all the sessions
                            try {
                                LoginManager.getInstance().logOut();
                            } catch ( Exception e ) {
                            }

                            System.out.println("Screen check1: " + tinyDB.getBoolean("RememberMe"));
                            // below statement will check if user checked remember me option while login then it will take user to Dashboard else on login
                            if ( tinyDB.getBoolean("RememberMe") ){
                                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            } else {
                                Intent mainIntent = new Intent(getApplicationContext(), Login.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    }
                });
            }
            // Below method wait for 20 second and the read the new statement
            private int doSomeWork ( ){
                try {
                    Thread.sleep(20);
                } catch ( InterruptedException e ) {
                }
                return ++ progress;
            }
        }).start();
    }


    //Method to get project Hash key for facebook login
//    private void getKeyHash() {
//
//        PackageInfo info;
//        try {
//            info = getPackageManager().getPackageInfo("com.moulinette", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                //String something = new String(Base64.encodeBytes(md.digest()));
//                System.out.println("hash key:- "+something);
//            }
//        } catch (PackageManager.NameNotFoundException e1) {
////            Log.e("name not found", e1.toString());
//        } catch ( NoSuchAlgorithmException e) {
////            Log.e("no such an algorithm", e.toString());
//        } catch (Exception e) {
////            Log.e("exception", e.toString());
//        }
//    }
}
