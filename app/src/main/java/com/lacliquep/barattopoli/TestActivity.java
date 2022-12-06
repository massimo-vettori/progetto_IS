package com.lacliquep.barattopoli;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lacliquep.barattopoli.classes.DataBaseInteractor;
import com.lacliquep.barattopoli.classes.Item;
import com.lacliquep.barattopoli.classes.User;
import com.lacliquep.barattopoli.views.ItemView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class TestActivity extends AppCompatActivity {

    //just to try images
    final private static String basicImage = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";

    //display an Item
    final public static DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    View view;
    TextView topText;
    ImageView imageContainer;
    TextView bottomText;
    Button takePicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //retrieving a previous activity value attached to the bundle
        Bundle b = getIntent().getExtras();
        String[] encodedImage = new String[1]; // or other values
        encodedImage[0] = (b != null)? b.getString("encodedImage"):basicImage;

        view = findViewById(R.id.scrollview);
        topText = findViewById(R.id.top_text);
        bottomText = findViewById(R.id.bottom_text);
        imageContainer = findViewById(R.id.image_container);
        takePicture = findViewById(R.id.take_picture);

        takePicture.setOnClickListener(view1 -> {
            Intent intent = new Intent(TestActivity.this, MyCameraActivity.class);
            Bundle c = new Bundle();
            //give to the next activity the fully qualified name of this class
            c.putString("previous activity", TestActivity.this.getClass().getCanonicalName()); //Your id
            intent.putExtras(c); //Put your id to your next Intent
            startActivity(intent);
        });

        User.retrieveCurrentUser(TestActivity.this, dbRef, new Consumer<User>() {
            @Override
            public void accept(User user) {
                User.getItemsOnBoard(TestActivity.this, user.getIdUser(), new Consumer<Map<String, ArrayList<String>>>() {
                    @Override
                    public void accept(Map<String, ArrayList<String>> stringArrayListMap) {
                        if (!(stringArrayListMap.isEmpty())) {
                            //retrieve the id of the first item on board
                            String firstItemId = DataBaseInteractor.listOfIdFromMap(stringArrayListMap).get(0);
                            //retrieve its basic Info
                            ArrayList<String> firstItemInfo = stringArrayListMap.get(firstItemId);
                            if (firstItemInfo.size() >= Item.INFO_LENGTH) {
                                topText.setText(firstItemInfo.get(7));
                                bottomText.setText(firstItemInfo.get(0));
                                Bitmap bm = DataBaseInteractor.decodeFileFromBase64(firstItemInfo.get(2));
                                if (bm != null)
                                    imageContainer.setImageBitmap(DataBaseInteractor.decodeFileFromBase64(firstItemInfo.get(2)));
                            }
                        } else {
                            Log.d("TAG", "emptyMap"); //TODO: substitute with sth like "no loaded items" instead of view
                            imageContainer.setImageBitmap(DataBaseInteractor.decodeFileFromBase64(encodedImage[0]));
                        }

                    }
                });
            }
        });
        /* sample of how to use the DataBaseInteractor methods: do not try taking out the values from the consumer
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(User.CLASS_USER_DB);
            DataBaseInteractor.retrieveUserById(MainActivity.this, dbRef, "mmNsy71Nf5e8ATR79b4LNk3uRSh1", new Consumer<User>() {
                @Override
                public void accept(User u) {
                    Toast.makeText(MainActivity.this, u.getUsername(), Toast.LENGTH_LONG).show();
                }
            });
         */
    }
}