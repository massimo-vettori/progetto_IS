package com.lacliquep.barattopoli;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lacliquep.barattopoli.classes.DataBaseInteractor;
import com.lacliquep.barattopoli.classes.User;
import com.lacliquep.barattopoli.views.ItemView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* sample of how to use the DataBaseInteractor methods: do not try taking out the values from the consumer
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(User.CLASS_USER_DB);
            DataBaseInteractor.retrieveUserById(MainActivity.this, dbRef, "mmNsy71Nf5e8ATR79b4LNk3uRSh1", new Consumer<User>() {
                @Override
                public void accept(User u) {
                    Toast.makeText(MainActivity.this, u.getUsername(), Toast.LENGTH_LONG).show();
                }
            });
         */
    }
}