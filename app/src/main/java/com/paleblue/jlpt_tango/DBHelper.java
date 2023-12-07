package com.paleblue.jlpt_tango;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "jlpt.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    private static DBHelper instance;

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        // 데이터베이스 파일을 assets 폴더에서 복사
        try {
            copyDatabaseFromAssets(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // JLPT_1 테이블 생성
        String createTableQuery1 = "CREATE TABLE IF NOT EXISTS JLPT_1 (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "word TEXT," +
                "pronunciation TEXT," +
                "meaning TEXT," +
                "know INTEGER DEFAULT 0," +
                "p_know INTEGER DEFAULT 0," +
                "m_know INTEGER DEFAULT 0" +
                ");";

        sqLiteDatabase.execSQL(createTableQuery1);

        // JLPT_2 테이블 생성
        String createTableQuery2 = "CREATE TABLE IF NOT EXISTS JLPT_2 (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "word TEXT," +
                "pronunciation TEXT," +
                "meaning TEXT," +
                "know INTEGER DEFAULT 0," +
                "p_know INTEGER DEFAULT 0," +
                "m_know INTEGER DEFAULT 0" +
                ");";

        sqLiteDatabase.execSQL(createTableQuery2);

        // JLPT_3 테이블 생성
        String createTableQuery3 = "CREATE TABLE IF NOT EXISTS JLPT_3 (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "word TEXT," +
                "pronunciation TEXT," +
                "meaning TEXT," +
                "know INTEGER DEFAULT 0," +
                "p_know INTEGER DEFAULT 0," +
                "m_know INTEGER DEFAULT 0" +
                ");";

        sqLiteDatabase.execSQL(createTableQuery3);

        // JLPT_4 테이블 생성
        String createTableQuery4 = "CREATE TABLE IF NOT EXISTS JLPT_4 (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "word TEXT," +
                "pronunciation TEXT," +
                "meaning TEXT," +
                "know INTEGER DEFAULT 0," +
                "p_know INTEGER DEFAULT 0," +
                "m_know INTEGER DEFAULT 0" +
                ");";

        sqLiteDatabase.execSQL(createTableQuery4);

        // JLPT_5 테이블 생성
        String createTableQuery5 = "CREATE TABLE IF NOT EXISTS JLPT_5 (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "word TEXT," +
                "pronunciation TEXT," +
                "meaning TEXT," +
                "know INTEGER DEFAULT 0," +
                "p_know INTEGER DEFAULT 0," +
                "m_know INTEGER DEFAULT 0" +
                ");";

        sqLiteDatabase.execSQL(createTableQuery5);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {

            // 기존 테이블을 백업하거나 데이터 이전 작업 수행
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS temp_table AS SELECT * FROM JLPT_2");
            // 새로운 테이블 생성 또는 기존 테이블 업그레이드
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS JLPT_2_v2 (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "word TEXT," +
                    "pronunciation TEXT," +
                    "meaning TEXT," +
                    "know INTEGER DEFAULT 0," +
                    "p_know INTEGER DEFAULT 0," +
                    "m_know INTEGER DEFAULT 0" +
                    ");");

            // 이전 데이터 복원 또는 재매핑
            sqLiteDatabase.execSQL("INSERT INTO JLPT_2_v2 SELECT * FROM temp_table");

            // 임시 테이블 삭제
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS temp_table");
        }
    }

    // DBHelper 클래스 내부에 copyDatabaseFromAssets 메서드 추가
    private void copyDatabaseFromAssets(Context context) throws IOException {
        // 복사할 데이터베이스 파일 이름
        String databaseName = "jlpt.db";

        // 내부 저장소의 데이터베이스 경로
        String outputPath = context.getDatabasePath(databaseName).getPath();

        // 이미 데이터베이스 파일이 존재하는 경우 복사하지 않음
        if (new File(outputPath).exists()) {
            return;
        }

        // assets 폴더에서 데이터베이스 파일 열기
        InputStream inputStream = context.getAssets().open(databaseName);

        // 출력 스트림 생성
        OutputStream outputStream = new FileOutputStream(outputPath);

        // 데이터 복사
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        // 스트림 닫기
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public void updateKnownStatusInTable(String tableName, int wordId, boolean isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int knowValue = isChecked ? 1 : 0;
        values.put("know", knowValue);

        // 해당 테이블에서 ID가 wordId인 레코드를 업데이트합니다.
        db.update(tableName, values, "id = ?", new String[]{String.valueOf(wordId)});
        db.close();
    }

    public String getRandomPronunciationFromDatabase(String tableName) {
        String pronunciation = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT pronunciation FROM " + tableName + " ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            pronunciation = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return pronunciation;
    }

    public String getRandomMeaningFromDatabase(String tableName) {
        String meaning = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT meaning FROM " + tableName + " ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            meaning = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return meaning;
    }

    public String getRandomKnowPronunciationFromDatabase(String tableName) {
        String pronunciation = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT pronunciation FROM " + tableName + " WHERE p_know = 1 ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            pronunciation = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return pronunciation;
    }

    public String getRandomKnowMeaningFromDatabase(String tableName) {
        String meaning = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT pronunciation FROM " + tableName + " WHERE m_know = 1 ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            meaning = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return meaning;
    }

    public String getRandomUnKnowPronunciationFromDatabase(String tableName) {
        String pronunciation = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT pronunciation FROM " + tableName + " WHERE p_know = 0 ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            pronunciation = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return pronunciation;
    }

    public String getRandomUnKnowMeaningFromDatabase(String tableName) {
        String meaning = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT meaning FROM " + tableName + " WHERE m_know = 0 ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            meaning = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return meaning;
    }

    // DBHelper 클래스 내부에서 메서드를 수정하고 접근 권한을 변경합니다.
    public String getWordByPronunciationFromDatabase(String tableName, String pronunciation) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT word FROM " + tableName + " WHERE pronunciation = ?";
        Cursor cursor = db.rawQuery(query, new String[] {pronunciation});
        String word = null;
        if (cursor.moveToFirst()) {
            word = cursor.getString(0);
        }
        cursor.close();
        return word;
    }

    public String getWordByMeaningFromDatabase(String tableName, String meaning) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT word FROM " + tableName + " WHERE meaning = ?";
        Cursor cursor = db.rawQuery(query, new String[] {meaning});
        String word = null;
        if (cursor.moveToFirst()) {
            word = cursor.getString(0);
        }
        cursor.close();
        return word;
    }

    public String getRandomWordFromDatabase(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT word FROM " + tableName + " ORDER BY RANDOM() LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        String word = null;
        if (cursor.moveToFirst()) {
            word = cursor.getString(0);
        }
        cursor.close();
        return word;
    }

    public void updatePKnowValueById(String tableName, int wordId, int p_knowValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("p_know", p_knowValue);

        // 단어 ID에 해당하는 레코드를 업데이트
        int rowsUpdated = db.update(tableName, values, "id = ?", new String[]{String.valueOf(wordId)});
        if (rowsUpdated > 0) {
            Log.d("DatabaseUpdate", "Updated p_know value for word ID " + wordId + " to " + p_knowValue);
        } else {
            Log.e("DatabaseUpdate", "Failed to update p_know value for word ID " + wordId);
        }

        db.close();
    }

    public void updateMKnowValueById(String tableName, int wordId, int m_knowValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("p_know", m_knowValue);

        // 단어 ID에 해당하는 레코드를 업데이트
        int rowsUpdated = db.update(tableName, values, "id = ?", new String[]{String.valueOf(wordId)});
        if (rowsUpdated > 0) {
            Log.d("DatabaseUpdate", "Updated m_know value for word ID " + wordId + " to " + m_knowValue);
        } else {
            Log.e("DatabaseUpdate", "Failed to update m_know value for word ID " + wordId);
        }

        db.close();
    }

    public int getPKnowCountForTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE p_know = 1";
        Cursor cursor = db.rawQuery(query, null);

        int pKnowCount = 0;
        if (cursor != null && cursor.moveToFirst()) {
            pKnowCount = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return pKnowCount;
    }

    public int getMKnowCountForTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE m_know = 1";
        Cursor cursor = db.rawQuery(query, null);

        int mKnowCount = 0;
        if (cursor != null && cursor.moveToFirst()) {
            mKnowCount = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return mKnowCount;
    }

    public int getWordCountForTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + tableName;
        Cursor cursor = db.rawQuery(query, null);

        int wordCount = 0;
        if (cursor != null && cursor.moveToFirst()) {
            wordCount = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return wordCount;
    }

    public int getPKnowValueForWord(String tableName, int wordId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT p_know FROM " + tableName + " WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(wordId)});

        int p_knowValue = 0; // 찾지 못한 경우 기본값은 0
        if (cursor != null && cursor.moveToFirst()) {
            p_knowValue = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return p_knowValue;
    }

    public int getMKnowValueForWord(String tableName, int wordId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT m_know FROM " + tableName + " WHERE id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(wordId)});

        int m_knowValue = 0; // 찾지 못한 경우 기본값은 0
        if (cursor != null && cursor.moveToFirst()) {
            m_knowValue = cursor.getInt(0);
            cursor.close();
        }

        db.close();
        return m_knowValue;
    }
}

