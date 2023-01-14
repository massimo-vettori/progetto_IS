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
import android.widget.ProgressBar;
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

/**
 * this activity performs the insertion of a new user in the database
 * @author pares, jack, gradiente
 * @since 1.0
 */
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
    private CheckBox checkBox, checkBoxAge;
    private ProgressBar progressBar;

    private String txtEmail = "", txtPassword = "", txtConfirmPassword = "", txtUsername = "", txtName = "", txtSurname = "", txtCountry = "", txtRegion = "", txtProvince = "", txtCity = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_insert_new_user, container, false);

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
        if(encodedImage[0].equals(basicImage)) takePicture();
        //set the image

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
        checkBoxAge = view.findViewById(R.id.check_box_age);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.INVISIBLE);


        register.setOnClickListener(v -> {
            //fetch text inserted in the fields
            txtEmail = insertEmail.getText().toString();
            txtPassword = insertPassword.getText().toString();
            txtConfirmPassword = confirmPassword.getText().toString();
            txtUsername = insertUsername.getText().toString();
            txtName = insertName.getText().toString();
            txtSurname = insertSurname.getText().toString();
            txtCountry = insertCountry.getText().toString();
            txtRegion = insertRegion.getText().toString();
            txtProvince = insertProvince.getText().toString();
            txtCity = insertCity.getText().toString();

            boolean checkedPrivacy = checkBox.isChecked();
            boolean checkedAge = checkBoxAge.isChecked();

            reg(checkedPrivacy, checkedAge, encodedImage[0]);
        });

        takePictureUser.setOnClickListener(v -> {
            takeNewPicture();
        });

        return view;
    }

    private void reg(boolean checkedPrivacy, boolean checkedAge, String image) {
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
                                            if (!checkedPrivacy) Toast.makeText(getActivity(), getString(R.string.select_error) + getString(R.string.accept_privacy), Toast.LENGTH_LONG).show();
                                            else {
                                                if (!checkedAge) Toast.makeText(getActivity(), getString(R.string.select_error) + getString(R.string.age_confirm), Toast.LENGTH_LONG).show();
                                                else {
                                                    if (Location.checkLocation(getActivity(), txtCountry, txtRegion, txtProvince, txtCity)) {
                                                        ArrayList<String> location = new ArrayList<>(Arrays.asList(txtCountry, txtRegion, txtProvince, txtCity));
                                                        registration(txtEmail, txtPassword, location, image);
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
            }
            Log.d("User", "0");
        }
    }

    /**
     * check the SDK version in order to handle the registration in background
     * @param email the provided email from the user
     * @param password the provided password from the user
     */
    private void registration(String email, String password, ArrayList<String> location, String image) {
        // TODO find out which is the eldest SDK version accepting concurrent
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            // Do something for R and above versions
            //using concurrent executors
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                //Background work here
                registerUser(email, password, location, image);
                handler.post(() -> {
                    //UI Thread work here
                    progressBar.setVisibility(View.VISIBLE);
                });
            });

        } else {
            // do something for phones running an SDK before R
            String[] values = new String[7];
            values[0] = email;
            values[1] = password;
            int i = 2;
            for(String s: location) {
                values[i] = s;
                i++;
            }
            values[6] = image;
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
            registerUser(strings[0], strings[1], location, strings[6]);
            //TODO delete or improve publishProgress
            for (int i = 0; i < 100; ++i) publishProgress(i);
            return null;
        }
        // TODO: add a progression bar or sth? delete or improve onProgressUpdate
        protected void onProgressUpdate(Integer... integers) {
            //Toast.makeText(getActivity(), getString(R.string.in_progress), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(integers[0]);
        }

    }
    /**
     * registration of the user in the database with {@link FirebaseAuth}
     * @param email the provided email
     * @param password the provided password
     */
    private void registerUser(String email, String password, ArrayList<String> location, String image) {
        //addOnCompleteListener is added to display a Toast for confirmation of the registration
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                //positive feedback
                Toast.makeText(getActivity(), getString(R.string.Registration) + getString(R.string.success), Toast.LENGTH_SHORT).show();
                //login of user
                loginUser(email, password);
                String userId = auth.getUid();
                if (userId != null) User.insertUserInDataBase(userId,txtUsername,txtName,txtSurname,location,image);
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
