package com.example.motive_v1.Goal;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_v1.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GoalDetailActivity extends AppCompatActivity{
    private RecyclerView recyclerViewDetail;
    private DetailGoalAdapter detailAdapter;
    private ArrayList<Goal> detailGoalList;
    private int dDayValueDetail;
    private long endMillis;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    TextView dDayText;
    TextView deadline;
    TextView topic;
    TextView title;
    TextView progressText;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_detail_page);

        dDayText = findViewById(R.id.dDayText);
        deadline = findViewById(R.id.deadline);
        title = findViewById(R.id.title);
        topic = findViewById(R.id.topic);
        progressText = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);


        // Intent에서 데이터 가져오기
        Intent intent = getIntent();

        String goalKey = intent.getStringExtra("goalKey");
        dDayText.setText(intent.getStringExtra("dDayText"));
        deadline.setText(intent.getStringExtra("selectedDate"));
        title.setText(intent.getStringExtra("title"));
        String goalText = intent.getStringExtra("goalText");
        if (goalText != null) {
            goalText = "[" + goalText + "]";
            topic.setText(goalText);
        }


        // GoalDetailActivity
        String selectedDateStr = intent.getStringExtra("selectedDate");
        String[] dates = selectedDateStr.split(" ~ ");

        final String selectedTitle = intent.getStringExtra("title");

        // Firebase 초기화
        FirebaseApp.initializeApp(this);
        // RecyclerView 초기화
        recyclerViewDetail = findViewById(R.id.recyclerViewdetail);
        detailGoalList = new ArrayList<>();
        detailAdapter = new DetailGoalAdapter(detailGoalList, this, progressBar, progressText);
        recyclerViewDetail.setAdapter(detailAdapter);
        recyclerViewDetail.setLayoutManager(new LinearLayoutManager(this));

        // Firebase 초기화 및 DatabaseReference 설정
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("AllGoalItem");

        // 데이터베이스에서 데이터 가져오기
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 데이터 변경이 발생할 때마다 호출됨
                detailGoalList.clear(); // 기존 데이터 삭제

                for (DataSnapshot goalSnapshot : dataSnapshot.getChildren()) {
                    Goal goal = goalSnapshot.getValue(Goal.class); // 데이터를 Goal 클래스로 변환

                    if (goal != null && goal.getTitle().equals(selectedTitle)) {  // 선택한 title과 일치하는 항목만 추가
                        goal.setKey(goalSnapshot.getKey()); // Firebase에서 제공하는 고유 키 설정
                        String getTitle = goal.getTitle();
                        String getSelectedDate = goal.getSelectedDate();

                        if (getSelectedDate == null || !getSelectedDate.contains(" ~ ")) {
                            // selectedDate가 null이거나 포맷이 잘못된 경우에 대한 처리
                            Log.e("Debug", "Invalid or null selectedDate for goalId: " + goal.getId());
                            continue; // 현재 반복을 건너뛰고 다음 아이템으로 이동
                        }
                        String[] dateParts = getSelectedDate.split(" ~ ");
                        String startDateString = dateParts[0];
                        int dayCount = calculateDayCount(startDateString, dateParts[1]);

                        if (goal.getKey() != null && goal.getKey().equals(goalKey)) {

                            // ProgressBar의 값을 데이터베이스에서 가져오기
                            Integer progressValue = goalSnapshot.child("progress").getValue(Integer.class);
                            if (progressValue != null) {
                                updateUIWithProgress(progressValue); // 가져온 값을 UI에 업데이트하는 메소드 호출
                            }

                            for (int i = 1; i <= dayCount; i++) {
                                Goal newItem = new Goal(getTitle);
                                newItem.setSelectedDate(getSelectedDate);
                                detailGoalList.add(goal);
                            }
                        }
                    }
                }

                detailAdapter.notifyDataSetChanged(); // 데이터 변경 알림
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 오류 발생 시 로그 출력
                Log.e(TAG, "Failed to read goals.", databaseError.toException());
            }
        });


        //상단
        // 뒤로가기 버튼 (TodayActivity로 돌아감)
        ImageButton backButton = findViewById(R.id.backbtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //상단 bottom sheet dialog
        final View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.goal_bottom_sheet, null);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        // 버튼 클릭 시 하단 시트를 표시
        ImageButton dialogButton = findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

        // changeText 클릭 이벤트 핸들러
        TextView changeText = bottomSheetDialog.findViewById(R.id.changeText);
        changeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이동하려는 화면으로 이동하는 코드를 여기에 추가
                //Intent intent = new Intent(GoalDetailActivity.this, 목표수정.class);
                //startActivity(intent);
            }
        });
        // deleteText 클릭 이벤트 핸들러
        TextView deleteText = bottomSheetDialog.findViewById(R.id.deleteText);
        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 삭제 시 데이터삭제, 창 닫고 오늘목표 페이지로 이동
                // 인텐트에서 목표 키 가져오기
                Intent receivedIntent = getIntent();
                String goalKey = receivedIntent.getStringExtra("goalKey");

                // Firebase 데이터베이스에서 목표 항목에 대한 참조
                DatabaseReference goalReference = database.getReference("AllGoalItem").child(goalKey);

                // 데이터베이스에서 목표 항목 삭제
                goalReference.removeValue();

                // 하단 시트 다이얼로그 닫기
                bottomSheetDialog.dismiss();

                // 삭제 후 이전 활동으로 이동 (또는 삭제 후에 원하는 위치로 이동하는 코드를 사용하세요)
                Intent newIntent = new Intent(GoalDetailActivity.this, GoalActivity.class);
                startActivity(newIntent);
            }
        });



    } //oncreate

    private int calculateDayCount(String startDateString, String endDateString) {
        if (startDateString == null || endDateString == null) {
            return -1; // null인 경우 에러 처리를 수행하거나 -1을 반환합니다.
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
        try {
            Date startDate = sdf.parse(startDateString);
            Date endDate = sdf.parse(endDateString);

            long diffInMilliseconds = endDate.getTime() - startDate.getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMilliseconds);

            return (int) diffInDays + 1;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // 날짜 형식이 잘못된 경우 에러 처리를 수행하거나 -1을 반환합니다.
        }
    }

    // 추가될 메소드
    private void updateUIWithProgress(int progressValue) {
        progressBar.setProgress(progressValue);
        progressText.setText(progressValue + "%");
    }
}
