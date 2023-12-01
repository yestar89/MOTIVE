package com.example.motive_v1.Club.Feed;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.motive_v1.User.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.example.motive_v1.R;

import com.example.motive_v1.User.DetailProfile;

public class  PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    Context mContext;
    ArrayList<Post> mPost;
    private FirebaseUser firebaseUser;


    public PostAdapter(Context mContext, ArrayList<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;

        // 추가
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }


    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_feed_post, parent, false);
        return new PostAdapter.ViewHolder(v);


    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);


        holder.category.setText(post.getCategory());
        holder.description.setText(post.getDescription());
        Glide.with(holder.itemView).load(post.getPostimage()).into(holder.post_image);

        publisherInfo(holder.image_profile, holder.username, post.getPublisher());
        isLiked(post.getPostid(), holder.like);
        nrLikes(holder.txt_like, post.getPostid());
        getComments(post.getPostid(), holder.txt_comment);

        long diffTimeMillis = System.currentTimeMillis() - post.getTimestamp();
        String timeDifference = calculateTimeDifference(diffTimeMillis);
        holder.upload_time.setText(timeDifference);

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailProfile.class);
                mContext.startActivity(intent);

            }
        });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, WriteComment.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
            }
        });
        // bottom sheet로 글 수정 삭제
        holder.btn_outline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomsheet(post); //게시글 id 전달
            }
        });



    }

    private String calculateTimeDifference(long diffTimeMillis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diffTimeMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffTimeMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(diffTimeMillis);
        long days = TimeUnit.MILLISECONDS.toDays(diffTimeMillis);

        String timeDifference;

        if (days > 0) {
            timeDifference = days + "일 전";
        } else if (hours > 0) {
            timeDifference = hours + "시간 전";
        } else if (minutes > 0) {
            timeDifference = minutes + "분 전";
        } else {
            timeDifference = seconds + "초 전";
        }
        return timeDifference;
    }


    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile, post_image, like, comment, btn_outline;
        public TextView username, txt_like, txt_comment, description, upload_time, category;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //프사
            image_profile = itemView.findViewById(R.id.image_profile);
            //삭제/수정
            btn_outline = itemView.findViewById(R.id.btn_outline);
            //게시글 이미지
            post_image = itemView.findViewById(R.id.post_image);
            //하트 이미지
            like = itemView.findViewById(R.id.like);
            //댓글 이미지
            comment = itemView.findViewById(R.id.comment);
            //댓글 텍스트
            txt_comment = itemView.findViewById(R.id.txt_comment);
            //유저 이름
            username = itemView.findViewById(R.id.username);
            // 좋아요 수
            txt_like = itemView.findViewById(R.id.txt_like);
            // 게시글 설명
            description = itemView.findViewById(R.id.description);
            // 업로드 시간
            upload_time = itemView.findViewById(R.id.upload_time);
            // 운동 or 공부
            category = itemView.findViewById(R.id.category);

        }
    }


    private void getComments(String postid, TextView txt_comment) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txt_comment.setText("댓글" + snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //get.pu
    private void publisherInfo(final ImageView image_profile, final TextView username, String userId) {
        if (userId != null && !userId.isEmpty()) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        // 이미지 URL과 닉네임 설정
                        Glide.with(mContext).load(user.getProfileImageUrl()).into(image_profile);
                        username.setText(user.getUsername());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void isLiked(String postid, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void nrLikes(TextView likes, String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likes.setText("좋아요" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showBottomsheet(final Post post) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View bottomSheetView = LayoutInflater.from(mContext).inflate(R.layout.activity_bottom_sheet, null);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        // 수정 버튼 클릭
        TextView setEdit = bottomSheetView.findViewById(R.id.changePost);
        setEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Postedit로 데이터를 전달하는 인텐트 생성
                Intent intent = new Intent(mContext, PostEdit.class);

                intent.putExtra("postid", post.getPostid());
                intent.putExtra("category", post.getCategory());
                intent.putExtra("description", post.getDescription());
                intent.putExtra("imageUrl", post.getPostimage());

                mContext.startActivity(intent);
                bottomSheetDialog.dismiss(); // BottomSheetDialog를 닫습니다.
            }
        });

        // 삭제 버튼 클릭
        TextView deletePost = bottomSheetView.findViewById(R.id.deletePost);
        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 게시글 삭제 로직을 여기에 추가
                // postid를 사용하여 Firebase에서 해당 게시글을 삭제합니다.
                String postIdToDelete = post.getPostid();
                DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("Posts").child(postIdToDelete);
                postsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // 게시글이 성공적으로 삭제되면 RecyclerView에서도 해당 아이템을 제거합니다.
                            int position = mPost.indexOf(post);
                            if (position != -1) {
                                mPost.remove(position);
                                notifyItemRemoved(position);
                            }
                            bottomSheetDialog.dismiss(); // BottomSheetDialog를 닫습니다.
                        } else {
                            // 게시글 삭제 실패 시 처리
                            // TODO: 실패 처리 로직을 여기에 추가하세요.
                        }
                    }
                });
            }
        });
    }

}



