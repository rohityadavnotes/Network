package com.network.internet;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Handler;
import android.os.PatternMatcher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import com.network.util.LogcatUtil;

public class RegisterAndUnregisterNetworkReceiver {

    private static final String TAG = RegisterAndUnregisterNetworkReceiver.class.getSimpleName();
    public static final String NETWORK_AVAILABILITY_ACTION = "com.download.NETWORK_AVAILABILITY_ACTION";

    private Context context;
    private ConnectivityManager connectivityManager;
    private NetworkStateChangeReceiver networkStateChangeReceiver;
    private NetworkCallbackImpl networkCallback;

    public RegisterAndUnregisterNetworkReceiver(@NonNull Context context) {
        this.context = context;
        networkStateChangeReceiver = new NetworkStateChangeReceiver();
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    public void registerNetworkReceiver(@NonNull Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            context.registerReceiver(networkStateChangeReceiver, new IntentFilter(NETWORK_AVAILABILITY_ACTION));
            if (connectivityManager != null)
            {
                networkCallback = new NetworkCallbackImpl(context);

                /* for devices above Nougat */
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                {
                    LogcatUtil.informationMessage(TAG, "NETWORK CALLBACK IF");
                    connectivityManager.registerDefaultNetworkCallback(networkCallback);
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    /* Starting with Oreo specifying a Handler is allowed.  Use this to avoid thread-hops. */
                   connectivityManager.registerNetworkCallback(provideNetworkRequest(), networkCallback, provideHandler());
                }
                /* for devices b/w Lollipop and Nougat */
                else
                {
                    LogcatUtil.informationMessage(TAG, "NETWORK CALLBACK ELSE");
                    connectivityManager.registerNetworkCallback(provideNetworkRequest(), networkCallback);
                }
            }

            // used because if network is off when app start
            if(NetworkConnectivityUtil.isConnectedAll(context)){
                context.sendBroadcast(networkCallback.getNetworkAvailabilityIntent(true));
            } else{
                context.sendBroadcast(networkCallback.getNetworkAvailabilityIntent(false));
            }
        }
        /* below lollipop */
        else
        {
            /*
            * Even if we consider it, it is not worth it because CONNECTIVITY_ACTION is already deprecated in Android 9.0 (API level 28) */
            if (networkStateChangeReceiver != null)
            {
                IntentFilter filter = new IntentFilter();
                filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
                filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                context.registerReceiver(networkStateChangeReceiver, filter);
                LogcatUtil.informationMessage(TAG, "CONNECTIVITY_ACTION");
            }
        }
    }

    public NetworkRequest provideNetworkRequest() {
        NetworkRequest networkRequest = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            addNetCapabilityValidated(builder);

            builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
            builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);

            addNetworkSpecifier(builder);

            networkRequest = builder.build();
        }
        return networkRequest;
    }

    public void addNetCapabilityValidated(NetworkRequest.Builder builder) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            builder.addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }
    }

    public void addNetworkSpecifier(NetworkRequest.Builder builder) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            NetworkSpecifier networkSpecifier =
                    new WifiNetworkSpecifier.Builder()
                            .setSsidPattern(new PatternMatcher("wifiApName", PatternMatcher.PATTERN_PREFIX))
                            .setWpa2Passphrase("password")
                            .build();

            builder.setNetworkSpecifier(networkSpecifier);
        }
    }

    public Handler provideHandler() {
        Handler handler = null;
        return handler;
    }

    public void unregisterNetworkReceiver(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            LogcatUtil.informationMessage(TAG, "UNREGISTER ABOVE 21");
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
        else
        {
            LogcatUtil.informationMessage(TAG, "UNREGISTER BELOW 21");
            context.unregisterReceiver(networkStateChangeReceiver);
        }

        LogcatUtil.informationMessage(TAG, "UNREGISTER..............................................");
    }
}
