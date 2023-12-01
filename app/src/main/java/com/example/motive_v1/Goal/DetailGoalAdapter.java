package com.example.motive_v1.Goal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_v1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import static android.content.ContentValues.TAG;

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

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DetailGoalAdapter extends RecyclerView.Adapter<DetailGoalAdapter.ViewHolder>{

    private ArrayList<Goal> gItems;
    private Context context;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private TextView percentageTextView;


    int lastPosition = -1;


    public DetailGoalAdapter(ArrayList<Goal> gItems, Context context, ProgressBar progressBar, TextView percentageTextView) {
        this.gItems = gItems;
        this.context = context;
        this.progressBar = progressBar;
        this.percentageTextView = percentageTextView;

        FirebaseApp.initializeApp(context); // Firebase 초기화
        database = FirebaseDatabase.getInstance(); // Firebase 데이터베이스 인스턴스를 가져옵니다
        databaseReference = database.getReference("AllGoalItem"); // 이제 참조를 가져올 수 있습니다
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goal_detail, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    // 데이터 뷰 홀더에 바인딩
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        if (viewHolder.getAdapterPosition() > lastPosition) {
            Goal item = gItems.get(position);

            // RecyclerView 항목에서 title, checkbox, eachDate 설정
            // title 설정
            viewHolder.title.setText(item.getTitle());
            // 날짜 추가
            String selectedDate = item.getSelectedDate();
            if (selectedDate != null && selectedDate.contains(" ~ ")) {
                String[] dateRange = selectedDate.split(" ~ ");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
                try {
                    Date startDate = sdf.parse(dateRange[0]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDate);
                    calendar.add(Calendar.DATE, position);
                    Date itemDate = calendar.getTime();

                    // 요일 계산
                    Calendar dayOfWeekCalendar = Calendar.getInstance();
                    dayOfWeekCalendar.setTime(itemDate);
                    int dayOfWeek = dayOfWeekCalendar.get(Calendar.DAY_OF_WEEK);

                    // 숫자로 된 요일을 문자열로 변환 (예: 일요일, 월요일, 등)
                    String dayOfWeekString = getDayOfWeekString(dayOfWeek);
                    String formattedDate = sdf.format(itemDate);

                    viewHolder.eachDate.setText(formattedDate.substring(8, 10) + "일 (" + dayOfWeekString + ")");
                } catch (ParseException e) {
                    e.printStackTrace();
                    viewHolder.eachDate.setText("날짜 형식 오류");
                }
            } else {
                viewHolder.eachDate.setText("날짜 정보 없음");
            }

            // 데이터 모델을 기반으로 체크박스 상태 초기화
            String displayDate = viewHolder.eachDate.getText().toString();
            String databaseDate = convertDateToDatabaseFormat(displayDate, item.getSelectedDate());
            boolean currentCheckedState = item.getCheckedDates().getOrDefault(databaseDate, false);
            viewHolder.checkbox.setImageResource(currentCheckedState ? R.drawable.shape_clicked : R.drawable.shape_unclicked);

            //체크박스
            viewHolder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 체크박스 상태 토글
                    boolean newCheckedState = !currentCheckedState;
                    item.getCheckedDates().put(databaseDate, newCheckedState);

                    // Firebase에 체크 상태 업데이트
                    DatabaseReference itemRef = database.getReference("AllGoalItem").child(item.getKey());
                    itemRef.child("checkedDates").child(databaseDate).setValue(newCheckedState);

                    // 진행률을 업데이트하고 Firebase에 저장
                    updateProgress(itemRef, viewHolder.getAdapterPosition());
                }
            });

        }
    }

    // 진행률을 업데이트하는 메소드
    private void updateProgress(DatabaseReference itemRef, int adapterPosition) {
        itemRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Goal goal = mutableData.getValue(Goal.class);
                if (goal == null) {
                    return Transaction.success(mutableData);
                }

                // Firebase에서 최신의 completedCount와 totalCount를 가져옴
                int completedCount = 0;
                for (Boolean checked : goal.getCheckedDates().values()) {
                    if (checked) {
                        completedCount++;
                    }
                }
                goal.setCompletedCount(completedCount); // 새로 계산된 completedCount 설정

                int progressPercentage = goal.getTotalCount() > 0 ?
                        (int) ((double) completedCount / goal.getTotalCount() * 100) : 0;
                goal.setProgress(progressPercentage); // 진행률 계산 후 설정

                // 변경된 Goal 객체를 Firebase에 저장
                mutableData.setValue(goal);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (committed) {
                    Goal updatedGoal = dataSnapshot.getValue(Goal.class);
                    if (updatedGoal != null) {
                        int newProgress = updatedGoal.getProgress();
                        progressBar.setProgress(newProgress); // UI의 ProgressBar 업데이트
                        percentageTextView.setText(newProgress + "%"); // 텍스트 뷰 업데이트
                        notifyItemChanged(adapterPosition); // RecyclerView에 변경 사항 알림
                    }
                } else if (databaseError != null) {
                    Log.e(TAG, "Firebase에서 진행률 업데이트 실패", databaseError.toException());
                }
            }
        });
    }

    private String convertDateToDatabaseFormat(String displayDate, String selectedDate) {
        // '21 (토)'에서 숫자 부분 '21'만 추출
        String day = displayDate.replaceAll("[^0-9]", "").trim();

        // 시작 날짜에서 일자만 추출
        String[] dateRange = selectedDate.split(" ~ ");
        String startDateStr = dateRange[0];
        String year = startDateStr.split("\\.")[0];
        String month = startDateStr.split("\\.")[1];

        // '20231021' 형식으로 반환
        return year + month + day;
    }


    private String getDayOfWeekString(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "일";
            case Calendar.MONDAY:
                return "월";
            case Calendar.TUESDAY:
                return "화";
            case Calendar.WEDNESDAY:
                return "수";
            case Calendar.THURSDAY:
                return "목";
            case Calendar.FRIDAY:
                return "금";
            case Calendar.SATURDAY:
                return "토";
            default:
                return "";
        }
    }


    @Override
    public int getItemCount() {
        return(gItems != null ? gItems.size() : 0);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView checkbox;
        TextView eachDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.goalTitle);
            this.checkbox = itemView.findViewById(R.id.checkbox);
            this.eachDate = itemView.findViewById(R.id.eachDate);
        }

    }
}
