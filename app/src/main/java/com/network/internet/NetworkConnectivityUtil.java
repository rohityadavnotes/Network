package com.network.internet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import com.network.util.LogcatUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkConnectivityUtil {

    /**
     * Method to open wireless settings
     *
     * @param activity - Activity
     */
    public static void openWirelessSettings(@NonNull Activity activity) {
        Intent intent;

        /* Determine the version of the mobile phone system, that is, if the API is greater than 10, it is version 3.0 or above */
        if (Build.VERSION.SDK_INT > 10 && !Build.MANUFACTURER.equals("Meizu"))
        {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        else
        {
            intent = new Intent(android.provider.Settings.ACTION_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            /*
             * OR
             * intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             */

            /*
             * OR
             * openSetting(Activity activity)
             */
        }
        activity.startActivity(intent);
    }

    /**
     * Method to open wireless settings
     *
     * @param activity - Activity
     */
    public static void openSetting(@NonNull Activity activity) {
        Intent intent = new Intent("/");
        ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
        intent.setComponent(componentName);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    /**
     * Determine whether mobile data is open
     *
     * @param context - Context
     * @return {@code true}: Yes <br>{@code false}: no
     */
    public static boolean getDataEnabled(@NonNull Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getMobileDataEnabledMethod = telephonyManager.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getMobileDataEnabledMethod) {
                return (boolean) getMobileDataEnabledMethod.invoke(telephonyManager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Turn mobile data on or off
     * <p>Need system application Need to add permission{@code <uses-permission android:name="android.permission.MODIFY_PHONE_STATE"/>}</p>
     *
     * @param context - Context
     * @param enabled {@code true}: turn on <br>{@code false}: shut down
     */
    @RequiresPermission(Manifest.permission.MODIFY_PHONE_STATE)
    public static void setDataEnabled(@NonNull Context context, final boolean enabled) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method setMobileDataEnabledMethod = telephonyManager.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            if (null != setMobileDataEnabledMethod) {
                setMobileDataEnabledMethod.invoke(telephonyManager, enabled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMobileDataEnabled(Context context, boolean enabled) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try
        {
            final Class conmanClass = Class.forName(connectivityManager.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(connectivityManager);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determine if wifi is on
     * <p>Need to add permissions {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}</p>
     *
     * @param context - Context
     * @return {@code true}: Yes <br>{@code false}: no
     */
    public static boolean getWifiEnabled(@NonNull Context context) {
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * Turn wifi on or off
     *
     * @param context - Context
     * @param enabled {@code true}: turn on <br>{@code false}: shut down
     */
    @RequiresPermission(Manifest.permission.CHANGE_WIFI_STATE)
    public static void setWifiEnabled(@NonNull Context context, boolean enabled) {
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (enabled) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        } else {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    /**
     * Get the name of the network operator
     * <p>China Mobile, such as China Unicom, China Telecom</p>
     *
     * @param context - Context
     * @return Operator name
     */
    public static String getNetworkOperatorName(@NonNull Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager != null ? telephonyManager.getNetworkOperatorName() : null;
    }

    /**
     * Get mobile terminal type
     *
     * @param context Context
     * @return Mobile phone system
     * <ul>
     * <li>{@link TelephonyManager#PHONE_TYPE_NONE } : 0 Unknown phone format</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_GSM  } : 1 Mobile phone standard is GSM，China Mobile and China Unicom</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_CDMA } : 2 The mobile phone standard is CDMA, telecom</li>
     * <li>{@link TelephonyManager#PHONE_TYPE_SIP  } : 3</li>
     * </ul>
     */
    public static int getPhoneType(@NonNull Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager != null ? telephonyManager.getPhoneType() : -1;
    }

    /**
     * Get domain name ip address
     *
     * @param domain domain name
     * @return ip address
     */
    @RequiresPermission(Manifest.permission.INTERNET)
    public static String getDomainAddress(final String domain) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(domain);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get active network information
     * <p>Need to add permissions {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context - Context
     * @return NetworkInfo
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private static NetworkInfo getActiveNetworkInfo(@NonNull Context context) {
        NetworkInfo networkInfo = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo;
    }

    /**
     * Is there a network
     * <p>Need to add permissions {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context - Context
     * @return boolean
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isNetWorkAvailable(@NonNull Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Determine whether the network is connected, check if there is any connectivity
     * <p>Need to add permissions {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context - Context
     * @return {@code true}: Yes <br>{@code false}: no
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isConnected(@NonNull Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnectedOrConnecting());
    }

    /**
     * Get network type
     *
     * @param context Context
     * @return
     * -1: No network
     * 1: WIFI network (TYPE_WIFI)
     * 2: wap network (TYPE_MOBILE)
     * 3: net network (TYPE_MOBILE)
     */
    public static int getNetworkType(@NonNull Context context) {
        int netType = -1;
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                netType = 3;
            } else {
                netType = 2;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;
        }
        return netType;
    }

    /**
     * Determine whether the network is mobile data
     * <p>Need to add permissions {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected <br>{@code false}: not connected
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isMobileConnected(@NonNull Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Determine whether wifi is connected
     * <p>Need to add permissions {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context - Context
     * @return {@code true}: connected <br>{@code false}: not connected
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isWifiConnected(@NonNull Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Determine fast connectivity is available or not
     * <p>Need to add permissions {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context - Context
     * @return {@code true}: Yes <br>{@code false}: no
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isConnectedFast(@NonNull Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected() && isConnectionFast(networkInfo.getType(),networkInfo.getSubtype()));
    }

    /**
     * Determine connection speed fast or not
     *
     * @param type
     * @param subType
     * @return {@code true}: Yes <br>{@code false}: no
     */
    public static boolean isConnectionFast(int type, int subType){
        if(type==ConnectivityManager.TYPE_WIFI)
        {
            return true;
        }
        else if(type==ConnectivityManager.TYPE_MOBILE)
        {
            switch(subType){
                case TelephonyManager.NETWORK_TYPE_1xRTT: // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA: // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE: // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS: // ~ 100 kbps
                    return false;

                case TelephonyManager.NETWORK_TYPE_EVDO_0: // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A: // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA: // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA: // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA: // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS: // ~ 400-7000 kbps
                    return true;
                /*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11  // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9 // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13 // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11 // ~ 10+ Mbps
                    return true;

                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8 // ~25 kbps
                    return false;
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Determine connection speed fast or not, When API Level 7 or Below
     *
     * @param type
     * @param subType
     * @return {@code true}: Yes <br>{@code false}: no
     */

    /*
     * These constants aren't yet available in my API level (7), but I need to
     * handle these cases if they come up, on newer versions
     */
    public static final int NETWORK_TYPE_EHRPD = 14; // Level 11
    public static final int NETWORK_TYPE_EVDO_B = 12; // Level 9
    public static final int NETWORK_TYPE_HSPAP = 15; // Level 13
    public static final int NETWORK_TYPE_IDEN = 11; // Level 8
    public static final int NETWORK_TYPE_LTE = 13; // Level 11

    public static boolean isConnectionFastApiLevel7(int type, int subType){
        if(type==ConnectivityManager.TYPE_WIFI)
        {
            return true;
        }
        else if(type==ConnectivityManager.TYPE_MOBILE)
        {
            switch(subType){
                case TelephonyManager.NETWORK_TYPE_1xRTT: // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA: // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE: // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS: // ~ 100 kbps
                    return false;

                case TelephonyManager.NETWORK_TYPE_EVDO_0: // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A: // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA: // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA: // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA: // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS: // ~ 400-7000 kbps
                    return true;

                /* NOT AVAILABLE YET IN API LEVEL 7 */
                case NetworkConnectivityUtil.NETWORK_TYPE_EHRPD: // API level 11  // ~ 1-2 Mbps
                case NetworkConnectivityUtil.NETWORK_TYPE_EVDO_B: // API level 9 // ~ 5 Mbps
                case NetworkConnectivityUtil.NETWORK_TYPE_HSPAP: // API level 13 // ~ 10-20 Mbps
                case NetworkConnectivityUtil.NETWORK_TYPE_LTE: // API level 11 // ~ 10+ Mbps
                    return true;

                case NetworkConnectivityUtil.NETWORK_TYPE_IDEN: // API level 8 // ~25 kbps
                    return false;
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        }
        else
        {
            return false;
        }
    }

    /***********************************************************************************************
     ********************** GET WHICH CONNECTION USED , WIFI, 4G, 3G, 2G etc., *********************
     **********************************************************************************************/

    public enum InternetConnectionType {
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

    private static final int NETWORK_TYPE_GSM      = 16;
    private static final int NETWORK_TYPE_TD_SCDMA = 17;
    private static final int NETWORK_TYPE_IWLAN    = 18;

    /**
     * Get the current internet connection type
     * <p>Need to add permissions {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context - Context
     * @return Internet Connection Type
     * <ul>
     * <li>{@link InternetConnectionType#NETWORK_WIFI   } </li>
     * <li>{@link InternetConnectionType#NETWORK_4G     } </li>
     * <li>{@link InternetConnectionType#NETWORK_3G     } </li>
     * <li>{@link InternetConnectionType#NETWORK_2G     } </li>
     * <li>{@link InternetConnectionType#NETWORK_UNKNOWN} </li>
     * <li>{@link InternetConnectionType#NETWORK_NO     } </li>
     * </ul>
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static InternetConnectionType getInternetConnectionType(@NonNull Context context) {

        InternetConnectionType internetConnectionType = InternetConnectionType.NETWORK_NO;

        NetworkInfo networkInfo = getActiveNetworkInfo(context);

        if (networkInfo != null && networkInfo.isAvailable())
        {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
            {
                internetConnectionType = InternetConnectionType.NETWORK_WIFI;
            }
            else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                switch (networkInfo.getSubtype())
                {
                    case NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        internetConnectionType = InternetConnectionType.NETWORK_2G;
                        break;

                    case NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        internetConnectionType = InternetConnectionType.NETWORK_3G;
                        break;

                    case NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        internetConnectionType = InternetConnectionType.NETWORK_4G;
                        break;

                    default:
                        String subtypeName = networkInfo.getSubtypeName();
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            internetConnectionType = InternetConnectionType.NETWORK_3G;
                        }
                        else
                        {
                            internetConnectionType = InternetConnectionType.NETWORK_UNKNOWN;
                        }
                        break;
                }
            }
            else
            {
                internetConnectionType = InternetConnectionType.NETWORK_UNKNOWN;
            }
        }
        return internetConnectionType;
    }

    /**
     * Determine whether the network is 4G
     * <p>Need to add permissions {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context - Context
     * @return {@code true}: Yes <br>{@code false}: no
     */
    public static boolean is4G(@NonNull Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable() && info.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /***********************************************************************************************
     ********************************************* ALL IN ONE **************************************
     **********************************************************************************************/

    /**
     * Method to checking internet is connected or disconnected all the device
     * For kitkat,lollipop,marshmallow,Nougat or higher or lower version of Api.
     *
     * @param context - Context
     * @return 1 : MOBILE, 2 : WIFI, 0 : NOT CONNECTED ( IF RETURN TYPE INT )
     * @return {@code true}: connected <br>{@code false}: not connected ( IF RETURN TYPE BOOLEAN )
     */
    public static boolean isConnectedAll(@NonNull Context context) {

        boolean connectedBoolean = false;
        int connectedInt         = 0;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (connectivityManager != null)
            {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if(networkCapabilities != null)
                {
                    if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    {
                        LogcatUtil.errorMessage("Network", "getNetworkCapabilities(connectivityManager.getActiveNetwork()) : MOBILE");
                        connectedBoolean    = true;
                        connectedInt        = 1;
                    }
                    else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    {
                        LogcatUtil.errorMessage("Network", "getNetworkCapabilities(connectivityManager.getActiveNetwork()) : WIFI");
                        connectedBoolean    = true;
                        connectedInt        = 2;
                    }
                }
            }
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (connectivityManager != null)
            {
                Network[] networks = connectivityManager.getAllNetworks();
                Network network;

                if (networks != null)
                    for (int i = 0; i < networks.length; i++)
                    {
                        network = networks[i];
                        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                        if(networkCapabilities != null)
                        {
                            if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                            {
                                LogcatUtil.errorMessage("Network", "getAllNetworks() : MOBILE");
                                connectedBoolean    = true;
                                connectedInt        = 1;
                                break;
                            }
                            else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                            {
                                LogcatUtil.errorMessage("Network", "getAllNetworks() : WIFI");
                                connectedBoolean    = true;
                                connectedInt        = 2;
                                break;
                            }
                        }
                    }
            }
        }
        else
        {
            if (connectivityManager != null)
            {
                /*
                 * Android 6.0 Marshmallow
                 *
                 * public NetworkInfo[] getAllNetworkInfo() :
                 *                                           Added in API level 1
                 *                                           Deprecated in API level 23
                 * This method does not support multiple connected networks of the same type.
                 *
                 * Use
                 *     public Network[] getAllNetworks() :
                 *                                        Added in API level 21
                 *     and
                 *
                 *     public NetworkInfo getNetworkInfo (Network network) :
                 *                                                          Added in API level 21
                 */
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                if (networkInfos != null)
                {
                    for (int i = 0; i < networkInfos.length; i++)
                    {
                        if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED)
                        {
                            if (networkInfos[i].getType() == ConnectivityManager.TYPE_MOBILE)
                            {
                                LogcatUtil.errorMessage("Network", "getAllNetworkInfo() : MOBILE");
                                connectedBoolean    = true;
                                connectedInt        = 1;
                                break;
                            }
                            else if (networkInfos[i].getType() == ConnectivityManager.TYPE_WIFI)
                            {
                                LogcatUtil.errorMessage("Network", "getAllNetworkInfo() : WIFI");
                                connectedBoolean    = true;
                                connectedInt        = 2;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return connectedBoolean;
        //return connectedInt;
    }

    /**
     * Method to checking internet is connected or disconnected all the device
     * For kitkat,lollipop,marshmallow,Nougat or higher or lower version of Api.
     *
     * @param context - Context
     * @return {@code true}: connected <br>{@code false}: not connected
     */
    public static boolean hasInternetConnection(@NonNull Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network;

        NetworkCapabilities capabilities = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            network = connectivityManager.getActiveNetwork();
            capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP)
        {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks)
            {
                networkInfo =connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
                {
                    return true;
                }
            }
        }
        else
        {
            if (connectivityManager != null)
            {
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        {
                            return true;
                        }
            }
        }
        return false;
    }

    public static boolean isMobileConnectAll(@NonNull Context context) {
        boolean connectedBoolean = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (connectivityManager != null)
            {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null)
                {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    {
                        LogcatUtil.errorMessage("Network", "getNetworkCapabilities(connectivityManager.getActiveNetwork()) : MOBILE");
                        connectedBoolean    = true;
                    }
                }
            }
        }
        else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP)
        {
            if (connectivityManager != null)
            {
                Network[] networks = connectivityManager.getAllNetworks();
                Network network;

                /*
                 * Android 10
                 * public class NetworkInfo :
                 *                              Added in API level 21
                 *                              This class was deprecated in API level 29.
                 */
                NetworkInfo networkInfo;

                if (networks != null)
                    for (int i = 0; i < networks.length; i++)
                    {
                        network = networks[i];
                        networkInfo = connectivityManager.getNetworkInfo(network);

                        if(networkInfo != null)
                        {
                            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
                            {

                                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                                {
                                    LogcatUtil.errorMessage("Network", "getAllNetworks() : MOBILE");
                                    connectedBoolean    = true;
                                    break;
                                }
                            }
                        }
                    }
            }
        }
        else
        {
            if (connectivityManager != null)
            {
                /*
                 * Android 6.0 Marshmallow
                 *
                 * public NetworkInfo[] getAllNetworkInfo() :
                 *                                           Added in API level 1
                 *                                           Deprecated in API level 23
                 * This method does not support multiple connected networks of the same type.
                 *
                 * Use
                 *     public Network[] getAllNetworks() :
                 *                                        Added in API level 21
                 *     and
                 *
                 *     public NetworkInfo getNetworkInfo (Network network) :
                 *                                                          Added in API level 21
                 */
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                if (networkInfos != null)
                {
                    for (int i = 0; i < networkInfos.length; i++)
                    {
                        if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED)
                        {
                            if (networkInfos[i].getType() == ConnectivityManager.TYPE_MOBILE)
                            {
                                LogcatUtil.errorMessage("Network", "getAllNetworkInfo() : MOBILE");
                                connectedBoolean    = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return connectedBoolean;
    }

    public static boolean isWifiConnectAll(@NonNull Context context) {
        boolean connectedBoolean = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (connectivityManager != null)
            {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null)
                {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    {
                        LogcatUtil.errorMessage("Network", "getNetworkCapabilities(connectivityManager.getActiveNetwork()) : WIFI");
                        connectedBoolean    = true;
                    }
                }
            }
        }
        else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP)
        {
            if (connectivityManager != null)
            {
                Network[] networks = connectivityManager.getAllNetworks();
                Network network;

                /*
                 * Android 10
                 * public class NetworkInfo :
                 *                              Added in API level 21
                 *                              This class was deprecated in API level 29.
                 */
                NetworkInfo networkInfo;

                if (networks != null)
                    for (int i = 0; i < networks.length; i++)
                    {
                        network = networks[i];
                        networkInfo = connectivityManager.getNetworkInfo(network);

                        if(networkInfo != null)
                        {
                            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED))
                            {
                                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                                {
                                    LogcatUtil.errorMessage("Network", "getAllNetworks() : WIFI");
                                    connectedBoolean    = true;
                                    break;
                                }
                            }
                        }
                    }
            }
        }
        else
        {
            if (connectivityManager != null)
            {
                /*
                 * Android 6.0 Marshmallow
                 *
                 * public NetworkInfo[] getAllNetworkInfo() :
                 *                                           Added in API level 1
                 *                                           Deprecated in API level 23
                 * This method does not support multiple connected networks of the same type.
                 *
                 * Use
                 *     public Network[] getAllNetworks() :
                 *                                        Added in API level 21
                 *     and
                 *
                 *     public NetworkInfo getNetworkInfo (Network network) :
                 *                                                          Added in API level 21
                 */
                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                if (networkInfos != null)
                {
                    for (int i = 0; i < networkInfos.length; i++)
                    {
                        if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED)
                        {
                            if (networkInfos[i].getType() == ConnectivityManager.TYPE_WIFI)
                            {
                                LogcatUtil.errorMessage("Network", "getAllNetworkInfo() : WIFI");
                                connectedBoolean    = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return connectedBoolean;
    }

    /***********************************************************************************************
     ************************************* OTHER METHODS UPDATED ***********************************
     **********************************************************************************************/

    /**
     * Method to checking is mobile or wifi is connected or not
     *
     * @param context - Context
     * @return true or false
     */
    public static boolean isConnectedNew(@NonNull Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (connectivityManager != null)
            {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null)
                {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    {
                        return true;
                    }
                    else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    {
                        return true;
                    }
                }
            }
        }
        else
        {
            if (connectivityManager != null)
            {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null)
                {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    {
                        return true;
                    }
                    else if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Method to checking network type
     *
     * @param context - Context
     * @return Returns connection type. 0: none; 1: mobile data; 2: wifi etc.,
     */
    public static int getNetworkTypeNew(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (connectivityManager != null)
            {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null)
                {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    {
                        return 1;
                    }
                    else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
                    {
                        return 2;
                    }
                    else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        return 3;
                    }
                }
            }
        }
        else
        {
            if (connectivityManager != null)
            {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null)
                {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    {
                        return 1;
                    }
                    else if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                    {
                        return 2;
                    }
                    else if (activeNetwork.getType() == ConnectivityManager.TYPE_VPN)
                    {
                        return 3;
                    }
                }
            }
        }
        return 0;
    }

    public static boolean isMobileConnectedNew(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (connectivityManager != null)
            {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null)
                {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                    {
                        return true;
                    }
                }
            }
        }
        else
        {
            if (connectivityManager != null)
            {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null)
                {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isWifiConnectedNew(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (connectivityManager != null)
            {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    }
                }
            }
        }
        else
        {
            if (connectivityManager != null)
            {
                NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                if (activeNetwork != null)
                {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private NetworkConnectivityUtil() {
        throw new UnsupportedOperationException(
                "Should not create instance of Util class. Please use as static..");
    }
}
