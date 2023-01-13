package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lacliquep.barattopoli.classes.Category;
import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.classes.User;
import com.lacliquep.barattopoli.fragments.sign.InsertNewUserFragment;
import com.lacliquep.barattopoli.fragments.sign.SignInUpFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

/**
 * this Activity discriminates between a logged user and not logged one when starting the app
 * and redirecting to the correct activity
 * @author pares, jack, gradiente
 * @since 1.0
 */
public class SignActivity extends AppCompatActivity {

    private static final String ACTIVITY_TAG_NAME = "SignActivity";
    // Creates an instance of Firebase Authentication
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user  = mAuth.getCurrentUser();

    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private Button logout_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Bundle c = getIntent().getExtras();
        if ((c != null) && c.containsKey("goToInsertNewUserFragment")) {
                int i = c.getInt("goToInsertNewUserFragment");
                if (i == 1) loadFragment(new InsertNewUserFragment());
        } else {
            if (user != null) {
                Toast.makeText(this, getString(R.string.welcome_back), Toast.LENGTH_LONG).show();
                // Change activity
                startActivity(new Intent(SignActivity.this, MainActivity.class));
            } else {
                loadFragment(new SignInUpFragment());
            }
        }
    }

    /**
     * replace the FrameLayout in activity_sign with a different new Fragment
     * @param fragment the fragment that will replace the FrameLayout
     */
    private void loadFragment(Fragment fragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
            fragmentTransaction.addToBackStack(fragment.toString());
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.commit();
    }
}

