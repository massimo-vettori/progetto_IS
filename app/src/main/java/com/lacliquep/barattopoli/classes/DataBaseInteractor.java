package com.lacliquep.barattopoli.classes;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * this class provides methods to interact with the database
 * @author pares
 * @since 1.0
 */
public class DataBaseInteractor {

    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //funziona
    /**
     * read from the database a list of id which represent the children of
     * the snapshot at the provided database reference <p>
     * Don't try taking out data from the consumer: it is not going to work
     * @param context the activity where this method is called
     * @param dbRef a reference to the database location
     * @param consumer the way the fetched data are being used
     */
    public static void readListOfId(Context context, DatabaseReference dbRef, Consumer<ArrayList<String>> consumer) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<String> children = new ArrayList<>();
                    for(DataSnapshot child: snapshot.getChildren()) {
                        children.add(child.getKey());
                    }
                    consumer.accept(children);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(context.toString(), "load:onCancelled", error.toException());
            }
        });
    }

    //funziona
    /**
     * read from the database a value at the given location <p>
     * Don't try taking out data from the consumer: it is not going to work
     * @param context the activity/fragment where this method is called
     * @param dbRef the database location
     * @param consumer the way the fetched data are being used
     */
    public static void readData(Context context, DatabaseReference dbRef, Consumer<Object> consumer) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) consumer.accept(snapshot.getValue());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(context.toString(), "load:onCancelled", error.toException());
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
     * @param context the activity/fragment where the method is called
     * @param dbRef the database location which identifies the parent of id
     * @param id the parent of the elements which will be stored in the map
     * @param infoLength the length of the main data stored for the specified id
     * @param consumer the way the data is being used
     */
    public static void getMapWithIdAndInfo(Context context, DatabaseReference dbRef, String id, int infoLength, Consumer<Map<String, ArrayList<String>>> consumer) {
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
                            for (int i = 0; i < infoLength; ++i) tmp.add(tmpAr[i]);
                            idAndInfo.put(key, tmp);
                        }
                    }
                }
                consumer.accept(idAndInfo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(context.toString(), "load:onCancelled", error.toException());
            }
        });
    }

    //TODO: ALL the retrieve, all the insert new and all the update in the classes

}
