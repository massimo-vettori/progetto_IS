package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    }

    protected void backToHome() {
        // This method is called when the back button is pressed
        finish();
    }

    protected void updateItemDescription(String desc) {
        // Finds the TextView for the item description and updates its text
        ((TextView) this.findViewById(R.id.description)).setText(desc);
    }

    protected void updateItemState(String state) {
        // Finds the TextView for the item state and updates its text
        ((TextView) this.findViewById(R.id.obj_state)).setText(state);
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
    protected void addImageToImageList(ImageView image) {
        // The horizontal linear layout containing the images
        LinearLayout imageList = findViewById(R.id.scrollable_images);
        // Adds the image to the imageList
        imageList.addView(image);
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