package com.lacliquep.barattopoli.fragments.sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lacliquep.barattopoli.InsertNewItemActivity;
import com.lacliquep.barattopoli.MainActivity;
import com.lacliquep.barattopoli.MyCameraActivity;
import com.lacliquep.barattopoli.R;
import com.lacliquep.barattopoli.SignActivity;
import com.lacliquep.barattopoli.classes.BarattopoliUtil;
import com.lacliquep.barattopoli.classes.Location;
import com.lacliquep.barattopoli.classes.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InsertNewUserFragment extends Fragment {

    //tag name for the logcat
    final private static String ACTIVITY_TAG_NAME = "InsertNewUserFragment";

    //get an instance of the FirebaseAuth
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final String basicImage = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";

    private View view;
    private ImageView imageContainer;
    private Button takePictureUser, register;
    private EditText insertUsername, insertName, insertSurname, insertCountry, insertRegion, insertProvince, insertCity;
    private EditText insertEmail, insertPassword, confirmPassword;
    private CheckBox checkBox;

    private String txtEmail = "", txtPassword = "", txtConfirmPassword = "", txtUsername = "", txtName = "", txtSurname = "", txtCountry = "", txtRegion = "", txtProvince = "", txtCity = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_insert_new_user, container, false);
        //retrieving a previous activity value attached to the bundle
        Bundle b = requireActivity().getIntent().getExtras();
        //array to enable its content be used from an inner class
        String[] encodedImage = new String[1];
        String userBasicInfo = "";
        //initialize the image with the basic one
        encodedImage[0] = basicImage;
        //retrieving an image if this Activity is started by camera Activity
        if (b != null) {
            String res = b.getString(getString(R.string.Bundle_tag_encoded_image));
            encodedImage[0] = res != null? res : basicImage;
            /*String res2 = b.getString(getString(R.string.Bundle_tag_user_basic_info));
            userBasicInfo = res2 != null? res2 : "";*/
        }
        //first things first: force the user to take a picture before any other choice
        //reason: otherwise, when coming back to this activity, all the preferences will be deleted

        if(encodedImage[0].equals(basicImage)) {
            takePicture();
        }

        //image
        imageContainer = view.findViewById(R.id.image_container);
        //setting the image
        imageContainer.setImageBitmap(BarattopoliUtil.decodeFileFromBase64(encodedImage[0]));
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

        //to retrieve fields which were previously set by the user when coming back from cameraActivity
        /*if (savedInstanceState != null) {
            String username = savedInstanceState.getString("username");
            String name = savedInstanceState.getString("name");
            String surname = savedInstanceState.getString("surname");
            String email = savedInstanceState.getString("email");
            String password = savedInstanceState.getString("password");
            String confirmPwd = savedInstanceState.getString("confirmPwd");
            String country = savedInstanceState.getString("country");
            String region = savedInstanceState.getString("region");
            String province = savedInstanceState.getString("province");
            String city = savedInstanceState.getString("city");

            if (username != null && !(username.isEmpty())) insertUsername.setText(username);
            if (name != null && !(name.isEmpty())) insertName.setText(name);
            if (surname != null && !(surname.isEmpty())) insertSurname.setText(surname);
            if (email != null && !(email.isEmpty())) insertEmail.setText(email);
            if (password != null && !(password.isEmpty())) insertPassword.setText(password);
            if (confirmPwd != null && !(confirmPwd.isEmpty())) confirmPassword.setText(confirmPwd);
            if (country != null && !(country.isEmpty())) insertCountry.setText(country);
            if (region != null && !(region.isEmpty())) insertRegion.setText(region);
            if (province != null && !(province.isEmpty())) insertProvince.setText(province);
            if (city != null && !(city.isEmpty())) insertCity.setText(city);
        }*/

        register.setOnClickListener(v -> {
            //fetch text inserted in the fields
            txtEmail = insertEmail.getText().toString();
            txtPassword = insertPassword.getText().toString();
            txtConfirmPassword = confirmPassword.getText().toString();
            txtUsername = insertUsername.getText().toString();
            txtName = insertUsername.getText().toString();
            txtSurname = insertUsername.getText().toString();
            txtCountry = insertCountry.getText().toString();
            txtRegion = insertRegion.getText().toString();
            txtProvince = insertProvince.getText().toString();
            txtCity = insertCity.getText().toString();

            boolean checked = checkBox.isChecked();

            reg(checked);
        });

        takePictureUser.setOnClickListener(v -> {
            takeNewPicture();
        });

        return view;
    }
     //save the already set fields when starting the cameraActivity
    /*@Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(!(txtEmail.equals(""))) outState.putString("email", txtEmail);
        if(!(txtPassword.equals(""))) outState.putString("password", txtPassword );
        if(!(txtConfirmPassword.equals(""))) outState.putString("confirmPwd", txtConfirmPassword);
        if(!(txtUsername.equals(""))) outState.putString("username", txtUsername);
        if(!(txtName.equals(""))) outState.putString("name", txtName);
        if(!(txtSurname.equals(""))) outState.putString("surname", txtSurname );
        if(!(txtCountry.equals(""))) outState.putString("country", txtCountry);
        if(!(txtRegion.equals(""))) outState.putString("region", txtRegion);
        if(!(txtProvince.equals(""))) outState.putString("province", txtProvince);
        if(!(txtCity.equals(""))) outState.putString("city", txtCity);
        super.onSaveInstanceState(outState);
    }*/

    void reg(boolean checked) {
        String empty = getString(R.string.empty_text);
        if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtEmail, getString(R.string.email) + empty)) {
            if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtPassword, getString(R.string.password) + empty)) {
                if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtConfirmPassword, getString(R.string.confirm_password) + empty)) {
                    if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtCountry, getString(R.string.insert_country) + empty)) {
                        if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtRegion, getString(R.string.insert_region) + empty)) {
                            if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtProvince, getString(R.string.insert_province) + empty)) {
                                if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtCity, getString(R.string.insert_city) + empty)) {
                                    if (txtPassword.length() < 6) Toast.makeText(getActivity(), getString(R.string.error_password), Toast.LENGTH_SHORT).show();
                                    else {
                                        if (!(txtPassword.equals(txtConfirmPassword))) Toast.makeText(getActivity(), getString(R.string.password) + ", " + getString(R.string.password_confirm) + getString(R.string.match_error), Toast.LENGTH_SHORT).show();
                                        else {
                                            if (!checked) Toast.makeText(getActivity(), getString(R.string.select_error) + getString(R.string.accept_privacy), Toast.LENGTH_LONG).show();
                                            else {
                                                if (Location.checkLocation(getActivity(), txtCountry, txtRegion, txtProvince, txtCity)) {
                                                    ArrayList<String> location = new ArrayList<>(Arrays.asList(txtCountry, txtRegion, txtProvince, txtCity));
                                                    registration(txtEmail, txtPassword, location);
                                                    //TODO: insert data in database
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

       /*

                        //registration(txtEmail, txtPassword);
                        //TODO: insert data in database
                        String userId = auth.getUid();
                        if (userId != null) {
                            User.insertUserInDataBase(userId,txtUsername,txtName,txtSurname,location,basicImage);
                            //TODO: show profile
                        } else {
                            Log.d(ACTIVITY_TAG_NAME,"something went wrong while logging-in the user after the registration");
                            //TODO:back to signInUpFragment
                        }
                    }
                }
            }
        }*/
            Log.d("User", "0");
        }
    }

    /**
     * check the SDK version in order to handle the registration in background
     * @param email the provided email from the user
     * @param password the provided password from the user
     */
    void registration(String email, String password, ArrayList<String> location) {
        // TODO find out which is the eldest SDK version accepting concurrent
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            // Do something for R and above versions
            //using concurrent executors
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                //Background work here
                registerUser(email, password, location);
                handler.post(() -> {
                    //UI Thread work here
                    // TODO change the string
                    Toast.makeText(getActivity(), "using concurrent executors", Toast.LENGTH_SHORT).show();
                });
            });

        } else {
            // do something for phones running an SDK before R
            String[] values = new String[6];
            values[0] = email;
            values[1] = password;
            int i = 2;
            for(String s: location) {
                values[i] = s;
                i++;
            }
            new AsyncRegister().execute(values);
        }
    }

    /**
     * class to handle registration in asynchronous way before SDK R
     */
    @SuppressLint("StaticFieldLeak")
    private class AsyncRegister extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            ArrayList<String> location = new ArrayList<>(Arrays.asList(strings[2], strings[3], strings[4], strings[5]));
            registerUser(strings[0], strings[1], location);
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
    void registerUser(String email, String password, ArrayList<String> location) {
        //addOnCompleteListener is added to display a Toast for confirmation of the registration
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                //positive feedback
                Toast.makeText(getActivity(), getString(R.string.Registration) + getString(R.string.success), Toast.LENGTH_SHORT).show();
                //login of user
                loginUser(email, password);
                String userId = auth.getUid();
                if (userId != null) User.insertUserInDataBase(userId,txtUsername,txtName,txtSurname,location,basicImage);
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

    /**
     * take a picture for the new item after warning the user about the non persistence of their chosen options
     */
    private void takePicture() {
        //take a new picture
        Fragment f = new InsertNewUserFragment();
        Intent intent = new Intent(requireActivity(),MyCameraActivity.class);
        Bundle c = new Bundle();
        //give to the next activity the fully qualified name of this class in order to enable it to return here
        c.putString(getString(R.string.Bundle_tag_Previous_activity), "InsertNewUserFragment");
        intent.putExtras(c);
        //go to camera activity
        startActivity(intent);
    }
    private void takeNewPicture() {
        //pop up alert to warn about taking a new picture
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(getString(R.string.Alert_take_new_picture))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        takePicture();
                    }
                })
                .setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //return to setting the item details and don't take a new picture
                        dialog.cancel();
                    }
                }).show();

    }
}
