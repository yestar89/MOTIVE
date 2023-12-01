package com.example.motive_v1.Club.Feed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.motive_v1.R;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private static final String TAG = "CommentAdapter";
    private Context mContext;
    private List<Comment> mComment;

    private FirebaseUser firebaseUser;

    public CommentAdapter(Context mContext, List<Comment> mComment) {
        this.mContext = mContext;
        this.mComment = mComment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_feed_comment, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mComment.get(position);

        holder.txt_cmt.setText(comment.getComment());
        getUserInfo(holder.image_profile, holder.username, comment.getPublisher());


        holder.lay_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomsheet(comment);
            }
        });

        long diffTimeMillis = System.currentTimeMillis() - comment.getTimestamp();
        String timeDifference = calculateTimeDifference(diffTimeMillis);
        holder.cmt_time.setText(timeDifference);

        // 새로운 댓글이 작성되었으므로, emptyCommentText를 숨깁니다.
        // 댓글이 비어있는 경우 emptyCommentText를 보이도록 설정
        TextView emptyCommentText = ((Activity) mContext).findViewById(R.id.emptyCommentText);
        if (mComment.isEmpty()) {
            emptyCommentText.setVisibility(View.VISIBLE);
        } else {
            emptyCommentText.setVisibility(View.GONE);
        }


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

    private void showBottomsheet(final Comment comment) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
        View bottomSheetView = LayoutInflater.from(mContext).inflate(R.layout.activity_bottom_sheet, null);

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        // 수정 버튼 클릭
        TextView cmt_Edit = bottomSheetView.findViewById(R.id.changePost);
        cmt_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UpdateComment.class);

                intent.putExtra("commentid", comment.getCommentid());
                intent.putExtra("comment", comment.getComment());
                intent.putExtra("publisher", comment.getPublisher());

                mContext.startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });
        TextView cmt_Del = bottomSheetView.findViewById(R.id.deletePost);
        cmt_Del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cmtToDelete = comment.getCommentid();
                DatabaseReference cmtRefs = FirebaseDatabase.getInstance().getReference("Comments").child(cmtToDelete);
                cmtRefs.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // 게시글이 성공적으로 삭제되면 RecyclerView에서도 해당 아이템을 제거합니다.
                            int position = mComment.indexOf(comment);
                            if (position != -1) {
                                mComment.remove(position);
                                notifyItemRemoved(position);
                            }
                            bottomSheetDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView cmt_time, username, txt_cmt;
        public ImageView image_profile;
        public RelativeLayout lay_comment;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            cmt_time = itemView.findViewById(R.id.cmt_time);
            username = itemView.findViewById(R.id.username);
            txt_cmt = itemView.findViewById(R.id.txt_cmt);
            lay_comment = itemView.findViewById(R.id.lay_comment);

        }
    }

    private void getUserInfo(ImageView image_profile, TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    // Firebase에서 사용자 객체를 성공적으로 가져온 경우
                    // 이미지 로드 및 텍스트 설정을 수행합니다.
                    Glide.with(mContext).load(user.getProfileImageUrl()).into(image_profile);
                    username.setText(user.getUsername());
                } else {
                    // Firebase에서 사용자 객체를 가져오지 못한 경우
                    // 에러 처리를 수행합니다.
                    Log.e(TAG, "사용자 정보를 가져올 수 없습니다.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
