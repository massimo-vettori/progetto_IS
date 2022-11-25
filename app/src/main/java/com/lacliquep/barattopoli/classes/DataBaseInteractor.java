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




    //TODO: ALL the retrieve, all the insert new and all the update



    /*//TODO: finire e fare un check a database
    public void insertNewReviewInDataBase(@NonNull Exchange exchange, Integer stars, String text) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("exchanges").child(exchange.getIdExchange()).child("exchange_status").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            //TODO: controllo dello stato dello scambio nel DB? meglio farlo in fetch di exchange??
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    String currUser = FirebaseAuth.getInstance().getUid();
                    String fetched_status = String.valueOf(task.getResult().getValue());
                    if (fetched_status.equals(String.valueOf(Exchange.ExchangeStatus.HAPPENED))) {
                        //TODO insert review
                        if (exchange.getApplicant().getIdUser().equals(currUser)) {
                            //TODO: mark exchange as reviewed by applicant
                        } else {
                            //mark exchange as reviewed by proposer
                        }
                    } else {
                        if (exchange.getApplicant().getIdUser().equals(currUser)) {
                            if(fetched_status.equals(String.valueOf(Exchange.ExchangeStatus.REVIEWED_BY_PROPOSER))) {
                                //TODO: mark exchange as reviewed by both and consequences
                            } else {
                                //error
                            }
                        } else {
                            if (exchange.getProposer().getIdUser().equals(currUser)) {
                                if(fetched_status.equals(String.valueOf(Exchange.ExchangeStatus.REVIEWED_BY_APPLICANT))) {
                                    //TODO: mark exchange as reviewed by both and consequences
                                } else {
                                    //error
                                }
                            }
                        }

                    };
                }
            }
        });
        //mDatabase.child("exchanges").child(exchange.getIdExchange()).child("exchange_status").addValueEventListener(ExchangeStatusListener);
    }*/
}
