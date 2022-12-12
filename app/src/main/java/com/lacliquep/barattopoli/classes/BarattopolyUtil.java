package com.lacliquep.barattopoli.classes;

import static android.provider.Settings.System.getString;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lacliquep.barattopoli.InsertNewItemActivity;
import com.lacliquep.barattopoli.MainActivity;
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
public class BarattopolyUtil {

    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
                            String[] tmpAr = toSplit.split(toSplit, infoLength);
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

    public static Bitmap decodeFileFromBase64(String encodedImage) {
        byte[] decodedString = Base64.getDecoder().decode(encodedImage);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
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
     * @param mandatoryFields a List of the mandatory fields
     * @param correspondentErrorMessages a List of their correspondent error messages to display with a Toast
     */
    public static void checkMandatoryTextIsNotEmpty (Context context, ArrayList<String> mandatoryFields, ArrayList<String> correspondentErrorMessages) {
        for (int i = 0; i < mandatoryFields.size(); ++i) {
            if (TextUtils.isEmpty(mandatoryFields.get(i)))
                Toast.makeText(context, correspondentErrorMessages.get(i), Toast.LENGTH_SHORT).show();
        }
    }

    private static void displayAlertLocation(Context context, String typeOfLocation, String country, String province, String region) {
        String alertMessage = "";
        ArrayList<String> availableToDisplay = new ArrayList<>();
        switch (typeOfLocation) {
            case "country":
                alertMessage = context.getString(R.string.insert_country);
                availableToDisplay = Location.getAvailableCountries();
                break;
            case "region":
                alertMessage = context.getString(R.string.insert_region);
                try {
                    availableToDisplay = Location.getAvailableRegionsForCountry(country);
                } catch (Location.noSuchCountryException e) { Log.d(context.toString(), e.getMessage());}
                break;
            case "province":
                alertMessage = context.getString(R.string.insert_province);
                try {
                    availableToDisplay = Location.getAvailableProvincesForRegion(region, country);
                } catch (Location.noSuchRegionException e) { Log.d(context.toString(), e.getMessage());}
                break;
            case "city":
                alertMessage = context.getString(R.string.insert_city);
                try {
                    availableToDisplay = Location.getAvailableCitiesForProvince(province, region, country);
                } catch (Location.noSuchProvinceException e) { Log.d(context.toString(), e.getMessage());}
                break;
            default: Log.d(context.toString(), "in displayAlertLocation param typeOfLocation is not correct");
        }
        alertMessage += context.getString(R.string.wrong_choice);
        for (String avail: availableToDisplay) {
            alertMessage += ("\n" + avail);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //display the item preview and insert in database if yes is clicked
        builder.setMessage(alertMessage)
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.understood), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }}).show();

}

    public static void checkLocation(Context context, String country, String region, String province, String city) {
        if (!(Location.getAvailableCountries().contains(country))) { //alert pop up to confirm the insert
            displayAlertLocation(context, "country", country, province, region);
        } else {
            try {
                if (!(Location.getAvailableRegionsForCountry(country).contains(region))) {
                    displayAlertLocation(context, "region", country, province, region);
                } else {
                    try {
                        if (!(Location.getAvailableProvincesForRegion(region, country).contains(province))) {
                            displayAlertLocation(context, "province", country, province, region);
                        } else {
                            try {
                                if (!(Location.getAvailableCitiesForProvince(province,region, country).contains(city))) {
                                    displayAlertLocation(context, "city", country, province, region);
                                }
                            } catch (Location.noSuchProvinceException e) { Log.d(context.toString(), e.getMessage());}
                        }
                    } catch (Location.noSuchRegionException e) { Log.d(context.toString(), e.getMessage());}
                }
            } catch (Location.noSuchCountryException e) { Log.d(context.toString(), e.getMessage());}
        }
    }



    //TODO: ALL the retrieve, all the insert new and all the update in the classes

}
