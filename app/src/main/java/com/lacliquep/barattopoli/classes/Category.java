package com.lacliquep.barattopoli.classes;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;
import java.util.function.Consumer;

//TODO: comments
//TODO: put an annotation and make private on methods which modify the category

/**
 * This class represents a Category stored in the database (categories in DB)
 *
 * @author pares
 * @since 1.0
 */
public class Category {
    public static final String CLASS_CATEGORY_DB = "categories";
    public static final String ID_CATEGORY_DB = "id_category";
    public static final String ID_ITEMS_DB = "id_items";
    public static final int ITEM_INFO_LENGTH = 8;
    private final String idCategory;

    public static final DatabaseReference dbRefCategories = FirebaseDatabase.getInstance().getReference().child(Category.CLASS_CATEGORY_DB);

    /**
     * a Set containing all the available categories. Each one has its correspondence in strings.xml
     * and in the database. To add a new category, it needs to be added here, in strings.xml and in the database
     */
    public static final Set<Category> categories = new HashSet<>();
    {
        categories.add(new Category("Tech"));
        categories.add(new Category("Clothes"));
        categories.add(new Category("Handmade"));
        categories.add(new Category("Games"));
        categories.add(new Category("House_and_garden"));
        categories.add(new Category("Food"));
        categories.add(new Category("School"));
        categories.add(new Category("Fun"));
    }

    private Category(String title) {
        this.idCategory = title;
    }


    //METHODS TO OPERATE ON A CATEGORY:

    /**
     *
     * @return the title of this Category
     */
    public String getTitle() {
        return this.idCategory;
    }


    //da verificare se funziona
    /**
     * Retrieve from the database the main information about the items which belong to the provided category
     * by saving them in a map which be manipulated by the consumer
     * @param context the activity/fragment where this method is called
     * @param category the category title
     * @param consumer the way the fetched data are being used
     */
    public static void getItemsByCategory(Context context, String category, Consumer<Map<String, ArrayList<String>>> consumer) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Category.CLASS_CATEGORY_DB);
        DataBaseInteractor.getMapWithIdAndInfo(context, dbRef, category, Category.ITEM_INFO_LENGTH, consumer);
    }
    //da verificare se funziona
    /**
     * add a new item with its main information to a category <p>
     * (to be called only when adding a category to an existing item
     * or when adding a new item in the database)
     * @param idItem the id of the item to be added to the provided category
     * @param itemBasicInfo the CSV format for the main information about the item
     * @param category the category which the item belongs to
     */
    public static void addItemToCategory(String idItem, String itemBasicInfo, String category) {
        dbRefCategories.child(category).child(Category.ID_ITEMS_DB).child(idItem).setValue(itemBasicInfo);
    }

    /**
     * remove an existing item from a category <p>
     * (to be called only when removing a category from an existing item or
     * when removing an existing item from a User's board)
     * @param idItem the id of the item to be removed
     * @param category the category which the items does not belong anymore
     */
    public static void removeItemFromCategory(String idItem, String category) {
        //TODO
    }

    /**
     * @return the titles of the existing categories
     */
    public static Collection<String> getCategories() {
        Collection<String> res = new ArrayList<>();
        for(Category s: Category.categories) res.add(s.getTitle());
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof Category)) return false;
        Category c = (Category) o;
        return  this.idCategory.equals(c.getTitle());
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idCategory.hashCode();
        return result;
    }

}
