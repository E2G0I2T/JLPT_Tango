package com.paleblue.jlpt_tango;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class InfoActivity extends AppCompatActivity {

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // 이메일 주소 복사 텍스트 클릭 이벤트 처리
        TextView copyEmailText = findViewById(R.id.copyEmailText);
        copyEmailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyEmailToClipboard();
            }
        });

        // 리뷰 작성 텍스트 클릭 이벤트 처리
        TextView writeReviewText = findViewById(R.id.writeReviewText);
        writeReviewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGooglePlayForReview();
            }
        });

        // AdView 초기화
        adView = findViewById(R.id.adView);

        // 광고 요청 생성
        AdRequest adRequest = new AdRequest.Builder().build();

        // 광고 로드
        adView.loadAd(adRequest);
    }

    private void copyEmailToClipboard() {
        // 이메일 주소를 클립보드에 복사
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            String emailAddress = "jihung7471@gmail.com"; // 여기에 이메일 주소를 입력하세요.
            ClipData clip = ClipData.newPlainText("이메일 주소", emailAddress);
            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(this, "이메일 주소가 복사되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGooglePlayForReview() {
        // 구글 플레이스토어 리뷰 창을 열기 위한 Intent 생성
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse("market://details?id=" + getPackageName()));
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            // 구글 플레이스토어 앱이 설치되어 있지 않은 경우 웹 브라우저에서 열기
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            startActivity(intent);
        }
    }
}
