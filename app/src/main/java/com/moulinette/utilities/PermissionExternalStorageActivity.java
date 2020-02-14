package com.moulinette.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

import androidx.annotation.RequiresApi;

public class PermissionExternalStorageActivity extends Activity {

    @RequiresApi ( api = Build.VERSION_CODES.M )
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        }, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("request_permissions", false)
                .commit();

        startActivity(new Intent(this, com.moulinette.activities.Login.class));
        finish();
    }

}
