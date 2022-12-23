package com.lacliquep.barattopoli.classes;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;
import java.util.function.Consumer;

/**
 * this class represents a User of the app
 *
 * @author pares
 * @since 1.0
 */
public class User {

    private static final String CLASS_TAG_NAME = "User";
    public static final Integer HIGHEST_RANK = 100;
    public static final String CLASS_USER_DB = "users";
    public static final String ID_USER_DB = "id_user";
    public static final String NAME_DB = "name";
    public static final String SURNAME_DB = "surname";
    public static final String USERNAME_DB = "username";
    public static final String LOCATION_DB = "location";
    public static final String IMAGE_DB = "image";
    public static final String RANK_DB = "rank";
    public static final String ID_REVIEWS_DB = "reviews";

    public static final String ID_ITEMS_ON_BOARD_DB = "items_on_board";
    public static final String ID_OBSERVED_ITEMS_DB = "observed_items";
    public static final String ID_EXCHANGES_DB = "exchanges";
    public static final int REVIEWS_INFO_LENGTH = Review.REVIEW_INFO_LENGTH;
    public static final int ITEMS_ON_BOARD_INFO_LENGTH = Item.INFO_LENGTH;
    //TODO: how to reflect the changes of an item on the list of the observed items for all the users?
    public static final int OBSERVED_ITEMS_INFO_LENGTH = Item.INFO_LENGTH;
    public static final int EXCHANGES_INFO_LENGTH = Exchange.EXCHANGE_INFO_LENGTH;
    /**
     * the number of the basic info elements about a User when stored in a different class
     */
    public static final int INFO_LENGTH = 4;
    /**
     * the basic info elements about a User when stored in a different class
     */
    public static final String INFO_PARAM = "id,image,rank,username";

    public static DatabaseReference dbRefUsers = FirebaseDatabase.getInstance().getReference().child(User.CLASS_USER_DB);
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final String idUser;
    private final String name;
    private final String surname;
    private final String username;
    private final ArrayList<String> location;
    private final String image;
    private final Integer rank;
    private final String userBasicInfo;






    /**
     * constructor of a User, after a fetch from the database
     *
     * @param idUser   the ID created via FirebaseAuth
     * @param username the username of this User
     * @param name the name of this User
     * @param surname the surname of this User
     * @param location    the coordinates of this User's location
     * @param image the profile picture for this User
     * @param rank the rank of this User, an Iteger value between 1 and HIGHER_RANK
     * @see com.google.firebase.auth.FirebaseAuth
     * @see User#HIGHEST_RANK
     */
    User(String idUser, String username, String name, String surname, ArrayList<String> location, Integer rank, String image) {
        this.idUser = idUser;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.location = location;
        this.rank = rank;
        this.image = image;
        //the basic info when a User is stored in an Item
        this.userBasicInfo = this.idUser + "," + this.image + "," + this.rank + "," + this.username;
    }

    public static User createUserFromBasicInfo(String userBasicInfo,@Nullable ArrayList<String> basicInfo) {
        if (basicInfo == null) basicInfo = new ArrayList<>(Arrays.asList(userBasicInfo.split(",", User.INFO_LENGTH)));
        User res = new User(basicInfo.get(0), basicInfo.get(3), "", "", new ArrayList<>(Arrays.asList("","","","","")), Integer.valueOf(basicInfo.get(2)), basicInfo.get(1));
        return res;
    }
    /**
     * used when creating other samples and in case of errors
     * @return a sample of a User who does not exist
     */
    public static User getSampleUser() { return new User("basicUser", "basicUser", "", "", new ArrayList<>(Arrays.asList("Italia", "Veneto", "Venezia", "Venezia")), User.getMediumRank(), "");}
    /**
     * creator of a User, to be called when creating a new User to store in the DB
     * the rank will be set to the medium value between 1 and HIGHER_RANK
     * @param idUser   the ID created via FirebaseAuth
     * @param username the username of this User
     * @param name the name of this User
     * @param surname the surname of this User
     * @param location the location of this User
     * @param image the profile picture for this User
     * @see com.google.firebase.auth.FirebaseAuth
     * @see User#HIGHEST_RANK
     */
    public static User createUser(String idUser, String username, String name, String surname, ArrayList<String> location, String image) {
        return new User(idUser, username, name, surname, location, getMediumRank() , image);
    }



    //FUNZIONA
    /**
     * read from the database all the values regarding the User with the provided id <p>
     * Don't try taking out data from the consumer: it is not going to work
     * @param contextTag the string representing the activity/fragment where this method is called
     * @param dbRef the "root" database reference
     * @param id the id of the User to retrieve
     * @param consumer the way the fetched data are being used
     */
    public static void retrieveUserById(String contextTag, DatabaseReference dbRef, String id, Consumer<User> consumer) {
        dbRef.child(User.CLASS_USER_DB).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Object> map = new HashMap<>();
                    for (DataSnapshot child: snapshot.getChildren()) {
                        map.put(child.getKey(), child.getValue());
                    }
                    ArrayList<String> UserData = new ArrayList<>();
                    for (int i = 0; i < 7; ++i) UserData.add("");
                    BarattopoliUtil.retrieveHelper(map, User.USERNAME_DB, UserData,0);
                    BarattopoliUtil.retrieveHelper(map, User.NAME_DB, UserData,1);
                    BarattopoliUtil.retrieveHelper(map, User.SURNAME_DB, UserData,2);
                    //BarattopoliUtil.retrieveHelper(map, User.LOCATION_DB, UserData,3);
                    BarattopoliUtil.retrieveHelper(map, User.RANK_DB, UserData,4);
                    BarattopoliUtil.retrieveHelper(map, User.IMAGE_DB, UserData,5);
                    Integer rank = UserData.get(4).equals("")?0: Integer.valueOf(UserData.get(4));
                    //since location is a nested data
                    BarattopoliUtil.getMapWithIdAndInfo(contextTag, dbRefUsers.child(id), User.LOCATION_DB, 1, new Consumer<Map<String, ArrayList<String>>>() {
                        @Override
                        public void accept(Map<String, ArrayList<String>> stringArrayListMap) {
                            ArrayList<String> location = new ArrayList<>();
                            location.add(stringArrayListMap.get("country").get(0));
                            location.add(stringArrayListMap.get("region").get(0));
                            location.add(stringArrayListMap.get("province").get(0));
                            location.add(stringArrayListMap.get("city").get(0));
                            Log.d("User", location.toString());
                            consumer.accept(new User(id, UserData.get(0), UserData.get(1), UserData.get(2), location, rank, UserData.get(5)));
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
    /**
     * read from the database all the values regarding the current logged User <p>
     * Don't try taking out data from the consumer: it is not going to work
     * @param contextTag the string representing the activity/fragment where this method is called
     * @param dbRef the "root" database reference
     * @param consumer the way the fetched data are being used
     */
    public static void retrieveCurrentUser(String contextTag, DatabaseReference dbRef, Consumer<User> consumer){
        User.retrieveUserById(contextTag, dbRef, FirebaseAuth.getInstance().getUid(), consumer);
    }

    /**
     * insert in the DataBase the basic User's info provided when setting them for the first time,
     * ideally, during the registration
     * the rank will be set to the medium value between 1 and HIGHER_RANK
     * @param idUser   the ID created via FirebaseAuth
     * @param username the username of this User
     * @param name the name of this User
     * @param surname the surname of this User
     * @param location the location of this User
     * @param image the profile picture for this User
     * @see com.google.firebase.auth.FirebaseAuth
     * @see User#HIGHEST_RANK
     */
    public static void insertUserInDataBase(String idUser, String username, String name, String surname, ArrayList<String> location, String image) {
        User user = createUser(idUser,username,name,surname,location,image);
        DatabaseReference dbRefUser = dbRefUsers.child(user.idUser);

        dbRefUser.child(User.ID_USER_DB).setValue(user.idUser);
        setUsername(user.username,dbRefUser);
        setName(user.name,dbRefUser);
        setSurname(user.surname,dbRefUser);
        setLocation(user.location,dbRefUser);
        setImage(user.image,dbRefUser);
        dbRefUser.child(User.RANK_DB).setValue(user.rank);
    }

    public static Integer getMediumRank() {
        return User.HIGHEST_RANK/2;
    }
    /**
     * @return this User's id
     */
    public String getIdUser() {
        return this.idUser;
    }

    /**
     * @return this User's name
     */
    public String getName() {
        return this.name;
    }
    /**
     * changes this User's name
     * @param dbRefUser the location od the User node in the database
     * @param name the name of this User
     */
    public static void setName(String name, DatabaseReference dbRefUser) {
        dbRefUser.child(User.NAME_DB).setValue(name);
    }

    /**
     * @return this User's surname
     */
    public String getSurname() {
        return this.surname;
    }
    /**
     * @param dbRefUser the location od the User node in the database
     * @param surname the new surname of the User
     */
    public static void setSurname(String surname, DatabaseReference dbRefUser) {
        dbRefUser.child(User.SURNAME_DB).setValue(surname);
    }

    /**
     * @return this User's username
     */
    public String getUsername() {
        return username;
    }
    /**
     * changes this User's username
     * in the database
     * @param dbRefUser the location od the User node in the database
     * @param username the username of the User
     */
    public static void setUsername(String username, DatabaseReference dbRefUser) {
        dbRefUser.child(User.USERNAME_DB).setValue(username);
    }

    /**
     * @return the location of this User's location
     */
    public ArrayList<String> getLocation() {
        return location;
    }
    /**
     * changes the coordinates of this User's location in the database
     * @param dbRefUser the location od the User node in the database
     * @param location the location of this User's new location
     */
    public static void setLocation(ArrayList<String> location, DatabaseReference dbRefUser) {
        DatabaseReference dbRefUserLocation = dbRefUser.child(User.LOCATION_DB);
        dbRefUserLocation.child("country").setValue(location.get(0));
        dbRefUserLocation.child("region").setValue(location.get(1));
        dbRefUserLocation.child("province").setValue(location.get(2));
        dbRefUserLocation.child("city").setValue(location.get(3));
    }

    /**
     * retrieve the reviews the provided user gave to another user or another user gave to the provided user
     * with their basic info (text not included) in the database
     * @param contextTag the string representing the activity/fragment where this method is called
     * @param idUser the id of the provided user
     * @param consumer the way the fetched data are used
     */
    public static void  getReviews(String contextTag,String idUser, Consumer<Map<String, ArrayList<String>>> consumer) {
        BarattopoliUtil.getMapWithIdAndInfo(contextTag, dbRefUsers.child(idUser), User.ID_REVIEWS_DB, User.REVIEWS_INFO_LENGTH, consumer);
    }

    /**
     * adds a review another User gave to the provided User or the provided User gave to another User in the database
     * it updates the provided User's Rank in the database if they are the receiver
     * (to be called only when creating a new review)
     * @param idReview the new id review
     * @param info the correspondent info of the review in CSV format
     */
    public static void addReview(String idUser, String idReview, String info) {
        //TODO
    }
    /**
     * removes a review id another User gave to this User or this User gave to another User in the database
     * it updates the provided User's rank in the database
     * @param idReview the new review
     */
    public static void removeIdReview(String idUser, String idReview) {
        //TODO
    }

    /**
     * retrieve the items on the provided user's board
     * with their basic info in the database
     * @param contextTag the activity/fragment where this method is called
     * @param idUser the id of the provided user
     * @param consumer the way the fetched data are used (provides a map with the items ids and a list with the basic info fields)
     * @see Item#INFO_PARAM
     */
    public static void  getItemsOnBoard(String contextTag,String idUser, Consumer<Map<String, ArrayList<String>>> consumer) {
        BarattopoliUtil.getMapWithIdAndInfo(contextTag, dbRefUsers.child(idUser), User.ID_ITEMS_ON_BOARD_DB, User.ITEMS_ON_BOARD_INFO_LENGTH, consumer);
    }

    /**
     * create and add a new Item with its basic info  to the provided User's board and in the class Items in the database <p>
     * as side effect, the item will be added to its correspondent categories, its correspondent range and in Items
     * @param itemTitle the title of the new Item
     * @param itemDescription a description of the new Item
     * @param itemIdRange the id of the Range of value of the new Item
     * @param currentUserBasicInfo the info param of the owner
     * @param itemLocation the location of the new Item
     * @param itemIsCharity if the new Item is offered for free
     * @param itemIsService if the new Item is a service
     * @param itemCategories a collection of categories which the new item belongs to
     *                      (if the category does not exists, it will be ignored)
     * @param itemImages a collection of Strings representing the images to display
     */

    public static void addNewItemOnBoard(String contextTag, String itemTitle, String itemDescription, String itemIdRange, ArrayList<String> currentUserBasicInfo, ArrayList<String> itemLocation, boolean itemIsCharity, boolean itemIsService, @NonNull ArrayList<String> itemCategories ,@NonNull ArrayList<String> itemImages) {
        Item newItem = Item.createItem(itemTitle,itemDescription,itemIdRange,currentUserBasicInfo,itemLocation,itemIsCharity,itemIsService,itemCategories,itemImages);
        String userId = currentUserBasicInfo.get(0);
        Log.d("TAG", userId + "  " + "mmNsy71Nf5e8ATR79b4LNk3uRSh1");
        dbRefUsers.child(currentUserBasicInfo.get(0)).child(User.ID_ITEMS_ON_BOARD_DB).child(newItem.getIdItem()).setValue(newItem.getItemBasicInfo());
        Item.insertItemInDataBase(contextTag, newItem);
    }

    //it works
    /**
     * removes an Item from the provided user's board in the database <p>
     * @param idItem the item to remove
     * @param contextTag the string which represent the Activity/Fragment wher this method is called
     * @param idUser the id of the user
     */
    public static void removeItemFromBoard(String contextTag, String idUser, String idItem) {
        Item.retrieveItemById(contextTag, BarattopoliUtil.mDatabase, idItem, new Consumer<Item>() {
            @Override
            public void accept(Item item) {
                Log.d(contextTag,item != null? "item exists": "item does not exist");
                if (item != null && item.isExchangeable()) {
                    //remove item from User's Board
                    dbRefUsers.child(idUser).child(User.ID_ITEMS_ON_BOARD_DB).child(idItem).removeValue();
                    //remove item from range
                    Log.d(contextTag,"Range: " + item.getIdRange());
                    Range.dbRefRange.child(item.getIdRange()).child(Range.ID_ITEMS_DB).child(idItem).removeValue();
                    for(String category: item.getCategories()) {
                        //remove the item from all its categories
                        Log.d(contextTag,"Category: " + category);
                        (Category.dbRefCategories).child(category).child(Category.ID_ITEMS_DB).child(idItem).removeValue();
                    }
                    //remove the item from Items node
                    (Item.dbRefItems).child(idItem).removeValue();
                } else Log.d(contextTag, "Item " + idItem + " not exchangeable, therefore, not removable from database" );
            }});


    }
    /**
     * retrieve the items on the provided user's observed items
     * with their basic info in the database
     * @param contextTag the activity/fragment where this method is called
     * @param idUser the id of the provided user
     * @param consumer the way the fetched data are used
     */
    public static void getObservedItems(String contextTag, String idUser, Consumer<Map<String, ArrayList<String>>> consumer ) {
        BarattopoliUtil.getMapWithIdAndInfo(contextTag, dbRefUsers.child(idUser), User.ID_OBSERVED_ITEMS_DB, User.OBSERVED_ITEMS_INFO_LENGTH, consumer);
    }

    /**
     * adds a new Item to the provided User's observed items in the database
     * @param idItem the new item id
     * @param idUser the id of the user
     */
    public static void addObservedItem(String idUser, String idItem) {
        //TODO
    }
    /**
     * removes an Item from the provided user's observed items in the database
     * @param idItem the item to remove
     * @param idUser the id of the user
     */
    public static void removeObservedItem(String idUser, String idItem) {
        //TODO
    }

    /**
     * retrieve the exchanges the provided user's is involved into
     * with their basic info in the database
     * @param contextTag the string representing the activity/fragment where this method is called
     * @param idUser the id of the provided user
     * @param consumer the way the fetched data are used
     */
    public static void getExchanges(String contextTag, String idUser, Consumer<Map<String, ArrayList<String>>> consumer ) {
        BarattopoliUtil.getMapWithIdAndInfo(contextTag, dbRefUsers.child(idUser), User.ID_EXCHANGES_DB, User.EXCHANGES_INFO_LENGTH, consumer);
    }

    /**
     * add a new exchange the provided user is involved into
     * (called only when creating a new exchange)
     * @param idUser the id of the user
     * @param idExchange the id of the exchange
     * @param info the basic info of the exchange in CSV format
     */
    public static void addExchange(String idUser, String idExchange, String info) {
        //TODO
    }

    /**
     * removes an exchange the provided user was involved into
     * (called only when deleting an exchange)
     * @param idExchange the item to remove
     * @param idUser the id of the user
     */
    public static void removeExchange(String idUser, String idExchange) {
        //TODO
    }

    /**
     * @return this User's profile picture
     */
    public String getImage() {
        return image;
    }

    /**
     * changes the coordinates of this User's location in the database
     * @param dbRefUser the location od the User node in the database
     * @param image the new profile picture of this User
     */
    public static void setImage(String image, DatabaseReference dbRefUser) {
        dbRefUser.child(User.IMAGE_DB).setValue(image);
    }

    /**
     * @return this User's Rank: an integer which represents the degree of this User's reliability,
     * according to the way they handled their exchanges
     */
    public Integer getRank() {
        return this.rank;
    }
    /**
     * Updates the current value of this User's rank in the database, by adding the value passed in valueToAdd
     * @param valueToAdd the value to sum to the current rank. If it is negative, it will be subtracted
     */
    public static void updateRank(Integer valueToAdd) {
        //TODO: interaction with the database
        // add constraints for param valueToAdd and reflect them in the comment .. if (valueToAdd)
    }

    private ArrayList<Long> sumOfStarsAndCntOfReviews() {
        ArrayList<Long> res = new ArrayList<>();
        long stars = 0L;
        long cnt = 0L;
        //TODO
        return res;
    }
    private Integer newRank(Integer stars) {
        long sts = this.sumOfStarsAndCntOfReviews().get(0) + stars;
        long cnt = this.sumOfStarsAndCntOfReviews().get(1) + 1;
        return (int)((((float)sts/(float)cnt) * HIGHEST_RANK)/5);
    }

    /**
     *
     * @return A CSV string with the basic information of this user when saved in an Item
     */
    public String getUserBasicInfo() { return this.userBasicInfo; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof User)) return false;
        User user = (User) o;
        return idUser.equals(user.idUser);
    }

    @Override
    public int hashCode() {
        return this.idUser.hashCode();
    }
}
