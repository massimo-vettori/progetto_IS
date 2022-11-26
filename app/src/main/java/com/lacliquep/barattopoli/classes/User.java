package com.lacliquep.barattopoli.classes;

import android.content.Context;
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

    public static final Integer HIGHEST_RANK = 100;
    public static final String CLASS_USER_DB = "users";
    public static final String ID_USER_DB = "id_user";
    public static final String NAME_DB = "name";
    public static final String SURNAME_DB = "surname";
    public static final String USERNAME_DB = "username";
    public static final String COORD_DB = "coord";
    public static final String IMAGE_DB = "image";
    public static final String RANK_DB = "rank";
    public static final String ID_REVIEWS_DB = "reviews";

    public static final String ID_ITEMS_ON_BOARD_DB = "items_on_board";
    public static final String ID_OBSERVED_ITEMS_DB = "observed_items";
    public static final String ID_EXCHANGES_DB = "exchanges";
    public static final int REVIEWS_INFO_LENGTH = Review.REVIEW_INFO_LENGTH;
    public static final int ITEMS_ON_BOARD_INFO_LENGTH = Item.ITEM_INFO_LENGTH;
    public static final int OBSERVED_ITEMS_INFO_LENGTH = Item.ITEM_INFO_LENGTH;
    public static final int EXCHANGES_INFO_LENGTH = Exchange.EXCHANGE_INFO_LENGTH;

    public static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child(User.CLASS_USER_DB);
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final String idUser;
    private final String name;
    private final String surname;
    private final String username;
    private final String coord;
    private final String image;
    private final Integer rank;




    /**
     * constructor of a User, after a fetch from the database
     *
     * @param idUser   the ID created via FirebaseAuth
     * @param username the username of this User
     * @param name the name of this User
     * @param surname the surname of this User
     * @param coord    the coordinates of this User's location
     * @param image the profile picture for this User
     * @param rank the rank of this User, an Iteger value between 1 and HIGHER_RANK
     * @see com.google.firebase.auth.FirebaseAuth
     * @see User#HIGHEST_RANK
     */
    User(String idUser, String username, String name, String surname, String coord, Integer rank, String image) {
        this.idUser = idUser;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.coord = coord;
        this.rank = rank;
        this.image = image;
    }
    /**
     * creator of a User, to be called when creating a new User to store in the DB
     * the rank will be set to the medium value between 1 and HIGHER_RANK
     * @param idUser   the ID created via FirebaseAuth
     * @param username the username of this User
     * @param name the name of this User
     * @param surname the surname of this User
     * @param coord    the coordinates of this User's location
     * @param image the profile picture for this User
     * @see com.google.firebase.auth.FirebaseAuth
     * @see User#HIGHEST_RANK
     */
    public static User createUser(String idUser, String username, String name, String surname, String coord, String image) {
        return new User(idUser, username, name, surname, coord, getMediumRank() , image);
    }


    //FUNZIONA
    private static void retrieveHelper(Map<String, Object> map, String dataBaseKeyTag, ArrayList<String> mainData, int mainDataIndex) {
        if (map.containsKey(dataBaseKeyTag)) {
            Object value = map.get(dataBaseKeyTag);
            mainData.set(mainDataIndex, value != null ? value.toString() : "");
        } else mainData.set(mainDataIndex, "");
    }
    //FUNZIONA
    /**
     * read from the database all the values regarding the User with the provided id <p>
     * Don't try taking out data from the consumer: it is not going to work
     * @param context the activity/fragment where this method is called
     * @param dbRef the database reference
     * @param id the id of the User to retrieve
     * @param consumer the way the fetched data are being used
     */
    public static void retrieveUserById(Context context, DatabaseReference dbRef, String id, Consumer<User> consumer) {
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
                    User.retrieveHelper(map, User.USERNAME_DB, UserData,0);
                    User.retrieveHelper(map, User.NAME_DB, UserData,1);
                    User.retrieveHelper(map, User.SURNAME_DB, UserData,2);
                    User.retrieveHelper(map, User.COORD_DB, UserData,3);
                    User.retrieveHelper(map, User.RANK_DB, UserData,4);
                    User.retrieveHelper(map, User.IMAGE_DB, UserData,5);
                    Integer rank = UserData.get(4).equals("")?0: Integer.valueOf(UserData.get(4));
                    consumer.accept(new User(id, UserData.get(0), UserData.get(1), UserData.get(2), UserData.get(3), rank, UserData.get(5)));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(context.toString(), "load:onCancelled", error.toException());
            }
        });
    }

    public static void retrieveCurrentUser(Context context, DatabaseReference dbRefUsers, Consumer<User> consumer){
        User.retrieveUserById(context, dbRefUsers, mAuth.getUid(), consumer);
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
     *
     * @param name the name of this User
     */
    public static void setName(String name) {
        //TODO
    }

    /**
     * @return this User's surname
     */
    public String getSurname() {
        return this.surname;
    }
    /**
     * changes this User's surname
     *
     * @param surname the surname of this User
     */
    public static void setSurname(String surname) {
        //TODO
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
     * @param username the username of this User
     */
    public static void setUsername(String username, String idUser) {
        //TODO
    }

    /**
     * @return the coordinated of this User's location
     */
    public String getCoord() {
        return coord;
    }
    /**
     * changes the coordinates of this User's location in the database
     * @param coord the coordinates of this User's new location
     */
    public static void setCoord(String coord, String idUser) {
        //TODO
    }

    /**
     * retrieve the reviews the provided user gave to another user or another user gave to the provided user
     * with their basic info (text not included) in the database
     * @param context the activity/fragment where this method is called
     * @param idUser the id of the provided user
     * @param consumer the way the fetched data are used
     */
    public static void  getReviews(Context context,String idUser, Consumer<Map<String, ArrayList<String>>> consumer) {
        DataBaseInteractor.getMapWithIdAndInfo(context, mDatabase.child(idUser), User.ID_REVIEWS_DB, User.REVIEWS_INFO_LENGTH, consumer);
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
     * @param context the activity/fragment where this method is called
     * @param idUser the id of the provided user
     * @param consumer the way the fetched data are used
     */
    public static void  getItemsOnBoard(Context context,String idUser, Consumer<Map<String, ArrayList<String>>> consumer) {
        DataBaseInteractor.getMapWithIdAndInfo(context, mDatabase.child(idUser), User.ID_ITEMS_ON_BOARD_DB, User.ITEMS_ON_BOARD_INFO_LENGTH, consumer);
    }

    /**
     * add a new Item with its basic info  to the provided User's board in the database <p>
     * (to be called only when creating a new item)
     * @param idItem the new item id
     * @param idUser the id of the User
     * @param info the basic info of the item in CSV format
     */
    public static void addItemOnBoard(String idUser, String idItem, String info) {
        //TODO
    }
    /**
     * removes an Item from the provided user's board in the database <p>
     * (to be called only when deleting an item)
     * @param idItem the item to remove
     * @param idUser the id of the user
     */
    public static void removeItemFromBoard(String idUser, String idItem) {
        //TODO
    }
    /**
     * retrieve the items on the provided user's observed items
     * with their basic info in the database
     * @param context the activity/fragment where this method is called
     * @param idUser the id of the provided user
     * @param consumer the way the fetched data are used
     */
    public static void getObservedItems(Context context, String idUser, Consumer<Map<String, ArrayList<String>>> consumer ) {
        DataBaseInteractor.getMapWithIdAndInfo(context, mDatabase.child(idUser), User.ID_OBSERVED_ITEMS_DB, User.OBSERVED_ITEMS_INFO_LENGTH, consumer);
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
     * @param context the activity/fragment where this method is called
     * @param idUser the id of the provided user
     * @param consumer the way the fetched data are used
     */
    public static void getExchanges(Context context, String idUser, Consumer<Map<String, ArrayList<String>>> consumer ) {
        DataBaseInteractor.getMapWithIdAndInfo(context, mDatabase.child(idUser), User.ID_EXCHANGES_DB, User.EXCHANGES_INFO_LENGTH, consumer);
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
     * @return this User's Rank: an integer which represents the degree of this User's reliability,
     * according to the way they handled their exchanges
     */
    public Integer getRank() {
        return rank;
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
