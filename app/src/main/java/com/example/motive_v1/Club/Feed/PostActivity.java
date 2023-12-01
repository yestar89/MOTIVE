package com.example.motive_v1.Club.Feed;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.request.target.Target;
import com.example.motive_v1.Club.ClubActivity;
import com.example.motive_v1.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity {

    Uri imageUrl;
    String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    ImageView close, image_added;
    Button sports, study;
    TextView post;
    EditText description;
    ProgressBar progressBar;
    FirebaseDatabase database;

    private static final int PICK_IMAGE = 1;

    private String selectedCategory = "";

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Picasso.with(this).load(resultUri).into(image_added);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_post);


        close = findViewById(R.id.close);
        image_added = findViewById(R.id.image_added);
        sports = findViewById(R.id.sports);
        study = findViewById(R.id.study);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);
        progressBar = findViewById(R.id.progressbar);

        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("posts");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //FirebaseUser currentUser = mAuth.getCurrentUser();

//        if (currentUser != null) {
//            final String userimage = currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "";
//            final String username = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "";

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostActivity.this, ClubActivity.class);
                startActivity(intent);
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
                selectedCategory = "운동";
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
                selectedCategory = "공부";
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedCategory.isEmpty()) {
                    Toast.makeText(PostActivity.this, "카테고리를 선택하세요.", Toast.LENGTH_SHORT).show();
                } else if (description.getText().toString().isEmpty()) {
                    Toast.makeText(PostActivity.this, "글을 작성하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }
            }
        });

        image_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
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
                        imageUrl = result.getData().getData();
                        Log.d("test", imageUrl.toString());

                        if (imageUrl != null) {
                            ImageView imageView = findViewById(R.id.image_added);

                            Glide.with(PostActivity.this)
                                    .load(imageUrl)
                                    .override(Target.SIZE_ORIGINAL)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)  // 디스크 캐시 끄기
                                    .skipMemoryCache(true)
                                    .into(image_added);




                        }
                    }
                }
            });

    private void uploadImage() {
        ProgressBar progressBar = findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        if (imageUrl != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUrl));

            uploadTask = fileReference.putFile(imageUrl);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postid = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", myUrl);
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("category", selectedCategory);
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("timestamp", System.currentTimeMillis());
//                        hashMap.put("userimage", userimage);
//                        hashMap.put("username", username);

//                        // 게시물 수정인 경우 postid를 인텐트로 받아와서 업데이트
//                        String receivedPostId = getIntent().getStringExtra("postid");
//                        if (receivedPostId != null) {
//                            // 게시물을 수정하는 로직 추가
//                            reference.child(receivedPostId).updateChildren(hashMap)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
//                                                Toast.makeText(PostActivity.this, "게시물이 수정되었습니다.", Toast.LENGTH_SHORT).show();
//                                                finish();
//                                            } else {
//                                                Toast.makeText(PostActivity.this, "게시물 수정 실패", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    });
//                        } else {
                        reference.child(postid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(PostActivity.this, "게시물이 게시되었습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PostActivity.this, ClubActivity.class);
                                    startActivity(intent);
//                                    FeedFragment feedFragment = new FeedFragment();
//                                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                                    transaction.replace(R.id.content, feedFragment);
//                                    transaction.addToBackStack(null);
//                                    transaction.commit();
                                } else {
                                    Toast.makeText(PostActivity.this, "게시물 게시 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(PostActivity.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, "오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

