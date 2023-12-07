package com.paleblue.jlpt_tango;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PronunciationFragment extends Fragment {

    private DBHelper dbHelper;
    private String selectedTable;
    private boolean isFragmentOpen = false;
    private String pronunciation;
    private Button button1, button2, button3, button4, btnNext;
    private Switch switchKnow;
    TextView tvPronunciation;
    HashMap<String, Boolean> switchStates = new HashMap<>();
    private AdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pronunciation, container, false);

        MobileAds.initialize(requireContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        dbHelper = DBHelper.getInstance(requireContext());

        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedTable = bundle.getString("tableName");
        }

        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        ImageButton settingsButton = view.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingDialog();
            }
        });

        switchKnow = view.findViewById(R.id.switchKnow);
        pronunciation = dbHelper.getRandomPronunciationFromDatabase(selectedTable);
        Boolean isKnown = switchStates.get(pronunciation);
        if (isKnown != null) {
            switchKnow.setChecked(isKnown);
        } else {
            switchKnow.setChecked(false); // 스위치 상태가 저장되지 않은 경우 기본값 설정 (예: false)
        }

        switchKnow.setOnCheckedChangeListener((buttonView, isChecked) -> {
            switchStates.put(pronunciation, isChecked); // 스위치 상태를 맵에 저장합니다
            int p_knowValue = isChecked ? 1 : 0;
            int wordId = getWordId(selectedTable, pronunciation);
            dbHelper.updatePKnowValueById(selectedTable, wordId, p_knowValue);

            if (isChecked) {
                Toast.makeText(requireContext(), "아는 단어로 설정되었습니다", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "모르는 단어로 설정되었습니다", Toast.LENGTH_SHORT).show();
            }
        });

        button1 = view.findViewById(R.id.button1);
        button2 = view.findViewById(R.id.button2);
        button3 = view.findViewById(R.id.button3);
        button4 = view.findViewById(R.id.button4);

        Button[] buttons = {button1, button2, button3, button4};

        int correctButtonIndex = new Random().nextInt(4);
        setCorrectButton(buttons, correctButtonIndex, pronunciation);
        setButtonTextBasedOnRandomIndex(buttons, correctButtonIndex, pronunciation);

        tvPronunciation = view.findViewById(R.id.tvPronunciation);
        tvPronunciation.setText(pronunciation);

        isFragmentOpen = true;
        ((PronunciationActivity) requireActivity()).setFragmentOpenStatus(isFragmentOpen);

        btnNext = view.findViewById(R.id.btnNext);
        btnNext.setVisibility(View.GONE);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                boolean isKnowChecked = sharedPreferences.getBoolean("isKnowChecked", true); // 기본값은 false
                boolean isUnknowChecked = sharedPreferences.getBoolean("isUnknowChecked", true); // 기본값은 false

                String newPronunciation = null;

                if (isKnowChecked && isUnknowChecked) {
                    newPronunciation = dbHelper.getRandomPronunciationFromDatabase(selectedTable);
                } else if (isKnowChecked || !isUnknowChecked) {
                    newPronunciation = dbHelper.getRandomKnowPronunciationFromDatabase(selectedTable);
                } else if (isUnknowChecked || !isKnowChecked) {
                    newPronunciation = dbHelper.getRandomUnKnowPronunciationFromDatabase(selectedTable);
                } else {
                }

                // 화면의 발음 업데이트
                tvPronunciation.setText(newPronunciation);

                // 새로운 정답 단어 업데이트
                int correctButtonIndex = new Random().nextInt(4);
                setCorrectButton(buttons, correctButtonIndex, newPronunciation);
                setButtonTextBasedOnRandomIndex(buttons, correctButtonIndex, newPronunciation);
                updateSwitchKnowState();
                // 버튼 색상 초기화
                for (Button button : buttons) {
                    button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background)); // 원하는 버튼 색상으로 변경
                }

                // 'btnNext' 버튼 감춤
                btnNext.setVisibility(View.GONE);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 프래그먼트가 닫혔을 때 상태 변경
        isFragmentOpen = false;
        ((PronunciationActivity) requireActivity()).setFragmentOpenStatus(isFragmentOpen);
    }

    private void setButtonClickListener(Button button, int correctButtonIndex) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button.getTag() != null && (int) button.getTag() == correctButtonIndex) {
                    // 올바른 버튼 클릭 시
                    button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorCorrect));
                    btnNext.setVisibility(View.VISIBLE);
                } else {
                    // 잘못된 버튼 클릭 시
                    button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorFail));
                }
            }
        });
    }

    private void setCorrectButton(Button[] buttons, int correctButtonIndex, String pronunciation) {
        if (pronunciation != null) {
            buttons[correctButtonIndex].setText(dbHelper.getWordByPronunciationFromDatabase(selectedTable, pronunciation));
            buttons[correctButtonIndex].setTag(correctButtonIndex); // 올바른 버튼에 태그 추가
            setButtonClickListener(buttons[correctButtonIndex], correctButtonIndex);
        } else {
            Toast.makeText(requireContext(), "설정을 확인해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    private void setButtonTextBasedOnRandomIndex(Button[] buttons, int correctButtonIndex, String pronunciation) {
        for (int i = 0; i < 4; i++) {
            if (i != correctButtonIndex) {
                buttons[i].setText(getRandomWordFromDatabase(selectedTable));
                buttons[i].setTag(i); // 잘못된 버튼에 태그 추가
                setButtonClickListener(buttons[i], correctButtonIndex);
            }
        }
    }

    private void openSettingDialog() {
        PSettingDialog psettingDialog = new PSettingDialog(requireContext());
        psettingDialog.show();
    }


    private int getWordId(String tableName, String pronunciation) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT id FROM " + tableName + " WHERE pronunciation = ?";
        Cursor cursor = db.rawQuery(query, new String[]{pronunciation});
        int wordId = -1; // 기본값은 -1 또는 다른 오류 코드로 설정합니다.

        if (cursor != null && cursor.moveToFirst()) {
            wordId = cursor.getInt(0); // 'id' 필드의 인덱스가 0이라고 가정합니다.
            cursor.close();
        }

        db.close();
        return wordId;
    }

    private String getRandomWordFromDatabase(String tableName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT word FROM " + tableName + " ORDER BY RANDOM() LIMIT 1";
        String word = dbHelper.getRandomWordFromDatabase(tableName);
        db.close();
        return word;
    }

    private void updateSwitchKnowState() {
        if (tvPronunciation != null) {
            String pronunciation = tvPronunciation.getText().toString();
            int wordId = getWordId(selectedTable, pronunciation);
            int p_knowValue = dbHelper.getPKnowValueForWord(selectedTable, wordId);

            // 스위치 초기화를 수행하지 않도록 'setOnCheckedChangeListener'를 잠시 해제
            switchKnow.setOnCheckedChangeListener(null);

            switchKnow.setChecked(p_knowValue == 1); // 스위치 상태 설정

            // 스위치 상태를 변경할 때만 데이터베이스 업데이트
            switchKnow.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int newPKnowValue = isChecked ? 1 : 0;

                // 'getWordId()' 메서드를 사용하여 해당 단어의 ID를 가져옵니다.
                int newWordId = getWordId(selectedTable, pronunciation);

                // 'updatePKnowValueById' 메서드를 호출하여 'p_know' 값을 업데이트합니다.
                dbHelper.updatePKnowValueById(selectedTable, newWordId, newPKnowValue);

                if (isChecked) {
                    Toast.makeText(requireContext(), "아는 단어로 설정되었습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "모르는 단어로 설정되었습니다", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
