package com.paleblue.jlpt_tango;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PronunciationAdapter extends RecyclerView.Adapter<PronunciationAdapter.ViewHolder> {

    private List<String> tableNames;
    private Context context;
    private OnItemClickListener listener;
    private DBHelper dbHelper;
    private boolean isFragmentOpen = false;

    public void setFragmentOpenStatus(boolean isOpen) {
        isFragmentOpen = isOpen;
        notifyDataSetChanged(); // 데이터셋 변경을 알림
    }

    public PronunciationAdapter(Context context, List<String> tableNames, OnItemClickListener listener, DBHelper dbHelper) {
        this.context = context;
        this.tableNames = tableNames;
        this.listener = listener;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pronunciation_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String tableName = tableNames.get(position);
        holder.bind(tableName);

        // "p_know" 값 가져오기
        int pKnowCount = getPKnowCount(tableName);

        // 해당 테이블의 단어 개수를 가져오기
        int wordCount = dbHelper.getWordCountForTable(tableName);

        // "p_know" 값을 tvCheck 및 tvFail에 설정
        holder.tvCheck.setText(String.valueOf(pKnowCount));

        // "tvFail" (unknown words count) 설정: 해당 테이블의 단어 개수 - "p_know" 값
        int unknownCount = wordCount - pKnowCount;
        holder.tvFail.setText(String.valueOf(unknownCount));

        // ProgressBar를 찾아서 가져오기
        ProgressBar progressBar = holder.itemView.findViewById(R.id.progressBar);

        // 프로그레스 바 설정: (pKnowCount / wordCount) * 100
        int progress = (int)((double)pKnowCount / wordCount * 100); // 백분율 계산
        holder.progressBar.setProgress(progress);

        // Fragment가 열려 있을 때 아이템의 클릭 가능 여부를 비활성화합니다.
        holder.itemView.setClickable(!isFragmentOpen);
        holder.itemView.setFocusable(!isFragmentOpen);
    }


    private int getPKnowCount(String tableName) {
        int pKnowCount = dbHelper.getPKnowCountForTable(tableName);
        return pKnowCount;
    }

    @Override
    public int getItemCount() {
        return tableNames.size();
    }

    public interface OnItemClickListener {
        void onItemClick(String tableName);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvLevel, tvFail, tvCheck;
        private ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLevel = itemView.findViewById(R.id.tvLevel);
            tvFail = itemView.findViewById(R.id.tvFail);
            tvCheck = itemView.findViewById(R.id.tvCheck);
            progressBar = itemView.findViewById(R.id.progressBar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String tableName = tableNames.get(position);

                        // 아이템을 클릭했을 때 PronunciationFragment로 이동하고 선택한 테이블 이름을 전달
                        if (!isFragmentOpen) {
                            PronunciationFragment pronunciationFragment = new PronunciationFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("tableName", tableName);
                            pronunciationFragment.setArguments(bundle);

                            // Fragment 전환을 위한 코드 추가
                            FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, pronunciationFragment);
                            transaction.addToBackStack("pronunciation");
                            transaction.commit();
                        }
                    }
                }
            });
        }

        public void bind(String tableName) {
            tvLevel.setText(tableName);
        }
    }
}