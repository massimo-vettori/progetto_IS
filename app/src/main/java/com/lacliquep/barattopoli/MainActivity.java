package com.lacliquep.barattopoli;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.classes.Ownership;
import com.lacliquep.barattopoli.classes.User;
import com.lacliquep.barattopoli.views.ItemView;


public class MainActivity extends AppCompatActivity {

    public static final String ACTIVITY_TAG_NAME = "MainActivity";
    private View view;
    private TextView topText;
    private ImageView imageContainer;
    private TextView bottomText;
    private Button insertNewItem;
    private Button delete;

    private String userBasicInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        topText = findViewById(R.id.top_text);
//        bottomText = findViewById(R.id.bottom_text);
//        imageContainer = findViewById(R.id.image_container);
        insertNewItem = findViewById(R.id.insertNewItem);
        delete = findViewById(R.id.deleteItem);

        this.addItem(Item.getSampleItem());
        this.addItem(Item.getSampleItem());
        this.addItem(Item.getSampleItem());

        //retrieving a previous activity value attached to the bundle
        Bundle b = getIntent().getExtras();
        //retrieving user's info if this Activity is started by the previous activity in its natural chain
        userBasicInfo = (b!= null)? b.getString(getString(R.string.Bundle_tag_user_basic_info)):"";

        insertNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                //attach a string with the current logged user basic info encoding to pass it forward
                Bundle c = new Bundle();
                c.putString(getString(R.string.Bundle_tag_user_basic_info), userBasicInfo);
                //create intent
                intent = new Intent(MainActivity.this, InsertNewItemActivity.class);
                //attach the string
                intent.putExtras(c);
                startActivity(intent);
                finish();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.removeItemFromBoard(MainActivity.ACTIVITY_TAG_NAME, "mmNsy71Nf5e8ATR79b4LNk3uRSh1","217b8390-b454-45b5-ab5f-6e947a08c29e");
            }
        });

        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    protected void addItem(Item item) {
        LinearLayout container = findViewById(R.id.main_scroller);
        View view = ItemView.createAndInflate(this, item, container);
        container.addView(view);
    }


}