package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lacliquep.barattopoli.classes.BarattopoliUtil;
import com.lacliquep.barattopoli.classes.User;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ProfileActivity extends AppCompatActivity {

    private static final String ACTIVITY_TAG_NAME = "ProfileActivity";
    Button backButton, button;
    TextView phone, country, region, province, city, email, nickname, nameAndSurname;
    ImageView profilePic;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        backButton = findViewById(R.id.back_button);
        button = findViewById(R.id.button);
        phone = findViewById(R.id.phone);
        country = findViewById(R.id.country);
        region = findViewById(R.id.region);
        province = findViewById(R.id.province);
        city = findViewById(R.id.city);
        email = findViewById(R.id.email);
        nickname = findViewById(R.id.nickname);
        nameAndSurname = findViewById(R.id.name_and_surname);
        profilePic = findViewById(R.id.profile_pic);

        if (auth != null) {
            String userId = auth.getUid();
            String userEmail = auth.getCurrentUser().getEmail();
            User.retrieveCurrentUser(ACTIVITY_TAG_NAME, dbRef, new Consumer<User>() {
                @Override
                public void accept(User user) {
                    ArrayList<String> loc = user.getLocation();

                    country.setText(getString(R.string.country) + ": " + loc.get(0));
                    region.setText(getString(R.string.region) + ": " + loc.get(1));
                    province.setText(getString(R.string.province) + ": " + loc.get(2));
                    city.setText(getString(R.string.city) + ": " + loc.get(3));

                    email.setText(userEmail);
                    nickname.setText(user.getUsername());
                    nameAndSurname.setText(user.getName() + " " + user.getSurname());
                    profilePic.setImageBitmap(BarattopoliUtil.decodeFileFromBase64(user.getImage()));
                }
            });
        }

    }
}