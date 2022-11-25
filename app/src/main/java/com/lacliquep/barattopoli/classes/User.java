package com.lacliquep.barattopoli.classes;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
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

    public static final Integer HIGHER_RANK = 100;
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

    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final String idUser;
    private String name;
    private String surname;
    private String username;
    private String coord;
    //just one in database: use array with one picture, leave it for further evolution)
    private final Collection<String> images = new ArrayList<>();
    private Integer rank;
    private final Set<String> idReviews = new HashSet<>();
    private final Set<String> idItemsOnBoard = new HashSet<>();
    private final Set<String> idObservedItems = new HashSet<>();
    private final Set<String> idExchanges = new HashSet<>();


    /**
     * constructor of a User, after a fetch from the database
     *
     * @param idUser   the ID created via FirebaseAuth
     * @param username the username of this User
     * @param name the name of this User
     * @param surname the surname of this User
     * @param coord    the coordinates of this User's location
     * @param images the loaded images for this User
     * @param rank the rank of this User, an Iteger value between 1 and HIGHER_RANK
     * @see com.google.firebase.auth.FirebaseAuth
     * @see User#HIGHER_RANK
     */
    User(String idUser, String username, String name, String surname, String coord, Integer rank, @NonNull ArrayList<String> images) {
        this.idUser = idUser;
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.coord = coord;
        this.rank = rank;
        this.images.addAll(images);
    }
    /**
     * creator of a User, to be called when creating a new User to store in the DB
     * the rank will be set to the medium value between 1 and HIGHER_RANK
     * @param idUser   the ID created via FirebaseAuth
     * @param username the username of this User
     * @param name the name of this User
     * @param surname the surname of this User
     * @param coord    the coordinates of this User's location
     * @param images the loaded images for this User
     * @see com.google.firebase.auth.FirebaseAuth
     * @see User#HIGHER_RANK
     */
    public static User createUser(String idUser, String username, String name, String surname, String coord, @Nullable ArrayList<String> images) {
        ArrayList<String> e = new ArrayList<>();
        if (images == null) images = e;
        return new User(idUser, username, name, surname, coord, getMediumRank() , images);
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
     * @param dbRefUsers the database location for the class users
     * @param id the id of the User to retrieve
     * @param consumer the way the fetched data are being used
     */
    public static void retrieveUserById(Context context, DatabaseReference dbRefUsers, String id, Consumer<User> consumer) {
        dbRefUsers.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    //TODO: parse UserData.get(5) to retrieve different images
                    Integer rank = UserData.get(4).equals("")?0: Integer.valueOf(UserData.get(4));
                    consumer.accept(new User(id, UserData.get(0), UserData.get(1), UserData.get(2), UserData.get(3), rank, new ArrayList<String> (Collections.singleton(UserData.get(5)))));
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
        return User.HIGHER_RANK/2;
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

    public Set<String> getIdReviews() {
        return idReviews;
    }

    public static void setIdReviews(ArrayList<String> ids) {
        //TODO
    }
    /**
     * adds a review id another User gave to this User or this User gave to another User in the database
     * as side-effect, it updates this User's Rank in the database
     * @param idReview the new id review
     */
    public static void addIdReview(String idReview, Integer stars) {
        //TODO
    }
    /**
     * removes a review id another User gave to this User or this User gave to another User in the database
     * it changes the User's rank in the database
     * @param idReview the new review
     */
    public static void removeIdReview(String idReview) {
        //TODO
    }

    public Set<String> getIdItemsOnBoard() {
        return idItemsOnBoard;
    }

    public static void setIdItemsOnBoard(ArrayList<String> ids) {
        //TODO
    }
    /**
     * adds a new Item to this User's board in the database
     * @param idItem the new item id
     */
    public static void addIdItemOnBoard(String idItem) {
        //TODO
    }
    /**
     * removes an Item from this user's board in the database
     * Also Removes the item from the sets of items of all the correspondent categories which this item belonged to in the database
     * @param idItem the item to remove
     */
    public static void removeIdItemFromBoard(String idItem) {
        //TODO
    }

    public Set<String> getIdObservedItems() {
        return idObservedItems;
    }

    public static void setIdObservedItems(ArrayList<String> ids) {
        //TODO
    }
    /**
     * adds a new Item to this User's observed items in the database
     * @param idItem the new item id
     */
    public static void addIdObservedItems(String idItem) {
        //TODO
    }
    /**
     * removes an Item from this user's observed items in the database
     * @param idItem the item to remove
     */
    public static void removeIdObservedItem(String idItem) {
        //TODO
    }


    public Set<String> getIdExchanges() {
        return idExchanges;
    }

    public void setIdExchanges(ArrayList<String> ids) {
        //TODO
    }
    /**
     * adds a new Item to this User's observed items in the database
     * @param idExchange the new item id
     */
    public static void addIdExchange(String idExchange) {
        //TODO
    }
    /**
     * removes an Item from this user's observed items in the database
     * @param idExchange the item to remove
     */
    public static void removeIdExchange(String idExchange) {
        //TODO
    }

    /**
     * @return the collection of the strings representing this User's loaded images
     */
    public Collection<String> getImages() {
        return images;
    }
    /**
     * adds a string which represents an image to be displayed for this User in the database
     * @param image the string which represents the image
     */
   public static void addImage(String image) {
        //TODO
    }
    /**
     * removes a string which represents an image for this User from the database
     * @param image the string which represents the image
     */
    public static void removeImage(String image) {
        //TODO
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

    /**
     * @return the collection of the reviews id which other Users gave to this User
     */
    public Collection<String> getIdReceivedReviews() {
        Collection<String> received = new ArrayList<>();
        for (String rev: this.idReviews) {
            //TODO: retrieve the Review corrispondant to the id and its idReceiver
           // if (rev.getReceiver().equals(this)) received.add(rev);
        }
        return received;
    }
    /**
     * @return the collection of the reviews id which this User gave to other Users
     */
    public Collection<Review> getIdGivenReviews() {
        Collection<Review> gave = new ArrayList<>();
        for (String rev: this.idReviews) {
            //TODO: retrieve the Review corrispondant to the id and its idAuthor
            //if (rev.getAuthor().equals(this)) gave.add(rev);
        }
        return gave;
    }

    private ArrayList<Long> sumOfStarsCntOfReviews() {
        ArrayList<Long> res = new ArrayList<>();
        long stars = 0L;
        long cnt = 0L;

        for (String rev: this.getIdReceivedReviews()) {
            //TODO: retrieve the Review corrispondant to the id and its stars
            //stars += rev.getStars().longValue();
            cnt += 1;
        }
        res.add(stars);
        res.add(cnt);
        return res;
    }
    private Integer newRank(Integer stars) {
        long sts = this.sumOfStarsCntOfReviews().get(0) + stars;
        long cnt = this.sumOfStarsCntOfReviews().get(1) + 1;
        return (int)((((float)sts/(float)cnt) * HIGHER_RANK)/5);
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
