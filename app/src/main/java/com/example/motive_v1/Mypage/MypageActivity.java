package com.example.motive_v1.Mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.motive_v1.Club.ClubActivity;
import com.example.motive_v1.Goal.GoalActivity;
import com.example.motive_v1.Home.HomeActivity;
import com.example.motive_v1.R;
import com.example.motive_v1.Watch.WatchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class MypageActivity extends AppCompatActivity {
    CircleImageView image_profile;
    TextView name;
    BottomNavigationView bottomNavigationView;
    private TextView totalTimeTextView;
    TextView tV_time;
    TextView analTime; // 갓생시간 분석

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        analTime = findViewById(R.id.analTime);

        analTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MypageActivity.this, activity_mypg_anal.class);
                startActivity(intent);
            }
        });

        // 사용자 사진, 닉네임, 갓생시간
        image_profile = findViewById(R.id.image_profile);
        name = findViewById(R.id.tV_name);
        tV_time = findViewById(R.id.tV_time);

        totalTimeTextView = findViewById(R.id.tV_time);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        // 현재 로그인한 사용자 가져오기
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // 현재 로그인한 사용자의 UID 가져오기
            String currentUserId = currentUser.getUid();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getInstance().getReference().child("Users").child(currentUserId);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.hasChild("profileImageUrl") && snapshot.hasChild("username")) {
                            String profile = snapshot.child("profileImageUrl").getValue().toString();
                            String usernameText = snapshot.child("username").getValue().toString();

                            Glide.with(MypageActivity.this).load(profile).into(image_profile);
                            name.setText(usernameText);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if (currentUser != null) {
                String userId = currentUser.getUid();

                // 데이터베이스 참조 설정 ("AllGoalItem" 경로로 변경)
                DatabaseReference databaseReference = database.getReference("AllGoalItem");


                // "AllGoalItem" 경로에서 데이터 읽기
                databaseReference.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("AllGoalData", "DataSnapshot: " + dataSnapshot.toString());
                        long totalSeconds = 0; // 시간 합산을 위한 변수

                        for (DataSnapshot goalSnapshot : dataSnapshot.getChildren()) {
                            // 각 데이터에서 "timeInSeconds" 값을 가져와 합산합니다.
                            Long timeInSeconds = goalSnapshot.child("timeInSeconds").getValue(Long.class);
                            if (timeInSeconds != null) {
                                totalSeconds += timeInSeconds;
                            }
                        }

                        // totalSeconds를 시간 형식으로 변환 (예: 시, 분)
                        long hours = totalSeconds / 3600;
                        long minutes = (totalSeconds % 3600) / 60;


                        // 시간과 분을 0시간 0분 형식으로 표시
                        String formattedTime = String.format("%d시간 %d분", hours, minutes);

                        // "totalTime" TextView에 합산된 시간을 표시합니다.
                        TextView totalTimeTextView = findViewById(R.id.tV_time);
                        totalTimeTextView.setText(formattedTime);

                        //프로그레스바
                        ProgressBar progressBar1 = findViewById(R.id.progressBar1);

                        // totalTime을 분 단위로 계산합니다 (totalTime이 시간 단위로 저장되어 있다고 가정합니다).
                        long totalTimeInMinutes = (hours * 60) + minutes; // totalTime을 분 단위로 변환합니다.
                        int progress = (int) (totalTimeInMinutes * 100 / (100 * 60)); // 100시간을 기준으로 진행 상태를 계산합니다.

                        // ProgressBar에 진행 상태를 설정합니다.
                        progressBar1.setProgress(progress);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // 데이터베이스 읽기 작업이 실패한 경우 호출됩니다.
                        Log.e("FirebaseError", "Error: " + databaseError.getMessage());
                    }
                });


                // 내 정보 상세조회로 넘어가기
                ImageButton tV_profileedit = findViewById(R.id.tV_profileedit);
                tV_profileedit.setOnClickListener(view -> {
                    Intent intent=new Intent(getApplicationContext(), MypgInformationActivity.class);
                    startActivity(intent);
                });

                // 공지사항 페이지
                RelativeLayout noticeLayout = findViewById(R.id.relativeLayout2_1);

                noticeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MypageActivity.this, Mypgnotice.class);
                        startActivity(intent);
                    }
                });

                // 문의하기 페이지
                RelativeLayout askLayout = findViewById(R.id.relativeLayout2_4);

                askLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MypageActivity.this, Mypgask.class);
                        startActivity(intent);
                    }
                });



                bottomNavigationView = findViewById(R.id.bottom_nav);
                bottomNavigationView.setSelectedItemId(R.id.menu_profile);
                bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {


                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent intent = null;;

                        switch (item.getItemId()) {
                            case R.id.menu_home:
                                // 홈 아이템 클릭 시 HomeActivity로 화면 전환
                                intent = new Intent(MypageActivity.this, HomeActivity.class);
                                break;
                            case R.id.menu_club:
                                // 클럽 아이템 클릭 시 ClubActivity로 화면 전환
                                intent = new Intent(MypageActivity.this, ClubActivity.class);
                                break;
                            case R.id.menu_goal:
                                // 목표 아이템 클릭 시 GoalActivity로 화면 전환
                                intent = new Intent(MypageActivity.this, GoalActivity.class);
                                break;
                            case R.id.menu_watch:
                                // 워치 아이템 클릭 시 WatchActivity로 화면 전환
                                intent = new Intent(MypageActivity.this, WatchActivity.class);
                                break;
                            case R.id.menu_profile:
                                // 마이페이지 아이템 클릭 시 ProfileActivity로 화면 전환
                                intent = new Intent(MypageActivity.this, MypageActivity.class);
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
        }
    }
}