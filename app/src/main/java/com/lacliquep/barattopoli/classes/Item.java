package com.lacliquep.barattopoli.classes;
import androidx.annotation.NonNull;

import java.util.*;


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
    public static final String ID_OWNER_DB = "id_user";
    public static final String LOCATION_DB = "location";
    public static final String IS_CHARITY_DB = "is_charity";
    public static final String IS_EXCHANGEABLE_DB = "is_exchangeable";
    public static final String IS_SERVICE_DB = "is_service";
    public static final String ID_CATEGORIES_DB = "id_category";
    public static final String IMAGES_DB = "images";

    private final String idItem;
    private String title;
    private String description;
    private final String idRange;
    private final String idOwner;
    private String location;
    private boolean isCharity;
    private boolean isExchangeable;
    private boolean isService;
    //id_category, just a collection of their id in DB

    private final Set<String> idCategories = new HashSet<>();
    private final Set<Category> categories = new HashSet<>();
    private final Collection<String> images = new ArrayList<>();

    //TODO: delete comment around range
    /**
     * Constructor for an Item, after a fetch from the database, called by DataBaseable.retrieveItem
     * @param title the title of this Item
     * @param description a description of this Item
     * @param idRange the id of the Range of value of this Item
     * @param idOwner the id of the owner of this Item
     * @param location the location of this Item
     * @param isCharity if this Item is offered for free
     * @param isExchangeable if this Item can be exchanged
     * @param isService if this Item is a service
     * @param categoriesTitles a collection of titles for categories which the item belongs to
     *                   (if the category does not exists, it will be ignored)
     * @param images a collection of Strings representing the images to display
     *
     */
    Item(String idItem, String title, String description,@NonNull String idRange, String idOwner, String location, boolean isCharity, boolean isExchangeable, boolean isService, @NonNull Collection<String> categoriesTitles ,@NonNull Collection<String> images) {
        this.idItem = idItem;
        this.description = description;
        this.title = title;
        this.idRange = idRange;
        this.idOwner = idOwner;
        this.isCharity = isCharity;
        this.isExchangeable = isExchangeable;
        this.isService = isService;
        this.location = location;
        for(String t: categoriesTitles) {
            Category c = Category.getCategoryByTitle(t);
            if (c != null) this.categories.add(c);
        }
        this.images.addAll(images);
    }
    /**
     * Creator of a new Item, to be called when the current User is creating a new Item, to store in the DB <p>
     * This item will be set as exchangeable
     * @param title the title of this Item
     * @param description a description of this Item
     * @param idRange the id of the Range of value of this Item
     * @param currentUser the the id of the owner of this Item
     * @param location the location of this Item
     * @param isCharity if this Item is offered for free
     * @param isService if this Item is a service
     * @param categoriesTitles a collection of titles for categories which the item belongs to
     *                   (if the category does not exists, it will be ignored)
     * @param images a collection of Strings representing the images to display
     *
     */
   public static Item createItem(String title, String description, String idRange, String currentUser, String location, boolean isCharity, boolean isService, @NonNull Collection<String> categoriesTitles ,@NonNull Collection<String> images) {
        return new Item(UUID.randomUUID().toString(), title, description, idRange, currentUser, location, isCharity, true, isService, categoriesTitles, images);
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

    /*public Range getRange() {
        DataBaseInteractor.retrieveRangeById(this.idRange, Range.CLASS_RANGE_DB);
    }*/

    /**
     *
     * @return the User'id whom this Item belongs to
     */
    public String getIdOwner() {
        return this.idOwner;
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
     * @return if this Item is offered for free
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
     * @return all the categories which this Item belongs to
     */
    public Set<Category> getCategories() {
        return this.categories;
    }

    /**
     *
     * @return all the loaded images for this Item
     */
    public Collection<String> getImages() {
        return this.images;
    }

    //EQUALS & HASHCODE

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || (!(o instanceof Item))) return false;
        Item item = (Item) o;
        return idOwner.equals(item.idOwner) && isCharity == item.isCharity && isExchangeable == item.isExchangeable && isService == item.isService && idItem.equals(item.idItem) && title.equals(item.title) && description.equals(item.description) && location.equals(item.location) && categories.equals(item.categories) && images.equals(item.images);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idItem.hashCode();
        result = prime * result + title.hashCode();
        result = prime * result + description.hashCode();
        result = prime * result + idOwner.hashCode();
        result = prime * result + location.hashCode();
        result = prime * result + (!isCharity?0:1);
        result = prime * result + (!isExchangeable?0:1);
        result = prime * result + categories.hashCode();
        result = prime * result + images.hashCode();
        return result;
    }

    //METHODS TO MODIFY THIS ITEM (TODO: ARE THEY USEFUL?)
    //since the changes will be only at DB level with correct functions

    /**
     * modifies the description of this Item
     * @param description the new description
     * @throws NonModifiableException if this Item is not exchangeable
     */
    protected void setDescription(String description) throws NonModifiableException {
        if (!this.isExchangeable) throw new NonModifiableException();
        this.description = description;
    }

    /**
     * modifies the title of this Item if it is still exchangeable
     * @param title the new title
     * @throws NonModifiableException if this Item is not exchangeable
     */
    protected void setTitle(String title) throws NonModifiableException {
        if (!this.isExchangeable) throw new NonModifiableException();
        this.title = title;
    }

    /**
     * modifies the range of value for this Item
     * @param range the Range of value of this Item
     * @throws NonModifiableException if this Item is not exchangeable
     */
    protected void setRange(Range range) throws NonModifiableException {
        if (!this.isExchangeable) throw new NonModifiableException();
       //this.range = range;
    }

    /**
     * modifies the current location of this Item
     * @param location the current location where this Item is
     * @throws NonModifiableException if this Item is not exchangeable
     */
    protected void setLocation(String location)throws NonModifiableException {
        if (!this.isExchangeable) throw new NonModifiableException();
        this.location = location;
    }

    /**
     * modifies the state of this Item, accordingly to the value of the parameter
     * @param charity If set on true, the Item becomes a good or service which is offered for free
     * @throws NonModifiableException if this Item is not exchangeable
     */
    protected void setCharity(boolean charity) throws NonModifiableException {
        if (!this.isExchangeable) throw new NonModifiableException();
        isCharity = charity;
    }

    /**
     * modifies the state of this Item, accordingly to the value of the parameter
     * @param exchangeable If set on true, the Item becomes a good or service which is possible to exchange,
     * e.g. it might be set to true after the refusal of an exchange or it could be set to false if
     * the Item has been proposed for an exchange
     */
    protected void setExchangeable(boolean exchangeable) {
        isExchangeable = exchangeable;
    }

    /**
     * modifies the state of this Item, accordingly to the value of the parameter
     * @param service If set on true, the Item becomes a service, otherwise it becomes a good
     * @throws NonModifiableException if this Item is not exchangeable
     */
    protected void setService(boolean service) throws NonModifiableException {
        if (!this.isExchangeable) throw new NonModifiableException();
        isService = service;
    }

    /**
     * adds a category which this Item belongs to.
     * (NO: as side effect, adds this item to the set of items of the specified Category.)
     * @param category the title of the category
     * @return true if the specified category exists and was not already contained in the set
     * of this Item categories, false otherwise.
     * @see Category
     * @see Item#getCategories()
     */
    protected boolean addCategory(String category) {
        Category c = Category.getCategoryByTitle(category);
        if (c != null) {
            //c.addItemToCategory(this);
            return this.categories.add(c);
        } else return false;
    }
    /**
     * removes a category which this Item does not belong anymore
     * (NO:as side effect, removes this item to the set of items of the specified Category.)
     * @param category the title of the category
     * @return true if the category exists and was contained in the set, false otherwise
     * @see Category
     * @see Item#getCategories()
     */
    protected boolean removeCategory(String category) {
        Category c = Category.getCategoryByTitle(category);
        if (c!= null) {
            //c.removeItemFromCategory(this);
            return this.categories.remove(c);
        } else return false;
    }

    /**
     * adds a string which represents an image to be displayed for this Item
     * @param image the string which represents the image
     * @return true if the image was not already contained in the collection of this Item images
     * @see Item#getImages()
     */
    protected boolean addImage(String image) {
        return this.images.add(image);
    }
    /**
     * removes a string which represents an image for this Item
     * @param image the string which represents the image
     * @return true if the image was contained in the set, false otherwise
     * @see Item#getImages()
     */
    protected boolean removeImage(String image) {
        return this.images.remove(image);
    }

}
