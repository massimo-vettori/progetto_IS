package com.lacliquep.barattopoli;

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
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

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