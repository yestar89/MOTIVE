package com.example.motive_v1.Club.Tip;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.motive_v1.R;

public class TipFragment extends Fragment {

    TextView tipfrg_imv_start;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tip, container, false);

        tipfrg_imv_start = v.findViewById(R.id.tipfrg_imv_start);
        tipfrg_imv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TipQuestion.class);
                startActivity(intent);
            }
        });

        return v;
    }
}

