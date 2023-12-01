package com.example.motive_v1.Mypage;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;



import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.motive_v1.Club.Feed.PostEdit;
import com.example.motive_v1.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.motive_v1.User.User;
import com.google.firebase.database.core.Tag;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class MypgModifyActivity extends AppCompatActivity {
    Button submit;
    CircleImageView profileImage;
    EditText nickname, mydesc;
    AppCompatButton female, male;
    ProgressBar progressBar;
    private String profileImageUrl;
    private static final int IMAGE_REQUEST = 1;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String userId; // 사용자 ID

    private Uri imageUri; // 수정된 이미지 Uri를 저장하기 위한 변수
    private StorageTask uploadTask; // 이미지 업로드를 관리하는 Task

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypg_modify);

        // 뒤로 가기 버튼 설정
        ImageView backBtn = findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼을 클릭했을 때 동작할 코드
                finish(); // 현재 액티비티 종료 (이전 화면으로 돌아감)
            }
        });

        profileImage = findViewById(R.id.imV_mypg_photo);
        nickname = findViewById(R.id.mypg_name);
        mydesc = findViewById(R.id.description);
        female = findViewById(R.id.btn_female);
        male = findViewById(R.id.btn_male);

        progressBar = findViewById(R.id.progress_view);

        // Firebase Database 레퍼런스 가져오기
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        // 현재 로그인한 사용자의 ID 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();

            // 사용자 정보 불러오기
            loadUserInfo();
        }

        // 수정 버튼 설정
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId != null) {
                    modifyUserInfo();
                }
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지를 선택하기 위한 Intent를 실행
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launcher.launch(intent);
            }
        });
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();

                        if (imageUri != null) {
                            Glide.with(MypgModifyActivity.this)
                                    .load(imageUri)
                                    .into(profileImage);
                        }
                    }
                }
            });

    private void loadUserInfo() {
        databaseReference.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 데이터베이스에서 사용자 정보를 가져옴
                User user = dataSnapshot.getValue(User.class);

                // 사용자 정보를 화면에 표시
                if (user != null) {
                    mydesc.setText(user.getDescription());
                    Glide.with(MypgModifyActivity.this).load(user.getProfileImageUrl()).into(profileImage);
                    nickname.setText(user.getUsername());

                    // 성별 정보 표시
                    if ("여성".equals(user.getGender())) {
                        female.setActivated(true);
                        male.setActivated(false);
                        female.setBackgroundResource(R.drawable.selected_rct);
                        male.setBackgroundResource(R.drawable.unselect_rct);
                    } else if ("남성".equals(user.getGender())) {
                        female.setActivated(false);
                        male.setActivated(true);
                        male.setBackgroundResource(R.drawable.selected_rct);
                        female.setBackgroundResource(R.drawable.unselect_rct);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadUserInfo:onCancelled", databaseError.toException());
            }
        });
    }

    private void modifyUserInfo() {
        progressBar.setVisibility(View.VISIBLE);
        final String description = mydesc.getText().toString();
        final String username = nickname.getText().toString();
        final String gender = female.isActivated() ? "여성" : (male.isActivated() ? "남성" : null);

        if (imageUri != null) {
            // 이미지를 선택했을 경우, Firebase Storage에 업로드
            final StorageReference fileReference = storageReference.child(userId + ".jpg");
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final Uri downloadUri = task.getResult();
                        final String updatedProfileImageUrl = downloadUri.toString();

                        updateUserInfoWithProfileImage(username, gender, description, updatedProfileImageUrl);
                    } else {
                        // 이미지 업로드 실패
                        Log.w(TAG, "Image upload failed: ", task.getException());
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            updateUserInfoWithoutProfileImage(username, gender, description);
        }
    }

    private void updateUserInfoWithProfileImage(final String username, final String gender, final String description, final String updatedProfileImageUrl) {
        final DatabaseReference userRef = databaseReference.child("Users").child(userId);

        userRef.child("username").setValue(username);
        userRef.child("gender").setValue(gender);
        userRef.child("description").setValue(description);
        userRef.child("profileImageUrl").setValue(updatedProfileImageUrl);

        // 사용자 정보 업데이트 성공
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(getApplicationContext(), MypgInformationActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUserInfoWithoutProfileImage(final String username, final String gender, final String description) {
        final DatabaseReference userRef = databaseReference.child("Users").child(userId);

        userRef.child("username").setValue(username);
        userRef.child("gender").setValue(gender);
        userRef.child("description").setValue(description);

        // 사용자 정보 업데이트 성공
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(getApplicationContext(), MypgInformationActivity.class);
        startActivity(intent);
        finish();
    }
}