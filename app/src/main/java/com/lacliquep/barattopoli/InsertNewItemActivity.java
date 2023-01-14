package com.lacliquep.barattopoli;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lacliquep.barattopoli.classes.BarattopoliUtil;
import com.lacliquep.barattopoli.classes.Range;
import com.lacliquep.barattopoli.classes.User;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public class InsertNewItemActivity extends AppCompatActivity {

    //tag name for the logcat
    final private static String ACTIVITY_TAG_NAME = "InsertNewItemActivity";
    //just a basic image
    final private static String basicImage = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";
    //display an Item
    final private static DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    //xml elements
    private EditText insertDescription, insertTitle, insertLocation;
    private ImageView imageContainer;
    private Button insert, takePicture;
    private CheckBox tech, fun, handmade, houseAndGarden, games, food, school, clothes;
    private RadioButton range0, range1, range2, range3, range4;
    private CheckBox isCharity, isService;

    //fields for creating the new item
    private String title, description, idRange = "", rangeDescription, location;
    private boolean isForCharity, isAService;
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> owner = new ArrayList<>();


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_new_item);
        //retrieving a previous activity value attached to the bundle
        Bundle b = getIntent().getExtras();
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

        /*recreating an array to create the item
        owner = new ArrayList<>(Arrays.asList(userBasicInfo.split(",", User.INFO_LENGTH)));
        Log.d("TAG", owner.toString());*/

        //INITIALIZE XML ELEMENTS

        //EditTexts
        insertDescription = findViewById(R.id.description);
        insertTitle = findViewById(R.id.title);
        //insertLocation = findViewById(R.id.location); //TODO: substitute with user's location

        //ImageView
        imageContainer = findViewById(R.id.image_container);

        //Buttons
        takePicture = findViewById(R.id.take_picture);
        insert = findViewById(R.id.insert);

        //categories (check_boxes)
        fun = findViewById(R.id.fun);
        tech = findViewById(R.id.tech);
        handmade = findViewById(R.id.Handmade);
        houseAndGarden = findViewById(R.id.House_and_garden);
        games = findViewById(R.id.Games);
        school = findViewById(R.id.school);
        food = findViewById(R.id.Food);
        clothes = findViewById(R.id.Clothes);

        //radio buttons for range choice (mutually exclusive choice)
        range0 = findViewById(R.id.range0);
        range1 = findViewById(R.id.range1);
        range2 = findViewById(R.id.range2);
        range3 = findViewById(R.id.range3);
        range4 = findViewById(R.id.range4);

        //check_boxes
        isCharity = findViewById(R.id.is_charity);
        isService = findViewById(R.id.is_service);

        imageContainer.setImageBitmap(BarattopoliUtil.decodeFileFromBase64(encodedImage[0]));

        //set the correct text to display for the radio buttons
        try {
            range0.setText(getString(R.string.range, getString(R.string.From), Range.getFrom("0"), getString(R.string.To) ,Range.getTo("0")));
            range1.setText(getString(R.string.range, getString(R.string.From), Range.getFrom("1"), getString(R.string.To) ,Range.getTo("1")));
            range2.setText(getString(R.string.range, getString(R.string.From), Range.getFrom("2"), getString(R.string.To) ,Range.getTo("2")));
            range3.setText(getString(R.string.range, getString(R.string.From), Range.getFrom("3"), getString(R.string.To) ,Range.getTo("3")));
            range4.setText(getString(R.string.range, getString(R.string.From), Range.getFrom("4"), getString(R.string.To) ,Range.getTo("4")));
        } catch (Range.noSuchRangeException e) {
            Log.d(InsertNewItemActivity.ACTIVITY_TAG_NAME, e.getMessage());
        }

        //when user presses insert button after setting the item details
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: understand why it does not work if wrapped in asyncTask
                insertButton(encodedImage[0]);
        }});


        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: understand why it does not work if wrapped in asyncTask
                takeNewPicture();
            }});

    }


    /**
     * take a picture for the new item after warning the user about the non persistence of their chosen options
     */
    private void takePicture() {
        //take a new picture
        Intent intent = new Intent(InsertNewItemActivity.this, MyCameraActivity.class);
        Bundle c = new Bundle();
        //give to the next activity the fully qualified name of this class in order to enable it to return here
        c.putString(getString(R.string.Bundle_tag_Previous_activity), InsertNewItemActivity.this.getClass().getCanonicalName());
        intent.putExtras(c);
        //go to camera activity
        startActivity(intent);
        finish();
    }
    private void takeNewPicture() {
        //pop up alert to warn about taking a new picture
        AlertDialog.Builder builder = new AlertDialog.Builder(InsertNewItemActivity.this);
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

    /**
     * fetch the user's settings for the new item, check if the mandatory ones have been choose and, eventually,
     * insert them in the database
     * @param image the image of the new Item
     */
    private void insertButton(String image) {
        //fetching the user's choices
        String insertedTitle = insertTitle.getText().toString();
        String insertedDescription = insertDescription.getText().toString();
        //String insertedLocation = insertLocation.getText().toString();
        isForCharity = isCharity.isChecked();
        isAService = isService.isChecked();
        //sanity checks
        if(insertedTitle.equals("")) Toast.makeText(InsertNewItemActivity.this, "title missing", Toast.LENGTH_LONG).show();
        else {
            title = insertedTitle;
            if(insertedDescription.equals("")) Toast.makeText(InsertNewItemActivity.this, "description missing", Toast.LENGTH_LONG).show();
            else {
                description = insertedDescription;
                /*if(insertedLocation.equals("")) Toast.makeText(InsertNewItemActivity.this, "location missing", Toast.LENGTH_LONG).show();
                else*/ {
                    //location = insertedLocation;
                    if(tech.isChecked()) categories.add("Tech");
                    if(clothes.isChecked()) categories.add("Clothes");
                    if(houseAndGarden.isChecked()) categories.add("House_and_garden");
                    if(handmade.isChecked()) categories.add("Handmade");
                    if(games.isChecked()) categories.add("Games");
                    if(food.isChecked()) categories.add("Food");
                    if(school.isChecked()) categories.add("School");
                    if(fun.isChecked()) categories.add("Fun");
                    if(categories.isEmpty()) Toast.makeText(InsertNewItemActivity.this, "insert at least one category", Toast.LENGTH_LONG).show();
                    else {
                        if (idRange.equals("") && !isForCharity) Toast.makeText(InsertNewItemActivity.this, "choose a range or set as charity", Toast.LENGTH_LONG).show();
                        else {
                            //everything mandatory has been chosen: set the item_preview
                            View itemPreview = View.inflate(InsertNewItemActivity.this, R.layout.item_preview, null);

                            TextView displayTitle = itemPreview.findViewById(R.id.title);
                            TextView displayDescription = itemPreview.findViewById(R.id.description);
                            TextView displayLocation = itemPreview.findViewById(R.id.location);
                            TextView displayCategories = itemPreview.findViewById(R.id.categories);
                            TextView displayService = itemPreview.findViewById(R.id.service);
                            TextView displayRange = itemPreview.findViewById(R.id.range);
                            ImageView displayImage = itemPreview.findViewById(R.id.image_container);

                            displayTitle.setText(title);
                            displayDescription.setText(description);
                            displayLocation.setText(location);
                            displayCategories.setText(categories.toString());
                            displayService.setText(isAService? "Service": "Item");
                            displayRange.setText(isForCharity? "Charity": rangeDescription);
                            displayImage.setImageBitmap(BarattopoliUtil.decodeFileFromBase64(image));

                            //alert pop up to confirm the insert
                            AlertDialog.Builder builder = new AlertDialog.Builder(InsertNewItemActivity.this);
                            //display the item preview and insert in database if yes is clicked
                            builder.setMessage(getString(R.string.Alert_insert_item))
                                    .setView(itemPreview)
                                    .setCancelable(false)
                                    .setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //insert the item in the database
                                            images.add(image);
                                            User.retrieveCurrentUser(InsertNewItemActivity.ACTIVITY_TAG_NAME, dbRef, new Consumer<User>() {
                                                @Override
                                                public void accept(User user) {
                                                    //set here the owner's basic info (otherwise it won't be fetched)
                                                    //set the location to be the same as the user's automatically for now
                                                    //TODO: further improvement: add the same thing in insertUserActivity for choosing a different location
                                                    ArrayList<String> userLocation = user.getLocation();
                                                    ArrayList<String> owner = new ArrayList<>(Arrays.asList(user.getUserBasicInfo().split(",", 0)));
                                                    User.addNewItemOnBoard(InsertNewItemActivity.ACTIVITY_TAG_NAME, title,description,idRange,owner, userLocation, isForCharity,isAService,categories, images);
                                                    Toast.makeText(InsertNewItemActivity.this, getString(R.string.inserting_a_new_item) + getString(R.string.success), Toast.LENGTH_LONG).show();
                                                    //back to the main activity //TODO: back to the "OnBoardActivity
                                                    startActivity(new Intent(InsertNewItemActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //return to set the item details
                                            dialog.cancel();
                                        }
                                    }).show();
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        String res = "";
        String id = "0";
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.range0:
                id = "0";
                if (checked) {
                    try {
                        res = getString(R.string.range, getString(R.string.From), Range.getFrom("0"), getString(R.string.To) ,Range.getTo("0"));
                    } catch(Range.noSuchRangeException e){
                        Log.d(InsertNewItemActivity.ACTIVITY_TAG_NAME, e.getMessage());
                    }
                }
                    break;
            case R.id.range1:
                id = "1";
                if (checked)
                    try {
                        res = getString(R.string.range, getString(R.string.From), Range.getFrom("1"), getString(R.string.To) ,Range.getTo("1"));
                    } catch(Range.noSuchRangeException e){
                        Log.d(InsertNewItemActivity.ACTIVITY_TAG_NAME, e.getMessage());
                    }
                    break;
            case R.id.range2:
                id = "2";
                if (checked)
                    try {
                        res = getString(R.string.range, getString(R.string.From), Range.getFrom("2"), getString(R.string.To) ,Range.getTo("2"));
                    } catch(Range.noSuchRangeException e){
                        Log.d(InsertNewItemActivity.ACTIVITY_TAG_NAME, e.getMessage());
                    }
                    break;
            case R.id.range3:
                id = "3";
                if (checked)
                    try {
                        res = getString(R.string.range, getString(R.string.From), Range.getFrom("3"), getString(R.string.To) ,Range.getTo("3"));
                    } catch(Range.noSuchRangeException e){
                        Log.d(InsertNewItemActivity.ACTIVITY_TAG_NAME, e.getMessage());
                    }
                    break;
            case R.id.range4:
                id = "4";
                if (checked)
                    try {
                        res = getString(R.string.range, getString(R.string.From), Range.getFrom("4"), getString(R.string.To) ,Range.getTo("4"));
                    } catch(Range.noSuchRangeException e){
                        Log.d(InsertNewItemActivity.ACTIVITY_TAG_NAME, e.getMessage());
                    }
                    break;
        }
        this.idRange = id;
        this.rangeDescription = res;
    }


}