package com.example.motive_v1.Goal;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_v1.Club.ClubActivity;
import com.example.motive_v1.Goal.Goal;
import com.example.motive_v1.Goal.TodayAchievement;
import com.example.motive_v1.Home.HomeActivity;
import com.example.motive_v1.Mypage.MypageActivity;
import com.example.motive_v1.R;
import com.example.motive_v1.Watch.WatchActivity;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class TodayActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private TextView itemCountTextView;
    private MaterialCalendarView calendarView;
    private Button changeModeButton;
    private CalendarMode currentMode = CalendarMode.WEEKS;
    private Drawable selectedDrawable;
    private LocalDate selectedDate; // 선택한 날짜 저장 변수

    private RecyclerView recyclerView;
    private GoalAdapter adapter;
    private ArrayList<Goal> gItems;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ArrayList<Goal> filteredGoals; // 필터링 된 목표 목록을 저장
    private TextView todayTextView;
    private ImageButton moveAchievementBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_today);

        filteredGoals = new ArrayList<>(); // filteredGoals 변수 초기화

        //전체목표 이동
        Button goalAllButton = findViewById(R.id.goal_all_btn);
        goalAllButton.setOnClickListener(view -> {
            Intent intent = new Intent(TodayActivity.this, GoalActivity.class);
            startActivity(intent);
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
        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }
        adapter = new GoalAdapter(gItems, this, selectedDate, new GoalAdapter.OnCheckboxClickListener() {
            @Override
            public void onCheckboxClick(Goal goal, String date, boolean newCheckState, ImageView checkbox) {
                updateCheckboxStatus(goal, date, newCheckState, checkbox);
            }
        });


        recyclerView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("AllGoalItem"); //DB 연결
        // 데이터베이스에서 데이터를 가져오기 위해 ValueEventListener를 추가합니다.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 기존 데이터 지우기
                gItems.clear();
                // dataSnapshot에서 데이터를 반복하고 gItems에 추가합니다.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Goal goal = snapshot.getValue(Goal.class);
                    if (goal != null) {
                        gItems.add(goal);
                    }
                }
                // 필터링된 목표 표시
                filterGoalsByDate(selectedDate);

                // 데이터 로드 후 itemCountTextView 업데이트
                int itemCount = filteredGoals.size();
                itemCountTextView.setText(String.valueOf(itemCount));
                // 데이터 변경 감지
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 오류 처리 (필요한 경우)
                Log.e("FirebaseError", "Firebase에서 데이터 로드 중 오류 발생: " + databaseError.getMessage());
            }
        });

        if (selectedDate == null) {
            selectedDate = LocalDate.now();
        }

        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

        //캘린더
        calendarView = findViewById(R.id.calendarView);
        changeModeButton = findViewById(R.id.changeModeButton);

        selectedDrawable = getResources().getDrawable(R.drawable.todaydate);

        //calendarView의 표시 모드를 초기값인 CalendarMode.WEEKS로 설정
        calendarView.state().edit().setCalendarDisplayMode(currentMode).commit();

        //캘린더 - 사용자 정의 타이틀 포매터를 설정
        calendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                int year = day.getYear();
                int month = day.getMonth();
                return year + "년 " + month + "월";
            }
        });

        //현재 날짜 가져오기
        LocalDate today = LocalDate.now();
        //선택된 날 꾸미기
        setDrawableForDate(today);
        selectedDate = today; // 초기에 오늘 날짜로 설정

        // 오늘 날짜의 목표를 표시하기 위해 초기 필터링 실행
        filterGoalsByDate(today);
        todayTextView = findViewById(R.id.today);
        itemCountTextView = findViewById(R.id.itemCountTextView);

        // 필터링된 목표 목록의 개수를 계산하여 itemCountTextView에 설정
        int itemCount = filteredGoals.size();
        itemCountTextView.setText(String.valueOf(itemCount));

        // 날짜가 선택될 때 필터링 메서드 호출
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                LocalDate newSelectedDate = date.getDate();
                clearDecorators();

                if (!newSelectedDate.equals(selectedDate)) {
                    selectedDate = newSelectedDate; // 선택된 날짜를 업데이트합니다.

                    filterGoalsByDate(selectedDate); // 목표들을 필터링합니다.
                    adapter.setSelectedDate(selectedDate); // 어댑터에 선택된 날짜를 업데이트합니다.
                    adapter.setGoals(filteredGoals); // 어댑터에 필터링된 목표들을 설정합니다.

                    updateUIForSelectedDate(selectedDate); // UI를 업데이트합니다.

                }

                // 선택한 날짜에서 "dd일"만 추출하여 TextView에 표시
                String dayText;
                if (newSelectedDate.isEqual(LocalDate.now())) {
                    dayText = "오늘";
                } else {
                    dayText = newSelectedDate.format(DateTimeFormatter.ofPattern("dd일"));
                }
                todayTextView.setText(dayText);

                // 해당 날짜에 해당하는 목표 개수를 계산하여 itemCountTextView에 표시
                int itemCount = filteredGoals.size(); // 필터링된 목표 목록의 개수
                itemCountTextView.setText(String.valueOf(itemCount)); // 개수를 TextView에 설정

            }
            private void clearDecorators() {
                calendarView.removeDecorators();
            }

        });


        //버튼 클릭 시 달력 모드 변경 (주/월)
        changeModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMode == CalendarMode.WEEKS) {
                    currentMode = CalendarMode.MONTHS;
                    changeModeButton.setText("한달");
                } else {
                    currentMode = CalendarMode.WEEKS;
                    changeModeButton.setText("일주일");
                }
                calendarView.state().edit()
                        .setCalendarDisplayMode(currentMode)
                        .commit();
            }
        });

        calendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));
        calendarView.setHeaderTextAppearance(R.style.CalendarHeaderStyle);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.menu_goal);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                ;

                switch (item.getItemId()) {
                    case R.id.menu_home:
                        // 홈 아이템 클릭 시 HomeActivity로 화면 전환
                        intent = new Intent(TodayActivity.this, HomeActivity.class);
                        break;
                    case R.id.menu_club:
                        // 클럽 아이템 클릭 시 ClubActivity로 화면 전환
                        intent = new Intent(TodayActivity.this, ClubActivity.class);
                        break;
                    case R.id.menu_goal:
                        // 목표 아이템 클릭 시 GoalActivity로 화면 전환
                        intent = new Intent(TodayActivity.this, GoalActivity.class);
                        break;
                    case R.id.menu_watch:
                        // 워치 아이템 클릭 시 WatchActivity로 화면 전환
                        intent = new Intent(TodayActivity.this, WatchActivity.class);
                        break;
                    case R.id.menu_profile:
                        // 마이페이지 아이템 클릭 시 ProfileActivity로 화면 전환
                        intent = new Intent(TodayActivity.this, MypageActivity.class);
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

    // 선택된 날짜 범위에 해당하는 목표를 필터링하여 어댑터에 설정하는 메서드
    private void filterGoalsByDate(LocalDate date) {
        filteredGoals.clear();
        for (Goal goal : gItems) {
            // Goal 클래스에 저장된 날짜 문자열을 파싱합니다.
            String dateRangeString = goal.getSelectedDate();
            if (dateRangeString != null) {
                String[] dateRange = dateRangeString.split(" ~ ");
                if (dateRange.length == 2) {
                    LocalDate startDateGoal = LocalDate.parse(dateRange[0], DateTimeFormatter.ofPattern("yyyy.MM.dd"));
                    LocalDate endDateGoal = LocalDate.parse(dateRange[1], DateTimeFormatter.ofPattern("yyyy.MM.dd"));
                    // 선택된 날짜가 목표 날짜 범위 안에 있는지 확인합니다.
                    if (!date.isBefore(startDateGoal) && !date.isAfter(endDateGoal)) {
                        filteredGoals.add(goal);
                    }
                }
            }
        }
        // 어댑터에 필터링된 목표 목록을 설정합니다.
        adapter.setGoals(filteredGoals);
        adapter.notifyDataSetChanged();
    }

    private void updateUIForSelectedDate(LocalDate date) {
        // 선택된 날짜에 해당하는 목표의 개수를 업데이트합니다.
        int itemCount = filteredGoals.size();
        itemCountTextView.setText(String.format(Locale.getDefault(), "%d개의 목표", itemCount));

        // 날짜 표시 형식을 설정합니다.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.getDefault());
        String formattedDate = date.format(formatter);
        todayTextView.setText(formattedDate);

        // 필요한 다른 UI 구성요소들을 업데이트하는 코드를 추가합니다.
        adapter.notifyDataSetChanged(); // RecyclerView를 업데이트합니다.
    }

    //캘린더 - 지정된 날짜 달력 데코레이터 추가
    private void setDrawableForDate(LocalDate today) {
        calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.getDate().equals(today);
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable(selectedDrawable);
            }
        });
    }

    // 날짜가 주어진 날짜 범위 내에 있는지 확인하는 메서드를 수정합니다.
    private boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isEqual(endDate); // 종료 날짜를 포함하도록 수정
    }


    // 이 메서드는 GoalAdapter에서 호출됩니다.
    // 이 메서드는 GoalAdapter에서 호출됩니다.
    private void updateCheckboxStatus(Goal goal, String date, boolean newCheckState, ImageView checkbox) {
        if (goal != null) {
            DatabaseReference itemRef = database.getReference("AllGoalItem").child(goal.getId());
            DatabaseReference completedCountRef = itemRef.child("completedCount");
            DatabaseReference totalCountRef = itemRef.child("totalCount");

            // 체크된 날짜 상태 업데이트
            itemRef.child("checkedDates").child(date).setValue(newCheckState, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        // 완료된 개수 업데이트
                        completedCountRef.runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                Integer completedCount = mutableData.getValue(Integer.class);
                                if (completedCount == null) {
                                    return Transaction.success(mutableData);
                                }

                                if (newCheckState) {
                                    mutableData.setValue(completedCount + 1);
                                } else {
                                    mutableData.setValue(completedCount - 1);
                                }

                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                if (databaseError != null) {
                                    Log.e("Firebase", "Firebase에서 completedCount 업데이트 실패: " + databaseError.getMessage());
                                }
                            }
                        });

                        // 진행률 업데이트
                        totalCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Integer totalCount = dataSnapshot.getValue(Integer.class);
                                    if (totalCount != null && totalCount > 0) {
                                        // 현재 완료된 개수를 가져오기 위한 새 리스너 추가
                                        completedCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Integer currentCompletedCount = dataSnapshot.getValue(Integer.class);
                                                if (currentCompletedCount != null) {
                                                    int progressPercentage = (currentCompletedCount * 100) / totalCount;
                                                    // Firebase에 진행률 업데이트
                                                    itemRef.child("progress").setValue(progressPercentage);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                // 에러 처리
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // 에러 처리
                            }
                        });
                    } else {
                        Log.e("Firebase", "Firebase에서 checkedDates 업데이트 실패: " + databaseError.getMessage());
                    }
                }
            });

        }
    }



}
