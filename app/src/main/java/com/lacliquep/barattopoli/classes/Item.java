package com.lacliquep.barattopoli.classes;
import java.util.*;


/**
 * This class represents a good or service which the user is going to offer or exchange
 *
 * @author pares
 * @since 1.0
 */
public class Item {

    //TODO: delete comments around range
    private final Long idItem;
    private static Long idItems = 0L;
    private String title;
    private String description;
    //private Range range;
    private final User owner;
    private String location;
    private boolean isCharity;
    private boolean isExchangeable;
    private boolean isService;

    private final Set<Category> categories = new HashSet<>();
    private final Collection<String> images = new ArrayList<>();

    //TODO: delete comment around range
    /**
     * Constructor for an Item
     * @param title the title of this Item
     * @param description a description of this Item
     * @param //range the Range of value of this Item
     * @param owner the owner of this Item
     * @param location the location of this Item
     * @param isCharity if this Item is offered for free
     * @param isExchangeable if this Item can be exchanged
     * @param isService if this Item is a service
     * @param categories a collection of titles for categories which the item belongs to
     *                   (if the category does not exists, it will be ignored)
     * @param images a collection of Strings representing the images to display
     *
     */
    public Item(String title, String description, /*Range range,*/ User owner, String location, boolean isCharity, boolean isExchangeable, boolean isService, Collection<String> categories, Collection<String> images) {
        this.idItem = Item.idItems++;
        this.description = description;
        this.title = title;
        //this.range = range;
        this.owner = owner;
        this.isCharity = isCharity;
        this.isExchangeable = isExchangeable;
        this.isService = isService;
        this.location = location;
        for (String cat: categories) this.addCategory(cat);
        for (String img: images) this.addImage(img);
    }

    /**
     *
     * @return this Item id
     */
    public Long getIdItem() {
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
     * modifies the title of this Item
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return the string which contains this Item description
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * modifies the description of this Item
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return the range of value of this Item
     */
    //public Range getRange() {
    //    return this.range;
    //}
    /**
     * modifies the range of value for this Item
     * @param range the Range of value of this Item
     */
    //public void setRange(Range range) {
     //   this.range = range;
    //}

    /**
     *
     * @return the User whom this Item belongs to
     */
    public User getOwner() {
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
     * modifies the current location of this Item
     * @param location the current location where this Item is
     */
    public void setLocation(String location) {
        this.location = location;
    }


    /**
     *
     * @return if this Item is offered for free
     */
    public boolean isCharity() {
        return this.isCharity;
    }
    /**
     * modifies the state of this Item, accordingly to the value of the parameter
     * @param charity If set on true, the Item becomes a good or service which is offered for free
     */
    public void setCharity(boolean charity) {
        isCharity = charity;
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
     * modifies the state of this Item, accordingly to the value of the parameter
     * @param exchangeable If set on true, the Item becomes a good or service which is possible to exchange,
     * e.g. it might be set to true after the refusal of an exchange or it could be set to false if
     * the Item has been proposed for an exchange
     */
    public void setExchangeable(boolean exchangeable) {
        isExchangeable = exchangeable;
    }

    /**
     *
     * @return true if this Item is a service, false if it is a good
     */
    public boolean isService() {
        return this.isService;
    }
    /**
     * modifies the state of this Item, accordingly to the value of the parameter
     * @param service If set on true, the Item becomes a service, otherwise it becomes a good
     */
    public void setService(boolean service) {
        isService = service;
    }



    /**
     *
     * @return all the categories which this Item belongs to
     */
    public Set<Category> getCategories() {
        return this.categories;
    }
    /**
     * adds a category which this Item belongs to.
     * as side effect, adds this item to the set of items of the specified Category.
     * @param category the title of the category
     * @return true if the specified category exists and was not already contained in the set
     * of this Item categories, false otherwise.
     * @see Category
     * @see Item#getCategories()
     */
    public boolean addCategory(String category) {
        Category c = Category.getCategoryByTitle(category);
        if (c != null) {
            c.addItemToCategory(this);
            return this.categories.add(c);
        } else return false;
    }
    /**
     * removes a category which this Item does not belong anymore
     * as side effect, removes this item to the set of items of the specified Category.
     * @param category the title of the category
     * @return true if the category exists and was contained in the set, false otherwise
     * @see Category
     * @see Item#getCategories()
     */
    public boolean removeCategory(String category) {
        Category c = Category.getCategoryByTitle(category);
        if (c!= null) {
            c.removeItemFromCategory(this);
            return this.categories.remove(c);
        } else return false;
    }

    /**
     *
     * @return all the loaded images for this Item
     */
    public Collection<String> getImages() {
        return this.images;
    }
    /**
     * adds a string which represents an image to be displayed for this Item
     * @param image the string which represents the image
     * @return true if the image was not already contained in the collection of this Item images
     * @see Item#getImages()
     */
    public boolean addImage(String image) {
        return this.images.add(image);
    }
    /**
     * removes a string which represents an image for this Item
     * @param image the string which represents the image
     * @return true if the image was contained in the set, false otherwise
     * @see Item#getImages()
     */
    public boolean removeImage(String image) {
        return this.images.remove(image);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Item)) return false;
        Item i = (Item)o;
        return this.idItem.equals(i.idItem);
    }
    @Override
    public int hashCode() {
        return Math.toIntExact(this.idItem);
    }
}
