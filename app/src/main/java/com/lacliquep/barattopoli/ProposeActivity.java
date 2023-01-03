package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.views.ListItemView;

import java.util.ArrayList;
import java.util.Arrays;

public class ProposeActivity extends AppCompatActivity {

    LinearLayout scroller;
    LinearLayout othersItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propose);

        othersItem = findViewById(R.id.others_item);

        Item item = (Item) getIntent().getSerializableExtra("item");
        Button back = findViewById(R.id.back_button);
        back.setOnClickListener(view -> {
            finish();
        });
        this.init(item);
    }
    public void addItem(Item item) {
        LinearLayout container = findViewById(R.id.board_item_list);
        View view = ListItemView.createAndInflate(this, item, container);
        container.addView(view);
        Button chooseItem = view.findViewById(R.id.choose_item);
        chooseItem.setVisibility(View.VISIBLE);
        view.setClickable(false);
    }

    protected void init(Item item) {
        View oth_item = ListItemView.createAndInflate(this, item, othersItem);
        oth_item.setClickable(false);
        othersItem.addView(oth_item);

        Item.retrieveMapWithAllItems(true,true,null, true, FirebaseAuth.getInstance().getUid(), new ArrayList<String>(Arrays.asList("Italia", "Veneto", "Venezia", "Venezia")), stringMapMap -> {
            stringMapMap.forEach((key, value) -> {
                addItem(value);
            });
        });
    }
}