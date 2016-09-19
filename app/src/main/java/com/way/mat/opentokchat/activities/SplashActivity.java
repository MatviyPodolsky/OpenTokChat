package com.way.mat.opentokchat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.way.mat.opentokchat.R;

/**
 * Created by oleh on 05.09.16.
 */
public class SplashActivity extends BaseActivity {

    private final static long SPLASH_DELAY = 1000;
    final private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected int getActivityResource() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, RoomsActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, SPLASH_DELAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}
