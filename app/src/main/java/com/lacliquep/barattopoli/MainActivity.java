package com.lacliquep.barattopoli;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import com.lacliquep.barattopoli.classes.User;


public class MainActivity extends AppCompatActivity {

    private static String ACTIVITY_TAG_NAME = "MainActivity";
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

        topText = findViewById(R.id.top_text);
        bottomText = findViewById(R.id.bottom_text);
        imageContainer = findViewById(R.id.image_container);
        insertNewItem = findViewById(R.id.insertNewItem);
        delete = findViewById(R.id.deleteItem);

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


}