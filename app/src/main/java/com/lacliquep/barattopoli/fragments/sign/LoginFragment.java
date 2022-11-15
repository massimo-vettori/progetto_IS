package com.lacliquep.barattopoli.fragments.sign;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lacliquep.barattopoli.MainActivity;
import com.lacliquep.barattopoli.R;
import com.lacliquep.barattopoli.SignActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * A {@link Fragment} subclass to handle the sign-in.
 */
public class LoginFragment extends Fragment {


    View view;
    //text email field
    EditText sign_in_email_field;
    //text password field
    EditText sign_in_password_field;
    //login button
    Button sign_in_button;

    //connection with the firebase authorization
    private final FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        // get the reference of Buttons and text fields
        sign_in_email_field = view.findViewById(R.id.sign_in_email_field);
        sign_in_password_field = view.findViewById(R.id.sign_in_password_field);
        sign_in_button = view.findViewById(R.id.sign_in_button);

        // perform setOnClickListener on login button
        sign_in_button.setOnClickListener(v -> {
            //the text inserted in the email
            String txt_email = sign_in_email_field.getText().toString();
            //the text inserted in the password
            String txt_password = sign_in_password_field.getText().toString();
            //the authentication
            login(txt_email, txt_password);

        });
        return view;
    }

    /**
     * check the SDK version in order to handle the login in background
     * @param email the provided email from the user
     * @param password the provided password from the user
     */
    void login(String email, String password) {
        // TODO find out which is the eldest SDK version accepting concurrent
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            // Do something for R and above versions
            //using concurrent executors
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                //Background work here
                loginUser(email, password);
                handler.post(() -> {
                    //UI Thread work here
                    // TODO change the string
                    Toast.makeText(getActivity(), "using concurrent executors", Toast.LENGTH_SHORT).show();
                });
            });

        } else {
            // do something for phones running an SDK before R
            new AsyncLogin().execute(email, password);
        }
    }

    /**
     * class to handle login in asynchronous way before SDK R
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncLogin extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {
                loginUser(strings[0], strings[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        // TODO: add a progression bar or sth?
        protected void onProgressUpdate(Integer... progress) {
            Toast.makeText(getActivity(), getString(R.string.in_progress), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Login of the user in the database using {@link FirebaseAuth}
     * @param email the provided email
     * @param password the provided password
     */
    private void loginUser(String email, String password) {
        //TODO: wrap in async task
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Toast.makeText(getActivity(), getString(R.string.Login) + getString(R.string.success), Toast.LENGTH_SHORT).show();
            //TODO: after login start MainActivity
            startActivity(new Intent(getActivity(), MainActivity.class));
        });
    }

}