package com.example.motive_v1.Goal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_v1.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AllGoalAdapter extends RecyclerView.Adapter<AllGoalAdapter.ViewHolder>{

    private ArrayList<Goal> gItems;
    private ArrayList<Goal> arrayList;
    private Context context;
    private String endDateString;
    private String listener;

    int lastPosition = -1;

    public AllGoalAdapter(ArrayList<Goal> gItems, Context context) {
        this.gItems = gItems;
        this.context = context;
    }

    public AllGoalAdapter(GoalAddPage mainActivity, ArrayList<Goal> arrayList, GoalAddPage endDateString, String listener) {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_allgoal, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // 데이터 뷰 홀더에 바인딩
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if (viewHolder.getAdapterPosition() > lastPosition) {
            Goal item = gItems.get(position);

            // RecyclerView 항목에서 goalText, title 및 selectedDate 설정
            viewHolder.goalText.setText(item.getGoalText());
            viewHolder.title.setText(item.getTitle());
            viewHolder.selectedDate.setText(item.getSelectedDate());
            viewHolder.progressBar.setProgress(item.getProgress());
            viewHolder.progressText.setText(item.getProgress() + "%");

            // goalText 내용에 따라 다른 배경 설정
            if (item.getGoalText().equals("운동")) {
                viewHolder.goalText.setBackgroundResource(R.drawable.square3);
            } else if (item.getGoalText().equals("공부")) {
                viewHolder.goalText.setBackgroundResource(R.drawable.square4);
            }

            // D-Day 계산
            String selectedDateStr = item.getSelectedDate();
            if (selectedDateStr != null && !selectedDateStr.isEmpty()) {
                String[] dates = selectedDateStr.split(" ~ "); // 날짜 범위가 " ~ "로 구분된다고 가정
                if (dates.length > 1) {
                    String endDateStr = dates[1];
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
                    try {
                        Date endDate = sdf.parse(endDateStr);
                        Calendar endCalendar = Calendar.getInstance();
                        endCalendar.setTime(endDate);

                        // 오늘의 날짜를 자정으로 설정
                        Calendar todayCalendar = Calendar.getInstance();
                        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        todayCalendar.set(Calendar.MINUTE, 0);
                        todayCalendar.set(Calendar.SECOND, 0);
                        todayCalendar.set(Calendar.MILLISECOND, 0);

                        long diff = endCalendar.getTimeInMillis() - todayCalendar.getTimeInMillis();
                        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                        if (days == 0) {
                            viewHolder.dDayText.setText("D-Day");
                            item.setdDayText("D-Day");
                        } else if (days > 0) {
                            viewHolder.dDayText.setText("D-" + days);
                            item.setdDayText("D-" + days);
                        } else {
                            viewHolder.dDayText.setText("D+" + Math.abs(days));
                            item.setdDayText("D+" + Math.abs(days)); //추가
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        viewHolder.dDayText.setText("D-Day Error");
                    }
                } else {
                    viewHolder.dDayText.setText("Date Error");
                }
            } else {
                viewHolder.dDayText.setText("No Date");
            }

            // ViewHolder의 루트 레이아웃에 이 onClickListener를 추가합니다
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int clickedPosition = viewHolder.getAdapterPosition();
                    if (clickedPosition != RecyclerView.NO_POSITION) { // 포지션 유효성 검사
                        Goal clickedGoal = gItems.get(clickedPosition);
                        Intent intent = new Intent(context, GoalDetailActivity.class);

                        intent.putExtra("dDayText", clickedGoal.getdDayText());
                        intent.putExtra("selectedDate", clickedGoal.getSelectedDate());
                        intent.putExtra("title", clickedGoal.getTitle());
                        intent.putExtra("goalText", clickedGoal.getGoalText());
                        intent.putExtra("goalKey", clickedGoal.getKey());

                        context.startActivity(intent);
                    }
                }
            });
        }
    }


    // 체크된 목표 개수를 계산하는 메서드
    private int getCheckedItemCount() {
        int count = 0;
        for (Goal goal : gItems) {
            if (goal.isChecked()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int getItemCount() {
        return(gItems != null ? gItems.size() : 0);
    }


    //아이템 삭제
    public void removeItem(int position){
        if (position >= 0 && position < gItems.size()) {
            gItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, gItems.size()); // 아이템 위치 재조정
        }
    }

    public void removeAllItem(){
        gItems.clear();
        notifyDataSetChanged(); // 모든 아이템 삭제 후 화면 갱신
    }

    //아이템 추가
    public void addItem(int position, Goal item) {
        if (position >= 0 && position <= gItems.size()) {
            gItems.add(position, item);
            notifyItemInserted(position);
            notifyItemRangeChanged(position, gItems.size()); // 아이템 위치 재조정
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView goalText;
        TextView title;
        TextView selectedDate;
        TextView dDayText;
        ProgressBar progressBar;
        TextView progressText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.goalText = itemView.findViewById(R.id.goalText);
            this.title = itemView.findViewById(R.id.goalTitle);
            this.selectedDate = itemView.findViewById(R.id.date);
            this.dDayText = itemView.findViewById(R.id.dDayText);
            this.progressBar = itemView.findViewById(R.id.progressBar);
            this.progressText = itemView.findViewById(R.id.progressText);
        }

    }
}