package com.paleblue.jlpt_tango;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Arrays;
import java.util.List;

public class LevelListActivity extends AppCompatActivity {

    private RecyclerView levelList;
    private LevelListAdapter adapter;
    ImageButton infoButton;
    Button btnGoQuiz;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_list);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        levelList = findViewById(R.id.levelList);
        adapter = new LevelListAdapter(this, getTableNames());
        levelList.setLayoutManager(new LinearLayoutManager(this));
        levelList.setAdapter(adapter);

        infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LevelListActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        btnGoQuiz = findViewById(R.id.btnGoQuiz);
        btnGoQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LevelListActivity.this, QuizMainActivity.class);
                startActivity(intent);
            }
        });

        // AdView 초기화
        adView = findViewById(R.id.adView);

        // 광고 요청 생성
        AdRequest adRequest = new AdRequest.Builder().build();

        // 광고 로드
        adView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 리사이클러 뷰 어댑터를 업데이트합니다.
        adapter = new LevelListAdapter(this, getTableNames());
        levelList.setAdapter(adapter);
    }

    private List<String> getTableNames() {
        // 여기에서 데이터베이스에서 테이블 이름을 가져오는 코드를 구현
        return Arrays.asList("JLPT_1", "JLPT_2", "JLPT_3", "JLPT_4", "JLPT_5");
    }
}