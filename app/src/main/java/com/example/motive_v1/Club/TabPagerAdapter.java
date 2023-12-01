package com.example.motive_v1.Club;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.motive_v1.Club.Diary.DiaryFragment;
import com.example.motive_v1.Club.Feed.FeedFragment;
import com.example.motive_v1.Club.Tip.TipFragment;

public class TabPagerAdapter extends FragmentStateAdapter {

    public TabPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FeedFragment();
            case 1:
                return new DiaryFragment();
            case 2:
                return new TipFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3; // 탭의 개수
    }
}
