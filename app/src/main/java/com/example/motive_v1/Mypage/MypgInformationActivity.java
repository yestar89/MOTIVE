package com.example.motive_v1.Mypage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.motive_v1.Home.HomeActivity;
import com.example.motive_v1.Login.Signin;
import com.example.motive_v1.R;
import com.example.motive_v1.User.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MypgInformationActivity extends AppCompatActivity {
    Button editButton; //편집 화면 이동
    ImageButton backMypg; //뒤로가기 - 마이페이지 기본

    // 프로필 3요소
    CircleImageView profileImage;

    TextView username, description;

    // 비번 변경, 로그아웃, 탈퇴
    RelativeLayout updatePw, goLogout, secession;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypg_information);
        //프사, 닉넴, 다짐
        profileImage = findViewById(R.id.imV_infor_pro);
        username = findViewById(R.id.tV_infor_name);
        description = findViewById(R.id.tV_infor_desc);

        // Firebase 인증 객체 가져오기
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
                        if (snapshot.hasChild("profileImageUrl") && snapshot.hasChild("username") && snapshot.hasChild("description")) {
                            String profile = snapshot.child("profileImageUrl").getValue().toString();
                            String usernameText = snapshot.child("username").getValue().toString();
                            String desc = snapshot.child("description").getValue().toString();

                            Glide.with(MypgInformationActivity.this).load(profile).into(profileImage);

                            username.setText(usernameText);
                            description.setText(desc);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            // [편집] 버튼 프로필 편집 화면 이동
            editButton = findViewById(R.id.button2);
            editButton.setOnClickListener(view -> {
                Intent intent = new Intent(getApplicationContext(), MypgModifyActivity.class);
                startActivity(intent);
            });

            // [<] 버튼 마이페이지 화면 이동
            backMypg = findViewById(R.id.imageButton12);
            backMypg.setOnClickListener(view -> {
                Intent intent = new Intent(getApplicationContext(), MypageActivity.class);
                startActivity(intent);
            });
            goLogout = findViewById(R.id.goLogout);
            // 로그아웃
            goLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    auth.signOut();
                    Intent intent = new Intent(MypgInformationActivity.this, Signin.class);
                    startActivity(intent);
                    finish();
                }

            });

            secession = findViewById(R.id.secession);
            // 회원 탈퇴
            secession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentUser.delete();
                }
            });

        }
    }
}