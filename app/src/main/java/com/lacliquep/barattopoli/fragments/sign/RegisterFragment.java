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

        // perform setOnClickListener on register button
        sign_up_button.setOnClickListener(v -> {
            // display a message by using a Toast
            Toast.makeText(getActivity(), "SIGNUPButton", Toast.LENGTH_LONG).show();
            //TODO: after registration do login and then start signActivity
        });
        return view;
    }

}