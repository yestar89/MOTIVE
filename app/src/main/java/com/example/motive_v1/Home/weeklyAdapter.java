package com.example.motive_v1.Home;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.motive_v1.Goal.Goal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


import com.example.motive_v1.User.User;
import com.example.motive_v1.R;


public class  weeklyAdapter extends RecyclerView.Adapter<weeklyAdapter.ViewHolder> {

    Context mContext;
    private ArrayList<User> userList;
    private ArrayList<Goal> goalList;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String largestGoalCategory;


    public weeklyAdapter(Context mContext, ArrayList<User> userList, ArrayList<Goal> goalList) {
        this.mContext = mContext;
        this.userList = userList;
        this.goalList = goalList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        largestGoalCategory = "";
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.weekly_godlife, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Firebase Authentication 초기화
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // 현재 사용자 가져오기
        FirebaseUser currentUser = mAuth.getCurrentUser();

        User user = userList.get(position);

        // 목표 분야 운동&공부

        if (user != null) {
            // 목표 분야 운동&공부
            if (user.getGoalText() != null) {
                holder.god_ctg.setText(user.getGoalText());

                // god_ctg 텍스트 컬러 변경
                if (user.getGoalText().equals("운동")) {
                    holder.god_ctg.setTextColor(Color.parseColor("#C728FF"));
                } else if (user.getGoalText().equals("공부")) {
                    holder.god_ctg.setTextColor(Color.parseColor("#FF6535"));
                }
            }

            // 달성 시간: formatTime() 함수를 사용하여 총 시간을 "시, 분" 형태로 변환 후 설정
            String formattedTime = formatTime(user.getTotalTime());
            holder.best_time.setText(formattedTime);
            // 사용자 프사
            Glide.with(holder.itemView).load(user.getProfileImageUrl()).into(holder.profile);
            // 사용자 닉네임
            holder.textView_username.setText(user.getUsername());
        }


    }
    private String formatTime(long seconds) {
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes %= 60;
        return hours + "시간 " + minutes + "분";


    }


    @Override
    public int getItemCount() {
        // userList와 goalList 크기 중 작은 것을 반환
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView god_ctg, textView_username, best_time;
        public ImageView profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // 운동 or 공부
            god_ctg = itemView.findViewById(R.id.god_ctg);
            // 금주의 갓생러 닉네임
            textView_username = itemView.findViewById(R.id.textView_username);
            // 달성시간
            best_time = itemView.findViewById(R.id.best_time);
            // 갓생러 프로필 사진
            profile = itemView.findViewById(R.id.profile);

        }

    }

    // 사용자 목록을 설정 또는 업데이트하기 위한 메서드
    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
        notifyDataSetChanged(); // 어댑터에 데이터 변경을 알립니다.
    }

}
