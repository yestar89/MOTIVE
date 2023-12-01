package com.example.motive_v1.Mypage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.motive_v1.Goal.Goal;
import com.example.motive_v1.Goal.GoalAdapter;
import com.example.motive_v1.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class activity_mypg_anal extends AppCompatActivity {

    private RecyclerView recyclerView;
    ArrayList<Goal> goalList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypg_anal);

        recyclerView = findViewById(R.id.analtime);
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); // 리사이클러뷰 역순
        layoutManager.setStackFromEnd(true); // 리사이클러뷰 역순
        recyclerView.setLayoutManager(layoutManager);

        goalList = new ArrayList<>();


        analAdapter adapter = new analAdapter(goalList, activity_mypg_anal.this);
        recyclerView.setAdapter(adapter);



        ImageButton backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_mypg_anal.this, MypageActivity.class);
                startActivity(intent);
            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("AllGoalItem");


// 모든 목표
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                goalList.clear(); // 기존 데이터를 지웁니다.
//                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    Goal goal = postSnapshot.getValue(Goal.class);
//                    goalList.add(goal); // 새로운 데이터를 추가합니다.
//                }
//                adapter.notifyDataSetChanged(); // 어댑터에 데이터가 변경되었음을 알립니다.
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // 에러 처리
//            }
//        });

        // 이번 달 모든 목표
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                goalList.clear(); // 기존 데이터를 지웁니다.
                int currentMonth = LocalDate.now().getMonthValue(); // 현재 달을 얻습니다.
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Goal goal = postSnapshot.getValue(Goal.class);
                    String selectedDate = goal.getSelectedDate();
                    String monthYear = selectedDate.split("~")[1].trim().substring(0, 7);  // "2023.11"
                    String[] parts = monthYear.split("\\.");
                    String monthNumber = parts[1];
                    int goalMonth = Integer.parseInt(monthNumber); // 목표의 달을 얻습니다.
                    if(goalMonth == currentMonth) {
                        goalList.add(goal); // 새로운 데이터를 추가합니다.
                    }
                }
                adapter.notifyDataSetChanged(); // 어댑터에 데이터가 변경되었음을 알립니다.
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 에러 처리
            }
        });



        // 이번 달 0분 이상 목표
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                goalList.clear(); // 기존 데이터를 지웁니다.
//                int currentMonth = LocalDate.now().getMonthValue(); // 현재 달을 얻습니다.
//                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                    Goal goal = postSnapshot.getValue(Goal.class);
//                    String selectedDate = goal.getSelectedDate();
//                    String monthYear = selectedDate.split("~")[1].trim().substring(0, 7);  // "2023.11"
//                    String[] parts = monthYear.split("\\.");
//                    String monthNumber = parts[1];
//                    int goalMonth = Integer.parseInt(monthNumber); // 목표의 달을 얻습니다.
//                    if(goal.getTimeInSeconds() > 0 && goalMonth == currentMonth) {
//                        goalList.add(goal); // 새로운 데이터를 추가합니다.
//                    }
//                }
//                adapter.notifyDataSetChanged(); // 어댑터에 데이터가 변경되었음을 알립니다.
//            }

//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // 에러 처리
//            }
//        });




        HashMap<String, HashMap<String, GoalData>> dataByMonth = new HashMap<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Goal goal = postSnapshot.getValue(Goal.class);

                    // 목표 설정 기간에서 마지막 월을 추출
                    String selectedDate = goal.getSelectedDate();
                    String monthYear = selectedDate.split("~")[1].trim().substring(0, 7);  // "2023.11"

                    String[] parts = monthYear.split("\\.");
                    String year = parts[0];
                    String monthNumber = parts[1];

                    // 정수로 변환된 월을 문자열로 변환하여 "월"을 추가
                    String month = Integer.parseInt(monthNumber) + "월";

                    // 해당 월의 데이터를 가져오거나 새로 생성합니다.
                    HashMap<String, GoalData> dataForMonth = dataByMonth.get(month);
                    if (dataForMonth == null) {
                        dataForMonth = new HashMap<>();
                        dataByMonth.put(month, dataForMonth);
                    }


                    // 해당 목표의 분야 데이터를 가져오거나 새로 생성합니다.
                    String goalText = goal.getGoalText();  // "운동" 또는 "공부"
                    GoalData goalData = dataForMonth.get(goalText);
                    if (goalData == null) {
                        goalData = new GoalData();
                        dataForMonth.put(goalText, goalData);
                    }

                    // 달성률을 추가합니다.
                    goalData.addProgress(goal.getProgress());
                }

                // 이제 dataByMonth에는 월별, 분야별 달성률 데이터가 들어있습니다.
                // 이 데이터를 이용하여 그래프를 그립니다.
                drawChart(dataByMonth);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 에러 처리
            }
        });







    }

    public void drawChart(HashMap<String, HashMap<String, GoalData>> dataByMonth) {
        BarChart chart = findViewById(R.id.chart);

        List<String> months = new ArrayList<>(dataByMonth.keySet());
//        Collections.sort(months);
        Collections.sort(months, (s1, s2) -> {
            // 월 이름을 숫자로 변환하여 비교
            int month1 = Integer.parseInt(s1.replace("월", ""));
            int month2 = Integer.parseInt(s2.replace("월", ""));
            return Integer.compare(month1, month2);
        });
        int currentMonthIndex = months.size() - 1;

        // 현재 월을 기준으로 최근 3개월을 유지합니다.
        List<String> displayedMonths = new ArrayList<>();
        for (int i = currentMonthIndex; i >= Math.max(0, currentMonthIndex - 2); i--) {
            displayedMonths.add(months.get(i));
        }

        // 역순으로 정렬된 월 목록을 다시 정렬
        Collections.reverse(displayedMonths);

        // Collections.sort(months);
//        Collections.reverse(months); // 월 역순으로 정렬

        // 각 분야별 데이터를 준비합니다.
        List<BarEntry> entries1 = new ArrayList<>();
        List<BarEntry> entries2 = new ArrayList<>();

        for (int i = 0; i < displayedMonths.size(); i++) {
            String month = displayedMonths.get(i);
            HashMap<String, GoalData> dataForMonth = dataByMonth.get(month);

//            float xValue1 = i * 3;  // 각 막대의 X 좌표를 수정
//            float xValue2 = i * 3 + 1;  // 각 막대의 X 좌표를 수정

            entries1.add(new BarEntry(i * 2, dataForMonth.get("운동").getAverageProgress()));
            entries2.add(new BarEntry(i * 2 + 1, dataForMonth.get("공부").getAverageProgress()));
        }

        // BarDataSet을 만듭니다.
        BarDataSet set1 = new BarDataSet(entries1, "운동");
        set1.setColor(Color.parseColor("#F0EEFE")); // 빨간색으로 변경
        BarDataSet set2 = new BarDataSet(entries2, "공부");
        set2.setColor(Color.parseColor("#FDEAEB")); // 파란색으로 변경

        // 막대바 경계선 유무
        set1.setBarBorderWidth(0.2f);  // 조절 필요
        set2.setBarBorderWidth(0.2f);  // 조절 필요

        // BarData를 만들고, 이를 차트에 설정합니다.
        BarData data = new BarData(set1, set2);
        float groupSpace = 1f;  // 막대 그룹 간의 간격을 조절합니다.
        float barWidth = 0.5f;   // 각 막대의 너비를 조절합니다.
        data.setBarWidth(barWidth);
        chart.setData(data);
        chart.groupBars(0, groupSpace, 0f);  // 0.1f는 막대 사이의 간격을 조절합니다.

        //추가
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Display the value as an integer with a percentage sign
                return Math.round(value) + "%";
            }
        });

        // X축 설정
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(displayedMonths));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(2f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);  // 그리드 라인 숨김
        xAxis.setDrawAxisLine(false);   // X축 라인 숨김
        xAxis.setDrawLabels(true);     // X축 레이블 표시 여부
        xAxis.setCenterAxisLabels(true);

        // Y축 설정
        YAxis yAxis = chart.getAxisLeft(); // 왼쪽 Y축을 가져옵니다.
        yAxis.setDrawGridLines(false); // 그리드 라인 숨김
        yAxis.setDrawAxisLine(false); // Y축 라인 숨김
        chart.setScaleEnabled(false);

        chart.getAxisRight().setEnabled(false); // 오른쪽 Y축 비활성화
        chart.getAxisLeft().setEnabled(false);  // 왼쪽 Y축 비활성화

        chart.animateY(1000);

        chart.getAxisLeft().setAxisMaximum(100f);

        chart.getDescription().setEnabled(false);  // Description Label 텍스트 숨김

        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value / 2;  // 수정된 부분
                if (index < 0 || index >= displayedMonths.size())
                    return "";
                else
                    return displayedMonths.get(index);
            }
        });

    }




    public class GoalData {
        private int totalProgress;
        private int count;

        public void addProgress(int progress) {
            totalProgress += progress;
            count++;
        }

        public float getAverageProgress() {
            if (count == 0) {
                return 0;
            } else {
                return totalProgress / (float) count;
            }
        }
    }

}