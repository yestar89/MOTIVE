package com.example.motive_v1.Watch;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.motive_v1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class WatchAlarmActivity extends AppCompatActivity {

    private ImageButton backbtn;
    private DatabaseReference settingsRef;
    private Switch notificationSwitch;
    private Switch doNotDisturbSwitch;
    private Switch viewAllSwitch;
    private TextView selectedTimeTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_alarm);

        // 뒤로가기 버튼
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WatchAlarmActivity.this, WatchActivity.class);
                startActivity(intent);
            }
        });

        // 파이어베이스 연결
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        settingsRef = database.getReference("watch");

        notificationSwitch = findViewById(R.id.switch1);
        doNotDisturbSwitch = findViewById(R.id.switch3);
        viewAllSwitch = findViewById(R.id.switch2);

        // Firebase에서 값 가져오기
        settingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean notificationSetting = dataSnapshot.child("notification").getValue(Boolean.class);
                Boolean doNotDisturbSetting = dataSnapshot.child("doNotDisturb").getValue(Boolean.class);
                Boolean viewAllSetting = dataSnapshot.child("viewAll").getValue(Boolean.class);

                if (notificationSetting != null && doNotDisturbSetting != null && viewAllSetting != null) {
                    notificationSwitch.setChecked(notificationSetting);
                    doNotDisturbSwitch.setChecked(doNotDisturbSetting);
                    viewAllSwitch.setChecked(viewAllSetting);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터베이스 읽기 중 오류가 발생한 경우 처리
            }
        });

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsRef.child("notification").setValue(isChecked);
            }
        });

        doNotDisturbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsRef.child("doNotDisturb").setValue(isChecked);

                if (isChecked) {
                    showTimeRangePickerDialog();
                } else {
                    // 방해 금지 모드가 꺼질 때의 처리 (Firebase에 저장 등)
                }
            }
        });

        viewAllSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsRef.child("viewAll").setValue(isChecked);
            }
        });

        // TextView 초기화
        selectedTimeTextView = findViewById(R.id.selectedTimeTextView);
    }

    // 시간 범위 설정 다이얼로그를 띄우는 메소드
    private void showTimeRangePickerDialog() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int startHourOfDay, int startMinute) {
                        String startTime = String.format("%02d:%02d", startHourOfDay, startMinute);
                        showEndTimePickerDialog(startTime);
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void showEndTimePickerDialog(final String startTime) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int endHourOfDay, int endMinute) {
                        String endTime = String.format("%02d:%02d", endHourOfDay, endMinute);
                        selectedTimeTextView.setText("선택된 시간 범위: " + startTime + " - " + endTime);

                        // TODO: 선택된 시작 시간과 종료 시간을 Firebase에 저장하는 코드 추가
                        settingsRef.child("doNotDisturbTime").setValue(startTime + " - " + endTime);

                        // TODO: 선택된 시간 범위에 따라 알람을 설정/해제하는 코드 추가
                        // 예를 들어, setAlarm(startTime, endTime) 등의 메소드 호출
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }
    public void showTimePickerDialog(View view) {
        showTimeRangePickerDialog();
    }

}