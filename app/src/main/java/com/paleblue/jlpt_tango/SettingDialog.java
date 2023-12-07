package com.paleblue.jlpt_tango;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SettingDialog extends Dialog {
    private Context context;
    private CheckBox checkBoxWord;
    private CheckBox checkBoxPronunciation;
    private CheckBox checkBoxMeaning;
    private CheckBox checkBoxKnow;
    private CheckBox checkBoxUnKnow;
    private WordListAdapter wordListAdapter;
    private List<WordItem> wordItemList;
    private boolean isKnownVisible;
    private boolean isUnknownVisible;
    private RadioGroup radioGroup;
    private RadioButton rbAToZ;
    private RadioButton rbZToA;
    private RadioButton rbRandom;
    private SharedPreferences sharedPreferences;
    private boolean wordChecked;
    private boolean pronunciationChecked;
    private boolean meaningChecked;
    private boolean knowChecked;
    private boolean unKnowChecked;

    public interface SettingDialogListener {
        void onSettingsChanged(boolean wordChecked, boolean pronunciationChecked, boolean meaningChecked, boolean knowChecked, boolean unKnowChecked);
    }
    private SettingDialogListener listener;

    public SettingDialog(@NonNull Context context, SettingDialogListener listener, WordListAdapter wordListAdapter, List<WordItem> wordItemList, SharedPreferences sharedPreferences) {
        super(context);
        this.context = context;
        this.wordListAdapter = wordListAdapter;
        this.wordItemList = wordItemList;
        this.sharedPreferences = sharedPreferences;
        this.listener = listener;

        wordChecked = sharedPreferences.getBoolean("checkBoxWord", true);
        pronunciationChecked = sharedPreferences.getBoolean("checkBoxPronunciation", true);
        meaningChecked = sharedPreferences.getBoolean("checkBoxMeaning", true);
        knowChecked = sharedPreferences.getBoolean("checkBoxKnow", true);
        unKnowChecked = sharedPreferences.getBoolean("checkBoxUnKnow", true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting);

        checkBoxWord = findViewById(R.id.checkBoxWord);
        checkBoxPronunciation = findViewById(R.id.checkBoxPronunciation);
        checkBoxMeaning = findViewById(R.id.checkBoxMeaning);
        checkBoxKnow = findViewById(R.id.checkBoxKnow);
        checkBoxUnKnow = findViewById(R.id.checkBoxUnKnow);
        radioGroup = findViewById(R.id.radioGroup);
        rbAToZ = findViewById(R.id.rb1);
        rbZToA = findViewById(R.id.rb2);
        rbRandom = findViewById(R.id.rb3);

        // 설정을 SharedPreferences에서 읽어와서 초기 상태 설정
        checkBoxWord.setChecked(sharedPreferences.getBoolean("checkBoxWord", true));
        checkBoxPronunciation.setChecked(sharedPreferences.getBoolean("checkBoxPronunciation", true));
        checkBoxMeaning.setChecked(sharedPreferences.getBoolean("checkBoxMeaning", true));
        checkBoxKnow.setChecked(sharedPreferences.getBoolean("checkBoxKnow", true));
        checkBoxUnKnow.setChecked(sharedPreferences.getBoolean("checkBoxUnKnow", true));

        // 라디오 버튼 초기 설정 읽어오기
        int selectedRadioId = sharedPreferences.getInt("selectedRadioId", R.id.rb1); // 기본값은 "A to Z"로 설정

        // 라디오 그룹에서 해당 라디오 버튼 선택
        radioGroup.check(selectedRadioId);

        // "Word" 체크박스 상태 변경 리스너
        checkBoxWord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wordListAdapter.setWordVisibility(isChecked);
                sharedPreferences.edit().putBoolean("checkBoxWord", isChecked).apply();
                // 설정 변경을 리스너를 통해 MainActivity로 전달
                listener.onSettingsChanged(
                        checkBoxWord.isChecked(),
                        checkBoxPronunciation.isChecked(),
                        checkBoxMeaning.isChecked(),
                        checkBoxKnow.isChecked(),
                        checkBoxUnKnow.isChecked()
                );
            }
        });

        // "Pronunciation" 체크박스 상태 변경 리스너
        checkBoxPronunciation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wordListAdapter.setPronunciationVisibility(isChecked);
                sharedPreferences.edit().putBoolean("checkBoxPronunciation", isChecked).apply();
                // 설정 변경을 리스너를 통해 MainActivity로 전달
                listener.onSettingsChanged(
                        checkBoxWord.isChecked(),
                        checkBoxPronunciation.isChecked(),
                        checkBoxMeaning.isChecked(),
                        checkBoxKnow.isChecked(),
                        checkBoxUnKnow.isChecked()
                );
            }
        });

        // "Meaning" 체크박스 상태 변경 리스너
        checkBoxMeaning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wordListAdapter.setMeaningVisibility(isChecked);
                sharedPreferences.edit().putBoolean("checkBoxMeaning", isChecked).apply();
                // 설정 변경을 리스너를 통해 MainActivity로 전달
                listener.onSettingsChanged(
                        checkBoxWord.isChecked(),
                        checkBoxPronunciation.isChecked(),
                        checkBoxMeaning.isChecked(),
                        checkBoxKnow.isChecked(),
                        checkBoxUnKnow.isChecked()
                );
            }
        });

        // "Known" 체크박스 상태 변경 리스너
        checkBoxKnow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isKnownVisible = isChecked;
                wordListAdapter.setKnownVisibility(isKnownVisible, isUnknownVisible);
                sharedPreferences.edit().putBoolean("checkBoxKnow", isChecked).apply();
                // 설정 변경을 리스너를 통해 MainActivity로 전달
                listener.onSettingsChanged(
                        checkBoxWord.isChecked(),
                        checkBoxPronunciation.isChecked(),
                        checkBoxMeaning.isChecked(),
                        checkBoxKnow.isChecked(),
                        checkBoxUnKnow.isChecked()
                );
            }
        });

        // "Unknown" 체크박스 상태 변경 리스너
        checkBoxUnKnow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isUnknownVisible = isChecked;
                wordListAdapter.setKnownVisibility(isKnownVisible, isUnknownVisible);
                sharedPreferences.edit().putBoolean("checkBoxUnKnow", isChecked).apply();
                // 설정 변경을 리스너를 통해 MainActivity로 전달
                listener.onSettingsChanged(
                        checkBoxWord.isChecked(),
                        checkBoxPronunciation.isChecked(),
                        checkBoxMeaning.isChecked(),
                        checkBoxKnow.isChecked(),
                        checkBoxUnKnow.isChecked()
                );
            }
        });

        // 라디오 버튼 그룹에 리스너 설정
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 선택된 라디오 버튼에 따라 처리
                switch (checkedId) {
                    case R.id.rb1:
                        // "A to Z" 라디오 버튼 선택
                        // 데이터를 "A to Z" 순으로 정렬
                        Collections.sort(wordItemList, new Comparator<WordItem>() {
                            @Override
                            public int compare(WordItem item1, WordItem item2) {
                                return item1.getWord().compareTo(item2.getWord());
                            }
                        });
                        wordListAdapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
                        break;
                    case R.id.rb2:
                        // "Z to A" 라디오 버튼 선택
                        // 데이터를 "Z to A" 순으로 정렬
                        Collections.sort(wordItemList, new Comparator<WordItem>() {
                            @Override
                            public int compare(WordItem item1, WordItem item2) {
                                return item2.getWord().compareTo(item1.getWord());
                            }
                        });
                        wordListAdapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
                        break;
                    case R.id.rb3:
                        // "RANDOM" 라디오 버튼 선택
                        // 데이터를 무작위로 섞음
                        Collections.shuffle(wordItemList);
                        wordListAdapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
                        break;
                }
                // 선택된 라디오 버튼 ID를 SharedPreferences에 저장
                sharedPreferences.edit().putInt("selectedRadioId", checkedId).apply();
            }
        });
    }
}
