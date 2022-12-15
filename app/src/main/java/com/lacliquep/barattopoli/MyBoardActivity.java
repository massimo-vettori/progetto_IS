package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.views.ListItemView;

import java.util.ArrayList;
import java.util.Objects;

public class MyBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_board);

        // Removes the title bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        addItem(Item.getSampleItem());
        addItem(Item.getSampleItem());
        addItem(Item.getSampleItem());

        // Disables the grid view button
        findViewById(R.id.activate_grid_view_btn).setEnabled(false);
    }

    public void addItem(Item item) {
        LinearLayout container = findViewById(R.id.board_item_list);
        View view = ListItemView.createAndInflate(this, item, container);
        container.addView(view);
    }
}