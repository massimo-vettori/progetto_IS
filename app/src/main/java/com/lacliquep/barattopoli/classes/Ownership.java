package com.lacliquep.barattopoli.classes;

import androidx.annotation.NonNull;

/**
 * this class represents the difference between users when showing items
 * @author pares, jack, gradiente
 * @since 1.0
 */
public enum Ownership {
    PERSONAL("Personal"),
    OTHER("Other");

    private final String type;

    Ownership(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return type;
    }

    public static Ownership from(String value) {
        for (Ownership o : Ownership.values()) {
            if (o.type.equals(value)) return o;
        }
        return null;
    }
}
