package com.lacliquep.barattopoli.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lacliquep.barattopoli.R;
import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.classes.User;
import com.lacliquep.barattopoli.views.ItemView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class ObjectFragment extends Fragment {

    protected LinearLayout container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_object, container, false);
        this.container = v.findViewById(R.id.main_scroller);
        /*
        For bottom bar
        ImageButton home = (ImageButton) v.findViewById(R.id.bottom_navigator_home);
        home.setBackgroundColor(Color.BLUE);*/

        //this.addItem(Item.getSampleItem());
        //this.addItem(Item.getSampleItem());
        //this.addItem(Item.getSampleItem());
        //this.addItem(Item.getSampleItem());
        //this.addItem(Item.getSampleItem());

        retrieveItems(false,false);
        return v;
    }

    public void addItem(Item item) {
        View view = ItemView.createAndInflate(this.getContext(), item, container);
        container.addView(view);
    }

    protected void retrieveItems(boolean charity, boolean service) {
        Item.retrieveMapWithAllItems(true,true, "School", false, "mmNsy71Nf5e8ATR79b4LNk3uRSh1", new ArrayList<String>(Arrays.asList("Italia", "Veneto", "Venezia", "Venezia")), new Consumer<Map<String, Item>>() {
            @Override
            public void accept(Map<String, Item> stringMapMap) {
                Log.d("61ObjectFragment", stringMapMap.toString());
                for(String idItem: stringMapMap.keySet()) {
                    Log.d("63", stringMapMap.get(idItem).getIdRange());
                    Log.d("63", stringMapMap.get(idItem).getIdItem());
                    Log.d("63", stringMapMap.get(idItem).getDescription());
                    Log.d("63", stringMapMap.get(idItem).getItemBasicInfo());
                    Log.d("63", stringMapMap.get(idItem).getTitle());
                    Log.d("63", stringMapMap.get(idItem).getCategories().toString());
                    Log.d("63", stringMapMap.get(idItem).getImages().toString());
                    Log.d("63", stringMapMap.get(idItem).getLocation().toString());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addItem(stringMapMap.get(idItem));
                        }
                    });
                }
            }
        });
    }
}
