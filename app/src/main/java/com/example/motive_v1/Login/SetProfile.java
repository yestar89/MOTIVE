package com.example.motive_v1.Login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.example.motive_v1.Home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.example.motive_v1.R;
import com.example.motive_v1.User.User;
import com.example.motive_v1.User.UserProfile;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetProfile extends AppCompatActivity {


    private Uri imageUri;
    private ImageView close;
    private AppCompatImageView uploadImage;
    private CircleImageView Viewimage;

    private TextInputLayout Username, user_desc;
    private RadioGroup genderRadioGroup;
    private AppCompatButton FemaleButton;
    private AppCompatButton MaleButton;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;
    private TextView uploadBtn;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setprofile);

        // 뷰 초기화
        uploadImage = findViewById(R.id.set_cam);
        Viewimage = findViewById(R.id.set_photo);
        uploadBtn = findViewById(R.id.btn_ok);
        progressBar = findViewById(R.id.progress_view);
        Username = findViewById(R.id.username);
        FemaleButton = findViewById(R.id.btn_female);
        MaleButton = findViewById(R.id.btn_male);
        user_desc = findViewById(R.id.user_desc);
        close = findViewById(R.id.close);

        progressBar.setVisibility(View.INVISIBLE);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        }

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        Viewimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImage();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SetProfile.this, Signup.class);
                startActivity(intent);
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserProfile();
                showProgressBar();
                Intent intent = new Intent(SetProfile.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        FemaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FemaleButton.setActivated(true);
                MaleButton.setActivated(false);
                FemaleButton.setBackgroundResource(R.drawable.selected_rct);
                MaleButton.setBackgroundResource(R.drawable.unselect_rct);
            }
        });

        MaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FemaleButton.setActivated(false);
                MaleButton.setActivated(true);
                MaleButton.setBackgroundResource(R.drawable.selected_rct);
                FemaleButton.setBackgroundResource(R.drawable.unselect_rct);
            }
        });
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


    private void saveUserProfile() {
        String username = Username.getEditText().getText().toString().trim();
        String gender = null;
        String description = user_desc.getEditText().getText().toString().trim();


        if (FemaleButton.isActivated()) {
            gender = "여성";
        } else if (MaleButton.isActivated()) {
            gender = "남성";
        }

        if (username.length() < 3 || gender == null) {
            if (username.length() < 3) {
                Username.setError("닉네임은 세 글자 이상이어야 합니다.");
            } else {
                Username.setError(null);
            }

            if (gender == null) {
                Toast.makeText(this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }

            hideProgressBar();
            return;
        }



        Username.setError(null);

        uploadProfileImage(username, gender, description);
    }


    private void uploadProfileImage(final String username, final String gender, final String description) {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile");
            storageReference.child("images").child(currentUserId + ".jpg").putFile(imageUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                // 업로드 성공
                                storageReference.child("images").child(currentUserId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(SetProfile.this).load(uri).into(Viewimage);
                                        saveUserDataToDatabase(username, gender, description, uri.toString());
                                    }
                                });
                            } else {
                                // 업로드 실패 처리
                                Toast.makeText(SetProfile.this, "프로필 사진 업로드 실패", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            saveUserDataToDatabase(username, gender, description, null);
        }
    }

    private void saveUserDataToDatabase(String username, String gender, String description, String profileImageUrl) {
        User user = new User(username, gender, description, profileImageUrl);

        // 현재 유저의 email과 userId 가져오기
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            user.setUserId(currentUser.getUid());
            user.setEmail(currentUser.getEmail());
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        mDatabase.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                hideProgressBar();
                if (task.isSuccessful()) {
                    Toast.makeText(SetProfile.this, "사용자 정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    //Log.e("MyApp", "사용자 정보 저장 실패", task.getException());
                    Toast.makeText(SetProfile.this, "사용자 정보 저장 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();

                        // 이미지 업로드 및 표시
                        uploadProfileImage(
                                Username.getEditText().getText().toString().trim(),
                                FemaleButton.isActivated() ? "여성" : (MaleButton.isActivated() ? "남성" : ""),
                                user_desc.getEditText().getText().toString().trim()
                        );
                    }
                }
            }
    );

    private void showImage() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile");
        storageReference.child("images").child(currentUserId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                AppCompatImageView Viewimage = findViewById(R.id.set_photo);

                //이미지를 로드하고 이미지뷰에 표시
                Glide.with(SetProfile.this)
                        .load(uri)
                        .into(Viewimage);
            }
        });
    }
}
