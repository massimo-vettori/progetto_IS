package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.os.Bundle;
import android.widget.Toast;

public class SignActivity extends AppCompatActivity {

    // Creates an instance of Firebase Authentication
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user  = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (user != null) {
            // User is signed in
            String uid = user.getUid();
            Toast.makeText(this, "User is signed in", Toast.LENGTH_SHORT).show();

            // Change activity
        } else {
            Toast.makeText(this, "User is not signed in", Toast.LENGTH_SHORT).show();
        }
    }
}
