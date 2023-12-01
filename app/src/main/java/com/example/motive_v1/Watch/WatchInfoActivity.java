package com.example.motive_v1.Watch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.motive_v1.R;

public class WatchInfoActivity extends AppCompatActivity {

    private ImageButton backbtn102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_info);

        backbtn102 = findViewById(R.id.backbtn102);
        backbtn102.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // backbtn102을 눌렀을 때 WatchGeneral.class로 이동하는 코드
                Intent intent = new Intent(WatchInfoActivity.this, WatchActivity.class);
                startActivity(intent);
            }
        });
    }
}