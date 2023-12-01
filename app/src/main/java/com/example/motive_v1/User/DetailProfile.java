package com.example.motive_v1.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_v1.Club.ClubActivity;
import com.example.motive_v1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.motive_v1.Login.SetProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailProfile extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private UserAdapter adapter;
    private ArrayList<User> mUser;

    ImageView close;
    TextView edit_profile;

    FirebaseUser firebaseUser;
    String profileid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_profile);

        close = findViewById(R.id.close);
        edit_profile = findViewById(R.id.edit_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mUser = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        // 사용자 ID 검색 및 기억 ? ?
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("PREFPS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // 여기에서 userId를 사용합니다.

            databaseReference = database.getReference("Users").child(userId);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mUser.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        mUser.add(user);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }




        adapter = new UserAdapter(this, mUser);
        recyclerView.setAdapter(adapter);


        // 가현
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();

                if (btn.equals("편집")) {
                    // 로그인한 유저일 경우 편집 화면 이동
                    if (firebaseUser != null) {
                        // 편집 눌렀을 때 가현이가 작업한 프로필 수정 화면으로 넘기기
                        Intent intent = new Intent(DetailProfile.this, SetProfile.class);
                        startActivity(intent);
                    } else {
                        // 편집 권한이 없는 사용자에겐 편집 텍스트가 안보이게 하기
                        edit_profile.setVisibility(View.GONE);
                    }
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailProfile.this, ClubActivity.class);
                startActivity(intent);
            }
        });
    }


}