package com.lacliquep.barattopoli.fragments.sign;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.lacliquep.barattopoli.R;


/**
 * A {@link Fragment} subclass to handle the choice between sign-in and sign-up.
 */
public class SignInUpFragment extends Fragment {


    View view;
    //login
    Button choice_sign_in_button;
    //register
    Button choice_sign_up_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_in_up, container, false);

        // get the reference of Buttons
        choice_sign_in_button = view.findViewById(R.id.choice_sign_in_button);
        choice_sign_up_button = view.findViewById(R.id.choice_sign_up_button);

        // perform setOnClickListener on login
        choice_sign_in_button.setOnClickListener(v -> {
            //change of fragment
            loadFragment(new LoginFragment());
        });
        // perform setOnClickListener on register
        choice_sign_up_button.setOnClickListener(v -> {
            //change of fragment
            loadFragment(new RegisterFragment());
        });

        return view;
    }

    /**
     * replace the FrameLayout in activity_sign with a different new Fragment
     * @param fragment the fragment that will replace the FrameLayout
     */
    void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        androidx.fragment.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }
}
