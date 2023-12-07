package com.paleblue.jlpt_tango;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LevelListAdapter extends RecyclerView.Adapter<LevelListAdapter.ViewHolder> {

    private List<String> tableNames;
    private Context context;
    private DataManager dataManager;

    public LevelListAdapter(Context context, List<String> tableNames) {
        this.context = context;
        this.tableNames = tableNames;
        dataManager = new DataManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.level_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String tableName = tableNames.get(position);
        holder.textTableName.setText(tableName);

        // 아는 단어와 모르는 단어의 숫자를 가져와 설정
        int knownWordCount = dataManager.getKnownWordCount(tableName);
        int unknownWordCount = dataManager.getUnknownWordCount(tableName);

        holder.textCheckCount.setText(context.getString(R.string.known_words_count, knownWordCount));
        holder.textFailCount.setText(context.getString(R.string.unknown_words_count, unknownWordCount));

        int totalWords = knownWordCount + unknownWordCount;
        int progress = totalWords > 0 ? (knownWordCount * 100) / totalWords : 0;

        // ProgressBar의 Drawable을 가져오고 ProgressBar의 진행 상태에 따라 색상을 설정
        Drawable progressDrawable = holder.progressBar.getProgressDrawable();

        // ProgressBar의 진행 표시 Drawable을 직접 설정하여 색상 변경
        if (progressDrawable != null) {
            progressDrawable.setColorFilter(
                    progress >= 50 ? Color.GREEN : Color.RED,
                    PorterDuff.Mode.SRC_IN
            );
        }

        holder.progressBar.setProgress(progress);

        // 아이템 클릭 이벤트 핸들러
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MainActivity로 이동하는 Intent를 생성하고 선택한 테이블 이름을 추가
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("tableName", tableName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tableNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textTableName;
        public TextView textCheckCount;
        public TextView textFailCount;
        public ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTableName = itemView.findViewById(R.id.tvLevel);
            textCheckCount = itemView.findViewById(R.id.tvCheck);
            textFailCount = itemView.findViewById(R.id.tvFail);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
