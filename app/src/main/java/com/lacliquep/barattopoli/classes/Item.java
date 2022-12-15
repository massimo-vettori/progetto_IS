package com.lacliquep.barattopoli.classes;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    public static final int INFO_LENGTH = 11;
    /**
     * the basic info elements about an Item when stored in a different class
     */
    public static final String INFO_PARAM = "category,range,image,is_charity,is_exchangeable,is_service,country,region,province,city,title";

    private final String idItem;
    private String title;
    private String description;
    private final String idRange;
    //location: country, region, province, city
    private ArrayList<String> location = new ArrayList<>();
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
    Item(String idItem, String title, String description,@NonNull String idRange, Collection<String> owner, ArrayList<String> location, boolean isCharity, boolean isExchangeable, boolean isService, @NonNull ArrayList<String> categories ,@NonNull Collection<String> images) {
        this.idItem = idItem;
        this.description = description;
        this.title = title;
        this.idRange = idRange;
        this.owner.addAll(owner);
        this.isCharity = isCharity;
        //this.isExchangeable = isExchangeable;
        this.isExchangeable = true;
        this.isService = isService;
        this.location = location;
        //for(String cat: categories) if (Category.getCategories().contains(cat)) this
        this.categories.addAll(categories);
        this.images.addAll(images);
        String category = "", image = "";
        if (!(this.categories.isEmpty())) category = this.categories.toArray()[0].toString();
        if (!(this.images.isEmpty())) image = this.images.toArray()[0].toString();
        this.itemBasicInfo = category + "," + idRange + "," + image + "," + String.valueOf(isCharity) + "," + String.valueOf(isExchangeable) + "," + String.valueOf(isService) + "," + location.get(0) + "," + location.get(1) + "," + location.get(2) + "," + location.get(3) + "," + title;
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
   static Item createItem(String title, String description, String idRange, ArrayList<String> currentUserBasicInfo, ArrayList<String> location, boolean isCharity, boolean isService, @NonNull ArrayList<String> categories ,@NonNull ArrayList<String> images) {
        return new Item(UUID.randomUUID().toString(), title, description, idRange, currentUserBasicInfo, location, isCharity, true, isService, categories, images);
   }

    /**
     * to be called when the current User is creating a new Item(from User.addNewItemOnBoard) . Store the item in the database in Items
     * and add the item to all the correspondent categories, and to its correspondent range
     * @see User#addNewItemOnBoard(String, String, String, String, ArrayList, ArrayList, boolean, boolean, ArrayList, ArrayList)
     * @param item the new item
     */
   static void insertItemInDataBase(String contextTag, Item item) {
       DatabaseReference dbRefItemsItem = dbRefItems.child(item.idItem);
       dbRefItemsItem.child(Item.ID_ITEM_DB).setValue(item.idItem);
       try {
           setDescription(true, item.description, dbRefItemsItem);
           setTitle(true, item.title, dbRefItemsItem);
           setLocation(true, item.location, dbRefItemsItem);
           setCharity(item.idItem, true, item.isCharity, dbRefItemsItem);
           setRange(item.idItem, item.itemBasicInfo, true, item.idRange, dbRefItemsItem);
           setExchangeable(true, dbRefItemsItem);
           setService(true, item.isService, dbRefItemsItem);
       } catch (NonModifiableException e) {
           Log.d(contextTag, e.getMessage());
       }
       String imgs = "", cats = "", ownr = "";
       //preparing the CSV strings for the database
       for(String img: item.images) imgs += (img + ",");
       for(String cat: item.categories) cats += (cat + ",");
       for(String info: item.owner) ownr += (info + ",");
       dbRefItemsItem.child(Item.IMAGES_DB).setValue(imgs);
       dbRefItemsItem.child(Item.ID_CATEGORIES_DB).setValue(cats);
       dbRefItemsItem.child(Item.OWNER_DB).setValue(ownr);
       //add the item to all the correspondent categories
       for(String category: item.categories) {
           Category.addItemToCategory(item.idItem, item.itemBasicInfo, category);
       }
       //add the item to its correspondent range: done in setRange
   }


    /**
     * read from the database all the values regarding the User with the provided id <p>
     * Don't try taking out data from the consumer: it is not going to work
     * @param contextTag the string representing the activity/fragment where this method is called
     * @param dbRef the database reference
     * @param id the id of the Item to retrieve
     * @param consumer the way the fetched data are being used
     */
    public static void retrieveItemById(String contextTag, DatabaseReference dbRef, String id, Consumer<Item> consumer) {
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
                    BarattopoliUtil.retrieveHelper(map, Item.TITLE_DB, ItemData,0);
                    BarattopoliUtil.retrieveHelper(map, Item.DESCRIPTION_DB, ItemData,1);
                    BarattopoliUtil.retrieveHelper(map, Item.ID_RANGE_DB, ItemData,2);
                    BarattopoliUtil.retrieveHelper(map, Item.OWNER_DB, ItemData,3);
                    //BarattopoliUtil.retrieveHelper(map, Item.LOCATION_DB, ItemData,4);
                    BarattopoliUtil.retrieveHelper(map, Item.IS_CHARITY_DB, ItemData,5);
                    BarattopoliUtil.retrieveHelper(map, Item.IS_EXCHANGEABLE_DB, ItemData,6);
                    BarattopoliUtil.retrieveHelper(map, Item.IS_SERVICE_DB, ItemData,7);
                    BarattopoliUtil.retrieveHelper(map, Item.ID_CATEGORIES_DB, ItemData,8);
                    BarattopoliUtil.retrieveHelper(map, Item.IMAGES_DB, ItemData,9);
                    ArrayList<String> own = new ArrayList<>(Arrays.asList(ItemData.get(3).split(",", User.INFO_LENGTH)));
                    ArrayList<String> cat = new ArrayList<>(Arrays.asList(ItemData.get(8).split(",", 0)));
                    ArrayList<String> img = new ArrayList<>(Arrays.asList(ItemData.get(9).split(",", 0)));
                    //since location is a nested data
                    BarattopoliUtil.getMapWithIdAndInfo(contextTag, dbRefItems.child(id), User.LOCATION_DB, 1, new Consumer<Map<String, ArrayList<String>>>() {
                        @Override
                        public void accept(Map<String, ArrayList<String>> stringArrayListMap) {
                            ArrayList<String> location = new ArrayList<>();
                            location.add(stringArrayListMap.get("country").get(0));
                            location.add(stringArrayListMap.get("region").get(0));
                            location.add(stringArrayListMap.get("province").get(0));
                            location.add(stringArrayListMap.get("city").get(0));
                            consumer.accept(new Item(id, ItemData.get(0), ItemData.get(1),ItemData.get(2), own, location, Boolean.getBoolean(ItemData.get(5)), Boolean.getBoolean(ItemData.get(6)), Boolean.getBoolean(ItemData.get(7)), cat, img));
                        }
                    });

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(contextTag, "load:onCancelled", error.toException());
            }
        });
    }

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
    public ArrayList<String> getLocation() {
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
     * @return a set containing the categories this item belongs to
     */
    public Set<String> getCategories() {
        return this.categories;
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

    /**
     * in consumer is provided a map with items ids and their correspondent maps with fields id and correspondent values
     * @param showCharity if true, the items will be only those for Charity and with the same province
     * @param showService if true, the items will be only those which are services, elsewhere those which are objects,
     *                    if showCharity is true, showService will be ignored
     * @param category    to be implemented
     * @param showUserBoard if true, the items will be those on the idUser's Board and their fields will be according
     *                      item basic info, whereas if false, the items will be those with the same province of the location,
     *                      and which are Services/Objects or Charity according to the other boolean params
     * @param idUser    the User's id of whom the items belong to if showUserBoard is true
     * @param location  the location where the items should be(ideally the User's one)
     * @param consumer  how the provided map will be used
     */
    public static void retrieveMapWithAllItems(boolean showCharity, boolean showService, String category, boolean showUserBoard, String idUser, ArrayList<String> location, Consumer<Map<String, Map<String, String>>> consumer) {
        //sanity check
        if (!showUserBoard || (showUserBoard && idUser != null)) {
            if (showUserBoard) {
                User.getItemsOnBoard("Item", idUser, new Consumer<Map<String, ArrayList<String>>>() {
                    Map<String, Map<String, String>> items = new HashMap<>();
                    @Override
                    public void accept(Map<String, ArrayList<String>> stringArrayListMap) {
                        for(String id: stringArrayListMap.keySet()) {
                            items.put(id, new HashMap<>());
                            for (int i = 0; i < Item.INFO_LENGTH; ++i) {
                                //"category,range,image,is_charity,is_exchangeable,is_service,country,region,province,city,title"
                                switch (i) {
                                    case 0: items.get(id).put(Item.ID_CATEGORIES_DB, stringArrayListMap.get(id).get(i));break;
                                    case 1: items.get(id).put(Item.ID_RANGE_DB, stringArrayListMap.get(id).get(i));break;
                                    case 2: items.get(id).put(Item.IMAGES_DB, stringArrayListMap.get(id).get(i));break;
                                    case 3: items.get(id).put(Item.IS_CHARITY_DB, stringArrayListMap.get(id).get(i));break;
                                    case 4: items.get(id).put(Item.IS_EXCHANGEABLE_DB, stringArrayListMap.get(id).get(i));break;
                                    case 5: items.get(id).put(Item.IS_SERVICE_DB, stringArrayListMap.get(id).get(i));break;
                                    case 6: items.get(id).put("country", stringArrayListMap.get(id).get(i));break;
                                    case 7: items.get(id).put("region", stringArrayListMap.get(id).get(i));break;
                                    case 8: items.get(id).put("province", stringArrayListMap.get(id).get(i));break;
                                    case 9: items.get(id).put("city", stringArrayListMap.get(id).get(i));break;
                                    case 10: items.get(id).put(Item.TITLE_DB, stringArrayListMap.get(id).get(i));break;
                                }
                            }
                        }
                        consumer.accept(items);
                    }
                });
            } else {
                Item.dbRefItems.orderByChild(Item.LOCATION_DB + "/province").equalTo(location.get(2)).limitToFirst(5).addChildEventListener(new ChildEventListener() {
                    Map<String, Map<String, String>> items = new HashMap<>();

                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()) {
                            String idItem = snapshot.getKey();
                            items.put(idItem, new HashMap<>());
                            Item.dbRefItems.child(idItem).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists() && snapshot.hasChildren()) {
                                        for (DataSnapshot fi : snapshot.getChildren()) {
                                            if (fi.exists()) {
                                                String field = fi.getKey();
                                                if (field != null && !(field.equals(Item.LOCATION_DB))) {
                                                    String fieldValue = String.valueOf(fi.getValue());
                                                    items.get(idItem).put(field, fieldValue);
                                                    //Log.d("Item", field + ": " + fieldValue);
                                                } else {
                                                    if (field != null) {
                                                        if (fi.hasChildren()) {
                                                            for (DataSnapshot loc : fi.getChildren()) {
                                                                String location = loc.getKey();
                                                                String locValue = String.valueOf(loc.getValue());
                                                                items.get(idItem).put(location, locValue);
                                                            }
                                                            //Log.d("Item", location + ": " + locValue);
                                                            for (String s : items.keySet()) {
                                                                boolean keep = true;
                                                                if ((items.get(s).get(Item.IS_EXCHANGEABLE_DB)).equals("true")) {
                                                                    if (!showCharity) {
                                                                        String val = items.get(s).get(Item.IS_SERVICE_DB);
                                                                        if ((showService) && ((val.equals("true"))) || ((!showService) && ((val.equals("false"))))) {
                                                                        } else keep = false;
                                                                    }
                                                                } else keep = false;
                                                                if (!keep) items.remove(s);
                                                            }
                                                            consumer.accept(items);
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
    }

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
     * @param dbRefItem the location of the Item in the database
     * @param isExchangeable the status of the item
     */
    public static void setDescription(boolean isExchangeable, String description, DatabaseReference dbRefItem) throws NonModifiableException {
        if (!isExchangeable) throw new NonModifiableException();
        dbRefItem.child(Item.DESCRIPTION_DB).setValue(description);
    }

    /**
     * modifies the title of this Item in the database if it is still exchangeable
     * @param title the new title
     * @param dbRefItem the location of the Item in the database
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void setTitle(boolean isExchangeable, String title, DatabaseReference dbRefItem) throws NonModifiableException {
        if (!isExchangeable) throw new NonModifiableException();
        dbRefItem.child(Item.TITLE_DB).setValue(title);
    }

    /**
     * modifies the range of value for this Item in the database if it is still exchangeable
     * it modifies the collection of items in the specified range in the database
     * @param idRange the id of the Range of value of this Item
     * @param idItem the id of the item to modify
     * @param dbRefItem the location of the Item in the database
     * @param itemBasicInfo the basic info of the item when stored in a different class
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void setRange(String idItem, String itemBasicInfo,boolean isExchangeable, String idRange, DatabaseReference dbRefItem) throws NonModifiableException {
        if (!isExchangeable) throw new NonModifiableException();
        dbRefItem.child(Item.ID_RANGE_DB).setValue(idRange);
        Range.addItemToRange(idItem,itemBasicInfo, idRange);
    }

    /**
     * modifies the current location of this Item in the database if it is still exchangeable
     * @param location the current location where this Item is
     * @param dbRefItem the location of the Item in the database
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void setLocation(boolean isExchangeable, ArrayList<String> location, DatabaseReference dbRefItem)throws NonModifiableException {
        if (!isExchangeable) throw new NonModifiableException();
        DatabaseReference dbRefItemLocation = dbRefItem.child(User.LOCATION_DB);
        dbRefItemLocation.child("country").setValue(location.get(0));
        dbRefItemLocation.child("region").setValue(location.get(1));
        dbRefItemLocation.child("province").setValue(location.get(2));
        dbRefItemLocation.child("city").setValue(location.get(3));
    }

    /**
     * modifies the state of this Item in the database if it is still exchangeable, according to the value of the parameter
     * @param charity If set on true, the Item becomes a good or service which is offered for free
     * @param idItem the id of the item to modify
     * @param dbRefItem the location of the Item in the database
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void setCharity(String idItem, boolean isExchangeable, boolean charity, DatabaseReference dbRefItem) throws NonModifiableException {
        if (!isExchangeable) throw new NonModifiableException();
        dbRefItem.child(Item.IS_CHARITY_DB).setValue(charity);
    }

    /**
     * modifies the state of this Item, according to the value of the parameter <p>
     * (called only as consequence of an exchange)
     * @param isExchangeable If set on true, the Item becomes a good or service which is possible to exchange, <p>
     * e.g. it might be set to true after the refusal of an exchange or it could be set to false if
     * the Item has been proposed for an exchange
     * @param dbRefItem the location of the Item in the database
     */
    public static void setExchangeable(boolean isExchangeable, DatabaseReference dbRefItem) {
        dbRefItem.child(Item.IS_EXCHANGEABLE_DB).setValue(isExchangeable);
    }

    /**
     * modifies the state of this Item in the database if it is still exchangeable, according to the value of the parameter
     * @param service If set on true, the Item becomes a service, otherwise it becomes a good
     * @param dbRefItem the location of the Item in the database
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void setService(boolean isExchangeable, boolean service, DatabaseReference dbRefItem) throws NonModifiableException {
        if (!isExchangeable) throw new NonModifiableException();
        dbRefItem.child(Item.IS_SERVICE_DB).setValue(service);
    }

    /**
     * adds a category in the database which this Item belongs to
     * (as side effect, add this item to the set of items of the specified Category.)
     * @param contextTag the string representing the Activity/Fragment where this method is called
     * @param category the title of the category
     * @param isExchangeable the status of the item
     * @param idItem the id of the item to modify
     * @param dbRefItem the location of the Item in the database
     * @throws NonModifiableException if this Item is not exchangeable
     */
    //TODO:da verificare se funziona veramente
    public static void addCategory(String contextTag, String idItem, DatabaseReference dbRefItem, String ItemBasicInfo, boolean isExchangeable, String category) throws NonModifiableException {
        if (!isExchangeable) throw new NonModifiableException();
        if (Category.getCategories().contains(category)) {
            DatabaseReference dbRef = dbRefItem.child(Item.ID_CATEGORIES_DB);
            BarattopoliUtil.readData(contextTag, dbRef, new Consumer<Object>() {
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
     * @param dbRefItem the location of the Item in the database
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void removeCategory(boolean isExchangeable, String category, String idItem, DatabaseReference dbRefItem) throws NonModifiableException {
        if (!isExchangeable) throw new NonModifiableException();
        //TODO: se addcategory funziona, fare cose simili
    }

    /**
     * adds a string which represents an image to be displayed for this Item in the database
     * @param image the string which represents the image
     * @param isExchangeable the status of the item
     * @param dbRefItem the location of the Item in the database
     * @param idItem the id of the item to modify
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void addImage(String idItem, boolean isExchangeable, String image, DatabaseReference dbRefItem) throws NonModifiableException{
        if (!isExchangeable) throw new NonModifiableException();
        //TODO: se addcategory funziona fare cose simili
    }
    /**
     * removes a string which represents an image for this Item
     * @param image the string which represents the image
     * @param isExchangeable the status of the item
     * @param dbRefItem the location of the Item in the database
     * @param idItem the id of the item to modify
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void removeImage(String idItem, boolean isExchangeable, String image, DatabaseReference dbRefItem) throws NonModifiableException {
        if (!isExchangeable) throw new NonModifiableException();
        //TODO: se addimage funziona, fare cose simili
    }

    /**
     * to be called only when removing an item from the board of a user
     * delete the provided item from the database (if it is exchangeable) <p>
     * Also Removes the item from the sets of items of all the correspondent
     * categories which this item belonged to in the database <p>
     * also removes the item from its owner's board
     * @param idItem
     * @param dbRefItem the location of the Item in the database
     * @param isExchangeable the status of the item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    public static void deleteItem(String idItem, boolean isExchangeable, DatabaseReference dbRefItem) throws NonModifiableException {
        if (!isExchangeable) throw new NonModifiableException();
        //TODO;
    }

}
