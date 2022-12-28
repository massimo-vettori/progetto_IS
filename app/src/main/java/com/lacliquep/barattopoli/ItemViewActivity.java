package com.lacliquep.barattopoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.classes.Ownership;
import com.lacliquep.barattopoli.classes.Range;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ItemViewActivity extends AppCompatActivity {

    protected String caller;

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
//        (Failed attempt to serialize/deserialize the item)
//        Item item       = Item.deserialize(intent.getCharSequenceArrayExtra("item"));
        Item item = (Item) intent.getSerializableExtra("item");

        // Get the ownership of the item from the intent

        Ownership owner = Ownership.from(intent.getStringExtra("owner"));
        // Get the calling activity from the intent
        caller = intent.getStringExtra("caller");

        // Updates the UI with the item's data
        if (owner == null) owner = Ownership.OTHER; // If the owner is null, set it to OTHER, in order to prevent a NullPointerException
        if (item != null) this.setup(item, owner);
    }

    public static String rangeString(@NonNull Item item, Context c) {
        int to = 0, from = 0;
        try {
            from = Range.getFrom(item.getIdRange());
            to = Range.getTo(item.getIdRange());
        } catch (Range.noSuchRangeException e) {}
       return(c.getString(R.string.range, c.getString(R.string.From), from , c.getString(R.string.To) , to));
    }

    protected void setup(@NonNull Item item, @NonNull Ownership owner) {
        updateItemDescription(item.getDescription());
        updateItemTitle(item.getTitle());
        updateItemLocation(item.getLocation()); //TODO:convert string fetch in array fetch
        updatePriceRange(rangeString(item, this));
        updateOwner(item.getOwner());
        //added
        updateUserAvatar(item.getOwnerImage());
        ImageView imageView1 = findViewById(R.id.imageView1);
        imageView1.setImageBitmap(item.getFirstImage());
        //updateUserName(item.getOwner().stream().reduce("", (a, b) -> a + " " + b));

        if (owner == Ownership.PERSONAL) {
            Button delete = findViewById(R.id.propose_btn);
            delete.setText(R.string.delete_item);
            delete.setBackgroundColor(getResources().getColor(R.color.red_500, null));
            delete.setTextColor(getResources().getColor(R.color.zinc_50, null));
            delete.setOnClickListener(view -> {
                Item.deleteItem(item);
                ItemViewActivity.this.backToHome();
            });
        }


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
        ((TextView) this.findViewById(R.id.item_title)).setText(title);
    }

    protected void updateItemLocation(ArrayList<String> loc) {
        // Finds the TextView for the item location and updates its text

        ((TextView) this.findViewById(R.id.location)).setText(
                loc.toString()
        );
    }

    protected void updateOwner(Collection<String> prop) {
        ArrayList<String> user = new ArrayList<>(prop);
        if (user.isEmpty() || user.size() < 3) return;

        ((TextView) this.findViewById(R.id.user_name)).setText(
                user.get(3)
        );
    }

    protected void updateUserAvatar(Bitmap avatar) {
        // Finds the ImageView for the user avatar and updates its image
        if (avatar == null) return;
        ((ImageView) this.findViewById(R.id.user_avatar)).setImageBitmap(avatar);
    }

    protected void updatePriceRange(String priceRange) {
        // Finds the TextView for the price range and updates its text
        ((TextView) this.findViewById(R.id.item_price_range)).setText(priceRange);
    }

    protected void updateUserName(String name) {
        // Finds the TextView for the user name and updates its text
        ((TextView) this.findViewById(R.id.user_name)).setText(name);
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