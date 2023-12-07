package com.paleblue.jlpt_tango;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> {

    private List<WordItem> wordItems;
    private Context context;
    private DBHelper dbHelper;
    private SharedPreferences sharedPreferences;
    private boolean isWordVisible;
    private boolean isPronunciationVisible;
    private boolean isMeaningVisible;
    private boolean isKnownVisible;
    private boolean isUnknownVisible;


    public WordListAdapter(Context context, List<WordItem> wordItems, SharedPreferences sharedPreferences) {
        this.context = context;
        this.wordItems = wordItems;
        this.dbHelper = new DBHelper(context);
        this.sharedPreferences = sharedPreferences;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.word_item, parent, false);
        return new WordListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WordItem wordItem = wordItems.get(position);

        String word = wordItem.getWord();
        String pronunciation = wordItem.getPronunciation();
        String meaning = wordItem.getMeaning();

        holder.tvWord.setText(word);
        holder.tvPronounce.setText(pronunciation);
        holder.tvMeaning.setText(meaning);

        holder.tvWord.setVisibility(isWordVisible ? View.VISIBLE : View.GONE);
        holder.tvPronounce.setVisibility(isPronunciationVisible ? View.VISIBLE : View.GONE);
        holder.tvMeaning.setVisibility(isMeaningVisible ? View.VISIBLE : View.GONE);

        boolean shouldShowItem = (isKnownVisible && wordItem.isKnown()) || (isUnknownVisible && !wordItem.isKnown());

        // 아이템의 가시성을 설정합니다.
        holder.itemView.setVisibility(shouldShowItem ? View.VISIBLE : View.GONE);

        // 빈 자리에 대한 처리
        if (!shouldShowItem) {
            holder.itemView.getLayoutParams().height = 0; // 빈 자리의 높이를 0으로 설정
            holder.itemView.setVisibility(View.INVISIBLE); // 빈 자리를 숨김
        } else {
            holder.itemView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT; // 아이템의 높이를 자동으로 설정
            holder.itemView.setVisibility(View.VISIBLE); // 아이템을 보임
        }

        holder.wordSwitch.setOnCheckedChangeListener(null);

        try {
            boolean isKnownFromPrefs = sharedPreferences.getBoolean("word" + wordItem.getId(), false);
            holder.wordSwitch.setChecked(isKnownFromPrefs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.wordSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            wordItem.setKnown(isChecked);
            String tableName = wordItem.getTableName();
            updateKnownStatusInDatabase(tableName, wordItem.getId(), isChecked);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("word" + wordItem.getId(), isChecked);
            editor.apply();

            if (isChecked) {
                // 체크됨
                Toast.makeText(context, "아는 단어로 설정되었습니다", Toast.LENGTH_SHORT).show();
            } else {
                // 체크 해제됨
                Toast.makeText(context, "모르는 단어로 설정되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setWordVisibility(boolean show) {
        isWordVisible = show;
        notifyDataSetChanged();
    }

    public void setPronunciationVisibility(boolean show) {
        isPronunciationVisible = show;
        notifyDataSetChanged();
    }

    public void setMeaningVisibility(boolean show) {
        isMeaningVisible = show;
        notifyDataSetChanged();
    }

    public void setWordList(List<WordItem> wordList) {
        this.wordItems = wordList;
    }

    public void setKnownVisibility(boolean isKnownVisible, boolean isUnknownVisible) {
        this.isKnownVisible = isKnownVisible;
        this.isUnknownVisible = isUnknownVisible;
        notifyDataSetChanged(); // 아이템의 가시성 변경을 RecyclerView에 알립니다.
    }

    private void updateKnownStatusInDatabase(String tableName, int wordId, boolean isChecked) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        int knowValue = isChecked ? 1 : 0;
        values.put("know", knowValue);
        db.update(tableName, values, "id = ?", new String[]{String.valueOf(wordId)});
        db.close();
    }


    @Override
    public int getItemCount() {
        return wordItems != null ? wordItems.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvWord;
        public TextView tvPronounce;
        public TextView tvMeaning;
        public Switch wordSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            tvPronounce = itemView.findViewById(R.id.tvPronounce);
            tvMeaning = itemView.findViewById(R.id.tvMeaning);
            wordSwitch = itemView.findViewById(R.id.wordSwitch);
        }
    }
}