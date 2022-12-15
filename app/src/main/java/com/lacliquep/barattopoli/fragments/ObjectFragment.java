package com.lacliquep.barattopoli.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lacliquep.barattopoli.R;

public class ObjectFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_object, container, false);
        /*
        For bottom bar
        ImageButton home = (ImageButton) v.findViewById(R.id.bottom_navigator_home);
        home.setBackgroundColor(Color.BLUE);*/

        return v;
    }
}
