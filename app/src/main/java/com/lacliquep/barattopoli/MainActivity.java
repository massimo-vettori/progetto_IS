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

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.lacliquep.barattopoli.classes.Exchange;
import com.lacliquep.barattopoli.fragments.ObjectFragment;

import android.annotation.SuppressLint;
import android.view.MenuItem;

import java.util.Objects;

import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.classes.Ownership;
import com.lacliquep.barattopoli.classes.User;
import com.lacliquep.barattopoli.views.ItemView;


/**
 * this class displays the left navigation bar and it is the main activity
 * @author pares, jack, gradiente
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
            ObjectFragment fragment = new ObjectFragment();
            Bundle args = new Bundle();
            args.putString("filter","Object");
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit(); // this start for the first when you open this activity
            navigationView.setCheckedItem(R.id.nav_object);
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
        Intent intent = null;

        if (item != null) {
            switch (item.getItemId()) {
                case R.id.nav_object:
                    changeItem(new ObjectFragment(),"Object");
                    enter = true;
                    break;
                case R.id.nav_services:
                    changeItem(new ObjectFragment(),"Service");
                    enter = true;
                    break;
                case R.id.nav_charity:
                    changeItem(new ObjectFragment(),"Charity");
                    enter = true;
                    break;

                case R.id.nav_profile:
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("caller", ACTIVITY_TAG_NAME);
                    startActivity(intent);
                    break;

                case R.id.nav_dashboard:
                    intent = new Intent(MainActivity.this, MyBoardActivity.class);
                    intent.putExtra("caller", ACTIVITY_TAG_NAME);
                    startActivity(intent);
                    break;

                case R.id.nav_exchanges:
                    // Sets the "proposer" extra to PERSONAL, in order to show only the exchanges the user has proposed
                    intent = new Intent(MainActivity.this, ExchangeViewActivity.class);
                    intent.putExtra("proposer", Ownership.PERSONAL.toString());
                    startActivity(intent);
                    break;

                case R.id.nav_other_exchanges:
                    // Sets the "proposer" extra to OTHER, in order to show only the exchanges that other users have proposed to the user
                    intent = new Intent(MainActivity.this, ExchangeViewActivity.class);
                    intent.putExtra("proposer", Ownership.OTHER.toString());
                    startActivity(intent);
                    break;

                case R.id.add_item:
                    intent = new Intent(MainActivity.this, InsertNewItemActivity.class);
                    startActivity(intent);
                    // finish();
                    break;
                case R.id.nav_logout:
                    mAuth.signOut();
                    intent = new Intent(MainActivity.this, SignActivity.class);
                    startActivity(intent);
                    finish();
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

    private void changeItem(ObjectFragment fragment, String filter){
        Bundle args = new Bundle();
        args.putString("filter",filter);
        ObjectFragment fragment1 = new ObjectFragment();
        fragment1.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment1).commit();
    }

/*
    protected void addItem(Item item) {
        LinearLayout container = findViewById(R.id.main_scroller);
        View view = ItemView.createAndInflate(this, item, container);
        container.addView(view);
    }
*/


    // for bottom navigation
  /* public void replaceFragments(Object fragmentClass) { // for bottom navigation
        Fragment fragment = null;
        Object o = R.id.fragment_container;
        fragment = (Fragment) fragmentClass;
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
                .commit();
    }*/
}