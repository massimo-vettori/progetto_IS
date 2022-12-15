package com.lacliquep.barattopoli.classes;

/**
 * this interface is used to wait the callBack from an async methos
 */
public interface AsyncWaiter {
    /**
     *
     * @param val a string with the fetched result from an async method.
     */
    void onCallBack(String val);
}
