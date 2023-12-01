package com.example.motive_v1.Club.Feed;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.motive_v1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;

import com.example.motive_v1.Club.ClubActivity;

public class PostEdit extends AppCompatActivity {

    ImageView close, image_added;
    Button sports, study;
    TextView saveEdit;
    EditText description;
    ProgressBar progressBar;
    private static final int IMAGE_REQUEST = 1;

    String postid, category, imageUrl, editDesc;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri imageUri; // 수정된 이미지 Uri를 저장하기 위한 변수
    private StorageTask uploadTask; // 이미지 업로드를 관리하는 Task

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();

                        if (imageUri != null) {
                            Glide.with(PostEdit.this)
                                    .load(imageUri)
                                    .into(image_added);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_post_edit);

        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        sports = findViewById(R.id.sports);
        study = findViewById(R.id.study);
        saveEdit = findViewById(R.id.saveEdit);
        description = findViewById(R.id.description);
        progressBar = findViewById(R.id.progressbar);

        storageReference = FirebaseStorage.getInstance().getReference("posts");
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");

        Intent intent = getIntent();
        if (intent != null) {
            // 인텐트에서 데이터를 추출합니다.
            postid = intent.getStringExtra("postid");
            category = intent.getStringExtra("category");
            editDesc = intent.getStringExtra("description");
            imageUrl = intent.getStringExtra("imageUrl");

            // 글 데이터 UI 설정
            description.setText(editDesc);
            Glide.with(this).load(imageUrl).into(image_added);

            // 기존 카테고리 데이터를 받아오고 버튼을 강조 표시
            databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        category = dataSnapshot.child("category").getValue().toString();
                        if ("운동".equals(category)) {
                            sports.setBackgroundResource(R.drawable.makebutton); // 선택된 상태의 드로어블을 적용
                            study.setBackgroundResource(R.drawable.makebutton2); // 선택되지 않은 상태의 드로어블을 적용
                            // 텍스트 색상 변경
                            sports.setTextColor(Color.parseColor("#FFFFFF")); // 선택된 상태일 때의 텍스트 색상
                            study.setTextColor(Color.parseColor("#999999")); // 선택되지 않은 상태일 때의 텍스트 색상
                        } else if ("공부".equals(category)) {
                            study.setBackgroundResource(R.drawable.makebutton); // 선택된 상태의 드로어블을 적용
                            sports.setBackgroundResource(R.drawable.makebutton2); // 선택되지 않은 상태의 드로어블을 적용
                            // 텍스트 색상 변경
                            study.setTextColor(Color.parseColor("#FFFFFF")); // 선택된 상태일 때의 텍스트 색상
                            sports.setTextColor(Color.parseColor("#999999")); // 선택되지 않은 상태일 때의 텍스트 색상
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }


        // 이미지 설정 (Glide 또는 Picasso 사용)
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into(image_added);
        }


        // 닫기 버튼 클릭 이벤트 처리 (수정 취소)
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostEdit.this, ClubActivity.class);
                finish();
            }
        });
        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sports.setBackgroundResource(R.drawable.makebutton); // 선택된 상태의 드로어블을 적용
                study.setBackgroundResource(R.drawable.makebutton2); // 선택되지 않은 상태의 드로어블을 적용
                // 텍스트 색상 변경
                sports.setTextColor(Color.parseColor("#FFFFFF")); // 선택된 상태일 때의 텍스트 색상
                study.setTextColor(Color.parseColor("#999999")); // 선택되지 않은 상태일 때의 텍스트 색상
                category = "운동";
            }
        });

        study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                study.setBackgroundResource(R.drawable.makebutton); // 선택된 상태의 드로어블을 적용
                sports.setBackgroundResource(R.drawable.makebutton2); // 선택되지 않은 상태의 드로어블을 적용
                // 텍스트 색상 변경
                study.setTextColor(Color.parseColor("#FFFFFF")); // 선택된 상태일 때의 텍스트 색상
                sports.setTextColor(Color.parseColor("#999999")); // 선택되지 않은 상태일 때의 텍스트 색상
                category = "공부";
            }
        });


        image_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePost();
            }
        });
    }
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);

    }
    private void updatePost() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("description", description.getText().toString());
        hashMap.put("postimage", imageUrl);
        hashMap.put("category", category);

        databaseReference.updateChildren(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                finish();
            }
        });
    }

}
