package com.lacliquep.barattopoli.classes;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;

/**
 * this class represents a review which a User can give to another User only as a consequence of an exchange
 * a review cannot be modified.
 *
 * @author pares
 * @since 1.0
 */
public class Review {
    //to give info about the class in a log
    public static final String TAG = "Review";
    //id_review
    private final String idReview = UUID.randomUUID().toString();
    //id_author, just the id in DB
    private final User author;
    //id_receiver, just the id in DB
    private final User receiver;
    //stars
    private final Integer stars ;
    //text
    private final String text;
    //reference to the DB
// ...


    /**
     * constructor of a Review (to examine an existing review in the DB)
     * @param author the User author of this Review
     * @param receiver the User who receives this Review
     * @param stars an integer value which belongs to [1,5].
     * @param text the text of this Review
     */
    private Review(User author, User receiver, Integer stars, String text) {
        this.author = author;
        this.receiver = receiver;
        this.stars = stars;
        this.text = text;
    }

    /**
     * method to create a new Review. Called before inserting a new review in the DB
     * Check whether he author is not the same as the receiver and if the stars are a value which belongs to [0,5]
     * @param author the User author of this Review
     * @param receiver the User who receives this Review
     * @param stars an integer value which belongs to [1,5].
     * @param text the text of this Review
     * @return a new Review if all the conditions are guaranteed
     * @throws Exception if at least one condition is not guaranteed
     */
    private Review createReview(@NonNull User author, @NonNull User receiver, Integer stars, String text) throws Exception {
        String feedback = "this Review has some problems";
        boolean go = true;
        if (author.equals(receiver)) {
            feedback += ", the author cannot be the same as the receiver of a Review";
            go = false;
        }
        if (stars < 0 || stars > 5) {
            feedback += ", stars should be an integer value which belongs to [0,5]";
        }
        if (go) return new Review(author, receiver, stars, text);
        else throw new Exception(feedback);
    }


    /**
     *
     * @return the id of this Review
     */
    public String getIdReview() {
        return idReview;
    }

    /**
     *
     * @return the id of the User who writes this Review
     */
    public User getAuthor() {
        return this.author;
    }

    /**
     *
     * @return the id of the User who receives this Review
     */
    public User getReceiver() {
        return this.receiver;
    }

    /**
     *
     * @return the amount of stars the author gave to the receiver
     * in this Review
     */
    public Integer getStars() {
        return this.stars;
    }

    /**
     *
     * @return the text of this Review
     */
    public String getText() {
        return this.text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || (!(o instanceof Review))) return false;
        Review review = (Review) o;
        return idReview.equals(review.idReview) && author.equals(review.author) && receiver.equals(review.receiver) && stars.equals(review.stars) && text.equals(review.text);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idReview.hashCode();
        result = prime * result + author.hashCode();
        result = prime * result + receiver.hashCode();
        result = prime * result + stars.hashCode();
        result = prime * result + text.hashCode();
        return result;
    }

    /*ValueEventListener ExchangeStatusListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            String exchangeStatus = dataSnapshot.getValue(String.class);
            // ..
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };*/





}
