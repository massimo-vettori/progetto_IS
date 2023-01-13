package com.lacliquep.barattopoli.classes;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.*;
import java.util.function.Consumer;

/**
 * This class represents a Category stored in the database (categories in DB)
 * @author pares, jack, gradiente
 * @since 1.0
 */
public class Category {
    /**
     * the category main node name in the database
     */
    static final String CLASS_CATEGORY_DB = "categories";
    /**
     * the category id node name in the database
     */
    static final String ID_CATEGORY_DB = "id_category";
    /**
     * the items id node name in the database
     */
    static final String ID_ITEMS_DB = "id_items";

    private final String idCategory;

    /**
     * a reference to the category main node in the database
     */
    static final DatabaseReference dbRefCategories = FirebaseDatabase.getInstance().getReference().child(Category.CLASS_CATEGORY_DB);

    /**
     * a Set containing all the available categories. Each one has its correspondence in strings.xml
     * and in the database. To add a new category, it needs to be added both here, in strings.xml, and in the database
     */
    private static final Set<Category> categories = new HashSet<>();
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


    //TODO: to be tested
    /**
     * Retrieve from the database the main information about the items which belong to the provided category
     * by saving them in a map which be manipulated by the consumer
     * @param contextTag the string representing the activity/fragment where this method is called
     * @param category the category title
     * @param consumer the way the fetched data are being used
     */
    public static void getItemsByCategory(String contextTag, String category, Consumer<Map<String, ArrayList<String>>> consumer) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Category.CLASS_CATEGORY_DB);
        BarattopoliUtil.getMapWithIdAndInfo(contextTag, dbRef, category, Item.INFO_LENGTH, consumer);
    }
    //TODO: to be tested
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
     * @return a collection of the existing categories titles
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
