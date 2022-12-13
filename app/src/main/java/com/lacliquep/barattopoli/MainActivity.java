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


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;
import com.lacliquep.barattopoli.fragments.ObjectFragment;
import com.lacliquep.barattopoli.fragments.ServicesFragment;
import com.lacliquep.barattopoli.views.ItemView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

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
        // TODO: To move
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
        // TODO: To move
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.removeItemFromBoard(MainActivity.ACTIVITY_TAG_NAME, "mmNsy71Nf5e8ATR79b4LNk3uRSh1","217b8390-b454-45b5-ab5f-6e947a08c29e");
            }
        });

        Objects.requireNonNull(getSupportActionBar()).hide();


        Toolbar toolbar = findViewById(R.id.toolbar); // define the toolbar because we removed the default ActionBar
        setSupportActionBar(toolbar); // set our ActionBar

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);// for create hamburger for open the NavigationaBar
        drawer.addDrawerListener(toggle);
        toggle.syncState(); // rotating the hamburger icon


        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ObjectFragment()).commit(); // this start for the first when you open this activity
        }

        /*if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ObjectFragment()).commit(); // this start for the first when you open this activity
            navigationView.setCheckedItem(R.id.nav_object);
        }*/
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        boolean enter = false;
        if (item != null) {
            switch (item.getItemId()) {
                case R.id.nav_object:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ObjectFragment()).commit();
                    enter = true;
                    break;
                case R.id.nav_services:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ServicesFragment()).commit();
                    enter = true;
                    break;
            }
        }

        if(enter){
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() { //when we press back button, if our navigation bar is opened we don't want to leave the activity but we want close it
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed(); // this close activity
        }
    }

   public void replaceFragments(Object fragmentClass) { // for bottom navigation
        Fragment fragment = null;
        Object o = R.id.fragment_container;
        fragment = (Fragment) fragmentClass;
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit();

    }


}