package com.example.motive_v1.Goal;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_v1.Club.ClubActivity;
import com.example.motive_v1.Home.HomeActivity;
import com.example.motive_v1.Mypage.MypageActivity;
import com.example.motive_v1.Watch.WatchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.motive_v1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GoalActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private RecyclerView recyclerView;
    private AllGoalAdapter adapter;
    private ArrayList<Goal> gItems;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal);



        //오늘목표 이동
        Button goalTodayButton = findViewById(R.id.goal_today_btn);
        goalTodayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoalActivity.this, TodayActivity.class);
                startActivity(intent);

            }
        });


        //목표개설 페이지 이동
        ImageButton plusButton = findViewById(R.id.plusButton);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GoalAddPage.class);
                startActivity(intent);
            }
        });


        // Firebase 초기화
        FirebaseApp.initializeApp(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); // 리사이클러뷰 역순
        layoutManager.setStackFromEnd(true); // 리사이클러뷰 역순
        recyclerView.setLayoutManager(layoutManager);

        gItems = new ArrayList<>();


        // 어댑터 초기화 및 RecyclerView에 연결
        adapter = new AllGoalAdapter(gItems, this);
        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("AllGoalItem"); //DB 연결

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("FirebaseData", "DataSnapshot: " + dataSnapshot.toString());
                // 데이터베이스에서 가져온 데이터를 초기화
                gItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Goal item = snapshot.getValue(Goal.class);
                    item.setKey(snapshot.getKey()); // Firebase 키를 설정
                    gItems.add(item); // 담은 데이터를 배열리스트에 넣고 뷰로 보낼 준비
                }
                // 어댑터에게 데이터가 변경되었음을 알립니다.
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });


        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.menu_goal);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;;

                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // 홈 아이템 클릭 시 HomeActivity로 화면 전환
                        intent = new Intent(GoalActivity.this, HomeActivity.class);
                        break;
                    case R.id.menu_club:
                        // 클럽 아이템 클릭 시 ClubActivity로 화면 전환
                        intent = new Intent(GoalActivity.this, ClubActivity.class);
                        break;
                    case R.id.menu_goal:
                        // 목표 아이템 클릭 시 GoalActivity로 화면 전환
                        intent = new Intent(GoalActivity.this, GoalActivity.class);
                        break;
                    case R.id.menu_watch:
                        // 워치 아이템 클릭 시 WatchActivity로 화면 전환
                        intent = new Intent(GoalActivity.this, WatchActivity.class);
                        break;
                    case R.id.menu_profile:
                        // 마이페이지 아이템 클릭 시 ProfileActivity로 화면 전환
                        intent = new Intent(GoalActivity.this, MypageActivity.class);
                        break;
                }

                // 액티비티 전환
                if (intent != null) {
                    startActivity(intent);
                }

                return true;
            }
        });

    }

    public void onItemClick(View view, int position) {
        // 클릭 이벤트 처리
        Intent intent = new Intent(this, GoalDetailActivity.class);

        String dDayText = gItems.get(position).getdDayText();
        String selectedDate = gItems.get(position).getSelectedDate();
        String title = gItems.get(position).getTitle();
        String goalText = gItems.get(position).getGoalText();
        String goalKey = gItems.get(position).getKey();

        intent.putExtra("dDayText", dDayText);
        intent.putExtra("selectedDate", selectedDate);
        intent.putExtra("title", title);
        intent.putExtra("goalText", goalText);
        intent.putExtra("goalKey", goalKey);

        startActivity(intent);

    }

}