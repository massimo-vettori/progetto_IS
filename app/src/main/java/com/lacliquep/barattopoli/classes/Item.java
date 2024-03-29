package com.lacliquep.barattopoli.classes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.lacliquep.barattopoli.R;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class represents a good or service which the user is going to offer or exchange
 * @author pares, jack, gradiente
 * @since 1.0
 */
public class Item implements Serializable {
    /**
     * the item main node name in the database
     */
    final static String CLASS_ITEM_DB = "items";
    /**
     * the item id node name in the database
     */
    private static final String ID_ITEM_DB = "id_item";
    /**
     * the item title node name in the database
     */
    private static final String TITLE_DB = "title";
    /**
     * the item description node name in the database
     */
    private static final String DESCRIPTION_DB = "description";
    /**
     * the item range node name in the database
     */
    private static final String ID_RANGE_DB = "id_range";
    /**
     * the item owner node name in the database
     */
    private static final String OWNER_DB = "user";
    /**
     * the item location node name in the database
     */
    private static final String LOCATION_DB = "location";
    /**
     * the item charity flag node name in the database
     */
    private static final String IS_CHARITY_DB = "is_charity";
    /**
     * the item exchangeable flag node name in the database
     */
    private static final String IS_EXCHANGEABLE_DB = "is_exchangeable";
    /**
     * the item service flag node name in the database
     */
    private static final String IS_SERVICE_DB = "is_service";
    /**
     * the item categories node name in the database
     */
    private static final String ID_CATEGORIES_DB = "categories";
    /**
     * the item images node name in the database
     */
    private static final String IMAGES_DB = "images";
    /**
     * the number of the basic info elements about an Item when stored in a different node in a CSV format
     */
    static final int INFO_LENGTH = 11;

    public static final String EMPTY_ITEM_ID = "EmptyItem";

    /**
     * the basic info elements about an Item when stored in a different node in a CSV format
     */
    static final String INFO_PARAM = "category,range,image,is_charity,is_exchangeable,is_service,country,region,province,city,title";

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
    private final ArrayList<String> images = new ArrayList<>();
    private final ArrayList<String> owner = new ArrayList<>();
    private final String itemBasicInfo;

    static DatabaseReference dbRefItems = FirebaseDatabase.getInstance().getReference().child(Item.CLASS_ITEM_DB);
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    /**
     * when an item is not exchangeable, it is not modifiable too
     */
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
    public Item(String idItem, String title, String description, @NonNull String idRange, Collection<String> owner, ArrayList<String> location, boolean isCharity, boolean isExchangeable, boolean isService, @NonNull ArrayList<String> categories, @NonNull Collection<String> images) {
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

        if (!(this.categories.isEmpty())) category = this.categories.stream().findFirst().orElse("");
        if (!(this.images.isEmpty())) image = this.images.stream().findFirst().orElse("");
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
     * read from the database all the values regarding the Item with the provided id <p>
     * Don't try taking out data from the consumer: it is not going to work
     * @param contextTag the string representing the activity/fragment where this method is called
     * @param dbRef the database reference
     * @param id the id of the Item to retrieve
     * @param consumer the way the fetched data are being used
     */
    public static void retrieveItemById(String contextTag, @Nullable DatabaseReference dbRef, String id, Consumer<Item> consumer) {
        DatabaseReference ref = dbRef != null ? dbRef : FirebaseDatabase.getInstance().getReference();

        retrieveItemsByIds(contextTag, ref, new ArrayList<>(Collections.singletonList(id)), new Consumer<ArrayList<Item>>() {
            @Override
            public void accept(ArrayList<Item> items) {
                consumer.accept(items.get(0));
            }
        });
    }

    /**
     * retrieve the list of items corresponding to the provided ids, from the main item node in the database
     * @param contextTag the string representing the activity/fragment where this method is being called from
     * @param dbRef a root database reference
     * @param ids the ids of the items to fetch
     * @param consumer accepts and provides the list of items
     */
    public static void retrieveItemsByIds(String contextTag, @Nullable DatabaseReference dbRef, ArrayList<String> ids, Consumer<ArrayList<Item>> consumer) {
        DatabaseReference ref = dbRef != null ? dbRef : FirebaseDatabase.getInstance().getReference();

        ref.child(Item.CLASS_ITEM_DB).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<Item> arr = new ArrayList<>();
                    for(String id: ids) {
                        if (snapshot.hasChild(id)) {
                            Map<String, Object> map = new HashMap<>();
                            for (DataSnapshot ch : (snapshot.child(id)).getChildren()) {
                                map.put(ch.getKey(), ch.getValue());
                            }

                            ArrayList<String> ItemData = new ArrayList<>();
                            for (int i = 0; i < 10; ++i) ItemData.add("");
                            BarattopoliUtil.retrieveHelper(map, Item.TITLE_DB, ItemData, 0);
                            BarattopoliUtil.retrieveHelper(map, Item.DESCRIPTION_DB, ItemData, 1);
                            BarattopoliUtil.retrieveHelper(map, Item.ID_RANGE_DB, ItemData, 2);
                            BarattopoliUtil.retrieveHelper(map, Item.OWNER_DB, ItemData, 3);
                            //BarattopoliUtil.retrieveHelper(map, Item.LOCATION_DB, ItemData,4);
                            BarattopoliUtil.retrieveHelper(map, Item.IS_CHARITY_DB, ItemData, 5);
                            BarattopoliUtil.retrieveHelper(map, Item.IS_EXCHANGEABLE_DB, ItemData, 6);
                            BarattopoliUtil.retrieveHelper(map, Item.IS_SERVICE_DB, ItemData, 7);
                            BarattopoliUtil.retrieveHelper(map, Item.ID_CATEGORIES_DB, ItemData, 8);
                            BarattopoliUtil.retrieveHelper(map, Item.IMAGES_DB, ItemData, 9);
                            ArrayList<String> own = new ArrayList<>(Arrays.asList(ItemData.get(3).split(",", User.INFO_LENGTH)));
                            ArrayList<String> cat = new ArrayList<>(Arrays.asList(ItemData.get(8).split(",", 0)));
                            ArrayList<String> img = new ArrayList<>(Arrays.asList(ItemData.get(9).split(",", 0)));
                            //since location is a nested data
                            ArrayList<String> location = new ArrayList<>();
                            for (DataSnapshot s: snapshot.child(id).child(Item.LOCATION_DB).getChildren()) {
                                if (s.getKey().equals("country")) location.add(s.getValue().toString());
                                if (s.getKey().equals("region")) location.add(s.getValue().toString());
                                if (s.getKey().equals("province")) location.add(s.getValue().toString());
                                if (s.getKey().equals("city")) location.add(s.getValue().toString());
                            }

                            if (location.size() < 4) {
                                for (int i = location.size(); i < 4; ++i) location.add("");
                            }

                            Item newItem = new Item(id, ItemData.get(0), ItemData.get(1), ItemData.get(2), own, location, Boolean.valueOf(ItemData.get(5)), Boolean.valueOf(ItemData.get(6)), Boolean.valueOf(ItemData.get(7)), cat, img);
                            arr.add(newItem);
                        }
                    }
                    consumer.accept(arr);
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
     *
     * @return this item owner's username
     */
    public String getOwnerUsername() {
        return (owner.size() >= 4)? owner.get(3): "";
    }

    /**
     *
     * @return this item owner's id
     */
    public String getOwnerId() {
        return (owner.size() >= 1)? owner.get(0): "";
    }

    /**
     *
     * @return this item owner's rank
     */
    public int getOwnerRank() {
        return (owner.size() >= 3)? Integer.parseInt(owner.get(2)): -1;
    }

    /**
     *
     * @return this item owner's image
     */
    public Bitmap getOwnerImage() {
        Bitmap b = null;
        if (owner.size() >= 2) b = BarattopoliUtil.decodeFileFromBase64(owner.get(1));
        return b;
    }

    /**
     * @return this Item owner's basic info
     * @see User#INFO_PARAM
     */
    public ArrayList<String> getOwner() {
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
    public boolean isService() {
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



    private static void retrieveItemsUserBoard(boolean showCharity, boolean showService, String category, boolean showUserBoard, String idUser, ArrayList<String> location, Consumer<Map<String, Item>> consumer) {

    }
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
    public static void retrieveMapWithAllItems(boolean showCharity, boolean showService, String category, boolean showUserBoard, String idUser, ArrayList<String> location, Consumer<Map<String, Item>> consumer) {
        //sanity check
        if (!showUserBoard || (showUserBoard && idUser != null)) {
            if (showUserBoard) {
                User.getItemsOnBoard("Item", idUser, new Consumer<Map<String, ArrayList<String>>>() {
                    final Map<String, Item> items = new HashMap<>();
                    @Override
                    public void accept(Map<String, ArrayList<String>> map) {
                        Log.d("342Item", map.toString());
                        for(String id: map.keySet()) {
                            //"category,range,image,is_charity,is_exchangeable,is_service,country,region,province,city,title"
                            ArrayList<String> itemFields = map.get(id);
                            ArrayList<String> location = new ArrayList<>(Arrays.asList(itemFields.get(6), itemFields.get(7), itemFields.get(8), itemFields.get(9)));
                            ArrayList<String> categories = new ArrayList<>(Arrays.asList(itemFields.get(0)));
                            ArrayList<String> images = new ArrayList<>(Arrays.asList(itemFields.get(2)));
                            Item it = new Item(id, itemFields.get(10), "", itemFields.get(1), new ArrayList<>(), location, Boolean.parseBoolean(itemFields.get(3)), Boolean.parseBoolean(itemFields.get(4)), Boolean.parseBoolean(itemFields.get(5)), categories, images);

                            if (!showCharity && !it.isCharity)
                                items.put(id, it);
                            else if (showCharity)
                                items.put(id, it);
                        }
                        consumer.accept(items);
                    }
                });
            } else {
                Item.dbRefItems.orderByChild(Item.LOCATION_DB + "/province").equalTo(location.get(2)).addChildEventListener(new ChildEventListener() {
                    HashMap<String, Item> items = new HashMap<>();
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        //for each item
                        items.clear();
                        if (snapshot.exists()) {
                            String idItem = snapshot.getKey();
                            /*Item.dbRefItems.child(idItem).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {*/
                                    if (snapshot.exists() && snapshot.hasChildren()) {
                                        boolean is_charity = false, is_service = false, is_exchangeable = false;
                                        String country = "", region = "", province = "", city = "", owner = "",
                                                description = "", id_range = "", title = "", categories = "", images = "";
                                        ArrayList<String> location = new ArrayList<>();
                                        ArrayList<String> categoriesArray = new ArrayList<>();
                                        ArrayList<String> imagesArray = new ArrayList<>();
                                        ArrayList<String> ownerArray = new ArrayList<>();
                                        //for each field
                                        for (DataSnapshot fi : snapshot.getChildren()) {
                                            if (fi.exists()) {
                                                String field = fi.getKey();
                                                if (field != null && !(field.equals(Item.LOCATION_DB))) {
                                                    String fieldValue = String.valueOf(fi.getValue());
                                                    Log.d("IT", "key: " + field + "fieldValue: " + fieldValue);
                                                    if (field.equals(Item.DESCRIPTION_DB))
                                                        description = String.valueOf(fi.getValue());
                                                    if (field.equals(Item.TITLE_DB))
                                                        title = String.valueOf(fi.getValue());
                                                    if (field.equals(Item.IS_SERVICE_DB))
                                                        is_service = Boolean.parseBoolean(String.valueOf(fi.getValue()));
                                                    if (field.equals(Item.IS_CHARITY_DB))
                                                        is_charity = Boolean.parseBoolean(String.valueOf(fi.getValue()));
                                                    if (field.equals(Item.IS_EXCHANGEABLE_DB))
                                                        is_exchangeable = Boolean.parseBoolean(String.valueOf(fi.getValue()));
                                                    if (field.equals(Item.ID_RANGE_DB))
                                                        id_range = String.valueOf(fi.getValue());
                                                    if (field.equals(Item.OWNER_DB))
                                                        owner = String.valueOf(fi.getValue());
                                                    if (field.equals(Item.ID_CATEGORIES_DB))
                                                        categories = String.valueOf(fi.getValue());
                                                    if (field.equals(Item.IMAGES_DB))
                                                        images = String.valueOf(fi.getValue());
                                                } else {
                                                    if (field != null) {
                                                        if (fi.hasChildren()) {
                                                            for (DataSnapshot loc : fi.getChildren()) {
                                                                String locate = loc.getKey();
                                                                String locValue = String.valueOf(loc.getValue());
                                                                if (locate.equals("country"))
                                                                    country = locValue;
                                                                if (locate.equals("region"))
                                                                    region = locValue;
                                                                if (locate.equals("province"))
                                                                    province = locValue;
                                                                if (locate.equals("city"))
                                                                    city = locValue;
                                                            }
                                                            location.addAll(Arrays.asList(country, region, province, city));
                                                        }
                                                    }
                                                }

                                            }
                                        } //END of children's fetch
                                        categoriesArray.addAll(Arrays.asList(categories.split(",")));
                                        imagesArray.addAll(Arrays.asList(images.split(",")));
                                        ownerArray.addAll(Arrays.asList(owner.split(",", User.INFO_LENGTH)));
                                        Item newItem =  new Item(idItem, title, description, id_range, ownerArray, location, is_charity, is_exchangeable, is_service, categoriesArray, imagesArray);
                                        Log.d("ITEM", "Item name: " + newItem.getTitle() + ",is exchangeable: " + newItem.isExchangeable());
                                        //do not show to logged user their own objects/services and only the exchangeable ones
                                        if (newItem.isExchangeable() && (!(newItem.getOwnerId().equals(mAuth.getUid())))) {
                                            if (category != null) {
                                                if (Category.getCategories().contains(category)) {
                                                    if (newItem.getCategories().contains(category)) {
                                                        //charity filter
                                                        if ((newItem.isCharity() && showCharity)) {
                                                            items.put(idItem, newItem);
                                                        } else if (!showCharity && !newItem.isCharity()) {
                                                            //service or object filter
                                                            if ((newItem.isService() && showService) || (!(newItem.isService()) && !showService))
                                                                items.put(idItem, newItem);
                                                        }
                                                    }
                                                }
                                            } else {
                                                //charity filter
                                                if ((newItem.isCharity() && showCharity)) {
                                                    items.put(idItem, newItem);
                                                } else {
                                                    if (!showCharity && !newItem.isCharity()) {
                                                        //service or object filter
                                                        if ((newItem.isService() && showService) || (!(newItem.isService()) && !showService))
                                                            items.put(idItem, newItem);
                                                    }
                                                }
                                            }
                                        }
                                    }


                        }
                        Log.d("63", items.toString());
                        consumer.accept(items);

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
    //TODO: to be tested
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
     *
     * @return this item first image
     */
    public Bitmap getFirstImage() {
        return (this.images.size() >= 1)? BarattopoliUtil.decodeFileFromBase64(images.get(0)): null;
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
    private static void deleteItem(String idItem, boolean isExchangeable, DatabaseReference dbRefItem) throws NonModifiableException {
        if (!isExchangeable) throw new NonModifiableException();
        //TODO;
    }

    /**
     * a sample Item which does not exist, just to be used in case of no item is provided
     * @return
     */
    public static Item getSampleItem() {
        ArrayList<String> owner = new ArrayList<>();
        owner.add("owner");
        ArrayList<String> categories = new ArrayList<>();
        categories.add("category");
        ArrayList<String> images = new ArrayList<>();
//        images.add(BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.surface).toString());

        ArrayList<String> location = new ArrayList<>();
        location.add("stato");
        location.add("regione");
        location.add("provincia");
        location.add("comune");
        return new Item("id", "title", "description", "idRange", owner, location, false, true, false, categories, images);
    }

    /**
     * @return an Item which does not exist, just to be used in case of no item is provided and no item has to be shown
     */
    public static Item getEmptyItem() {
        ArrayList<String> owner = new ArrayList<>();
        ArrayList<String> categories = new ArrayList<>();
        ArrayList<String> images = new ArrayList<>();
        ArrayList<String> location = new ArrayList<>();

        owner.add("");
        categories.add("");
        images.add(null);
        location.add("");
        location.add("");
        location.add("");
        location.add("");
        return new Item(EMPTY_ITEM_ID, "", "", "", owner, location, false, true, false, categories, images);
    }

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


    private static void deleteItem(String itemID) {
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Item.CLASS_ITEM_DB).child(itemID);
//        dbRef.removeValue();
    }

    private static void deleteItem(Item item) {
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Item.CLASS_ITEM_DB).child(item.idItem);
//        dbRef.removeValue();
    }

    //failed attempts to serialize/deserialize
   private static CharSequence[] serialize(Item item) {
        CharSequence[] serializedItem = new CharSequence[11];
        serializedItem[0] = item.getIdItem();
        serializedItem[1] = item.getTitle();
        serializedItem[2] = item.getDescription();
        serializedItem[3] = item.getOwner().stream().reduce("", (a, b) -> a + "," + b);
        serializedItem[4] = item.getCategories().stream().reduce("", (a, b) -> a + "," + b);
        serializedItem[5] = item.getImages().stream().reduce("", (a, b) -> a + "," + b);
        serializedItem[6] = item.isExchangeable() ? "1" : "0";
        serializedItem[7] = item.isService() ? "1" : "0";
        serializedItem[8] = item.isCharity() ? "1" : "0";
        serializedItem[9] = item.getIdRange();
        serializedItem[10] = item.getLocation().stream().reduce("", (a, b) -> a + "," + b);
        return serializedItem;
    }



//    String idItem, String title, String description,@NonNull String idRange, Collection<String> owner, ArrayList<String> location, boolean isCharity, boolean isExchangeable, boolean isService, @NonNull ArrayList<String> categories ,@NonNull Collection<String> images
    private  static Item deserialize(CharSequence[] serializedItem) {
        String id = serializedItem[0].toString();
        String title = serializedItem[1].toString();
        String description = serializedItem[2].toString();
        ArrayList<String> owner = new ArrayList<>(Arrays.asList(serializedItem[3].toString().split(",")));
        ArrayList<String> categories = new ArrayList<>(Arrays.asList(serializedItem[4].toString().split(",")));
        ArrayList<String> images = new ArrayList<>(Arrays.asList(serializedItem[5].toString().split(",")));
        boolean isExchangeable = serializedItem[6].toString().equals("1");
        boolean isService = serializedItem[7].toString().equals("1");
        boolean isCharity = serializedItem[8].toString().equals("1");
        String idRange = serializedItem[9].toString();
        ArrayList<String> location = new ArrayList<>(Arrays.asList(serializedItem[10].toString().split(",")));

        return new Item(id, title, description, idRange, owner, location, isCharity, isExchangeable, isService, categories, images);
    }
}
