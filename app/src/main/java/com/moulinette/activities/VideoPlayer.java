package com.moulinette.activities;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import com.moulinette.R;
import com.moulinette.databinding.ActivityVideoPlayerBinding;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class VideoPlayer extends Activity {

    ActivityVideoPlayerBinding binding;
    String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_player);
//        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_LAYOUT_STABLE | SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        url = getIntent().getStringExtra("VideoLink");

        binding.videoView1.setUp(url, "");
        binding.videoView1.startVideo();
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();

    }
}
