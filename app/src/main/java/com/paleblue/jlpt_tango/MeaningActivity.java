package com.paleblue.jlpt_tango;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Arrays;
import java.util.List;

public class MeaningActivity extends AppCompatActivity {
    boolean isFragmentOpen = false;
    private MeaningAdapter adapter;
    private DBHelper dbHelper;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meaning);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        RecyclerView recyclerView = findViewById(R.id.quizList);

        dbHelper = new DBHelper(this);

        adapter = new MeaningAdapter(this, getTableNames(), new MeaningAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String tableName) {

            }
        }, dbHelper);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private List<String> getTableNames() {
        return Arrays.asList("JLPT_1", "JLPT_2", "JLPT_3", "JLPT_4", "JLPT_5");
    }

    public void setFragmentOpenStatus(boolean isOpen) {
        isFragmentOpen = isOpen;

        // 어댑터에 Fragment 상태를 업데이트하도록 알립니다.
        adapter.setFragmentOpenStatus(isOpen);

        // RecyclerView의 클릭 가능 여부를 업데이트합니다.
        RecyclerView recyclerView = findViewById(R.id.quizList);
        recyclerView.setClickable(!isFragmentOpen);
        recyclerView.setFocusable(!isFragmentOpen);
        recyclerView.setNestedScrollingEnabled(!isFragmentOpen);
    }
}