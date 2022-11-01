package com.lacliquep.barattopoli.fragments.sign;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lacliquep.barattopoli.R;


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
            Toast.makeText(getActivity(), "SIGNINButton", Toast.LENGTH_LONG).show();
            //TODO: after login start SignActivity
        });
        return view;
    }

}