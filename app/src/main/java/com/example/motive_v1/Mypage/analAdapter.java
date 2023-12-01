package com.example.motive_v1.Mypage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_v1.Goal.Goal;
import com.example.motive_v1.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class analAdapter extends RecyclerView.Adapter<analAdapter.ViewHolder> {

    private List<Goal> goalList;
    private Context context;

    public analAdapter(List<Goal> goalList, Context context) {
        this.goalList = (goalList != null) ? goalList : new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public analAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.analtime_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull analAdapter.ViewHolder holder, int position) {
        Goal goal = goalList.get(position);
        holder.goalText.setText(goal.getGoalText());
        holder.goalTitle.setText(goal.getTitle());
        holder.time.setText(formatTime(goal.getTimeInSeconds()));



        if (goal.getGoalText().equals("운동")) {
            holder.goalText.setBackgroundResource(R.drawable.square3);
        } else if (goal.getGoalText().equals("공부")) {
            holder.goalText.setBackgroundResource(R.drawable.square4);
        }
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView goalText;
        TextView goalTitle;
        TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            goalText = itemView.findViewById(R.id.goalText);
            goalTitle = itemView.findViewById(R.id.goalTitle);
            time = itemView.findViewById(R.id.time);
        }
    }

    private String formatTime(int seconds) {
        if (seconds > 0) {
            long hours = TimeUnit.SECONDS.toHours(seconds);
            long minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60;

            if (hours > 0) {
                return String.format("%2d시간 %02d분", hours, minutes);
            } else {
                return String.format("%02d분", minutes);
            }
        } else {
            return "0분";
        }
    }
}
