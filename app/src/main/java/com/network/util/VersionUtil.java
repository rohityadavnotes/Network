package com.network.util;

import android.os.Build;

public class VersionUtil {

    public static boolean atLeastApiLevel(int version) {
        return Build.VERSION.SDK_INT >= version;
    }

    private VersionUtil() {
        throw new UnsupportedOperationException(
                "Should not create instance of Util class. Please use as static..");
    }
}
