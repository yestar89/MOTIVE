package com.example.motive_v1.Goal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_v1.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;

public class TodayAchievement extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GoalAdapter adapter;
    private ArrayList<Goal> goalList;
    private ProgressBar progressBar;
    private TextView percentageTextView;
    private LocalDate selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_today_achievement);

        //리사이클러뷰
        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<Goal> goalList = (ArrayList<Goal>) intent.getSerializableExtra("goalList");

            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);

            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(layoutManager);

//            // 어댑터 초기화 및 RecyclerView에 연결
//            adapter = new GoalAdapter(goalList, this, selectedDate);
//            recyclerView.setAdapter(adapter);
//
//            // progressBar와 percentageTextView 초기화
//            progressBar = findViewById(R.id.progressBar); // 이 부분은 XML 파일에 맞게 수정해야 합니다.
//            percentageTextView = findViewById(R.id.percentage); // 이 부분은 XML 파일에 맞게 수정해야 합니다.
//
//            // GoalAdapter 객체 생성할 때 progressBar와 percentageTextView 전달
//            adapter = new GoalAdapter(goalList, this, selectedDate);
//            recyclerView.setAdapter(adapter);

        }

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
                //Intent intent = new Intent(MainActivity.this, 목표수정.class);
                //startActivity(intent);
            }
        });

        // deleteText 클릭 이벤트 핸들러
        TextView deleteText = bottomSheetDialog.findViewById(R.id.deleteText);
        deleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 삭제 시 데이터삭제, 창 닫고 오늘목표 페이지로 이동
                //Intent intent = new Intent(MainActivity.this, 오늘목표.class);
                //startActivity(intent);
            }
        });

    } //oncreate


}