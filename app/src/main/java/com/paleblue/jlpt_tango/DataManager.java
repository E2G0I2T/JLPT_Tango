package com.paleblue.jlpt_tango;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private List<WordItem> wordItemList;

    public DataManager(Context context) {
        dbHelper = DBHelper.getInstance(context);
        wordItemList = new ArrayList<>();
        database = dbHelper.getReadableDatabase();
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getTableData(String tableName) {
        String query = "SELECT * FROM " + tableName;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String word = cursor.getString(cursor.getColumnIndex("word"));
                @SuppressLint("Range") String pronunciation = cursor.getString(cursor.getColumnIndex("pronunciation"));
                @SuppressLint("Range") String meaning = cursor.getString(cursor.getColumnIndex("meaning"));

                if (word != null && pronunciation != null && meaning != null) {
                    WordItem wordItem = new WordItem(id, word, pronunciation, meaning, tableName);
                    wordItemList.add(wordItem);
                } else {
                    Log.e("DataManager", "데이터가 올바르지 않습니다. Word: " + word + ", Pronunciation: " + pronunciation + ", Meaning: " + meaning);
                }
            } while (cursor.moveToNext());
        } else {
            Log.e("DataManager", "데이터가 없거나 조회에 실패했습니다.");
        }

        return cursor;
    }

    public int getKnownWordCount(String tableName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // 해당 테이블의 데이터베이스 객체 초기화
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE know = 1";
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;

        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }

        // 사용한 데이터베이스 객체를 닫아야 합니다.
        db.close();

        return count;
    }

    public int getUnknownWordCount(String tableName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase(); // 해당 테이블의 데이터베이스 객체 초기화
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE know = 0";
        Cursor cursor = db.rawQuery(query, null);
        int count = 0;

        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }

        // 사용한 데이터베이스 객체를 닫아야 합니다.
        db.close();

        return count;
    }

    public void updateKnownStatusInAllTables(int wordId, boolean isChecked) {
        dbHelper.updateKnownStatusInTable("JLPT_1", wordId, isChecked);
        dbHelper.updateKnownStatusInTable("JLPT_2", wordId, isChecked);
        dbHelper.updateKnownStatusInTable("JLPT_3", wordId, isChecked);
        dbHelper.updateKnownStatusInTable("JLPT_4", wordId, isChecked);
        dbHelper.updateKnownStatusInTable("JLPT_5", wordId, isChecked);
    }
}
