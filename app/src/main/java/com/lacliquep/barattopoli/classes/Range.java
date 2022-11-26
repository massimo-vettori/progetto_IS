package com.lacliquep.barattopoli.classes;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.*;
import java.util.function.Consumer;

/**
 * this class represent a Range of values to give to an Item
 */
public class Range {
    public static final String CLASS_RANGE_DB = "range";
    public static final String ID_RANGE_DB = "id_range";
    public static final String FROM_DB = "from";
    public static final String TO_DB = "to";
    public static final String ID_ITEMS_DB = "id_items";


    private final String idRange;
    private final Integer from;
    private final Integer to;


    private Range(String idRange, Integer from, Integer to) {
        this.idRange = idRange;
        this.from = from;
        this.to = to;
    }

    /**
     * a Set containing all the available ranges. Each one has its correspondence in the database. <p>
     * To add a new category, it needs to be added here and in the database
     */
    public static final Set<Range> ranges = new HashSet<>();
    {
        ranges.add(new Range("0",0,10));
        ranges.add(new Range("1",11,20));
        ranges.add(new Range("2",21,30));
        ranges.add(new Range("3",31,40));
        ranges.add(new Range("4",41,50));
    }
    public static Set<Range> getRanges() { return Range.ranges; }

    /**
     * Retrieve from the database the main information about the items which belong to the provided range
     * by saving them in a map which be manipulated by the consumer
     * @param context the activity/fragment where this method is called
     * @param idRange the range id
     * @param consumer the way the fetched data are being used
     */
    public static void getItemsIdAndInfo(Context context, String idRange, Consumer<Map<String, ArrayList<String>>> consumer) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Range.CLASS_RANGE_DB);
        DataBaseInteractor.getMapWithIdAndInfo(context, dbRef, idRange, Item.ITEM_INFO_LENGTH, consumer);
    }

    //TODO:equals-hashcode-getters

}


