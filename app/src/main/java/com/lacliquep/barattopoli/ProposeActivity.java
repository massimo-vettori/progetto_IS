package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
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
        scroller   = findViewById(R.id.board_item_list);
        othersItem = findViewById(R.id.others_item);

        Item item = (Item) getIntent().getSerializableExtra("item");
        this.init(item);
    }

    protected void init(Item item) {
        View oth_item = ListItemView.createAndInflate(this, item, othersItem);
        oth_item.setClickable(false);
        othersItem.addView(oth_item);

        Item.retrieveMapWithAllItems(true,true,null, true, FirebaseAuth.getInstance().getUid(), new ArrayList<String>(Arrays.asList("Italia", "Veneto", "Venezia", "Venezia")), stringMapMap -> {
            stringMapMap.forEach((key, value) -> {
                View view = ListItemView.createAndInflate(this, item, scroller);
                scroller.addView(view);
            });
        });
    }
}