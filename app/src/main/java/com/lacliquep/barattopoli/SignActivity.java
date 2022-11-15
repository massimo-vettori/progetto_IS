package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lacliquep.barattopoli.fragments.sign.SignInUpFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class SignActivity extends AppCompatActivity {

    // Creates an instance of Firebase Authentication
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user  = mAuth.getCurrentUser();

    private Button logout_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        if (user != null) {
            //TODO: embed this button in new activity and delete it from SignActivity.xml
            logout_button = findViewById(R.id.logout_button);
            // User is signed in
            String uid = user.getUid();
            //called whenever the button logout is clicked
            //leading to the Button_startActivity where the login or registration is required
            logout_button.setOnClickListener(view -> {
                //logout
                // TODO wrap in asynctask??
                mAuth.signOut();
                //positive feedback
                Toast.makeText(SignActivity.this, getString(R.string.Logout)+getString(R.string.success), Toast.LENGTH_SHORT).show();
                loadFragment(new SignInUpFragment());
            });
            Toast.makeText(this, "User is signed in", Toast.LENGTH_LONG).show();


            // Change activity
            Intent intent = new Intent(SignActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            loadFragment(new SignInUpFragment());
            Toast.makeText(this, "User is not signed in", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * replace the FrameLayout in activity_sign with a different new Fragment
     * @param fragment the fragment that will replace the FrameLayout
     */
    void loadFragment(Fragment fragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
            fragmentTransaction.addToBackStack(fragment.toString());
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
    }
}

