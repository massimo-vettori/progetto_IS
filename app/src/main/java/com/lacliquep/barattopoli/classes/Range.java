package com.lacliquep.barattopoli.classes;

import androidx.annotation.Nullable;

import java.util.*;

/**
 * this class represent a Range of values to give to an Item
 */
public class Range {
    public static final String CLASS_RANGE_DB = "range";
    public static final String ID_RANGE_DB = "id_range";
    public static final String FROM_DB = "from";
    public static final String TO_DB = "to";
    public static final String ID_ITEMS_DB = "id_items";
    public static final Set<Range> ranges = new HashSet<>();

    private final String idRange;
    private final Integer from;
    private final Integer to;
    private final Set<String> idItems = new HashSet<>();
    private final Set<Item> items = new HashSet<>();

    private Range(String idRange, Integer from, Integer to, @Nullable Collection<String> idItems) {
        this.idRange = idRange;
        this.from = from;
        this.to = to;
        if (idItems != null) this.idItems.addAll(idItems);
    }

    //public static Set<Range> getRanges() {}

}


