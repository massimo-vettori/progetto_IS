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

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * this class represents an exchange of Items between two Users.
 * @author pares, jack, gradiente
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

    // TODO: Replace this enum and its methods with the one in ExchangeState.java
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

    /**
     * the exchanges main node name in the database
     */
    private static final String CLASS_EXCHANGE_DB = "exchanges";
    /**
     * the exchange applicant node name in the database (i.e. the person who chose and requires someone else's service/object for an exchange)
     */
    private static final String APPLICANT_DB = "applicant";
    /**
     * the exchange proposer node name in the database (i.e. the person whose object/service is chosen by someone else for an exchange)
     */
    private static final String PROPOSER_DB = "proposer";
    /**
     * the exchange id node name in the database
     */
    private static final String ID_EXCHANGE_DB = "id_exchange";
    /**
     * the exchange proposer list of items node name in the database
     */
    private static final String PROPOSER_ITEMS_DB = "proposer_items";
    /**
     * the exchange applicant list of items node name in the database
     */
    private static final String APPLICANT_ITEMS_DB = "applicant_items";
    /**
     * the exchange status node name in the database
     */
    private static final String EXCHANGE_STATUS_DB = "exchange_status";
    /**
     * the exchange date node name in the database
     */
    private static final String DATE_DB = "date";

    /**
     * the length of the String in CSV fromat representing an exchange when saved in a nested way inside a different node in the databse
     */
    static final int EXCHANGE_INFO_LENGTH = 7;

    private static final String INFO_PARAM = "id_exchange,exchange_status,date,id_applicant,id_proposer,first_applicant_item,first_proposer_item";

    private ExchangeStatus exchangeStatus;
    private final String date;
    private final String idApplicant;
    private final User applicant;
    private final String idProposer;
    private final User proposer;
    private final String idExchange; //= UUID.randomUUID().toString();
    private final ArrayList<Item> proposerItems = new ArrayList<>();
    private final ArrayList<Item> applicantItems = new ArrayList<>();

    /**
     * a reference to the exchanges main node in the database
     */
    static final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(Exchange.CLASS_EXCHANGE_DB);

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

    /**
     * convert the CSV string which represents an exchange when saved in a nested way inside the database in an Exchange instance
     * which is accepted and provided by the consumer, ready to be used.
     * @param exchangeBasicInfo the CSV string representing the exchange
     * @param consumer the consumer which accepts and provides the Exchange instance
     */
    public static void getExchangeFromBasicInfo(String exchangeBasicInfo, Consumer<Exchange> consumer) {
        //StringValueOfExchangeStatus(exchangeStatus) + "," + date + "," + applicant.getIdUser() + "," + proposer.getIdUser() + "," + applicantFirstItem + "," + proposerItems.get(0).getIdItem();
        ArrayList<String> values = new ArrayList<>(Arrays.asList(exchangeBasicInfo.split(",", Exchange.EXCHANGE_INFO_LENGTH)));
        Item.retrieveItemsByIds("Exchange", FirebaseDatabase.getInstance().getReference(), new ArrayList<>(Arrays.asList(values.get(5), values.get(6))), new Consumer<ArrayList<Item>>() {
            @Override
            public void accept(ArrayList<Item> items) {
                Item proposer_item  = items.get(1);
                Item applicant_item = items.get(0);

                User proposer = new User(
                        proposer_item.getOwnerId(),
                        proposer_item.getOwnerUsername(),
                        "",
                        "",
                        proposer_item.getLocation(),
                        proposer_item.getOwnerRank(),
                        BarattopoliUtil.encodeImageToBase64(proposer_item.getOwnerImage())
                );

                User applicant;

                if (applicant_item == null) {
                    applicant_item = Item.getEmptyItem();
                    applicant = User.getEmptyUser();
                } else {
                    applicant = new User(
                            applicant_item.getOwnerId(),
                            applicant_item.getOwnerUsername(),
                            "",
                            "",
                            applicant_item.getLocation(),
                            applicant_item.getOwnerRank(),
                            BarattopoliUtil.encodeImageToBase64(applicant_item.getOwnerImage())
                    );
                }

                consumer.accept(new Exchange(values.get(0), values.get(2), Exchange.exchangeStatusValueOf(values.get(1)), applicant, proposer, new ArrayList<>(Collections.singletonList(applicant_item)), new ArrayList<>(Collections.singletonList(proposer_item))));
            }
        });
    }

    /**
     * the exchanges of a user saved and ready to be used only inside the consumer
     * @param idUser the user whose exchanges are being fetched
     * @param applicant if the user is the person who requested and therefore created the exchange
     * @param both if there is no need to discriminate between the exchanges when displying them
     * @param consumer accepts and provides the exchanges
     */
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
                                if (both) {
                                    consumer.accept(exchange);
                                } else {
                                    if (applicant) {
                                        if ((exchange.getApplicant().getIdUser()).equals(idUser)) consumer.accept(exchange);
                                    } else {
                                        if ((exchange.getProposer().getIdUser()).equals(idUser)) consumer.accept(exchange);
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
     * retrieve all the exchanges saved in the database at the main exchange node location
     * @param consumer accepts and provides the exchanges
     */
    public static void retrieveAllExchanges(Consumer<Exchange> consumer) {
        FirebaseDatabase.getInstance().getReference().child(Exchange.CLASS_EXCHANGE_DB).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (DataSnapshot exch: snapshot.getChildren()) {
                        Exchange.retrieveExchangeById("Exchange", exch.getKey(), new Consumer<Exchange>() {
                            @Override
                            public void accept(Exchange exchange) {
                                consumer.accept(exchange);
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
     * deletion of an exchange when it is still unapproved and contains an item which is involved in an another exchange
     * which has been approved
     * @param approved the approved exchange
     */
    public static void deleteUnapprovedExchanges(@NonNull Exchange approved) {
        String exchangeId         = approved.getIdExchange();
        ArrayList<String> itemIds = new ArrayList<>();

        for (Item item: approved.getApplicantItems()) {
            itemIds.add(item.getIdItem());
        }

        for (Item item: approved.getProposerItems()) {
            itemIds.add(item.getIdItem());
        }

        retrieveAllExchanges(new Consumer<Exchange>() {
            @Override
            public void accept(Exchange exchange) {
                if (exchange.exchangeStatus == ExchangeStatus.IN_APPROVAL && !exchange.getIdExchange().equals(exchangeId)) {
                    boolean toDelete = true;

                    for (Item item: exchange.getApplicantItems()) {
                        if (!itemIds.contains(item.getIdItem())) {
                            toDelete = false;
                            break;
                        }
                    }

                    for (Item item: exchange.getProposerItems()) {
                        if (!itemIds.contains(item.getIdItem())) {
                            toDelete = false;
                            break;
                        }
                    }

                    if (toDelete) Exchange.deleteExchange(exchange);
                }
            }
        });
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
                    consumer.accept(new Exchange(map.get(Exchange.ID_EXCHANGE_DB), date, Exchange.exchangeStatusValueOf(map.get(Exchange.EXCHANGE_STATUS_DB)), applicant, proposer, applicantItems, proposerItems));

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

    /**
     * insert a new exchange in the database
     * @param contextTag the string representing the activity where this method is being called from
     * @param firstProposerItemId the first item proposed in the exchange by the proposer
     * @param firstApplicantItemId the first item requested by the applicant in the exchange
     * @see Exchange#PROPOSER_DB
     * @see Exchange#APPLICANT_DB
     */
    public static void insertExchangeInDatabase(String contextTag, String firstProposerItemId, String firstApplicantItemId) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        ArrayList<String> itemsIds = new ArrayList<>();
        itemsIds.add(firstProposerItemId);
        if (firstApplicantItemId != null) itemsIds.add(firstApplicantItemId);

        Item.retrieveItemsByIds(contextTag, dbRef, new ArrayList<>(itemsIds), new Consumer<ArrayList<Item>>() {
            @Override
            public void accept(ArrayList<Item> items) {
                if (firstApplicantItemId != null) {
                    User proposer = User.createUserFromBasicInfo("", new ArrayList<String>(items.get(0).getOwner()));
                    User applicant = User.createUserFromBasicInfo("", new ArrayList<String>(items.get(1).getOwner()));
                    ArrayList<Item> applicantItems = new ArrayList<>();
                    ArrayList<Item> proposerItems = new ArrayList<>();
                    proposerItems.add(items.get(0));
                    applicantItems.add(items.get(1));
                    insertExchangeInDatabaseAux(applicant, proposer, applicantItems,proposerItems);
                } else {
                    User proposer = User.createUserFromBasicInfo("", new ArrayList<String>(items.get(0).getOwner()));
                    ArrayList<Item> proposerItems = new ArrayList<>();
                    proposerItems.add(items.get(0));
                    User.retrieveCurrentUser(contextTag, dbRef, new Consumer<User>() {
                        @Override
                        public void accept(User applicant) {
                            insertExchangeInDatabaseAux(applicant, proposer, new ArrayList<>(), proposerItems);
                        }
                    });
                }
            }
        });
    }

    private static void insertExchangeInDatabaseAux(User applicant, User proposer, ArrayList<Item> applicantItems, ArrayList<Item> proposerItems) {
        if (argSanityCheck(applicant, proposer, applicantItems, proposerItems)) {
            String date = (new Date()).toString();
            String idExchange = UUID.randomUUID().toString();
            setIdExchangeDb(idExchange);
            setDateDb(idExchange, date);
            setExchangeStatusDb(idExchange, null,  ExchangeStatus.IN_APPROVAL);
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

    private boolean legalExchangeTransition(ExchangeStatus currExchangeStatus, ExchangeStatus nextExchangeStatus) {
        boolean res = true;
        String curr = Exchange.StringValueOfExchangeStatus(currExchangeStatus);
        String next = Exchange.StringValueOfExchangeStatus(nextExchangeStatus);
        //terminal states
        if (curr.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.REFUSED)) ||
                curr.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.ANNULLED)) ||
                curr.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.ILLEGAL))) {
            res = false;
        }
        if (curr.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.IN_APPROVAL))) {
            if (!(next.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.ACCEPTED)) ||
                    next.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.REFUSED)))) {
                res = false;
            }
        }
        if (curr.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.ACCEPTED))) {
            //confirmation of acceptance
            if (!(next.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.CLOSED)) ||
                    //object has been destroyed or ruined
                next.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.ANNULLED)))) {
                res = false;
            }
        }
        if (curr.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.CLOSED))) {
            //the exchange has been made
            if (!(next.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.HAPPENED)))) {
                res = false;
            }
        }
        if (curr.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.HAPPENED))) {
            //ready for review
            if (!(next.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.REVIEWED_BY_APPLICANT)) ||
                    next.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.REVIEWED_BY_PROPOSER)))) {
                res = false;
            }
        }
        if (next.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.REVIEWED_BY_BOTH))) {
            if (!(curr.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.REVIEWED_BY_APPLICANT)) ||
                    curr.equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.REVIEWED_BY_PROPOSER)))) {
                res = false;
            }
        }
        return res;
    }
    /**
     * change the status of the exchange in the database, after checking it is legal
     * @param exchange the Exchange where to change the status
     * @param nextExchangeStatus the new exchange status of the specified Exchange
     */
    public static void changeExchangeStatusDb(Exchange exchange, ExchangeStatus nextExchangeStatus) {
        ExchangeStatus currExchangeStatus = exchange.getExchangeStatus();
        if (exchange.legalExchangeTransition(currExchangeStatus, nextExchangeStatus)) {
            setExchangeStatusDb(exchange.getIdExchange(), exchange, nextExchangeStatus);
            if (Exchange.StringValueOfExchangeStatus(nextExchangeStatus).equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.ACCEPTED))) {
               //change from exchangeable to not exchangeable
                for (Item item: exchange.getApplicantItems()) {
                    Item.setExchangeable(false, FirebaseDatabase.getInstance().getReference().child(Item.CLASS_ITEM_DB).child(item.getIdItem()));
                }
                for (Item item: exchange.getProposerItems()) {
                    Item.setExchangeable(false, FirebaseDatabase.getInstance().getReference().child(Item.CLASS_ITEM_DB).child(item.getIdItem()));
                }
                Exchange.deleteUnapprovedExchanges(exchange);
            }
            if (Exchange.StringValueOfExchangeStatus(nextExchangeStatus).equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.REFUSED)) ||
                    Exchange.StringValueOfExchangeStatus(nextExchangeStatus).equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.ANNULLED)) ||
                    Exchange.StringValueOfExchangeStatus(nextExchangeStatus).equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.ILLEGAL))) {
                //delete exchange
                Exchange.deleteExchange(exchange);
                //change from not exchangeable to exchangeable
                for (Item item: exchange.getApplicantItems()) {
                    Item.setExchangeable(true, FirebaseDatabase.getInstance().getReference().child(Item.CLASS_ITEM_DB).child(item.getIdItem()));
                }
                for (Item item: exchange.getProposerItems()) {
                    Item.setExchangeable(true, FirebaseDatabase.getInstance().getReference().child(Item.CLASS_ITEM_DB).child(item.getIdItem()));
                }
            }
            if (Exchange.StringValueOfExchangeStatus(nextExchangeStatus).equals(Exchange.StringValueOfExchangeStatus(ExchangeStatus.REVIEWED_BY_BOTH))) {
                //delete exchange, delete items
                Exchange.deleteExchange(exchange);
                for (Item item: exchange.getApplicantItems()) {
                    User.removeItemFromBoard("Exchange", item.getOwnerId(), item.getIdItem());
                }
                for (Item item: exchange.getProposerItems()) {
                    User.removeItemFromBoard("Exchange", item.getOwnerId(), item.getIdItem());
                }
            }


            //for each item involved in the exchange:
            // set not exchangeable if transition is: in approval->accepted
            //set exchangeable if transition is: in approval->refused, or anystatus(except for happened and all the reviewed)->annulled
            //delete the exchange (from exchanges and from the user's list of exchanges), and all the involved items (use remove item from board)
            //if newExchangeStatus is: reviewed by both
        }
    }

    /**
     * deletion from the database of an exchange
     * delete the exchange from the involved users' boards as well
     * @param exchange the exchange to be deleted
     */
    public static void deleteExchange(Exchange exchange) {
        Exchange.retrieveExchangeById("Exchange", exchange.getIdExchange(), new Consumer<Exchange>() {
            @Override
            public void accept(Exchange exchange1) {
                Log.d("Exchange",exchange1 != null? "exchange exists": "exchange does not exist");
                if (exchange1 != null) {
                    //remove exchange from Users' Board
                    User.dbRefUsers.child(exchange1.getApplicant().getIdUser()).child(User.ID_EXCHANGES_DB).child(exchange1.getIdExchange()).removeValue();
                    User.dbRefUsers.child(exchange1.getProposer().getIdUser()).child(User.ID_EXCHANGES_DB).child(exchange1.getIdExchange()).removeValue();
                    //remove exchange from exchanges
                    (Exchange.dbRef).child(exchange1.getIdExchange()).removeValue();
                } else Log.d("Exchange", "Exchange " + exchange1.getIdExchange() + " does not exist anymore" );
            }});
    }
    private static void setExchangeStatusDb(String idExchange, @Nullable  Exchange exchange, ExchangeStatus exchangeStatus) {
        Exchange.dbRef.child(idExchange).child(Exchange.EXCHANGE_STATUS_DB).setValue(Exchange.StringValueOfExchangeStatus(exchangeStatus));
        if (exchange != null) Exchange.exchangeStatusInBasicInfo(exchange, exchangeStatus);
    }
    private static void exchangeStatusInBasicInfo(Exchange exchange, ExchangeStatus exchangeStatus) {
        exchange.exchangeStatus = exchangeStatus;
        String basicInfo = Exchange.getExchangeBasicInfo(exchange.getIdExchange(), exchange.getDate(), exchangeStatus, exchange.getApplicant(), exchange.getProposer(), exchange.getApplicantItems(), exchange.getProposerItems());
        User.dbRefUsers.child(exchange.getApplicant().getIdUser()).child(Exchange.CLASS_EXCHANGE_DB).child(exchange.getIdExchange()).setValue(basicInfo);
        User.dbRefUsers.child(exchange.getProposer().getIdUser()).child(Exchange.CLASS_EXCHANGE_DB).child(exchange.getIdExchange()).setValue(basicInfo);
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
}
