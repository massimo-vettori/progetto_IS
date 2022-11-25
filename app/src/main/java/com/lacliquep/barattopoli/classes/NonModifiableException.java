package com.lacliquep.barattopoli.classes;

public class NonModifiableException extends Exception {
    public NonModifiableException() {
        super("this Item is not exchangeable, therefore it cannot be modified");
    }
}
