package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

}