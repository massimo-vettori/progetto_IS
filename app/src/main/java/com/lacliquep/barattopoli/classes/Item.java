package com.lacliquep.barattopoli.classes;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;


/**
 * This class represents a good or service which the user is going to offer or exchange
 *
 * @author pares
 * @since 1.0
 */
public class Item {
    public final static String CLASS_ITEM_DB = "items";
    public static final String ID_ITEM_DB = "id_item";
    public static final String TITLE_DB = "title";
    public static final String DESCRIPTION_DB = "description";
    public static final String ID_RANGE_DB = "id_range";
    public static final String OWNER_DB = "user";
    public static final String LOCATION_DB = "location";
    public static final String IS_CHARITY_DB = "is_charity";
    public static final String IS_EXCHANGEABLE_DB = "is_exchangeable";
    public static final String IS_SERVICE_DB = "is_service";
    public static final String ID_CATEGORIES_DB = "categories";
    public static final String IMAGES_DB = "images";
    /**
     * the number of the basic info elements about an Item when stored in a different class
     */
    public static final int INFO_LENGTH = 8;
    /**
     * the basic info elements about an Item when stored in a different class
     */
    public static final String INFO_PARAM = "category,range,image,is_charity,is_exchangeable,is_service,location,title";

    private final String idItem;
    private String title;
    private String description;
    private final String idRange;
    private String location;
    private boolean isCharity;
    private boolean isExchangeable;
    private boolean isService;
    private final Set<String> categories = new HashSet<>();
    private final Collection<String> images = new ArrayList<>();
    private final ArrayList<String> owner = new ArrayList<>();
    private final String itemBasicInfo;

    public static DatabaseReference dbRefItems = FirebaseDatabase.getInstance().getReference().child(Item.CLASS_ITEM_DB);
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    static class NonModifiableException extends Exception {
        public NonModifiableException() {
            super("this Item is not exchangeable, therefore it cannot be modified");
        }
    }
    /**
     * Constructor for an Item, after a fetch from the database
     * @param title the title of this Item
     * @param description a description of this Item
     * @param idRange the id of the Range of value of this Item
     * @param owner a list with the basic info of the owner of this Item
     * @param location the location of this Item
     * @param isCharity if this Item is offered for free
     * @param isExchangeable if this Item can be exchanged
     * @param isService if this Item is a service
     * @param categories a collection of categories which the item belongs to
     *                   (if the category does not exists, it will be ignored)
     * @param images a collection of Strings representing the images to display
     *
     */
    Item(String idItem, String title, String description,@NonNull String idRange, Collection<String> owner, String location, boolean isCharity, boolean isExchangeable, boolean isService, @NonNull ArrayList<String> categories ,@NonNull Collection<String> images) {
        this.idItem = idItem;
        this.description = description;
        this.title = title;
        this.idRange = idRange;
        this.owner.addAll(owner);
        this.isCharity = isCharity;
        this.isExchangeable = isExchangeable;
        this.isService = isService;
        this.location = location;
        //for(String cat: categories) if (Category.getCategories().contains(cat)) this
        this.categories.addAll(categories);
        this.images.addAll(images);
        String category = "", image = "";
        if (!(this.categories.isEmpty())) category = this.categories.toArray()[0].toString();
        if (!(this.images.isEmpty())) image = this.images.toArray()[0].toString();
        this.itemBasicInfo = category + "," + idRange + "," + image + "," + String.valueOf(isCharity) + "," + String.valueOf(isExchangeable) + "," + String.valueOf(isService) + "," + location + "," + title;
    }
    /**
     * Creator of a new Item, to be called when the current User is creating a new Item, to store in the DB <p>
     * This item will be set as exchangeable
     * @param title the title of this Item
     * @param description a description of this Item
     * @param idRange the id of the Range of value of this Item
     * @param currentUserBasicInfo the the info param of the owner
     * @see User#INFO_PARAM
     * @param location the location of this Item
     * @param isCharity if this Item is offered for free
     * @param isService if this Item is a service
     * @param categories a collection of categories which the item belongs to
     *                   (if the category does not exists, it will be ignored)
     * @param images a collection of Strings representing the images to display
     *
     */
   static Item createItem(String title, String description, String idRange, ArrayList<String> currentUserBasicInfo, String location, boolean isCharity, boolean isService, @NonNull ArrayList<String> categories ,@NonNull ArrayList<String> images) {
        return new Item(UUID.randomUUID().toString(), title, description, idRange, currentUserBasicInfo, location, isCharity, true, isService, categories, images);
   }

    /**
     * to be called when the current User is creating a new Item. Store the item in the database in Items
     * and add the item to all the correspondent categories
     * @see User#addNewItemOnBoard(String, String, String, ArrayList, String, boolean, boolean, ArrayList, ArrayList)
     * @param item the new item
     */
   static void insertItemInDataBase(Item item) {
       dbRefItems.child(item.idItem).child(Item.ID_ITEM_DB).setValue(item.idItem);
       dbRefItems.child(item.idItem).child(Item.DESCRIPTION_DB).setValue(item.description);
       dbRefItems.child(item.idItem).child(Item.TITLE_DB).setValue(item.title);
       dbRefItems.child(item.idItem).child(Item.ID_RANGE_DB).setValue(item.idRange);
       dbRefItems.child(item.idItem).child(Item.LOCATION_DB).setValue(item.location);
       dbRefItems.child(item.idItem).child(Item.IS_CHARITY_DB).setValue(item.isCharity);
       dbRefItems.child(item.idItem).child(Item.IS_EXCHANGEABLE_DB).setValue(item.isExchangeable);
       dbRefItems.child(item.idItem).child(Item.IS_SERVICE_DB).setValue(item.isService);
       String imgs = "", cats = "", ownr = "";
       //preparing the CSV strings for the database
       for(String img: item.images) imgs += (img + ",");
       for(String cat: item.categories) cats += (cat + ",");
       for(String info: item.owner) ownr += (info + ",");
       dbRefItems.child(item.idItem).child(Item.IMAGES_DB).setValue(imgs);
       dbRefItems.child(item.idItem).child(Item.ID_CATEGORIES_DB).setValue(cats);
       dbRefItems.child(item.idItem).child(Item.OWNER_DB).setValue(ownr);
       //add the item to all the correspondent categories
       for(String category: item.categories) {
           Category.addItemToCategory(item.idItem, item.itemBasicInfo, category);
       }

   }


    /**
     * read from the database all the values regarding the User with the provided id <p>
     * Don't try taking out data from the consumer: it is not going to work
     * @param context the activity/fragment where this method is called
     * @param dbRef the database reference
     * @param id the id of the User to retrieve
     * @param consumer the way the fetched data are being used
     */
    public static void retrieveItemById(Context context, DatabaseReference dbRef, String id, Consumer<Item> consumer) {
        dbRef.child(Item.CLASS_ITEM_DB).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Object> map = new HashMap<>();
                    for (DataSnapshot child: snapshot.getChildren()) {
                        map.put(child.getKey(), child.getValue());
                    }
                    ArrayList<String> ItemData = new ArrayList<>();
                    for (int i = 0; i < 10; ++i) ItemData.add("");
                    DataBaseInteractor.retrieveHelper(map, Item.TITLE_DB, ItemData,0);
                    DataBaseInteractor.retrieveHelper(map, Item.DESCRIPTION_DB, ItemData,1);
                    DataBaseInteractor.retrieveHelper(map, Item.ID_RANGE_DB, ItemData,2);
                    DataBaseInteractor.retrieveHelper(map, Item.OWNER_DB, ItemData,3);
                    DataBaseInteractor.retrieveHelper(map, Item.LOCATION_DB, ItemData,4);
                    DataBaseInteractor.retrieveHelper(map, Item.IS_CHARITY_DB, ItemData,5);
                    DataBaseInteractor.retrieveHelper(map, Item.IS_EXCHANGEABLE_DB, ItemData,6);
                    DataBaseInteractor.retrieveHelper(map, Item.IS_SERVICE_DB, ItemData,7);
                    DataBaseInteractor.retrieveHelper(map, Item.ID_CATEGORIES_DB, ItemData,8);
                    DataBaseInteractor.retrieveHelper(map, Item.IMAGES_DB, ItemData,9);
                    ArrayList<String> own = new ArrayList<>(Arrays.asList(ItemData.get(3).split(",", User.INFO_LENGTH)));
                    ArrayList<String> cat = new ArrayList<>(Arrays.asList(ItemData.get(8).split(",", 0)));
                    ArrayList<String> img = new ArrayList<>(Arrays.asList(ItemData.get(9).split(",", 0)));
                    consumer.accept(new Item(id, ItemData.get(0), ItemData.get(1),ItemData.get(2), own, ItemData.get(4), Boolean.getBoolean(ItemData.get(5)), Boolean.getBoolean(ItemData.get(6)), Boolean.getBoolean(ItemData.get(7)), cat, img));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(context.toString(), "load:onCancelled", error.toException());
            }
        });
    }
     
    //TODO: insert Item in the database with side effects on range and categories

    //GETTERS

    /**
     *
     * @return this Item id
     */
    public String getIdItem() {
        return this.idItem;
    }

    /**
     *
     * @return the String which contains this Item title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     *
     * @return the string which contains this Item description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     *
     * @return the range of value of this Item
     */
    public String getIdRange() {
        return this.idRange;
    }


    /**
     * @return this Item owner's basic info
     * @see User#INFO_PARAM
     */
    public Collection<String> getOwner() {
        return this.owner;
    }

    /**
     *
     * @return the location where this Item is
     */
    public String getLocation() {
        return this.location;
    }

    /**
     *
     * @return true if this Item is offered for free
     */
    public boolean isCharity() {
        return this.isCharity;
    }

    /**
     *
     * @return true if this Item can be exchanged with another one,
     * false if this Item is in a transitional state, e.g. it is involved in an exchange, waiting for approval
     */
    public boolean isExchangeable() {
        return this.isExchangeable;
    }

    /**
     *
     * @return true if this Item is a service, false if it is a good
     */
    private boolean isService() {
        return this.isService;
    }

    /**
     *
     * @param context the Activity/Fragment where the method is called
     * @param idItem the id of the item
     * @param consumer how the data are going to be used. The keys of the map are the string value of incremental Integer values,
     *                 while in the correspondent values are the categories titles
     */
    public static void  getCategories(Context context,String idItem, Consumer<Map<String, String>> consumer) {
        DataBaseInteractor.mapChildren(context, dbRefItems.child(idItem).child(Item.ID_CATEGORIES_DB), consumer);
    }

    /**
     *
     * @return all the loaded string-encoded images for this Item
     */
    public Collection<String> getImages() {
        return this.images;
    }

    /**
     *
     * @return the basic info of this Item, to represent it a s a CSV string inside a different class in the database
     */
    public String getItemBasicInfo() { return this.itemBasicInfo; }

    //EQUALS & HASHCODE

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || (!(o instanceof Item))) return false;
        Item item = (Item) o;
        return owner.equals(item.owner) && isCharity == item.isCharity && isExchangeable == item.isExchangeable && isService == item.isService && idItem.equals(item.idItem) && title.equals(item.title) && description.equals(item.description) && location.equals(item.location) && categories.equals(item.categories) && images.equals(item.images);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idItem.hashCode();
        result = prime * result + title.hashCode();
        result = prime * result + description.hashCode();
        result = prime * result + owner.hashCode();
        result = prime * result + location.hashCode();
        result = prime * result + (!isCharity?0:1);
        result = prime * result + (!isExchangeable?0:1);
        result = prime * result + categories.hashCode();
        result = prime * result + images.hashCode();
        return result;
    }


    /**
     * modifies the description of this Item in the database
     * @param description the new description
     * @throws NonModifiableException if this Item is not exchangeable
     * @param idItem the id of the item to modify
     * @param isExchangeable the status of the item
     */
    public static void setDescription(String idItem, boolean isExchangeable, String description) throws NonModifiableException {
        if (!isExchangeable) throw new NonModifiableException();
        dbRefItems.child(idItem).child(Item.DESCRIPTION_DB).setValue(description);
    }

    /**
     * modifies the title of this Item in the database if it is still exchangeable
     * @param title the new title
     * @param idItem the id of the item to modify
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void setTitle(String idItem, boolean isExchangeable, String title) throws NonModifiableException {
        if (!isExchangeable) throw new NonModifiableException();
        dbRefItems.child(idItem).child(Item.TITLE_DB).setValue(title);
    }

    /**
     * modifies the range of value for this Item in the database if it is still exchangeable
     * it modifies the collection of items in the specified range in the database
     * @param idRange the id of the Range of value of this Item
     * @param idItem the id of the item to modify
     * @param itemBasicInfo the basic info of the item when stored in a different class
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void setRange(String idItem, String itemBasicInfo,boolean isExchangeable, String idRange) throws NonModifiableException {
        if (isExchangeable) throw new NonModifiableException();
        dbRefItems.child(idItem).child(Item.ID_RANGE_DB).setValue(idRange);
        Range.addItemToRange(idItem,itemBasicInfo, idRange);
    }

    /**
     * modifies the current location of this Item in the database if it is still exchangeable
     * @param location the current location where this Item is
     * @param idItem the id of the item to modify
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void setLocation(String idItem, boolean isExchangeable, String location)throws NonModifiableException {
        if (isExchangeable) throw new NonModifiableException();
        dbRefItems.child(idItem).child(Item.LOCATION_DB).setValue(location);
    }

    /**
     * modifies the state of this Item in the database if it is still exchangeable, according to the value of the parameter
     * @param charity If set on true, the Item becomes a good or service which is offered for free
     * @param idItem the id of the item to modify
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void setCharity(String idItem, boolean isExchangeable, boolean charity) throws NonModifiableException {
        if (isExchangeable) throw new NonModifiableException();
        dbRefItems.child(idItem).child(Item.IS_CHARITY_DB).setValue(charity);
    }

    /**
     * modifies the state of this Item, according to the value of the parameter <p>
     * (called only as consequence of an exchange)
     * @param exchangeable If set on true, the Item becomes a good or service which is possible to exchange, <p>
     * e.g. it might be set to true after the refusal of an exchange or it could be set to false if
     * the Item has been proposed for an exchange
     * @param idItem the id of the item to modify
     */
    public static void setExchangeable(String idItem, boolean exchangeable) {
        dbRefItems.child(idItem).child(Item.IS_EXCHANGEABLE_DB).setValue(exchangeable);
    }

    /**
     * modifies the state of this Item in the database if it is still exchangeable, according to the value of the parameter
     * @param service If set on true, the Item becomes a service, otherwise it becomes a good
     * @param idItem the id of the item to modify
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void setService(String idItem, boolean isExchangeable, boolean service) throws NonModifiableException {
        if (isExchangeable) throw new NonModifiableException();
        dbRefItems.child(idItem).child(Item.IS_SERVICE_DB).setValue(service);
    }

    /**
     * adds a category in the database which this Item belongs to
     * (as side effect, add this item to the set of items of the specified Category.)
     * @param category the title of the category
     * @param isExchangeable the status of the item
     * @param idItem the id of the item to modify
     * @throws NonModifiableException if this Item is not exchangeable
     */
    //da verificare se funziona veramente
    public static void addCategory(Context context, String idItem, String ItemBasicInfo, boolean isExchangeable, String category) throws NonModifiableException {
        if (isExchangeable) throw new NonModifiableException();
        if (Category.getCategories().contains(category)) {
            DatabaseReference dbRef = dbRefItems.child(idItem).child(Item.ID_CATEGORIES_DB);
            DataBaseInteractor.readData(context, dbRef, new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {
                        String newCategories = "";
                        if (o != null) {
                            String existingCategories = o.toString();
                            newCategories = existingCategories + "," + category;
                        }
                        //updating the CSV string of categories of this item in databse
                        dbRef.setValue(newCategories);
                        //updating the collection of items with this item and its basic info in the specified category in database
                        Category.addItemToCategory(idItem, ItemBasicInfo, category);
                    }
            });

        }
    }
    /**
     * removes a category which this Item does not belong anymore
     * as side effect, remove this item to the set of items of the specified Category.
     * @param category the title of the category
     * @param idItem the id of the item to modify
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void removeCategory(String idItem, boolean isExchangeable, String category) throws NonModifiableException {
        if (isExchangeable) throw new NonModifiableException();
        //TODO: se addcategory funziona, fare cose simili
    }

    /**
     * adds a string which represents an image to be displayed for this Item in the database
     * @param image the string which represents the image
     * @param isExchangeable the status of the item
     * @param idItem the id of the item to modify
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void addImage(String idItem, boolean isExchangeable, String image) throws NonModifiableException{
        if (isExchangeable) throw new NonModifiableException();
        //TODO: se addcategory funziona fare cose simili
    }
    /**
     * removes a string which represents an image for this Item
     * @param image the string which represents the image
     * @param isExchangeable the status of the item
     * @param idItem the id of the item to modify
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void removeImage(String idItem, boolean isExchangeable, String image) throws NonModifiableException {
        if (isExchangeable) throw new NonModifiableException();
        //TODO: se addimage funziona, fare cose simili
    }

    /**
     * to be called only when removing an item from the board of a user
     * delete the provided item from the database (if it is exchangeable) <p>
     * Also Removes the item from the sets of items of all the correspondent
     * categories which this item belonged to in the database <p>
     * also removes the item from its owner's board
     * @param idItem
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void deleteItem(String idItem, boolean isExchangeable) throws NonModifiableException {
        if (isExchangeable) throw new NonModifiableException();
        //TODO;
    }

}
