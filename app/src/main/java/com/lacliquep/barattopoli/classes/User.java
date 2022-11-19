package com.lacliquep.barattopoli.classes;

import androidx.annotation.NonNull;

import java.util.*;

/**
 * this class represents a User of the app
 *
 * @author pares
 * @since 1.0
 */
public class User {
    //TODO: delete comments around exchange
    private final String idUser;
    private String name;
    private String surname;
    private String username;
    private String coord;
    private final Collection<String> images = new ArrayList<>();
    private Integer rank;
    private final Collection<String> reviews = new ArrayList<>();

    private final Set<Item> itemsOnBoard = new HashSet<>();
    //private final Set<Exchange> exchanges = new HashSet<>();

    /**
     * constructor of a User
     * @param idUser the ID created via FirebaseAuth
     * @param username the username of this User
     * @param coord the coordinates of this User's location
     * @see com.google.firebase.auth.FirebaseAuth
     */
    public User(String idUser, String username, String coord) {
        this(idUser,username, "", "", coord);
    }
    public User(String idUser, String username, String name, String surname, String coord) {
        this.idUser = idUser;
        this.username = username;
        this.coord = coord;
        this.rank = 50;
    }

    /**
     *
     * @return this User's id
     */
    public String getIdUser() {
        return this.idUser;
    }

    /**
     *
     * @return this User's name
     */
    public String getName() {
        return this.name;
    }
    /**
     * changes this User's name
     * @param name the name of this User
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return this User's surname
     */
    public String getSurname() {
        return this.surname;
    }
    /**
     * changes this User's surname
     * @param surname the surname of this User
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     *
     * @return this User's username
     */
    public String getUsername() {
        return username;
    }
    /**
     * changes this User's username
     * @param username the username of this User
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return the coordinated of this User's location
     */
    public String getCoord() {
        return coord;
    }
    /**
     * changes the coordinates of this User's location
     * @param coord the coordinates of this User's new location
     */
    public void setCoord(String coord) {
        this.coord = coord;
    }


    /**
     *
     * @return the collection of the strings representing this User's loaded images
     */
    public Collection<String> getImages() {
        return images;
    }
    /**
     * adds a string which represents an image to be displayed for this User
     * @param image the string which represents the image
     * @return true if the image was not already contained in the collection of this User's images
     * @see User#getImages()
     */
    public boolean addImage(String image) {
        return this.images.add(image);
    }
    /**
     * removes a string which represents an image for this User
     * @param image the string which represents the image
     * @return true if the image was contained in the set, false otherwise
     * @see User#getImages()
     */
    public boolean removeImage(String image) {
        return this.images.remove(image);
    }

    /**
     *
     * @return this User's Rank: an integer which represents the degree of this User's reliability,
     * according to the way they handled their exchanges
     */
    public Integer getRank() {
        return rank;
    }
    /**
     * Updates the current value of this User's rank by adding the value passed in valueToAdd
     * @param valueToAdd the value to sum to the current rank. If it is negative, it will be subtracted
     * @see User#getRank()
     */
    public void updateRank(Integer valueToAdd) {
        //TODO: add constraints for param valueToAdd and reflect them in the comment .. if (valueToAdd)
        this.rank += valueToAdd;
    }

    /**
     *
     * @return the collection of the reviews which other Users gave to this user
     */
    public Collection<String> getReviews() {
        return reviews;
    }
    /**
     * adds a string which represents a review another User gave to this User
     * @param review the string which represents the review
     * @return true if the review was not already contained in the collection of this User's reviews
     * @see User#getReviews()
     */
    public boolean addReview(String review) {
        return this.reviews.add(review);
    }
    /**
     * removes a string which represents a review another User gave to this User
     * @param review the string which represents the review
     * @return true if the review was contained in the collection of this User's reviews, false otherwise
     * @see User#getReviews()
     */
    public boolean removeReview(String review) {
        return this.reviews.remove(review);
    }

    /**
     *
     * @return the set containing all the Items this User has in their Board
     */
    public Set<Item> getItemsOnBoard() {
        return itemsOnBoard;
    }
    /**
     * adds a new Item to this User's board if this User is the actual owner of the item
     * @param item the new item
     * @return true if the item was not already contained in this User's board, false otherwise.
     * @throws Exception if this User is not the actual owner of the item
     * @see Item
     * @see User#getItemsOnBoard()
     */
    public boolean addItemOnBoard(@NonNull Item item) throws Exception {
        if (!(item.getOwner().equals(this))) throw new Exception("this User is not the actual owner of the item");
        else return this.itemsOnBoard.add(item);
    }
    /**
     * removes an Item from this user's board.
     * Also Removes the item from the sets of items of all the correspondent categories which this item belonged to
     * @param item the item to remove
     * @return true if the item was contained in this User's board, false otherwise
     * @throws Exception if this User is not the actual owner of the item or the item is not exchangeable
     * @see Item
     * @see User#getItemsOnBoard()
     */
    public boolean removeItemFromBoard(@NonNull Item item) throws Exception{

        if (!(item.getOwner().equals(this))) throw new Exception("this User is not the actual owner of the item");
        else {
            if (!(item.isExchangeable())) throw new Exception("this item needs to be set as exchangeable before its removal");
            else {
                for (Category cat: item.getCategories()) {
                    cat.removeItemFromCategory(item);
                }
                return this.itemsOnBoard.remove(item);
            }
        }
    }

    /**
     *
     * @return the set containing all the exhanges in which this User is involved
     */
    //public Set<Exchange> getExchanges() {
    //    return exchanges;
    //}
}

