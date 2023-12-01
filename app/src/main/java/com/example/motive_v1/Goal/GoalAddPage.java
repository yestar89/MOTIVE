package com.example.motive_v1.Goal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.example.motive_v1.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class GoalAddPage extends AppCompatActivity {

    private EditText title;
    private TextView date;
    private TextView timeText;
    private Button exerciseButton;
    private Button studyButton;
    private Button addButton;
    private boolean isExerciseSelected = true; // 운동 버튼이 선택되었는지 여부를 나타내는 플래그
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private long endMillis;
    private long startMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_add);

        //EditText 초기화 추가
        title = findViewById(R.id.goalTitle);
        date = findViewById(R.id.date);

        // 글자 수 세는 TextView
        TextView characterCountTextView = findViewById(R.id.count);
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentLength = s.length();
                characterCountTextView.setText(currentLength + "/40");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 버튼 초기화
        exerciseButton = findViewById(R.id.exercise_btn);
        studyButton = findViewById(R.id.study_btn);

        // 공부 버튼 클릭 시
        studyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExerciseSelected = false;
                studyButton.setBackgroundResource(R.drawable.makebutton); // 선택된 상태의 드로어블을 적용
                exerciseButton.setBackgroundResource(R.drawable.makebutton2); // 선택되지 않은 상태의 드로어블을 적용
                // 텍스트 색상 변경
                studyButton.setTextColor(Color.parseColor("#FFFFFF")); // 선택된 상태일 때의 텍스트 색상
                exerciseButton.setTextColor(Color.parseColor("#999999")); // 선택되지 않은 상태일 때의 텍스트 색상
            }
        });

        // 운동 버튼 클릭 시
        exerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExerciseSelected = true;
                exerciseButton.setBackgroundResource(R.drawable.makebutton); // 선택된 상태의 드로어블을 적용
                studyButton.setBackgroundResource(R.drawable.makebutton2); // 선택되지 않은 상태의 드로어블을 적용
                // 텍스트 색상 변경
                exerciseButton.setTextColor(Color.parseColor("#FFFFFF")); // 선택된 상태일 때의 텍스트 색상
                studyButton.setTextColor(Color.parseColor("#999999")); // 선택되지 않은 상태일 때의 텍스트 색상
            }
        });

        // 목표 기간 설정 (캘린더 이미지 클릭 시 MaterialDatePicker 표시)
        ImageButton calendarButton = findViewById(R.id.calendarButton);
        TextView finalDateTextView1 = date;
        calendarButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0); // 시간을 0으로 설정하여 오늘의 시작 시간으로 설정
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                long today = calendar.getTimeInMillis();
                Log.d("Debug", "Today in milliseconds: " + today);

                CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
                constraintsBuilder.setStart(today); // 시작일로 오늘을 설정
                constraintsBuilder.setEnd(today + (365 * 24 * 60 * 60 * 1000L)); // 1년 후까지 선택 가능

                MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                builder.setTitleText("날짜 선택");
                builder.setTheme(R.style.CustomMaterialDatePickerTheme);
                builder.setCalendarConstraints(constraintsBuilder.build());

                MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

                picker.show(getSupportFragmentManager(), "DATE_PICKER");

                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {

                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        long startMillis = selection.first;
                        long endMillis = selection.second;

                        // startMillis와 endMillis 값을 설정
                        setStartMillis(startMillis);
                        setEndMillis(endMillis);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                        LocalDate startDate = Instant.ofEpochMilli(startMillis).atZone(ZoneId.systemDefault()).toLocalDate();
                        LocalDate endDate = Instant.ofEpochMilli(endMillis).atZone(ZoneId.systemDefault()).toLocalDate();
                        String selectedDate = startDate.format(formatter) + " ~ " + endDate.format(formatter);

                        // 선택한 기간을 TextView에 표시
                        finalDateTextView1.setText(startDate.format(formatter) + " ~ " + endDate.format(formatter));
                        Log.d("Debug", "startMillis: " + startMillis + ", endMillis: " + endMillis);
                    }

                    private void setEndMillis(long endMillis) {
                        GoalAddPage.this.endMillis = endMillis;
                        Log.d("Debug", "setEndMillis called with value: " + endMillis);
                    }

                    private void setStartMillis(long startMillis) {
                        GoalAddPage.this.startMillis = startMillis;
                        Log.d("Debug", "setStartMillis called with value: " + startMillis);
                    }
                });
            }
        });



        // Firebase 데이터베이스 초기화
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("AllGoalItem"); //데베 노드 이름

        // 저장 버튼 클릭 시
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 입력한 데이터 가져오기
                String exerciseType = isExerciseSelected ? "운동" : "공부"; // isExerciseSelected 값에 따라서 운동 또는 공부로 구분
                String title1 = title.getText().toString(); // EditText에서 목표 제목을 가져옴
                String selectedDate = date.getText().toString(); // TextView에서 선택한 날짜를 가져옴
                HashMap<String, Boolean> checkedDatesMap = initializeCheckedDatesMap(startMillis, endMillis);

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();


                // 데이터를 Firebase에 업로드
                String key = databaseReference.push().getKey(); // 데이터베이스에서 고유한 키 생성
                Goal goal = new Goal(key, exerciseType, title1, selectedDate, false, checkedDatesMap);
                goal.setUserId(userId);
                goal.setId(key);

                // startMillis와 endMillis 값을 Goal 객체에 설정
                goal.setStartMillis(startMillis);
                goal.setEndMillis(endMillis);

                databaseReference.child(key).setValue(goal);




                // 저장 후 필요한 작업
                // 데이터를 전달할 Intent 생성
                Intent intent = new Intent(GoalAddPage.this, GoalActivity.class);

                // TodayActivity 시작
                startActivity(intent);
                finish();
            }
            }
        });



        // 뒤로 가기 버튼
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private HashMap<String, Boolean> initializeCheckedDatesMap(long startMillis, long endMillis) {
        HashMap<String, Boolean> checkedDates = new HashMap<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startMillis);

        while (calendar.getTimeInMillis() <= endMillis) {
            String formattedDate = sdf.format(calendar.getTime());
            checkedDates.put(formattedDate, false);  // set false for each date
            calendar.add(Calendar.DAY_OF_MONTH, 1);  // move to the next day
        }

        return checkedDates;
    }
}
