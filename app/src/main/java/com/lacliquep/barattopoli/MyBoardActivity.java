package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.views.ListItemView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class MyBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_board);

        // Removes the title bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        retrieveItems();

        Button back = findViewById(R.id.back_button);
        back.setOnClickListener(view -> {
            finish();
        });

        // Disables the grid view button
        findViewById(R.id.activate_grid_view_btn).setEnabled(false);
    }

    public void addItem(Item item) {
        LinearLayout container = findViewById(R.id.board_item_list);
        View view = ListItemView.createAndInflate(this, item, container);
        container.addView(view);
    }

    protected void retrieveItems() {
        Item.retrieveMapWithAllItems(true,true,null, true, "mmNsy71Nf5e8ATR79b4LNk3uRSh1", new ArrayList<String>(Arrays.asList("Italia", "Veneto", "Venezia", "Venezia")), stringMapMap -> {
            stringMapMap.forEach((key, value) -> {
                addItem(value);
            });
        });
    }
}