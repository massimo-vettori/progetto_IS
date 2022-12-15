package com.lacliquep.barattopoli;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lacliquep.barattopoli.classes.BarattopoliUtil;
import com.lacliquep.barattopoli.fragments.sign.InsertNewUserFragment;

public class MyCameraActivity extends Activity
{
    //
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    //string-encoded image to pass back to the activity bundled with this activity

    //tag name for the logcat
    private static final String ACTIVITY_TAG_NAME = "MyCameraActivity";
    private static String con = "";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);
        Button photoButton = (Button) this.findViewById(R.id.take_picture);
        Button preferNotTo = (Button) this.findViewById(R.id.no);
        Bundle c = getIntent().getExtras();
        //fetching the previous activity class name

        if (c != null) {
            con = c.getString(getString(R.string.Bundle_tag_Previous_activity));
            con = (con != null) ? con : "";
            photoButton.setText(getString(R.string.take_profile_picture));
        }
        photoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO: enable permissions in manifest
                /*if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else*/
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent,CAMERA_REQUEST);
                }
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //photo encoding as a string
            String image = BarattopoliUtil.encodeImageToBase64(photo);
            //passing the encoded image back to the previous activity
            //create a bundle between the two activities
            changeActivity(image);

        }
    }
    private void changeActivity(String encodedImage) {
        Intent intent;
        if (con.equals("InsertNewUserFragment")) {
            Bundle b = new Bundle();
            b.putString(getString(R.string.Bundle_tag_encoded_image), encodedImage);
            b.putInt("goToInsertNewUserFragment", 1);
            intent = new Intent(MyCameraActivity.this, SignActivity.class);
            intent.putExtras(b);
        } else {
            try {
                //get the class name of the previous activity
                Class<?> cls = Class.forName(con);
                //attach a string with the image encoding to pass it back
                Bundle b = new Bundle();
                b.putString(getString(R.string.Bundle_tag_encoded_image), encodedImage);
                //create intent to came back to the previous activity
                intent = new Intent(MyCameraActivity.this, cls);
                //attach the string
                intent.putExtras(b);
            } catch (ClassNotFoundException e) {
                //don't loop please, go back to the main activity if sth went wrong
                intent = new Intent(MyCameraActivity.this, MainActivity.class);
                //TODO:change in alert?
                //logcat when debugging
                Log.d(MyCameraActivity.ACTIVITY_TAG_NAME, e.getMessage());
            }
        }
        startActivity(intent);
        finish();
    }
}