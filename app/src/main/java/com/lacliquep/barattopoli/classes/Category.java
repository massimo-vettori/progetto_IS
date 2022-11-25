package com.lacliquep.barattopoli.classes;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.*;

//TODO: comments
//TODO: put an annotation and make private on methods which modify the category
/**
 * This class represents a Category stored in the database (categories in DB)
 *
 * @author pares
 * @since 1.0
 */
public class Category {
    public static final String TAG = "Category";
    //id_category
    private final String idCategory = UUID.randomUUID().toString();
    //title
    private final String title;
    //id_item, just their id in DB, whereas items contains the actual Items which belong to this Category
    private final Set<Item> items = new HashSet<>();
    /**
     * a Set containing all the available categories.
     */
    public static final Set<Category> categories = new HashSet<>();
    {
        categories.add(new Category("Tecnologia"));
        categories.add(new Category("Abbigliamento e Accessori"));
        categories.add(new Category("Ferramenta"));
        categories.add(new Category("Trasporti"));
        categories.add(new Category("Giochi"));
        categories.add(new Category("Istruzione"));
        categories.add(new Category("Divertimento"));
        categories.add(new Category("Artigianato"));
    }

    private Category(String title) {
        this.title = title;
    }


    //METHODS TO OPERATE ON A CATEGORY:

    /**
     *
     * @return the title of this Category
     */
    public String getTitle() {
        return this.title;
    }

    /**
     *
     * @return the id of this Category
     */
    public String getIdCategory() {
        return this.idCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof Category)) return false;
        Category c = (Category) o;
        return this.title.equals(c.title) && this.idCategory.equals(c.getIdCategory()) && this.items.equals(c.getItems());
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idCategory.hashCode();
        result = prime * result + title.hashCode();
        result = prime * result + items.hashCode();
        return result;
    }

    //METHODS TO OPERATE ON THE SET "ITEMS":

    /**
     *
     * @return A set containing all the items which belong to this Category
     * @see Item
     */
    public Set<Item> getItems() {
        return this.items;
    }
    /**
     *
     * @return A set with the id of all the items which belong to this Category
     * @see Item
     * @see Item#getIdItem()
     */
    public Set<String> getIdItems() {
        Set<String> idItems = new HashSet<>();
        for (Item x: this.items) { idItems.add(x.getIdItem()); }
        return idItems;
    }

    /**
     * adds a good or service to the set of items which belongs to this Category
     * @param item the good or service
     * @return true if the set Category.categories did not already contain the item.
     * Otherwise,the call leaves the set unchanged and returns false
     * @see Category#categories
     * @see Item
     * @see Set#add(Object)
     *
     */
    private boolean addItemToCategory(@NonNull Item item) {
        return this.items.add(item);
    }

    /**
     * Removes the specified item from the set of this Category items
     * @param item a good or service which does not belong to this Category anymore
     *             or which has been deleted
     * @return true if items already contained the item.
     * @see Category#items
     * @see Item
     * @see Set#remove(Object)
     */
    private boolean removeItemFromCategory(@NonNull Item item) {
        return this.items.remove(item);
    }

    //STATIC METHODS TO OPERATE ON "CATEGORIES":
    /**
     *
     * @param title the title of the category
     * @return the id to which the specified category title correspond,
     * or null if a category with such title does not exist
     * @see Category#categories
     */
    public static String getIdCategory(String title) {
        return Category.getCategories().get(title);
    }
    /**
     *
     * @param idCategory the id of the category
     * @return the title to which the specified category id correspond,
     * or null if a category with such id does not exist
     * @see Category#categories
     */
    public static String getTitle(String idCategory) {
        String res = null;
        Category c = Category.getCategoryById(idCategory);
        if (c != null) res = c.getTitle();
        return res;
    }

    /**
     *
     * @param idCategory the id of the Category
     * @return the correspondent Category if there exists one with the specified id,
     * otherwise return null.
     * @see Category#categories
     */
    public static Category getCategoryById(String idCategory) {
        Category res = null;
        for(Category x: Category.categories) {
            if (x.getIdCategory().equals(idCategory)) res = x;
        }
        return res;
    }
    /**
     *
     * @param title the title of the Category
     * @return the correspondent Category if there exists one with the specified title,
     * otherwise return null.
     * @see Category#categories
     */
    public static Category getCategoryByTitle(String title) {
        Category res = null;
        for(Category x: Category.categories) {
            if (x.getTitle().equals(title)) res = x;
        }
        return res;
    }


    /**
     *
     * @return a map containing the titles of the Category.categories with their corresponding id
     * @see Category#categories
     */
    public static Map<String, String> getCategories() {
        Map<String, String> id = new HashMap<>();
        for(Category x: categories) {
            id.put(x.getTitle(), x.getIdCategory());
        }
        return id;
    }

    /**
     * adds a new category to the set Category.categories
     * @param title the title of the new Category
     * @see Category#categories
     * @see Item
     * @see Set#add(Object)
     */
    private static void addNewCategory(String title) {
        Category.categories.add(new Category(title));
    }
}
