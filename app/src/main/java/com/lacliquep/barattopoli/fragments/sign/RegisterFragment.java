package com.lacliquep.barattopoli.fragments.sign;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lacliquep.barattopoli.R;
import com.lacliquep.barattopoli.SignActivity;

import java.util.Objects;


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

    //get an instance of the FirebaseAuth
    private final FirebaseAuth auth = FirebaseAuth.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_register, container, false);

        // get the reference of Buttons and text fields
        sign_up_email_field = view.findViewById(R.id.sign_up_email_field);
        sign_up_password_field = view.findViewById(R.id.sign_up_password_field);
        sign_up_password_confirm_field = view.findViewById(R.id.sign_up_password_confirm_field);
        sign_up_button = view.findViewById(R.id.sign_up_button);

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
            String txt_confirm_password = sign_up_password_field.getText().toString();

            //TODO:add checkbox control
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
                } else {//everything seems to be ok, so call the method for the actual registration
                    registerUser(txt_email, txt_password);

                }
            }
            //TODO: after registration do login and then start signActivity
        });

        return view;
    }



    /**
     * registration of the user in the database with {@link FirebaseAuth}
     * @param email the provided email
     * @param password the provided password
     */
    void registerUser(String email, String password) {
        //addOnCompleteListener is added to display a Toast for confirmation of the registration
        //TODO: wrapping in async task
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                //positive feedback
                Toast.makeText(getActivity(), getString(R.string.Registration) + getString(R.string.success), Toast.LENGTH_SHORT).show();
                //login of user
                loginUser(email, password);
            } else {
                //TODO: delete exception
                String error = (Objects.requireNonNull(task.getException()).getMessage());
                //negative feedback
                Toast.makeText(getActivity(), getString(R.string.Registration) + getString(R.string.failure) + ":\n" + error, Toast.LENGTH_SHORT).show();
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
            //TODO: after login start SignActivity
            startActivity(new Intent(getActivity(), SignActivity.class));
        });
    }
}