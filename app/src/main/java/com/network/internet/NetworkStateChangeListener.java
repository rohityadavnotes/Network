package com.network.internet;

public interface NetworkStateChangeListener {
    /**
     * When the connection state is changed and there is a connection, this method is called
     */
    void networkAvailable();

    /**
     * Connection state is changed and there is not a connection, this method is called
     */
    void networkUnavailable();
}
