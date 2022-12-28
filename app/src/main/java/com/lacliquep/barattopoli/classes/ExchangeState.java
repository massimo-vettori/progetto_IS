package com.lacliquep.barattopoli.classes;

import androidx.annotation.NonNull;

public enum ExchangeState {
    IN_APPROVAL("in_approval"),
    ACCEPTED("accepted"),
    ANNULLED("annulled"),
    CLOSED("closed"),
    HAPPENED("happened"),
    ILLEGAL("illegal"),
    REFUSED("refused"),

    REVIEWED_BY_APPLICANT("REVIEWED_BY_APPLICANT"),
    REVIEWED_BY_PROPOSER("REVIEWED_BY_PROPOSER"),
    REVIEWED_BY_BOTH("REVIEWED_BY_BOTH"),

    OTHER("Other");

    private final String type;

    ExchangeState(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return type;
    }

    public static ExchangeState from(String value) {
        for (ExchangeState o : ExchangeState.values()) {
            if (o.type.equals(value)) return o;
        }
        return null;
    }
}
