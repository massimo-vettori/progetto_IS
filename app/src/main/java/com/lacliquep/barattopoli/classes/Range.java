package com.lacliquep.barattopoli.classes;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.*;
import java.util.function.Consumer;

/**
 * this class represents a Range of values to give to an Item
 * @author pares
 * @since 1.0
 */
public class Range {
    //macros with the name of the classes in the database
    public static final String CLASS_RANGE_DB = "range";
    public static final String ID_RANGE_DB = "id_range";
    public static final String FROM_DB = "from";
    public static final String TO_DB = "to";
    public static final String ID_ITEMS_DB = "id_items";

    //database location of the class Range
    public static final DatabaseReference dbRefRange = FirebaseDatabase.getInstance().getReference().child(Range.CLASS_RANGE_DB);

    //private fields
    private final String idRange;
    private final Integer from;
    private final Integer to;


    private Range(String idRange, Integer from, Integer to) {
        this.idRange = idRange;
        this.from = from;
        this.to = to;
    }

    //dedicated Exception
    public static class noSuchRangeException extends Exception {
        public noSuchRangeException(String s) {
            super(s);
        }
    }

    /**
     * a Set containing all the available ranges. Each one has its correspondence in the database. <p>
     * To add a new range, it needs to be added here and in the database
     */
    public static final Set<Range> ranges = new HashSet<>();
    static {
        ranges.add(new Range("0",0,10));
        ranges.add(new Range("1",11,20));
        ranges.add(new Range("2",21,30));
        ranges.add(new Range("3",31,40));
        ranges.add(new Range("4",41,50));
    }
    public static Set<Range> getRanges() { return Range.ranges; }

    /**
     * Retrieve from the database the id and the basic information about the items which belong to the specified range
     * by saving them in a map which will be manipulated by the consumer
     * @param context the activity/fragment where this method is called
     * @param idRange the range id
     * @param consumer the way the fetched data are being used
     */
    public static void getItemsIdAndInfo(Context context, String idRange, Consumer<Map<String, ArrayList<String>>> consumer) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Range.CLASS_RANGE_DB);
        BarattopolyUtil.getMapWithIdAndInfo(context, dbRef, idRange, Item.INFO_LENGTH, consumer);
    }

    //da verificare se funziona
    /**
     * add a new item with its main information to a range in the database <p>
     * (to be called only when setting the range of an existing item
     * or when adding a new item in the database)
     * @param idItem the id of the item to be added to the provided category
     * @param itemBasicInfo the CSV format for the main information about the item
     * @param idRange the category which the item belongs to
     */
    static void addItemToRange(String idItem, String itemBasicInfo, String idRange) {
        dbRefRange.child(idRange).child(Range.ID_ITEMS_DB).child(idItem).setValue(itemBasicInfo);
    }

    public String getIdRange() {
        return this.idRange;
    }



    /**
     *
     * @return the lower bound for the specified range
     * @throws noSuchRangeException if the idRange does not exist
     */
    public static Integer getFrom(String idRange) throws noSuchRangeException {
        for (Range r: Range.ranges) {
            if (r.idRange.equals(idRange)) return r.from;
        }
        throw new noSuchRangeException(idRange);
    }

    /**
     *
     * @return the upper bound for the specified range
     * @throws noSuchRangeException if the idRange does not exist
     */
    public static Integer getTo(String idRange) throws noSuchRangeException {
        for (Range r: Range.ranges) {
            if (r.idRange.equals(idRange)) return r.to;
        }
        throw new noSuchRangeException(idRange);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof Range)) return false;
        Range r = (Range) o;
        return  this.idRange.equals(r.idRange);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idRange.hashCode();
        return result;
    }


}


