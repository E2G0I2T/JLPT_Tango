package com.paleblue.jlpt_tango;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class MSettingDialog extends Dialog {
    CheckBox checkBoxKnow, checkBoxUnKnow;
    private static final String PREFS_NAME = "MyPrefs";
    public MSettingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_msetting);

        checkBoxKnow = findViewById(R.id.checkBoxKnow);
        checkBoxUnKnow = findViewById(R.id.checkBoxUnKnow);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isKnowChecked = sharedPreferences.getBoolean("isKnowChecked", true); // 기본값은 false
        boolean isUnknowChecked = sharedPreferences.getBoolean("isUnknowChecked", true); // 기본값은 false

        checkBoxKnow.setChecked(isKnowChecked);
        checkBoxUnKnow.setChecked(isUnknowChecked);

        checkBoxKnow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // "알고 있는" 체크박스 상태가 변경될 때 실행할 코드
                // SharedPreferences를 사용하여 설정을 저장
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isKnowChecked", isChecked);
                editor.apply();
            }
        });

        checkBoxUnKnow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // "알지 못하는" 체크박스 상태가 변경될 때 실행할 코드
                // SharedPreferences를 사용하여 설정을 저장
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isUnknowChecked", isChecked);
                editor.apply();
            }
        });
    }
}