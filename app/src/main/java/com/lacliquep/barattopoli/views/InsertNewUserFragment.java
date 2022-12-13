package com.lacliquep.barattopoli.views;

import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lacliquep.barattopoli.MainActivity;
import com.lacliquep.barattopoli.R;
import com.lacliquep.barattopoli.classes.BarattopolyUtil;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InsertNewUserFragment extends Fragment {

    //tag name for the logcat
    final private static String ACTIVITY_TAG_NAME = "InsertNewUserFragment";

    //get an instance of the FirebaseAuth
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private View view;
    private ImageView imageContainer;
    private Button takePictureUser, register;
    private EditText insertUsername, insertName, insertSurname, insertCountry, insertRegion, insertProvince, insertCity;
    private EditText insertEmail, insertPassword, confirmPassword;
    private CheckBox checkBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_insert_new_user, container, false);

        //image
        imageContainer = view.findViewById(R.id.image_container);
        //Buttons
        takePictureUser = view.findViewById(R.id.take_picture_user);
        register = view.findViewById(R.id.register);
        //EditText
        insertUsername = view.findViewById(R.id.insert_username);
        insertName = view.findViewById(R.id.insert_name);
        insertSurname = view.findViewById(R.id.insert_surname);
        insertCountry = view.findViewById(R.id.insert_country);
        insertRegion = view.findViewById(R.id.insert_region);
        insertProvince = view.findViewById(R.id.insert_province);
        insertCity = view.findViewById(R.id.insert_city);
        insertEmail = view.findViewById(R.id.insert_email);
        insertPassword = view.findViewById(R.id.insert_password);
        confirmPassword = view.findViewById(R.id.confirm_password);
        //checkBox
        // TODO add privacy form
        checkBox = view.findViewById(R.id.check_box);
        //TODO: all the sanity-check on the inserted country, region, province and city with pop up which display the available ones
        //TODO: do the same thing for Items or set automatically the item location with the user's location (most likely)
        // called whenever the button register is clicked
        // it checks:
        // if the text inserted in email or password is empty
        // if the password length is less than 6 characters
        // if the confirm password is equal to the password
        // if the checkbox has been checked
        // eventually activates the registration
        register.setOnClickListener(v -> {
            //fetch text inserted in the fields
            String txtEmail = insertEmail.getText().toString();
            String txtPassword = insertPassword.getText().toString();
            String txtConfirmPassword = confirmPassword.getText().toString();
            String txtUsername = insertUsername.getText().toString();
            String txtName = insertUsername.getText().toString();
            String txtSurname = insertUsername.getText().toString();
            String txtCountry = insertUsername.getText().toString();
            String txtRegion = insertUsername.getText().toString();
            String txtProvince = insertUsername.getText().toString();
            String txtCity = insertUsername.getText().toString();

            boolean checked = checkBox.isChecked();

            //prepare mandatory fields to be checked against emptiness
            ArrayList<String> mandatoryFields = new ArrayList<>();
            ArrayList<String> correspondentErrorMessages = new ArrayList<>();

            mandatoryFields.add(txtEmail);
            correspondentErrorMessages.add((R.string.email) + getString(R.string.empty_text));
            mandatoryFields.add(txtPassword);
            correspondentErrorMessages.add((R.string.password) + getString(R.string.empty_text));
            mandatoryFields.add(txtConfirmPassword);
            correspondentErrorMessages.add((R.string.password_confirm) + getString(R.string.empty_text));
            mandatoryFields.add(txtUsername);
            correspondentErrorMessages.add((R.string.insert_username) + getString(R.string.empty_text));
            mandatoryFields.add(txtCountry);
            correspondentErrorMessages.add((R.string.insert_country) + getString(R.string.empty_text));
            mandatoryFields.add(txtRegion);
            correspondentErrorMessages.add((R.string.insert_region) + getString(R.string.empty_text));
            mandatoryFields.add(txtProvince);
            correspondentErrorMessages.add((R.string.insert_province) + getString(R.string.empty_text));
            mandatoryFields.add(txtCity);
            correspondentErrorMessages.add((R.string.insert_city) + getString(R.string.empty_text));
            //check them
            BarattopolyUtil.checkMandatoryTextIsNotEmpty(getActivity(), mandatoryFields, correspondentErrorMessages);
            //check password minimum length
            if (txtPassword.length() < 6) Toast.makeText(getActivity(), getString(R.string.error_password), Toast.LENGTH_SHORT).show();
            //check correspondence between password and its confirmation
            if (!(txtPassword.equals(txtConfirmPassword))) Toast.makeText(getActivity(), getString(R.string.password) + ", " + getString(R.string.password_confirm) + getString(R.string.match_error), Toast.LENGTH_SHORT).show();
            //check privacy check box
            if (!checked) Toast.makeText(getActivity(), getString(R.string.select_error) + getString(R.string.accept_privacy), Toast.LENGTH_LONG).show();
            //check the location
            BarattopolyUtil.checkLocation(getActivity(), txtCountry, txtRegion, txtProvince, txtCity);
            //everything seems to be ok, so call the method for the actual registration
            registration(txtEmail, txtPassword);
            //TODO: insert data in database
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
