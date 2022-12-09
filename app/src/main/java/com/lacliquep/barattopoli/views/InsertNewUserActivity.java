package com.lacliquep.barattopoli.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lacliquep.barattopoli.R;
import com.lacliquep.barattopoli.classes.Location;

import java.util.ArrayList;

public class InsertNewUserActivity extends AppCompatActivity {

    //tag name for the logcat
    final private static String ACTIVITY_TAG_NAME = "InsertNewUserActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_new_user);
        //TODO: all the sanity-check on the inserted country, region, province and city with pop up which display the available ones
        //TODO: do the same thing for Items or set automatically the item location with the user's location (most likely)
    }
}