package com.tidbits.firebase;

/**
 * this is the interface for the callback on completion of data, crucial
 * for async behaviour of the functions from the FirebaseHelper class
 */
public interface onDataReceivedCallback {
    void onDataReceived(Object obj);
}
