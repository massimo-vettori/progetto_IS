package com.lacliquep.barattopoli;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.lacliquep.barattopoli.views.ItemView;
import android.os.Bundle;
import android.util.AttributeSet;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar); // define the toolbar because we removed the default ActionBar
        setSupportActionBar(toolbar); // set our ActionBar

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);// for create hamburger for open the NavigationaBar
        drawer.addDrawerListener(toggle);
        toggle.syncState(); // rotating the hamburger icon
    }

    @Override
    public void onBackPressed() { //when we press back button, if our navigation bar is opened we don't want to leave the activity but we want close it
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed(); // this close activity
        }
    }
}