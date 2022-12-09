package com.lacliquep.barattopoli;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lacliquep.barattopoli.classes.DataBaseInteractor;
import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.classes.User;
import com.lacliquep.barattopoli.views.ItemView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    View view;
    TextView topText;
    ImageView imageContainer;
    TextView bottomText;
    Button insertNewItem;

    String userBasicInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topText = findViewById(R.id.top_text);
        bottomText = findViewById(R.id.bottom_text);
        imageContainer = findViewById(R.id.image_container);
        insertNewItem = findViewById(R.id.insertNewItem);

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


    }
}