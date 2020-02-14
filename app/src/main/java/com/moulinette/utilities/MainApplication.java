package com.moulinette.utilities;

import android.content.Context;
import android.net.ConnectivityManager;

import androidx.multidex.MultiDexApplication;

import com.evernote.android.job.JobManager;
import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.moulinette.interfaces.ApiCalls;
import com.moulinette.sms.DemoJobCreator;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import cn.jzvd.JzvdStd;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainApplication extends MultiDexApplication {

    private static ApiCalls apiCalls;
    public static Retrofit retrofit;
    private static MainApplication mInstance;
    public static final String CHANNEL_1_ID = "Activity Channel";
    private FirebaseAnalytics firebaseAnalytics;


    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

//        FirebaseApp.initializeApp(this);
//        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/opensans_regular.ttf");
//        FontsOverride.setDefaultFont(this, "SERIF", "fonts/opensans_semibold.ttf");
//        FontsOverride.setDefaultFont(this, "SANS", "fonts/roboto_bold.ttf");
        Fresco.initialize( this );


//        FacebookSdk.sdkInitialize(getApplicationContext());

        JzvdStd.WIFI_TIP_DIALOG_SHOWED = false;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1040, TimeUnit.SECONDS).readTimeout(1040, TimeUnit.SECONDS).addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiCalls = retrofit.create(ApiCalls.class);

        // Obtain the Firebase Analytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        JobManager.create(this).addJobCreator(new DemoJobCreator());
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE ));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    public static synchronized MainApplication getInstance() {
        return mInstance;
    }

    public static ApiCalls getApiService(){
        return apiCalls;
    }

    public static String numberFormater(String num) {
        double m = Double.parseDouble(num);
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return formatter.format(m);
    }
}
