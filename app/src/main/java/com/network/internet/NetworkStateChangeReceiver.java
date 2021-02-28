package com.network.internet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import com.network.util.LogcatUtil;

public class NetworkStateChangeReceiver extends BroadcastReceiver {

    private static final String TAG = NetworkStateChangeReceiver.class.getSimpleName();
    public static NetworkStateChangeListener networkStateChangeListener;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        final String action = intent.getAction();

        if (action != null) {
            if (networkStateChangeListener != null) {

                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    if (NetworkConnectivityUtil.isConnectedAll(context))
                    {
                        LogcatUtil.errorMessage(TAG,"ConnectivityManager.CONNECTIVITY_ACTION - 1");
                        networkStateChangeListener.networkAvailable();
                    }
                    else
                    {
                        LogcatUtil.errorMessage(TAG,"ConnectivityManager.CONNECTIVITY_ACTION - 2");
                        networkStateChangeListener.networkUnavailable();
                    }
                }

                if (!intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false))
                {
                    LogcatUtil.errorMessage(TAG,"RegisterAndUnregisterNetworkReceiver.NETWORK_AVAILABILITY_ACTION - 1");
                    networkStateChangeListener.networkAvailable();
                }
                else
                {
                    LogcatUtil.errorMessage(TAG,"RegisterAndUnregisterNetworkReceiver.NETWORK_AVAILABILITY_ACTION - 2");
                    networkStateChangeListener.networkUnavailable();
                }
            }
        }
    }

    public static void setNetworkStateChangeListener(NetworkStateChangeListener listener) {
        NetworkStateChangeReceiver.networkStateChangeListener = listener;
    }
}