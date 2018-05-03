package com.w3engineers.rmunity.server;

/**
 * Created by USER22 on 4/24/2018.
 */

public interface DataListener {
    void onDataReceived(String id, String data);
    void onDataPrepare(String msg);
}
