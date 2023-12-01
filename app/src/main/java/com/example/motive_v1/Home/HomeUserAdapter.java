package com.example.motive_v1.Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import com.example.motive_v1.User.User;
import com.example.motive_v1.R;


public class HomeUserAdapter extends RecyclerView.Adapter<HomeUserAdapter.ViewHolder> {

    Context mContext;
    private ArrayList<User> uItems;
    private FirebaseUser firebaseUser;

    public HomeUserAdapter(Context mContext, ArrayList<User> uItems) {
        this.mContext = mContext;
        this.uItems = uItems;
        ;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_info_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User user = uItems.get(position);

        holder.username.setText(user.getUsername());
        Glide.with(holder.itemView).load(user.getProfileImageUrl()).into(holder.image_profile);

    }

    @Override
    public int getItemCount() {
        return uItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile;
        public TextView username;

        public ViewHolder(View view) {
            super(view);

            image_profile = view.findViewById(R.id.image_profile);
            username = view.findViewById(R.id.username);
        }
    }
}
