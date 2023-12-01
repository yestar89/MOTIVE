package com.example.motive_v1.Mypage;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motive_v1.R;

public class Mypgask extends AppCompatActivity {
    ImageButton backbtnask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypgin_ask);


        backbtnask = findViewById(R.id.backbtnask);
        backbtnask.setOnClickListener(new View.OnClickListener() {
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
