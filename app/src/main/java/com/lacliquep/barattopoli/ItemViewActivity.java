package com.lacliquep.barattopoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lacliquep.barattopoli.classes.Item;

import java.util.Objects;

public class ItemViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);
        Objects.requireNonNull(getSupportActionBar()).hide();

        disableProposeButton(); // This disables the propose button to prevent barter proposals until they are properly implemented

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> {
            ItemViewActivity.this.backToHome();
        });

        // Get the intent that started this activity
        Intent intent = getIntent();
        // Get the serialized item from the intent and deserialize it into an Item object
        Item item = (Item) intent.getSerializableExtra("item");
        // Updates the UI with the item's data
        if (item != null) this.setup(item);
    }

    protected void setup(@NonNull Item item) {
        updateItemDescription(item.getDescription());
        updateItemTitle(item.getTitle());
        //updateItemLocation(item.getLocation()); //TODO:convert string fetch in array fetch

//        TODO: either implement image decoding or add methods to Item and User classes to get the images as a Bitmap
//        updateUserAvatar(item.getOwner());
//        for (Bitmap image : item.getImages()) {
//            addImageToImageList(image);
//        }
    }



    protected void backToHome() {
        // This method is called when the back button is pressed
        finish();
    }

    protected void updateItemDescription(String desc) {
        // Finds the TextView for the item description and updates its text
        ((TextView) this.findViewById(R.id.description)).setText(desc);
    }

    protected void updateItemTitle(String title) {
        // Finds the TextView for the item title and updates its text
        ((TextView) this.findViewById(R.id.title)).setText(title);
    }

    protected void updateItemLocation(String location) {
        // Finds the TextView for the item location and updates its text
        ((TextView) this.findViewById(R.id.location)).setText(location);
    }

    protected void updateUserAvatar(Bitmap avatar) {
        // Finds the ImageView for the user avatar and updates its image
        ((ImageView) this.findViewById(R.id.user_avatar)).setImageBitmap(avatar);
    }

    protected void enableProposeButton() {
        // Enables the propose button
        this.findViewById(R.id.propose_btn).setEnabled(true);
    }

    protected void disableProposeButton() {
        // Disables the propose button
        this.findViewById(R.id.propose_btn).setEnabled(false);
    }

    /**
     * Returns the list of images in the horizontal image scroller
     * @return ImageView[] containing the images in the horizontal image scroller
     */
    protected ImageView[] getImageList() {
        // The horizontal linear layout containing the images
        LinearLayout imageList = findViewById(R.id.scrollable_images);
        // Creates an array of ImageView objects with the same length as the number of children of the imageList
        ImageView[] images = new ImageView[imageList.getChildCount()];

        // Iterates through the children of the imageList and adds them to the images array
        for (int i = 0; i < imageList.getChildCount(); i++) {
            images[i] = (ImageView) imageList.getChildAt(i);
        }

        // Returns the array of ImageView objects
        return images;
    }

    /**
     * Appends an ImageView to the horizontal image scroller
     * @param image The ImageView to append to the horizontal image scroller
     * @implNote Do not call this method in any "drawing" methods, such as draw(), onDraw(), onMeasure(), etc.
     */
    protected void addImageToImageList(Bitmap image) {
        // Creates a new ImageView object
        ImageView img = new ImageView(this);
        // Sets the image of the ImageView to the image passed as a parameter
        img.setImageBitmap(image);
        // The horizontal linear layout containing the images
        LinearLayout imageList = findViewById(R.id.scrollable_images);
        // Adds the image to the imageList
        imageList.addView(img);
    }

    /**
     * Removes an ImageView from the horizontal image scroller
     * @param image The ImageView to remove from the horizontal image scroller
     * @implNote Do not call this method in any "drawing" methods, such as draw(), onDraw(), onMeasure(), etc.
     */
    protected void removeImageFromImageList(ImageView image) {
        // The horizontal linear layout containing the images
        LinearLayout imageList = findViewById(R.id.scrollable_images);
        // Removes the image from the imageList
        imageList.removeView(image);
    }
}