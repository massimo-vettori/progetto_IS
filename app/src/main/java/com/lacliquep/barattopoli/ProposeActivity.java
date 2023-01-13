package com.lacliquep.barattopoli;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.lacliquep.barattopoli.classes.Exchange;
import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.views.ListItemView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * this Activity handles when choosing an item displayed with the help of ObjectFragment and creating an exchange (insertion in database too)
 * @see com.lacliquep.barattopoli.fragments.ObjectFragment
 * @author pares, jack, gradiente
 */
public class ProposeActivity extends AppCompatActivity {

    private LinearLayout scroller;
    private LinearLayout othersItem;
    private Item proposerItem;
    private static String TAG = "ProposeActivity";

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

    private void addItem(Item item) {
        LinearLayout container = findViewById(R.id.board_item_list);
        View view = ListItemView.createAndInflate(this, item, container);
        container.addView(view);
//        Button chooseItem = view.findViewById(R.id.choose_item);
//        chooseItem.setVisibility(View.VISIBLE);

        view.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Vuoi proporre questo scambio?")
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Exchange.insertExchangeInDatabase(ProposeActivity.TAG, proposerItem.getIdItem(), item.getIdItem());
                        }
                    })
                    .setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //return to setting the item details and don't take a new picture
                            dialog.cancel();
                        }
                    }).show();
        });

        view.setClickable(true);
    }

    private void init(Item item) {
        View oth_item = ListItemView.createAndInflate(this, item, othersItem);
        oth_item.setClickable(false);
        othersItem.addView(oth_item);
        proposerItem = item;

        Item.retrieveMapWithAllItems(true,true,null, true, FirebaseAuth.getInstance().getUid(), new ArrayList<String>(Arrays.asList("Italia", "Veneto", "Venezia", "Venezia")), stringMapMap -> {
            stringMapMap.forEach((key, value) -> {
                if (value.isExchangeable())
                    addItem(value);
            });
        });
    }
}