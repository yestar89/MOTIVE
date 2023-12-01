package com.example.motive_v1.Club.Feed;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_v1.Club.ClubActivity;
import com.example.motive_v1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WriteComment extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText commentEditText;
    private Button submitCommentButton;
    private TextView emptyCommentText;
    private ImageButton backbtn;
    String postid;
    String publisherid;

    FirebaseUser firebaseUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_writecomment);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(commentAdapter);

        //댓글 작성란
        commentEditText = findViewById(R.id.commentEditText);

        //보내기 버튼
        submitCommentButton = findViewById(R.id.submitCommentButton);

        //댓글이 없을 때 뜨는 텍스트
        emptyCommentText = findViewById(R.id.emptyCommentText);

        //뒤로 가기
        backbtn = findViewById(R.id.backbtn);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        // 뒤로가기
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(WriteComment.this, ClubActivity.class);
                startActivity(intent);
                finish();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//                // 피드 화면인 FeedFragment를 생성합니다. (프래그먼트 이름과 클래스명은 실제 코드와 일치하도록 수정해야 합니다)
//                FeedFragment feedFragment = new FeedFragment();
//
//                // 트랜잭션을 커밋합니다.
//                transaction.commit();
            }
        });





        submitCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commentEditText.getText().toString().equals("")) {
                    Toast.makeText(WriteComment.this, "댓글을 작성하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });
        readComments();

    }

    private void addComment() {
        Log.d("MyApp", "addComment_postid: " + postid);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        String commentid = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("commentid", commentid);
        hashMap.put("comment", commentEditText.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("timestamp", System.currentTimeMillis());

        // 댓글 작성 시 초기 화면 텍스트 숨기기
        reference.push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    commentEditText.setText("");
                    emptyCommentText.setVisibility(View.GONE);
                }
            }
        });
    }

    private void readComments() {
        Log.d("MyApp", "postid: " + postid);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear(); // 기존 리스트를 비웁니다.
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Comment comment = snapshot1.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}