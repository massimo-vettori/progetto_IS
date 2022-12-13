package com.lacliquep.barattopoli.classes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.*;

/**
 * this class represents an exchange of Items between two Users.
 *
 * @author pares
 * @since 1.0
 */
public class Exchange {

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

    public static final String ID_APPLICANT_DB = "id_applicant";
    public static final String ID_PROPOSER_DB = "id_proposer";
    public final String ID_EXCHANGE_DB = "id_exchange";
    public static final String ID_PROPOSED_ITEMS_DB = "id_proposed_items";
    public static final String ID_REQUIRED_ITEMS_DB = "id_required_items";
    public static final String CLASS_EXCHANGE_DB = "exchanges";
    public static final int EXCHANGE_INFO_LENGTH = 5;

    /* TODO: da rivedere completamente tutta la classe
    private ExchangeStatus exchangeStatus;
    private final String idApplicant;
    private User applicant;
    private final String idProposer;
    private User proposer;

    private final String idExchange = UUID.randomUUID().toString();

    private final Set<String> idProposedItems = new HashSet<>();
    private final Set<Item> proposedItems = new HashSet<>();

    private final Set<String> idRequiredItems = new HashSet<>();
    private final Set<Item> requiredItems = new HashSet<>();
    */
    /*
    /**
     * constructor of an exchange (to be called to recreate the existing Exchanges of a User in the DB in DataBaseable.retrieveExchange)
     * @param exchangeStatus the status of the exchange
     * @param idApplicant the id of the User who wants the proposed Object (they create the exchange)
     * @param idProposer the id of the User whose Item has been chosen for this Exchange.
     * @param idProposedItem the id of the the first or the only Item the applicant offers in exchange for the Item they choose from the proposer's board
     * @param idRequiredItem the id of the the first or the only Item which has been chosen for the exchange, from the proposer's board, by the applicant
     * @param idOtherProposedItems optional Set of id of the Items the applicant offers in exchange for the Item/s they choose from the proposer's board
     * @param idOtherRequiredItems optional Set of id of the Items which have been chosen for the exchange, from the proposer's board, by the applicant
     * @see ExchangeStatus
     */
    /*
    private Exchange(ExchangeStatus exchangeStatus, @NonNull String idApplicant, @NonNull String idProposer, @Nullable String idProposedItem, @NonNull String idRequiredItem, @Nullable Set<String> idOtherProposedItems, @Nullable Set<String> idOtherRequiredItems)  {
        this.idApplicant = idApplicant;
        this.idProposer = idProposer;
        if (idProposedItem != null) this.idProposedItems.add(idProposedItem);
        if (idOtherProposedItems != null) this.idProposedItems.addAll(idOtherProposedItems);
        if (idOtherRequiredItems!= null) this.idRequiredItems.addAll(idOtherRequiredItems);
        this.exchangeStatus = exchangeStatus;
    }*/

    /* TODO da rivedere completamente
    /**
     * to be called before inserting a new Exchange in the DB
     * The proposed Item, together with the otherProposedItems,
     * can be Null only if all the required Items(required Item and otherRequiredItems),
     * are for charity. <p>
     * It checks whether the applicant and the proposer are the same User and if the Items
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

    /**
     *
     * @return the current status of this Exchange
     * @see ExchangeStatus
     */
    /*public ExchangeStatus getExchangeStatus() {
        return this.exchangeStatus;
    }*/

    /**
     *
     * @return the User who choose an Item from a proposer's board and, therefore, created this Exchange
     */
    /*public User getApplicant() {
        return this.applicant = BarattopolyUtil.retrieveUserById(this.idApplicant);
    }*/

    /**
     *
     * @return the User whose Item has been chosen from their board by an applicant for this Exchange
     */
    /*public User getProposer() {
        return this.proposer = BarattopolyUtil.retrieveUserById(this.idProposer);
    }

    /**
     *
     * @return the id of this Exchange
     */
    /*public String getIdExchange() {
        return this.idExchange;
    }*/

    /**
     *
     * @return all the Items which have been proposed by the applicant for this Exchange
     * It can be empty, since the required Items could be for charity
     */
    /*public Set<Item> getProposedItems() {
        return this.proposedItems;
    }*/

    /**
     *
     * @return all the Items which have been choose by the applicant from the proposer's board for this Exchange
     */
    /*public Set<Item> getRequiredItems() {
        return this.requiredItems;
    }*/

    /*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null  || !(o instanceof Exchange)) return false;
        Exchange exchange = (Exchange) o;
        return getExchangeStatus() == exchange.getExchangeStatus() && getApplicant().equals(exchange.getApplicant()) && getProposer().equals(exchange.getProposer()) && getIdExchange().equals(exchange.getIdExchange()) && getProposedItems().equals(exchange.getProposedItems()) && getRequiredItems().equals(exchange.getRequiredItems());
    }*/

    /*
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.idExchange.hashCode();
        result = prime * result + this.applicant.hashCode();
        result = prime * result + this.exchangeStatus.hashCode();
        result = prime * result + this.proposedItems.hashCode();
        result = prime * result + this.requiredItems.hashCode();
        result = prime * result + this.proposer.hashCode();
        return result;
    }*/

    //TODO: fetch, insert and update  to database, (with all the related side-effects)
}
