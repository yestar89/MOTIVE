package com.example.motive_v1.Club.Feed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.example.motive_v1.Club.Feed.PostAdapter;
import com.example.motive_v1.Club.Feed.Post;
import com.example.motive_v1.Club.Feed.PostActivity;
import com.example.motive_v1.R;

public class FeedFragment extends Fragment {
   private RecyclerView recyclerView;
   private DatabaseReference databaseReference;
    private FirebaseDatabase database;
   private PostAdapter postAdapter;
   private ArrayList<Post> mPost;

   private AppCompatButton goPost;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true); // 리사이클러뷰 역순
        layoutManager.setStackFromEnd(true); // 리사이클러뷰 역순
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setLayoutManager(layoutManager);

        mPost = new ArrayList<>();

        postAdapter = new PostAdapter(getContext(), mPost);
        recyclerView.setAdapter(postAdapter);

        database =FirebaseDatabase.getInstance();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference = database.getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            //@SuppressLint("NotifyDataSetChanged")
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPost.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    post.setKey(snapshot.getKey());
                    mPost.add(post);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FeedFragment", "Failed to read value.", error.toException());
            }
        });




        goPost = view.findViewById(R.id.goPost);
        //글쓰기 버튼 이동
        goPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PostActivity.class);
                startActivity(intent);
            }
        });

        //readPosts();


        return view;


    }
}








//        private void readPosts() {
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
//
//            reference.addValueEventListener(new ValueEventListener() {
//                //데이터 변경 됐을 때 호출. 변경 사항 처리
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    postLists.clear();
//                    for (DataSnapshot datasnapshot : snapshot.getChildren()) {
//                        Post post = datasnapshot.getValue(Post.class);
//
//                        if (post != null) {
//                            postLists.add(post);
//                        }
//                    }
//                    postAdapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });

        //;}





