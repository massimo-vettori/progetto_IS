package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    
    Button backButton, button;
    TextView phone, location, email, nickname, nameAndSurname;
    ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backButton = findViewById(R.id.back_button);
        button = findViewById(R.id.back_button);
        phone = findViewById(R.id.phone);
        location = findViewById(R.id.location);
        email = findViewById(R.id.email);
        nickname = findViewById(R.id.nickname);
        nameAndSurname = findViewById(R.id.name_and_surname);
        profilePic = findViewById(R.id.profile_pic);
    }
}