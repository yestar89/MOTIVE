package com.example.motive_v1.Home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_v1.Club.ClubActivity;
import com.example.motive_v1.Goal.Goal;
import com.example.motive_v1.Goal.GoalActivity;
import com.example.motive_v1.Goal.GoalAdapter;
//import com.example.motive_v1.Home.weeklyAdapter;
import com.example.motive_v1.Goal.GoalDetailActivity;
import com.example.motive_v1.Goal.TodayActivity;
import com.example.motive_v1.Home.HomeUserAdapter;
import com.example.motive_v1.Home.weeklyAdapter;
import com.example.motive_v1.Mypage.MypageActivity;
import com.example.motive_v1.R;
import com.example.motive_v1.User.User;
import com.example.motive_v1.Watch.WatchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    TextView logout;
    private FirebaseAuth mAuth;

    private boolean isClicked = false;
    private RecyclerView recyclerView;
    private RecyclerView god_recyclerview;
    private RecyclerView user_recyclerview;
    private GoalAdapter adapter;
    private HomeUserAdapter userAdapter;
    private com.example.motive_v1.Home.weeklyAdapter weeklyAdapter;
    private ArrayList<Goal> gItems;
    private ArrayList<User> uItems;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference usersReference;
    private TextView totalTimeTextView;
    FirebaseUser firebaseUser;
    LocalDate selectedDate= LocalDate.now(); // 오늘 날짜 설정;
    GoalAdapter.OnCheckboxClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mAuth = FirebaseAuth.getInstance();

        // > 버튼 클릭 시 목표창 이동
        ImageButton moveGoalButton = findViewById(R.id.movegoal);
        moveGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, TodayActivity.class);
                startActivity(intent);
            }
        });


        // "totalTime" TextView 초기화
        totalTimeTextView = findViewById(R.id.totalTime);


        // 사용자 리사이클러뷰 설정
        user_recyclerview = findViewById(R.id.home_user);
        user_recyclerview.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
        user_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        // 금주의 갓생러 리사이클러뷰 설정
        god_recyclerview = findViewById(R.id.god_recyclerview);
        god_recyclerview.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
        god_recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        // 유영) 오늘의 목표 리사이클러뷰 완성되면 주석 풀어주시길
        // 목표 리사이클러뷰 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); // 리사이클러뷰 역순
        layoutManager.setStackFromEnd(true); // 리사이클러뷰 역순
        recyclerView.setLayoutManager(layoutManager);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // 데이터 추가
        gItems = new ArrayList<>();
        uItems = new ArrayList<>();

        // 유저 어댑터 초기화 및 리사이클러뷰 연결
        userAdapter = new HomeUserAdapter(HomeActivity.this, uItems);
        user_recyclerview.setAdapter(userAdapter);

        // 여기는 '오늘의 목표 어댑터'입니다.
        // 어댑터 초기화 및 RecyclerView에 연결
        listener = (goal, date,  newCheckState, checkbox) -> {
            // 여기에 체크박스가 클릭됐을 때 실행할 코드를 작성하세요.
        };

        // GoalAdapter 초기화 및 RecyclerView에 연결
        adapter = new GoalAdapter(gItems, this, selectedDate, new GoalAdapter.OnCheckboxClickListener() {
            @Override
            public void onCheckboxClick(Goal goal, String date, boolean newCheckState, ImageView checkbox) {
                // 여기에 체크박스 클릭 이벤트 처리를 추가할 수 있습니다.
            }
        });
        recyclerView.setAdapter(adapter);


        // FirebaseApp 초기화 (이미 코드에 포함되어 있다면 생략 가능)
        FirebaseApp.initializeApp(this);

        // Firebase Database 인스턴스 가져오기
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // "Users" 참조 가져오기
        usersReference = database.getReference("Users");

        // 사용자 정보
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // 여기에서 userId를 사용합니다.

            databaseReference = database.getReference("Users").child(userId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    uItems.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        uItems.add(user);
                        Log.d("uItems", "User ID: " + user.getUserId() + ", Username: " + user.getUsername());

                    }

                    userAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        // ys
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
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
                    TextView totalTimeTextView = findViewById(R.id.totalTime);
                    totalTimeTextView.setText(formattedTime);

                    // ProgressBar 뷰를 찾습니다.
                    ProgressBar progressBar1 = findViewById(R.id.progressBar1);

                    // 사용자의 totalTime을 분 단위로 계산합니다 (totalTime이 시간 단위로 저장되어 있다고 가정합니다).

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
        }
        // ys
        // 금주의 갓생러
        databaseReference = database.getReference("AllGoalItem");
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("GodLifeData", "DataSnapshot: " + dataSnapshot.toString());
                Map<String, Map<String, Integer>> totalTimeByUserAndCategory = new HashMap<>();

                for (DataSnapshot goalSnapshot : dataSnapshot.getChildren()) {
                    String goalText = goalSnapshot.child("goalText").getValue(String.class);
                    Integer timeInSeconds = goalSnapshot.child("timeInSeconds").getValue(Integer.class);

                    if (goalText != null && timeInSeconds != null) {
                        String userId = goalSnapshot.child("userId").getValue(String.class);

                        if (userId != null) {
                            if (!totalTimeByUserAndCategory.containsKey(userId)) {
                                totalTimeByUserAndCategory.put(userId, new HashMap<>());
                            }

                            Map<String, Integer> userMap = totalTimeByUserAndCategory.get(userId);

                            int previousTotalTime = userMap.getOrDefault(goalText, 0);
                            userMap.put(goalText, previousTotalTime + timeInSeconds);
                        }
                    }
                }


                // 갓생러 어댑터 초기화 및 리사이클러뷰 연결
                weeklyAdapter = new weeklyAdapter(HomeActivity.this, uItems, gItems);
                god_recyclerview.setAdapter(weeklyAdapter);
                //주석 uItems.clear(); // 기존에 저장된 항목을 모두 제거
                Map<String, User> maxUserByCategory = new HashMap<>();
                AtomicInteger loadedUserCount = new AtomicInteger();


                for (Map.Entry<String, Map<String,Integer>> entry : totalTimeByUserAndCategory.entrySet()) {

                    String userId=entry.getKey();

                    usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            User user=dataSnapshot.getValue(User.class);

                            if (user != null) {
                                String goalText=entry.getValue().entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
                                int totalTime=entry.getValue().get(goalText);

                                user.setGoalText(goalText);
                                user.setTotalTime(totalTime);

                                // 해당 카테고리의 기존 최대 달성시간과 비교하여 업데이트
                                if (!maxUserByCategory.containsKey(goalText) || totalTime > maxUserByCategory.get(goalText).getTotalTime()) {
                                    maxUserByCategory.put(goalText, user);
                                }

                                long totalTimeInSeconds = user.getTotalTime(); // totalTime을 가져옴 (단위: 초)

                                // totalTimeInSeconds를 시간과 분으로 변환
                                long hours = totalTimeInSeconds / 3600; // 1시간은 3600초
                                long minutes = (totalTimeInSeconds % 3600) / 60;



                            }


                            // 모든 사용자 정보가 로드되었으면 UI 갱신 요청하기
                            if (loadedUserCount.incrementAndGet() == totalTimeByUserAndCategory.size()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        uItems.clear();
                                        uItems.addAll(maxUserByCategory.values());
                                        weeklyAdapter.setUserList(uItems);
                                        weeklyAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // 사용자 정보를 가져오는 동안 오류 발생 시 처리
                            Log.e("FirebaseError", "Error fetching user data: " + databaseError.getMessage());
                        }

                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터베이스 읽기 작업이 실패한 경우 호출됩니다.
                Log.e("FirebaseError", "Error: " + databaseError.getMessage());
            }
        });



        //오늘목표 연결
        ArrayList<Goal> goals = new ArrayList<>();

        databaseReference = database.getReference("AllGoalItem");

        selectedDate = LocalDate.now(); // 오늘 날짜 설정

        // Firebase 데이터베이스에서 목표 데이터 가져오기
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gItems.clear(); // 기존 데이터 초기화

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Goal goal = snapshot.getValue(Goal.class);
                    if (goal != null) {
                        gItems.add(goal);

                        // 여기에서 checkedDates 업데이트
                    }
                }

                // 오늘 날짜에 해당하는 목표 필터링
                ArrayList<Goal> todayGoals = filterGoalsByDate(selectedDate);

                // 어댑터에 오늘의 목표 목록 설정 및 업데이트
                adapter.setGoals(todayGoals);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Firebase에서 데이터 로드 중 오류 발생: " + databaseError.getMessage());
            }
        });


        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.menu_home);

//        logout = findViewById(R.id.goSign);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;

                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // 홈 아이템 클릭 시 HomeActivity로 화면 전환
                        intent = new Intent(HomeActivity.this, HomeActivity.class);
                        break;
                    case R.id.menu_club:
                        // 클럽 아이템 클릭 시 ClubActivity로 화면 전환
                        intent = new Intent(HomeActivity.this, ClubActivity.class);
                        break;
                    case R.id.menu_goal:
                        // 목표 아이템 클릭 시 GoalActivity로 화면 전환
                        intent = new Intent(HomeActivity.this, GoalActivity.class);
                        break;
                    case R.id.menu_watch:
                        // 워치 아이템 클릭 시 WatchActivity로 화면 전환
                        intent = new Intent(HomeActivity.this, WatchActivity.class);
                        break;
                    case R.id.menu_profile:
                        // 마이페이지 아이템 클릭 시 ProfileActivity로 화면 전환
                        intent = new Intent(HomeActivity.this, MypageActivity.class);
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

    // 오늘 날짜에 해당하는 목표를 필터링하는 메서드
    private ArrayList<Goal> filterGoalsByDate(LocalDate date) {
        ArrayList<Goal> filteredGoals = new ArrayList<>();

        for (Goal goal : gItems) {
            String dateRangeString = goal.getSelectedDate();

            if (dateRangeString != null) {
                String[] dateRange = dateRangeString.split(" ~ ");
                if (dateRange.length == 2) {
                    LocalDate startDateGoal = LocalDate.parse(dateRange[0], DateTimeFormatter.ofPattern("yyyy.MM.dd"));
                    LocalDate endDateGoal = LocalDate.parse(dateRange[1], DateTimeFormatter.ofPattern("yyyy.MM.dd"));

                    if (!startDateGoal.isAfter(date) && !endDateGoal.isBefore(date)) {
                        filteredGoals.add(goal);
                    }
                }
            }
        }

        return filteredGoals;
    }

}
