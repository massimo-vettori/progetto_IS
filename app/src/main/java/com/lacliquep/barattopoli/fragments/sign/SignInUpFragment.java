package com.lacliquep.barattopoli.fragments.sign;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lacliquep.barattopoli.R;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInUpFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user  = mAuth.getCurrentUser();

    private Button loginButton;
    private Button registerButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignInUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignInUpFragment newInstance(String param1, String param2) {
        SignInUpFragment fragment = new SignInUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (user != null) { /*TODO: check if is verified and then login*/ }

//        Activity activity = getActivity();
//
//        if (activity != null) {
//            loginButton    = this.requireView().findViewById(R.id.choice_sign_in_button);
//            registerButton = this.requireView().findViewById(R.id.choice_sign_up_button);
//        }
//
//
//
//        loginButton.setOnClickListener( v -> { changeFragment(new LoginFragment(), R.id.fragment_login); } );
//        registerButton.setOnClickListener( v -> { changeFragment(new RegisterFragment(), R.id.fragment_register); } );
    }

//    @Override
//    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
//        loginButton    = view.findViewById(R.id.choice_sign_in_button);
//        registerButton = view.findViewById(R.id.choice_sign_up_button);
//
//        loginButton.setOnClickListener( v -> { changeFragment(new LoginFragment(), R.id.fragment_login); } );
//        registerButton.setOnClickListener( v -> { changeFragment(new RegisterFragment(), R.id.fragment_register); } );
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in_up, container, false);
    }
}