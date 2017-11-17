package com.example.muzafarimran.lastingsales.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.example.muzafarimran.lastingsales.R;

public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 1000;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mCountDownTimer = new CountDownTimer(SPLASH_TIME_OUT, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                Intent intent = new Intent(SplashActivity.this, NavigationBottomMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        };
        mCountDownTimer.start();
    }
}
