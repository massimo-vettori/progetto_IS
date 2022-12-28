package com.lacliquep.barattopoli.classes;

import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * this class represents an exchange of Items between two Users.
 *
 * @author pares
 * @since 1.0
 */
public class Exchange implements Serializable {

    //in_approval: proposed object still exchangeable
    //accepted: items become not-exchangeable, waiting for the conclusion
    //annulled: annulled
    //closed: confirmation of acceptance has been made
    //happened: possibility of review by proposer and applicant,
    //reviewed_by_applicant: possibility of review by proposer
    //reviewed_by_proposer: possibility of review by applicant
    //reviewed_by_both: elimination of object, elimination of exchange
    //illegal: creation prevented, ex: same id_item, same id_user
    //refused: delete exchange

    /**
     * IN_APPROVAL: proposed object still exchangeable, <p>
     * ACCEPTED: items become not-exchangeable, waiting for the conclusion, <p>
     * ANNULLED: annulled, <p>
     * CLOSED: confirmation of acceptance has been made, <p>
     * HAPPENED: possibility of review by applicant and proposer <p>
     * REVIEWED_BY_APPLICANT: possibility of review by proposer, <p>
     * REVIEWED_BY_PROPOSER: possibility of review by applicant, <p>
     * REVIEWED_BY_BOTH: elimination of object, elimination of exchange, <p>
     * ILLEGAL: creation prevented, ex: same id_item, same id_user, <p>
     * REFUSED: delete exchange
     */
    public enum ExchangeStatus {IN_APPROVAL, ACCEPTED, ANNULLED, CLOSED, HAPPENED, REVIEWED_BY_APPLICANT, REVIEWED_BY_PROPOSER, REVIEWED_BY_BOTH, ILLEGAL, REFUSED}

    public static final String CLASS_EXCHANGE_DB = "exchanges";
    public static final String APPLICANT_DB = "applicant";
    public static final String PROPOSER_DB = "proposer";
    public static final String ID_EXCHANGE_DB = "id_exchange";
    public static final String PROPOSER_ITEMS_DB = "proposer_items";
    public static final String APPLICANT_ITEMS_DB = "applicant_items";
    public static final String EXCHANGE_STAUS_DB = "exchange_status";
    public static final String DATE_DB = "date";
    public static final int EXCHANGE_INFO_LENGTH = 7;
    public static final String INFO_PARAM = "id_exchange,exchange_status,date,id_applicant,id_proposer,first_applicant_item,first_proposer_item";

    private ExchangeStatus exchangeStatus;
    private final String date;
    private final String idApplicant;
    private final User applicant;
    private final String idProposer;
    private final User proposer;
    private final String idExchange; //= UUID.randomUUID().toString();
    private final ArrayList<Item> proposerItems = new ArrayList<>();
    private final ArrayList<Item> applicantItems = new ArrayList<>();

    public static final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Exchange.CLASS_EXCHANGE_DB);




    /**
     * constructor of an exchange (to be called from other methods which check the arguments sanity)
     * @param exchangeStatus the status of the exchange
     * @param applicant the User who wants the proposed Object (they create the exchange)
     * @param proposer the User whose Item has been chosen for this Exchange.
     * @param applicantItems the Items the applicant offers in exchange for the Item/s they choose from the proposer's board.
     * @param proposerItems the Items which have been chosen for the exchange, from the proposer's board, by the applicant
     * @see ExchangeStatus
     */

    private Exchange(String idExchange, String date, ExchangeStatus exchangeStatus, User applicant, User proposer, ArrayList<Item> applicantItems, ArrayList<Item> proposerItems) {
        this.idExchange = idExchange;
        this.date = date;
        this.proposer = proposer;
        this.idProposer = proposer.getIdUser();
        this.applicant = applicant;
        this.idApplicant = applicant.getIdUser();
        this.proposerItems.addAll(proposerItems);
        this.applicantItems.addAll(applicantItems);
        this.exchangeStatus = exchangeStatus;
    }

    public static void getExchangeFromBasicInfo(String exchangeBasicInfo, Consumer<Exchange> consumer) {
        //StringValueOfExchangeStatus(exchangeStatus) + "," + date + "," + applicant.getIdUser() + "," + proposer.getIdUser() + "," + applicantFirstItem + "," + proposerItems.get(0).getIdItem();
        ArrayList<String> values = new ArrayList<>(Arrays.asList(exchangeBasicInfo.split(",", Exchange.EXCHANGE_INFO_LENGTH)));
        Item.retrieveItemsByIds("Exchange", FirebaseDatabase.getInstance().getReference(), new ArrayList<>(Arrays.asList(values.get(5), values.get(6))), new Consumer<ArrayList<Item>>() {
            @Override
            public void accept(ArrayList<Item> items) {
                consumer.accept(new Exchange(values.get(0), values.get(2), Exchange.exchangeStatusValueOf(values.get(1)), User.getSampleUser(), User.getSampleUser(), new ArrayList<>(Collections.singletonList(items.get(0))), new ArrayList<>(Collections.singletonList(items.get(1)))));
            }
        });
    }

    public static void getUserExchanges(String idUser, boolean applicant, boolean both, Consumer<Exchange> consumer) {
        FirebaseDatabase.getInstance().getReference().child(User.CLASS_USER_DB).child(idUser).child(Exchange.CLASS_EXCHANGE_DB).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    ArrayList<Exchange> listOfExchanges = new ArrayList<>();
                    for (DataSnapshot exch: snapshot.getChildren()) {
                        getExchangeFromBasicInfo(exch.getValue().toString(), new Consumer<Exchange>() {
                            @Override
                            public void accept(Exchange exchange) {
                                if (both) consumer.accept(exchange);
                                else {
                                    if (applicant) {
                                        if (exchange.getApplicant().getIdUser().equals(idUser)) consumer.accept(exchange);
                                    } else {
                                        if (exchange.getProposer().getIdUser().equals(idUser)) consumer.accept(exchange);
                                    }
                                }

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * used when a sample is needed or in case of errors
     * @return a sample of an ILLEGAL exchange (it won't be shown)
     */
    public static Exchange getSampleExchange() {
        return new Exchange("basicExchange", (new Date(System.currentTimeMillis())).toString(), ExchangeStatus.ILLEGAL, User.getSampleUser(), User.getSampleUser(), new ArrayList<>(Collections.singletonList(Item.getSampleItem())), new ArrayList<>(Collections.singletonList(Item.getSampleItem())));
    }



    /**
     * convert the string value of an exchange status in the correct enum value (when retrieving its value from the database)
     * @param exchangeStatus the string value to be converted
     * @return the enum correspondent value to the provided exchange status string
     */
    public static ExchangeStatus exchangeStatusValueOf(@Nullable String exchangeStatus) {
        ExchangeStatus res = ExchangeStatus.ILLEGAL;
        if (exchangeStatus != null) {
            switch (exchangeStatus) {
                //IN_APPROVAL, ACCEPTED, ANNULLED, CLOSED, HAPPENED, REVIEWED_BY_APPLICANT, REVIEWED_BY_PROPOSER, REVIEWED_BY_BOTH, ILLEGAL, REFUSED}
                case "in_approval":
                    res = ExchangeStatus.IN_APPROVAL;
                    break;
                case "accepted":
                    res = ExchangeStatus.ACCEPTED;
                    break;
                case "annulled":
                    res = ExchangeStatus.ANNULLED;
                    break;
                case "closed":
                    res = ExchangeStatus.CLOSED;
                    break;
                case "happened":
                    res = ExchangeStatus.HAPPENED;
                    break;
                case "REVIEWED_BY_APPLICANT":
                    res = ExchangeStatus.REVIEWED_BY_APPLICANT;
                    break;
                case "REVIEWED_BY_PROPOSER":
                    res = ExchangeStatus.REVIEWED_BY_PROPOSER;
                    break;
                case "REVIEWED_BY_BOTH":
                    res = ExchangeStatus.REVIEWED_BY_BOTH;
                    break;
                case "illegal":
                    res = ExchangeStatus.ILLEGAL;
                    break;
                case "refused":
                    res = ExchangeStatus.REFUSED;
                    break;
            }
        }
        return res;
    }

    /**
     * convert the enum exchange status in a string (to store its value in the database)
     * @param exchangeStatus the enum value to be converted
     * @return the String correspondent value to the provided exchange status enum
     */
    public static String StringValueOfExchangeStatus(ExchangeStatus exchangeStatus) {
        String res = "illegal";
        switch (exchangeStatus) {
            //IN_APPROVAL, ACCEPTED, ANNULLED, CLOSED, HAPPENED, REVIEWED_BY_APPLICANT, REVIEWED_BY_PROPOSER, REVIEWED_BY_BOTH, ILLEGAL, REFUSED}
            case IN_APPROVAL: res = "in_approval"; break;
            case ACCEPTED: res = "accepted"; break;
            case ANNULLED: res = "annulled"; break;
            case CLOSED: res = "closed"; break;
            case HAPPENED: res = "happened"; break;
            case REVIEWED_BY_APPLICANT: res = "REVIEWED_BY_APPLICANT"; break;
            case REVIEWED_BY_PROPOSER: res = "REVIEWED_BY_PROPOSER"; break;
            case REVIEWED_BY_BOTH: res = "REVIEWED_BY_BOTH"; break;
            case ILLEGAL: res = "illegal"; break;
            case REFUSED: res = "refused"; break;
        }
        return res;
    }
    /**
     * read from the database all the values regarding the User with the provided id <p>
     * Don't try taking out data from the consumer: it is not going to work
     * @param contextTag the string representing the activity/fragment where this method is called
     * @param id the id of the Exchange to retrieve
     * @param consumer the way the fetched data are being used
     */
    public static void retrieveExchangeById(String contextTag, String id, Consumer<Exchange> consumer) {
        Exchange.dbRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //map with field=value
                    Map<String, String> map = new HashMap<>();
                    ArrayList<Map<String, ArrayList<String>>> items = new ArrayList<>();
                    //proposer items
                    items.add(new HashMap<>());
                    //applicant items
                    items.add(new HashMap<>());
                    String date = "";
                    for (DataSnapshot child: snapshot.getChildren()) {
                        String field = child.getKey();
                        if (field != null) {
                            if (field.equals(Exchange.APPLICANT_ITEMS_DB) || field.equals(Exchange.PROPOSER_ITEMS_DB)) {
                                int index = 0;
                                if (field.equals(Exchange.APPLICANT_ITEMS_DB)) index = 1;
                                for (DataSnapshot it : child.getChildren()) {
                                    String idItem = it.getKey();
                                    String itemBasicInfo = Objects.requireNonNull(it.getValue()).toString();
                                    items.get(index).put(idItem, new ArrayList<>(Arrays.asList(itemBasicInfo.split(",", Item.INFO_LENGTH))));
                                }
                            } else {
                                if (field.equals(Exchange.DATE_DB)) {
                                    date = child.getValue().toString();
                                } else map.put(child.getKey(), Objects.requireNonNull(child.getValue()).toString());
                            }
                        }
                    }

                    User applicant = User.createUserFromBasicInfo((map.get(Exchange.APPLICANT_DB)), null);
                    User proposer = User.createUserFromBasicInfo((map.get(Exchange.PROPOSER_DB)), null);
                    ArrayList<Item> proposerItems = new ArrayList<>();
                    ArrayList<Item> applicantItems = new ArrayList<>();
                    ArrayList<ArrayList<String>> usersBasicInfo = new ArrayList<>();
                    usersBasicInfo.add(new ArrayList<>(Arrays.asList(proposer.getIdUser(), proposer.getImage(), proposer.getRank().toString(), proposer.getUsername())));
                    usersBasicInfo.add(new ArrayList<>(Arrays.asList(applicant.getIdUser(), applicant.getImage(), applicant.getRank().toString(), applicant.getUsername())));

                    for (int i = 0; i < 2; ++i) {
                        //if i = 0: proposer, i = 1: applicant
                        for (String idItem : items.get(i).keySet()) {
                            ArrayList<String> itemData = items.get(i).get(idItem);
                            if (itemData != null) {
                                //data for each item
                                //"category,range,image,is_charity,is_exchangeable,is_service,country,region,province,city,title"
                                ArrayList<String> location = new ArrayList<>(Arrays.asList(itemData.get(6), itemData.get(7), itemData.get(8), itemData.get(9)));
                                ArrayList<String> categories = new ArrayList<>(Collections.singletonList(itemData.get(0)));
                                ArrayList<String> images = new ArrayList<>(Collections.singletonList(itemData.get(2)));
                                Item newItem = new Item(idItem, itemData.get(10), "", itemData.get(1), usersBasicInfo.get(i), location, Boolean.parseBoolean(itemData.get(3)), Boolean.parseBoolean(itemData.get(4)), Boolean.parseBoolean(itemData.get(5)), categories, images);
                                if (i == 0) proposerItems.add(newItem);
                                else applicantItems.add(newItem);
                            }
                        }
                    }
                    consumer.accept(new Exchange(map.get(Exchange.ID_EXCHANGE_DB), date, Exchange.exchangeStatusValueOf(map.get(Exchange.EXCHANGE_STAUS_DB)), applicant, proposer, applicantItems, proposerItems));

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(contextTag, "load:onCancelled", error.toException());
            }
        });
    }

    private static boolean argSanityCheck(User applicant, User proposer, ArrayList<Item> applicantItems, ArrayList<Item> proposerItems) {
        //TODO:
        //everything not null
        //applicant different from proposer
        //proposed items not empty and all with same owner
        //applicant items empty iff all proposed items for charity and all with same owner
        return true;
    }

    public static void insertExchangeInDatabase(String contextTag, String firstProposerItemId, String firstApplicantItemId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Item.retrieveItemsByIds(contextTag, dbRef, new ArrayList<>(Arrays.asList(firstProposerItemId, firstApplicantItemId)), new Consumer<ArrayList<Item>>() {
            @Override
            public void accept(ArrayList<Item> items) {
                User proposer = User.createUserFromBasicInfo("", new ArrayList<String>(items.get(0).getOwner()));
                User applicant = User.createUserFromBasicInfo("", new ArrayList<String>(items.get(1).getOwner()));
                ArrayList<Item> applicantItems = new ArrayList<>();
                ArrayList<Item> proposerItems = new ArrayList<>();
                proposerItems.add(items.get(0));
                applicantItems.add(items.get(1));
                insertExchangeInDatabaseAux(applicant, proposer, applicantItems,proposerItems);
            }
        });
    }

    private static void insertExchangeInDatabaseAux(User applicant, User proposer, ArrayList<Item> applicantItems, ArrayList<Item> proposerItems) {
        if (argSanityCheck(applicant, proposer, applicantItems, proposerItems)) {
            String date = (new Date()).toString();
            String idExchange = UUID.randomUUID().toString();
            setIdExchangeDb(idExchange);
            setDateDb(idExchange, date);
            setExchangeStatusDb(idExchange, ExchangeStatus.IN_APPROVAL);
            setApplicantDb(idExchange, applicant);
            setProposerDb(idExchange, proposer);
            setApplicantItemsDb(idExchange, applicantItems);
            setProposerItemsDb(idExchange, proposerItems);
            //insert in applicant's list of exchanges
            User.dbRefUsers.child(applicant.getIdUser()).child(User.ID_EXCHANGES_DB).child(idExchange).setValue(Exchange.getExchangeBasicInfo(idExchange,date,ExchangeStatus.IN_APPROVAL,applicant,proposer,applicantItems,proposerItems));
            //insert in proposer's list of exchanges
            User.dbRefUsers.child(proposer.getIdUser()).child(User.ID_EXCHANGES_DB).child(idExchange).setValue(Exchange.getExchangeBasicInfo(idExchange, date,ExchangeStatus.IN_APPROVAL,applicant,proposer,applicantItems,proposerItems));
        }
    }
    /**
     *
     * @param exchange the Exchange where to change the status
     * @param newExchangeStatus the new exchange status of the specified Exchange
     */
    public static void changeExchangeStatusDb(Exchange exchange, ExchangeStatus newExchangeStatus) {
        //TODO: add controls on legal statuses transitions
        setExchangeStatusDb(exchange.getIdExchange(), newExchangeStatus);
        //TODO:
        //for each item involved in the exchange:
        // set not exchangeable if transition is: in approval->accepted
        //set exchangeable if transition is: in approval->refused, or anystatus(except for happened and all the reviewed)->annulled
        //delete the exchange (from exchanges and from the user's list of exchanges), and all the involved items (use remove item from board)
        //if newExchangeStatus is: reviewed by both
    }
    private static void setExchangeStatusDb(String idExchange, ExchangeStatus exchangeStatus) {
        Exchange.dbRef.child(idExchange).child(Exchange.EXCHANGE_STAUS_DB).setValue(Exchange.StringValueOfExchangeStatus(exchangeStatus));
    }
    private static void setDateDb(String idExchange, String date) {
        Exchange.dbRef.child(idExchange).child(Exchange.DATE_DB).setValue(date);
    }
    private static void setUserDb(String idExchange, User user, String dataBaseChild) {
        Exchange.dbRef.child(idExchange).child(dataBaseChild).setValue(user.getUserBasicInfo());
    }

    private static void setApplicantDb(String idExchange, User applicant) {
        setUserDb(idExchange, applicant, Exchange.APPLICANT_DB);
    }

    private static void setProposerDb(String idExchange, User proposer) {
        setUserDb(idExchange, proposer, Exchange.PROPOSER_DB);
    }

    private static void setItemsDb(String idExchange, ArrayList<Item> items, String dataBaseChild) {
        for (Item item: items) {
            Exchange.dbRef.child(idExchange).child(dataBaseChild).child(item.getIdItem()).setValue(item.getItemBasicInfo());
        }
    }

    private static void setApplicantItemsDb(String idExchange, ArrayList<Item> applicantItems) {
        setItemsDb(idExchange, applicantItems, Exchange.APPLICANT_ITEMS_DB);
    }

    private static void setProposerItemsDb(String idExchange, ArrayList<Item> proposerItems) {
        setItemsDb(idExchange, proposerItems, Exchange.PROPOSER_ITEMS_DB);
    }

    private static void setIdExchangeDb(String idExchange) {
        Exchange.dbRef.child(idExchange).child(Exchange.ID_EXCHANGE_DB).setValue(idExchange);
    }

    /**
     *
     * @return a string in CSV format representing the basic info of this Exchange
     * @see Exchange#INFO_PARAM
     */
    public static String getExchangeBasicInfo(String idExchange, String date, ExchangeStatus exchangeStatus, User applicant, User proposer, ArrayList<Item> applicantItems, ArrayList<Item> proposerItems) {
        String applicantFirstItem = "";
        if (!applicantItems.isEmpty()) applicantFirstItem = (applicantItems.get(0).getIdItem());
        return idExchange + "," + StringValueOfExchangeStatus(exchangeStatus) + "," + date + "," + applicant.getIdUser() + "," + proposer.getIdUser() + "," + applicantFirstItem + "," + proposerItems.get(0).getIdItem();
    }
    /**
     *
     * @return the current status of this Exchange
     * @see ExchangeStatus
     */
    public ExchangeStatus getExchangeStatus() {
        return this.exchangeStatus;
    }

    /**
     *
     * @return the User who choose an Item from a proposer's board and, therefore, created this Exchange
     */
    public User getApplicant() {
        return this.applicant;
    }

    /**
     *
     * @return the User whose Item has been chosen from their board by an applicant for this Exchange
     */
    public User getProposer() {
        return this.proposer;
    }

    /**
     *
     * @return the id of this Exchange
     */
    public String getIdExchange() {
        return this.idExchange;
    }

    /**
     *
     * @return all the Items which have been proposed by the applicant for this Exchange
     * It can be empty, since the required Items could be for charity
     */
    public ArrayList<Item> getProposerItems() {
        return this.proposerItems;
    }

    /**
     *
     * @return all the Items which have been choose by the applicant from the proposer's board for this Exchange
     */
    public ArrayList<Item> getApplicantItems() {
        return this.applicantItems;
    }

    public String getDate() {return this.date;}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null  || !(o instanceof Exchange)) return false;
        Exchange exchange = (Exchange) o;
        return exchangeStatus == exchange.getExchangeStatus() && applicant.equals(exchange.getApplicant()) && proposer.equals(exchange.getProposer()) && idExchange.equals(exchange.getIdExchange()) && proposerItems.equals(exchange.getProposerItems()) && applicantItems.equals(exchange.getApplicantItems());
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.idExchange.hashCode();
        result = prime * result + this.applicant.hashCode();
        result = prime * result + this.exchangeStatus.hashCode();
        result = prime * result + this.proposerItems.hashCode();
        result = prime * result + this.applicantItems.hashCode();
        result = prime * result + this.proposer.hashCode();
        return result;
    }

    //TODO: fetch, insert and update  to database, (with all the related side-effects)




    /* TODO da rivedere completamente
    /**
     * to be called before inserting a new Exchange in the DB
     * The proposed Item, together with the otherProposedItems,
     * can be Null only if all the required Items(required Item and otherRequiredItems),
     * are for charity. <p>
     * It checks whether the applicant and the proposer are the same User and if the ItemsNonNull String idRequiredItem, @Nullable Set<String> idOtherProposedItems, @Nullable Set<String> idOtherRequiredItems)  {
        this.idApplicant = idApplicant;
     * involved in the exchange belong to the correct User. <p>
     * otherProposedItems and otherRequiredItems are optional, since usually an exchange involves just one or two Items. <p>
     * if all the conditions are guaranteed, a New Exchange will be created and its status will be set to "IN_APPROVAL"
     * @param idApplicant the User who wants the proposed Object (they create the exchange)
     * @param idProposer the User whose Item has been chosen for this Exchange.
     * @param proposedItem the first or the only Item the applicant offers in exchange for the Item they choose from the proposer's board
     * @param requiredItem the first or the only Item which has been chosen for the exchange, from the proposer's board, by the applicant
     * @param otherProposedItems optional Set of Items the applicant offers in exchange for the Item/s they choose from the proposer's board
     * @param otherRequiredItems optional Set of Items which have been chosen for the exchange, from the proposer's board, by the applicant
     * @throws Exception if one of the checked conditions is not guaranteed
     * @return a new Exchange to be inserted in the DB
     * @see ExchangeStatus
     * @see Item#isCharity()
     */
    /*
    private Exchange createExchange(@NonNull String idApplicant, @NonNull String idProposer, @Nullable Item proposedItem, @NonNull Item requiredItem, @Nullable Set<Item> otherProposedItems, @Nullable Set<Item> otherRequiredItems) throws Exception {
        String feedback = "this Exchange has some problems";
        String idProposedItem = proposedItem == null? null: proposedItem.getIdItem();
        Set<String> idOtherProposedItems = null;
        Set<String> idOtherRequiredItems = null;
        boolean go = true;
        if (idApplicant.equals(idProposer)) {
            feedback += ", the applicant is the same as the proposer";
            go = false;
        }
        if (otherRequiredItems != null) {
            idOtherRequiredItems = new HashSet<>();
            for (Item i : otherRequiredItems) {
                if (proposedItem == null && (otherProposedItems == null || otherProposedItems.isEmpty())) {
                    if ((!i.isCharity()) || (!requiredItem.isCharity())) {
                        feedback += ", the Item or the items which has been chosen from the proposer's board are not all for charity, but the applicant offered no Item in Exchange";
                        go = false;
                        idOtherRequiredItems = null;
                        break;
                    }
                }
                if (!(i.getOwner().equals(idProposer)) || !(requiredItem.getOwner().equals(idProposer))) {
                    feedback += ", the owner of some of the required items is not the proposer";
                    go = false;
                    idOtherRequiredItems = null;
                    break;
                }
                if (!(i.isExchangeable()) || !(requiredItem.isExchangeable())) {
                    feedback += ", some of the required Items are not exchangeable";
                    go = false;
                    idOtherRequiredItems = null;
                    break;
                }
                idOtherRequiredItems.add(i.getIdItem());
            }
        }
        if (otherProposedItems != null) {
            idOtherProposedItems = new HashSet<>();
            for (Item i: otherProposedItems) {
                if (!(i.getOwner().equals(applicant)) || (proposedItem != null && !(proposedItem.getOwner().equals(applicant)))) {
                    feedback += ", the owner of some of the proposed items is not the applicant";
                    go = false;
                    idOtherProposedItems = null;
                    break;
                }
                if (!(i.isExchangeable()) || (proposedItem != null && !(proposedItem.isExchangeable()))) {
                    feedback += ", some of the Items the applicant is proposing are not exchangeable";
                    go = false;
                    idOtherProposedItems = null;
                    break;
                }
                idOtherProposedItems.add(i.getIdItem());
            }
        }

        if (go) return new Exchange(ExchangeStatus.IN_APPROVAL, applicant.getIdUser(), proposer.getIdUser(), idProposedItem, requiredItem.getIdItem(), idOtherProposedItems, idOtherRequiredItems);
        else throw new Exception(feedback);
    }*/
}
