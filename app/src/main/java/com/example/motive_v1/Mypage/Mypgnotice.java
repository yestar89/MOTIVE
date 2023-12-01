package com.example.motive_v1.Mypage;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motive_v1.R;

public class Mypgnotice extends AppCompatActivity {

    TextView tv1;
    ImageButton backbtnmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypgin_notice);

        tv1 = findViewById(R.id.tv1);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 클릭 시 할 작업 추가
            }
        });

        backbtnmp = findViewById(R.id.backbtnmp);
        backbtnmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // 현재 액티비티를 종료합니다.
    }
}
