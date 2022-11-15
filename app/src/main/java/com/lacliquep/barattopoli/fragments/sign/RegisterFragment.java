package com.lacliquep.barattopoli.fragments.sign;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.lacliquep.barattopoli.MainActivity;

import com.lacliquep.barattopoli.R;
import com.lacliquep.barattopoli.SignActivity;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * A {@link Fragment} subclass to handle the sign-up.
 */
public class RegisterFragment extends Fragment {


    View view;
    //email text field
    EditText sign_up_email_field;
    //password text field
    EditText sign_up_password_field;
    //confirm password text field
    EditText sign_up_password_confirm_field;
    //register button
    Button sign_up_button;
    //check box
    CheckBox check_box;

    //get an instance of the FirebaseAuth
    private final FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);

        // get the reference of Buttons, text fields and check box
        sign_up_email_field = view.findViewById(R.id.sign_up_email_field);
        sign_up_password_field = view.findViewById(R.id.sign_up_password_field);
        sign_up_password_confirm_field = view.findViewById(R.id.sign_up_password_confirm_field);
        sign_up_button = view.findViewById(R.id.sign_up_button);
        // TODO add privacy form
        check_box = view.findViewById(R.id.check_box);

        // called whenever the button register is clicked
        // it checks:
        // if the text inserted in email or password is empty
        // if the password length is less than 6 characters
        // if the confirm password is equal to the password
        // if the checkbox has been checked
        // eventually activates the registration
        sign_up_button.setOnClickListener(v -> {
            //the text inserted in the email field
            String txt_email = sign_up_email_field.getText().toString();
            //the text inserted in the password field
            String txt_password = sign_up_password_field.getText().toString();
            //the text inserted in the confirm password field
            String txt_confirm_password = sign_up_password_confirm_field.getText().toString();
            boolean checked = check_box.isChecked();

            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_confirm_password)) {
                if (TextUtils.isEmpty(txt_email) && TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(getActivity(), getString(R.string.email) + " , " + getString(R.string.password) + " , " + getString(R.string.password_confirm) + " , " + getString(R.string.empty_text), Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.isEmpty(txt_email)) {
                        Toast.makeText(getActivity(), getString(R.string.email) + getString(R.string.empty_text), Toast.LENGTH_SHORT).show();
                    } else {
                        if (TextUtils.isEmpty(txt_password)) {
                            Toast.makeText(getActivity(), getString(R.string.password) + getString(R.string.empty_text), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.password_confirm) + getString(R.string.empty_text), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                if (txt_password.length() < 6) {
                    Toast.makeText(getActivity(), getString(R.string.error_password), Toast.LENGTH_SHORT).show();
                } else {
                    if (!(txt_password.equals(txt_confirm_password))) {
                        Toast.makeText(getActivity(), getString(R.string.password) + ", " + getString(R.string.password_confirm) + getString(R.string.match_error), Toast.LENGTH_SHORT).show();
                    } else {
                        if (!checked) {
                            Toast.makeText(getActivity(), getString(R.string.select_error) + getString(R.string.accept_privacy), Toast.LENGTH_LONG).show();
                        } else {
                            //everything seems to be ok, so call the method for the actual registration
                            registration(txt_email, txt_password);
                        }
                    }
                }
            }
        });

        return view;
    }

    /**
     * check the SDK version in order to handle the registration in background
     * @param email the provided email from the user
     * @param password the provided password from the user
     */
    void registration(String email, String password) {
        // TODO find out which is the eldest SDK version accepting concurrent
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            // Do something for R and above versions
            //using concurrent executors
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                //Background work here
                registerUser(email, password);
                handler.post(() -> {
                    //UI Thread work here
                    // TODO change the string
                    Toast.makeText(getActivity(), "using concurrent executors", Toast.LENGTH_SHORT).show();
                });
            });

        } else {
            // do something for phones running an SDK before R
            new AsyncRegister().execute(email, password);
        }
    }

    /**
     * class to handle registration in asynchronous way before SDK R
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncRegister extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... strings) {
                registerUser(strings[0], strings[1]);
                //TODO delete or improve publishProgress
                publishProgress(0);
            return null;
        }
        // TODO: add a progression bar or sth? delete or improve onProgressUpdate
        protected void onProgressUpdate(Integer... integers) {
            Toast.makeText(getActivity(), getString(R.string.in_progress) + integers[0], Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * registration of the user in the database with {@link FirebaseAuth}
     * @param email the provided email
     * @param password the provided password
     */
    void registerUser(String email, String password) {
        //addOnCompleteListener is added to display a Toast for confirmation of the registration
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                //positive feedback
                Toast.makeText(getActivity(), getString(R.string.Registration) + getString(R.string.success), Toast.LENGTH_SHORT).show();
                //login of user
                loginUser(email, password);
            } else {
                String error = (Objects.requireNonNull(task.getException()).getMessage());
                //negative feedback
                Toast.makeText(getActivity(), getString(R.string.Registration) + getString(R.string.failure) + ": \n" + error, Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * Login of the user in the database using {@link FirebaseAuth}
     * @param email the provided email
     * @param password the provided password
     */
    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Toast.makeText(getActivity(), getString(R.string.Login) + getString(R.string.success), Toast.LENGTH_SHORT).show();
            //TODO: after login start MainActivity
            startActivity(new Intent(getActivity(), MainActivity.class));
        });
    }
}