package com.paleblue.jlpt_tango;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SettingDialog.SettingDialogListener {

    RecyclerView wordList;
    WordListAdapter wordListAdapter;
    List<WordItem> wordItemList = new ArrayList<>();
    SharedPreferences sharedPreferences;
    private boolean wordChecked;
    private boolean pronunciationChecked;
    private boolean meaningChecked;
    private boolean knowChecked;
    private boolean unKnowChecked;
    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordList = findViewById(R.id.wordList);

        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        boolean containsKey = sharedPreferences.contains("checkBoxWord"); // "checkBoxWord" 키가 존재하는지 확인
        if (containsKey) {
            // "checkBoxWord" 키가 SharedPreferences에 존재하는 경우
            boolean checkBoxWordValue = sharedPreferences.getBoolean("checkBoxWord", true);
            Log.d("Settings", "checkBoxWordValue: " + checkBoxWordValue);
            // checkBoxWordValue를 사용하여 설정 값을 읽어올 수 있습니다.
        } else {
            // "checkBoxWord" 키가 SharedPreferences에 존재하지 않는 경우
            // 설정 값이 아직 저장되지 않았거나 잘못된 키를 사용한 경우입니다.
            Log.d("Settings", "checkBoxWord key does not exist in SharedPreferences.");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        wordList.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        String tableName = intent.getStringExtra("tableName");
        SharedPreferences tableSharedPreferences = getSharedPreferences(tableName + "_prefs", Context.MODE_PRIVATE);

        wordListAdapter = new WordListAdapter(this, wordItemList, tableSharedPreferences);

        wordChecked = true;
        pronunciationChecked = true;
        meaningChecked = true;
        knowChecked = true;
        unKnowChecked = true;

        // SharedPreferences에서 설정 읽어와서 어댑터에 반영
        wordChecked = sharedPreferences.getBoolean("checkBoxWord", wordChecked);
        pronunciationChecked = sharedPreferences.getBoolean("checkBoxPronunciation", pronunciationChecked);
        meaningChecked = sharedPreferences.getBoolean("checkBoxMeaning", meaningChecked);
        knowChecked = sharedPreferences.getBoolean("checkBoxKnow", knowChecked);
        unKnowChecked = sharedPreferences.getBoolean("checkBoxUnKnow", unKnowChecked);

        Log.d("MainActivity", "Word Checked: " + wordChecked);
        Log.d("MainActivity", "Pronunciation Checked: " + pronunciationChecked);
        Log.d("MainActivity", "Meaning Checked: " + meaningChecked);
        Log.d("MainActivity", "Know Checked: " + knowChecked);
        Log.d("MainActivity", "Unknow Checked: " + unKnowChecked);

        wordListAdapter.setWordVisibility(wordChecked);
        wordListAdapter.setPronunciationVisibility(pronunciationChecked);
        wordListAdapter.setMeaningVisibility(meaningChecked);
        wordListAdapter.setKnownVisibility(knowChecked, unKnowChecked);

        DataManager dataManager = new DataManager(this);

        try {
            dataManager.open();
            Cursor cursor = dataManager.getTableData(tableName);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                    @SuppressLint("Range") String word = cursor.getString(cursor.getColumnIndex("word"));
                    @SuppressLint("Range") String pronunciation = cursor.getString(cursor.getColumnIndex("pronunciation"));
                    @SuppressLint("Range") String meaning = cursor.getString(cursor.getColumnIndex("meaning"));

                    if (word != null && pronunciation != null && meaning != null) {
                        boolean isKnownFromPrefs = tableSharedPreferences.getBoolean("word" + id, false);
                        WordItem wordItem = new WordItem(id, word, pronunciation, meaning, tableName);
                        wordItem.setKnown(isKnownFromPrefs);
                        wordItemList.add(wordItem);
                    } else {
                        Log.e("DataManager", "데이터가 올바르지 않습니다. Word: " + word + ", Pronunciation: " + pronunciation + ", Meaning: " + meaning);
                    }
                } while (cursor.moveToNext());

                wordList.setAdapter(wordListAdapter);
                wordListAdapter.notifyDataSetChanged();

            } else {
                Log.e("DataManager", "데이터가 없거나 조회에 실패했습니다.");
            }

        } catch (SQLException e) {
            handleDatabaseError();
        } finally {
            dataManager.close();
        }

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView tableNameTextView = findViewById(R.id.tableName);
        tableNameTextView.setText(tableName);

        ImageButton searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogView = getLayoutInflater().inflate(R.layout.search_dialog, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(dialogView)
                        .setTitle("검색")
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("검색", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText searchEditText = dialogView.findViewById(R.id.searchEditText);
                                String searchQuery = searchEditText.getText().toString();
                                performSearch(searchQuery); // 검색을 수행하는 메서드 호출
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        ImageButton settingsButton = findViewById(R.id.settingsButton);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingDialog();
            }
        });

        // AdView 초기화
        adView = findViewById(R.id.adView);

        // 광고 요청 생성
        AdRequest adRequest = new AdRequest.Builder().build();

        // 광고 로드
        adView.loadAd(adRequest);
    }

    private void openSettingDialog() {
        SettingDialog settingDialog = new SettingDialog(
                this, this, wordListAdapter, wordItemList, sharedPreferences);
        settingDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wordChecked = sharedPreferences.getBoolean("checkBoxWord", wordChecked);
        pronunciationChecked = sharedPreferences.getBoolean("checkBoxPronunciation", pronunciationChecked);
        meaningChecked = sharedPreferences.getBoolean("checkBoxMeaning", meaningChecked);
        knowChecked = sharedPreferences.getBoolean("checkBoxKnow", knowChecked);
        unKnowChecked = sharedPreferences.getBoolean("checkBoxUnKnow", unKnowChecked);

        // 설정 변경 사항을 SharedPreferences에 저장
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("checkBoxWord", wordChecked);
        editor.putBoolean("checkBoxPronunciation", pronunciationChecked);
        editor.putBoolean("checkBoxMeaning", meaningChecked);
        editor.putBoolean("checkBoxKnow", knowChecked);
        editor.putBoolean("checkBoxUnKnow", unKnowChecked);
        editor.apply();

        Log.d("MainActivity", "Word Checked (onPause): " + wordChecked);
        Log.d("MainActivity", "Pronunciation Checked (onPause): " + pronunciationChecked);
        Log.d("MainActivity", "Meaning Checked (onPause): " + meaningChecked);
        Log.d("MainActivity", "Know Checked (onPause): " + knowChecked);
        Log.d("MainActivity", "Unknow Checked (onPause): " + unKnowChecked);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // SharedPreferences에서 설정 읽어와서 어댑터에 반영
        wordChecked = sharedPreferences.getBoolean("checkBoxWord", true);
        pronunciationChecked = sharedPreferences.getBoolean("checkBoxPronunciation", true);
        meaningChecked = sharedPreferences.getBoolean("checkBoxMeaning", true);
        knowChecked = sharedPreferences.getBoolean("checkBoxKnow", true);
        unKnowChecked = sharedPreferences.getBoolean("checkBoxUnKnow", true);

        // wordListAdapter 및 다른 관련 객체를 업데이트
        if (wordListAdapter != null) {
            wordListAdapter.setWordVisibility(wordChecked);
            wordListAdapter.setPronunciationVisibility(pronunciationChecked);
            wordListAdapter.setMeaningVisibility(meaningChecked);
            wordListAdapter.setKnownVisibility(knowChecked, unKnowChecked);
        }
    }


    private void handleDatabaseError() {
        wordItemList.clear();
        SharedPreferences sharedPreferences = getSharedPreferences("word_prefs", Context.MODE_PRIVATE);
        wordListAdapter = new WordListAdapter(this, wordItemList, sharedPreferences);
        wordList.setAdapter(wordListAdapter);
        wordListAdapter.notifyDataSetChanged();
    }

    private void performSearch(String searchQuery) {
        List<WordItem> searchResults = new ArrayList<>();

        for (WordItem wordItem : wordItemList) {
            // 검색어가 단어, 발음, 뜻 중 하나라도 포함되어 있는 경우 결과 목록에 추가
            if (wordItem.getWord().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    wordItem.getPronunciation().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    wordItem.getMeaning().toLowerCase().contains(searchQuery.toLowerCase())) {
                searchResults.add(wordItem);
            }
        }

        // 검색 결과를 RecyclerView에 설정
        wordListAdapter.setWordList(searchResults);
        wordListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSettingsChanged(boolean wordChecked, boolean pronunciationChecked, boolean meaningChecked, boolean knowChecked, boolean unKnowChecked) {
        // 변경된 설정 값을 이곳에서 사용
        this.wordChecked = wordChecked;
        this.pronunciationChecked = pronunciationChecked;
        this.meaningChecked = meaningChecked;
        this.knowChecked = knowChecked;
        this.unKnowChecked = unKnowChecked;

        // 변경된 설정 값을 어댑터에 반영
        wordListAdapter.setWordVisibility(wordChecked);
        wordListAdapter.setPronunciationVisibility(pronunciationChecked);
        wordListAdapter.setMeaningVisibility(meaningChecked);
        wordListAdapter.setKnownVisibility(knowChecked, unKnowChecked);

        // 설정 값을 SharedPreferences에 저장
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("checkBoxWord", wordChecked);
        editor.putBoolean("checkBoxPronunciation", pronunciationChecked);
        editor.putBoolean("checkBoxMeaning", meaningChecked);
        editor.putBoolean("checkBoxKnow", knowChecked);
        editor.putBoolean("checkBoxUnKnow", unKnowChecked);
        editor.apply();
    }
}
