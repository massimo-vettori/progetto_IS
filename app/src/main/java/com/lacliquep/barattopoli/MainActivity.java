package com.lacliquep.barattopoli;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lacliquep.barattopoli.classes.DataBaseInteractor;
import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.classes.User;
import com.lacliquep.barattopoli.views.ItemView;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    View view;
    TextView topText;
    ImageView imageContainer;
    TextView bottomText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topText = findViewById(R.id.top_text);
        bottomText = findViewById(R.id.bottom_text);
        imageContainer = findViewById(R.id.image_container);

        Objects.requireNonNull(getSupportActionBar()).hide();
    }
}