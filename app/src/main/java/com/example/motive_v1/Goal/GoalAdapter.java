package com.example.motive_v1.Goal;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_v1.Goal.Goal;
import com.example.motive_v1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    private ArrayList<Goal> gItems;
    private Context context;
    private LocalDate selectedDate;

    public interface OnCheckboxClickListener {
        void onCheckboxClick(Goal goal, String date, boolean newCheckState, ImageView checkbox);
    }

    private OnCheckboxClickListener onCheckboxClickListener;

    public GoalAdapter(ArrayList<Goal> gItems, Context context, LocalDate selectedDate, OnCheckboxClickListener listener) {
        this.gItems = gItems;
        this.context = context;
        if (selectedDate == null) {
            throw new IllegalArgumentException("selectedDate cannot be null");
        }
        this.selectedDate = selectedDate;
        this.onCheckboxClickListener = listener;
    }

    public void setGoals(ArrayList<Goal> filteredGoals) {
        this.gItems = filteredGoals;
        notifyDataSetChanged();
    }

    public void setSelectedDate(LocalDate newSelectedDate) {
        if (newSelectedDate != null) {
            this.selectedDate = newSelectedDate;
            notifyDataSetChanged();
        } else {
            throw new IllegalArgumentException("newSelectedDate cannot be null");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goal, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Goal item = gItems.get(position);

        viewHolder.goalText.setText(item.getGoalText());
        viewHolder.title.setText(item.getTitle());
        viewHolder.selectedDate.setText(item.getSelectedDate() != null ? item.getSelectedDate() : "날짜 없음");

        // goalText 내용에 따라 다른 배경 설정
        if (item.getGoalText().equals("운동")) {
            viewHolder.goalText.setBackgroundResource(R.drawable.square3);
        } else if (item.getGoalText().equals("공부")) {
            viewHolder.goalText.setBackgroundResource(R.drawable.square4);
        }

        // checkedDates 맵에서 현재 선택된 날짜에 대한 체크 여부를 가져옵니다.
        Boolean isChecked = item.getCheckedDates().get(selectedDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        // 체크박스의 시각적 상태를 초기화합니다.
        if (isChecked != null && isChecked) {
            viewHolder.checkbox.setImageResource(R.drawable.shape_clicked);
        } else {
            viewHolder.checkbox.setImageResource(R.drawable.shape_unclicked);
        }

        // 체크박스 클릭 리스너 설정
        viewHolder.checkbox.setOnClickListener(v -> {
            // 체크 여부를 반전시킵니다.
            boolean newCheckState = isChecked == null || !isChecked;
            item.setCheckedOnDate(selectedDate, newCheckState); // Goal 객체의 상태를 업데이트합니다.

            // 체크박스 UI를 업데이트합니다.
            viewHolder.checkbox.setImageResource(newCheckState ? R.drawable.shape_clicked : R.drawable.shape_unclicked);

            // 변경 사항을 리스너에 알립니다.
            if (onCheckboxClickListener != null) {
                onCheckboxClickListener.onCheckboxClick(item, selectedDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), newCheckState, viewHolder.checkbox);
            }
        });

        // ViewHolder가 재사용될 때 체크박스의 체크 여부가 정확하게 표시되도록 하기 위해 이 부분을 추가합니다.
        viewHolder.setIsRecyclable(false);
    }


    @Override
    public int getItemCount() {
        return (gItems != null ? gItems.size() : 0);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView goalText;
        TextView title;
        TextView selectedDate;
        ImageView checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.goalText = itemView.findViewById(R.id.goalText);
            this.title = itemView.findViewById(R.id.goalTitle);
            this.selectedDate = itemView.findViewById(R.id.date);
            this.checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
}