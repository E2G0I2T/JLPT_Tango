package com.paleblue.jlpt_tango;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // 이미지뷰를 찾습니다.
        ImageView rotatingImageView = findViewById(R.id.rotatingImageView);

        // ObjectAnimator를 사용하여 이미지를 회전시킵니다.
        ObjectAnimator rotation = ObjectAnimator.ofFloat(rotatingImageView, "rotation", 0, 360);
        rotation.setDuration(1000);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setRepeatCount(ObjectAnimator.INFINITE);

        // 애니메이션 시작
        rotation.start();

        // 스플래시 화면을 표시한 후 일정 시간이 지난 뒤 메인 화면으로 이동
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 메인 액티비티로 이동
                Intent intent = new Intent(SplashScreenActivity.this, LevelListActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000); // 3초 동안 스플래시 화면을 표시 (1000 = 1초)
    }
}
