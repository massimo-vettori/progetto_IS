package com.lacliquep.barattopoli.classes;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lacliquep.barattopoli.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * this class provides methods to interact with the database
 * @author pares
 * @since 1.0
 */
public class BarattopoliUtil {

    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final String BLANK_IMAGE = "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";

    //funziona
    /**
     * map from the database a list of id and their values which represent the children of
     * the snapshot at the provided database reference <p>
     * Don't try taking out data from the consumer: it is not going to work
     * @param contextTag the string representing the Activity/Fragment where this method is called
     * @param dbRef a reference to the database location of the father node
     * @param consumer the way the fetched data are being used
     */
    public static void mapChildren(String contextTag, DatabaseReference dbRef, Consumer<Map<String, String>> consumer) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, String> children = new HashMap<>();
                    for(DataSnapshot child: snapshot.getChildren()) {
                        children.put(child.getKey(), child.getValue().toString());
                    }
                    consumer.accept(children);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(contextTag, "load:onCancelled", error.toException());
            }
        });
    }

    //funziona
    /**
     * read from the database a value at the given location <p>
     * Don't try taking out data from the consumer: it is not going to work
     * @param contextTag the string representing the activity/fragment where this method is called
     * @param dbRef the database location
     * @param consumer the way the fetched data are being used
     */
    public static void readData(String contextTag, DatabaseReference dbRef, Consumer<Object> consumer) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) consumer.accept(snapshot.getValue());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(contextTag, "load:onCancelled", error.toException());
            }
        });
    }

    /**
     * Retrieve from the database the keys and their main information at the specified location in the database,
     * by using the provided database reference and its child id <p>
     * the data will be saved in a map which can be manipulated by the consumer <p>
     * Retrieve the collection (param id) of those elements which are represented in a database class with a key,
     * correspondent to their id and a string in CSV format with the basic info to be displayed <p>
     * Too many database queries just to fetch simple data are avoided
     * @param contextTag the string representing the activity/fragment where the method is called
     * @param dbRef the database location which identifies the parent of id
     * @param id the parent of the elements which will be stored in the map
     * @param infoLength the length of the main data stored for the specified id
     * @param consumer the way the data is being used
     */
    public static void getMapWithIdAndInfo(String contextTag, DatabaseReference dbRef, String id, int infoLength, Consumer<Map<String, ArrayList<String>>> consumer) {
        dbRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String,ArrayList<String>> idAndInfo= new HashMap<>();
                if(snapshot.exists() && snapshot.hasChildren()) {
                    for(DataSnapshot id: snapshot.getChildren()) {
                        String key = id.getKey();
                        Object info = id.getValue();
                        ArrayList<String> tmp = new ArrayList<>();
                        if (info != null) {
                            String toSplit = info.toString();
                            String[] tmpAr = toSplit.split(",", infoLength);
                            tmp.addAll(Arrays.asList(tmpAr).subList(0, infoLength));
                            idAndInfo.put(key, tmp);
                        }
                    }
                    Log.d("getMapWithIdAndInfo", idAndInfo.toString());
                }
                consumer.accept(idAndInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(contextTag, "load:onCancelled", error.toException());
            }
        });
    }

    //FUNZIONA
    public static void retrieveHelper(Map<String, Object> map, String dataBaseKeyTag, ArrayList<String> mainData, int mainDataIndex) {
        if (map.containsKey(dataBaseKeyTag)) {
            Object value = map.get(dataBaseKeyTag);
            mainData.set(mainDataIndex, value != null ? value.toString() : "");
        } else mainData.set(mainDataIndex, "");
    }

    //to convert an image into a Base64 string
    public static String encodeImageToBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encodedFile = new String(Base64.getEncoder().encode(byteArray));
        return encodedFile;
    }

    // TODO: Solve string compatibility with b64 format
    public static Bitmap decodeFileFromBase64(@Nullable String encodedImage) {
        String b64 = encodedImage != null ? encodedImage : BarattopoliUtil.BLANK_IMAGE;
        byte[] decodedString = Base64.getDecoder().decode(b64);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static Bitmap decodeDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        return null;
    }

    public static ArrayList<String> listOfIdFromMap(Map<String, ArrayList<String>> idAndInfoMap) {
        Set<String> keyset = idAndInfoMap.keySet();
        ArrayList<String> keylist = new ArrayList<>();
        keylist.addAll(keyset);
        return keylist;
    }
    /**
     * show a message if some of the mandatory EditText fields are empty
     * @param context the Fragment/Activity where this method is called
     * @param mandatoryField the field that has not to be empty
     * @param correspondentErrorMessage the correspondent error message to display with a Toast
     */
    public static boolean checkMandatoryTextIsNotEmpty (Context context, String mandatoryField, String correspondentErrorMessage) {
        boolean ok = true;
            if (mandatoryField.equals("")) {
                Log.d("User", "1");
                Toast.makeText(context, correspondentErrorMessage, Toast.LENGTH_SHORT).show();
                ok = false;
            }
        return ok;
    }




    //TODO: ALL the retrieve, all the insert new and all the update in the classes

}
