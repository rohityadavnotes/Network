package com.network.internet;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.network.util.LogcatUtil;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {

    private static final String TAG = NetworkCallbackImpl.class.getSimpleName();
    private Context context;

    NetworkCallbackImpl(@NonNull Context context) {
        this.context = context;
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        LogcatUtil.informationMessage(TAG,"Network Available (CONNECTED)");
        context.sendBroadcast(getNetworkAvailabilityIntent(true));
    }

    @Override
    public void onLosing(@NonNull Network network, int maxMsToLive) {
        super.onLosing(network, maxMsToLive);
        /*
         * Called when the network is about to be disconnected. Usually paired with the call of the
         * new replacement network,
         */
        LogcatUtil.informationMessage(TAG,"Network Losing");
        context.sendBroadcast(getNetworkAvailabilityIntent(false));
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        LogcatUtil.informationMessage(TAG,"Network Lost (DISCONNECTED)");
        context.sendBroadcast(getNetworkAvailabilityIntent(false));
    }

    @Override
    public void onUnavailable() {
        super.onUnavailable();
        /*
         * If the network is not found within the specified timeout period, call
         */
        LogcatUtil.informationMessage(TAG,"Network Unavailable");
        context.sendBroadcast(getNetworkAvailabilityIntent(false));
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        /*
         * Called when the network is connected to the requested framework* to change the function but still meets the specified requirements.
         */
        boolean isInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);//Get whether you can connect to the Internet
        LogcatUtil.informationMessage(TAG,"onCapabilitiesChanged : isInternet = " + isInternet);

        /*if(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
        {
            if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
            {
                LogcatUtil.errorMessage("Network", "getAllNetworks() : MOBILE");
                context.sendBroadcast(getNetworkAvailabilityIntent(true
            }
            else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
            {
                LogcatUtil.errorMessage("Network", "getAllNetworks() : WIFI");
                context.sendBroadcast(getNetworkAvailabilityIntent(true));
            }
        }*/
    }

    @Override
    public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties);
        /*
         * Called when the framework network connected to the request changes.
         */
        LogcatUtil.informationMessage(TAG,"onLinkPropertiesChanged");
    }

    @Override
    public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
        super.onBlockedStatusChanged(network, blocked);
        /*
         * Called when access to the specified network is blocked or unblocked
         */
        LogcatUtil.informationMessage(TAG,"onBlockedStatusChanged");
    }

    public Intent getNetworkAvailabilityIntent(boolean isNetworkAvailable) {
        Intent intent = new Intent(RegisterAndUnregisterNetworkReceiver.NETWORK_AVAILABILITY_ACTION);
        intent.putExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, !isNetworkAvailable);
        return intent;
    }
}
