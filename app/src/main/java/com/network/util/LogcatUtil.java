package com.network.util;

import android.util.Log;
import com.network.BuildConfig;

public class LogcatUtil {

    public static boolean LOG_ENABLE = BuildConfig.DEBUG;

    /**
     * Log.isLoggable() will throw an exception if the length of the tag is greater than
     * 23 characters, so trim it if necessary to avoid the exception.
     *
     *  The priority is one of the following values:
     *
     *     V: Verbose (lowest priority) = 2
     *     D: Debug = 2
     *     I: Info = 3
     *     W: Warning = 4
     *     E: Error = 5
     *     A: Assert = 6
     *
     *  The log message format is:
     *      date time PID-TID/package priority/tag: message
     *      12-10 13:02:50.071 1901-4229/com.google.android.gms V/AuthZen: Handling delegate intent.
     *
     *  Where,
     *          V priority.
     *          Those are PID-TID. PID is process ID, TID is thread id.
     *
     * Colors,
     *          e CD0000
     *          w A66F00
     *          i 00CD00
     *          v 0000EE
     *          d 00CCCC
     *          a 7F0000
     */
    public static String getTag23Characters(String tag)
    {
        /**
         * Note: Log.isLoggable() will only return true for levels of INFO and above by
         * default. To override this behavior, set system properties as described in the
         * documentation for Log.isLoggable(), or add appropriate filtering code here.
         */
        if (tag.length() > 23)
        {
            tag = tag.substring(0, 22);
        }
        return tag;
    }

    private static String buildMessage(String message) {
        StringBuilder buffer = new StringBuilder();
        final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        buffer.append("[ ");
        buffer.append(Thread.currentThread().getName());
        buffer.append(": ");
        buffer.append(stackTraceElement.getFileName());
        buffer.append(": ");
        buffer.append(stackTraceElement.getLineNumber());
        buffer.append(": ");
        buffer.append(stackTraceElement.getMethodName());
        buffer.append("() ] =======> ");
        buffer.append(message);
        return buffer.toString();
    }

    /**
     * Verbose Message.
     *
     * @param tag     - Application Tag.
     * @param message - Logging message.
     */
    public static void verboseMessage(final String tag, final String message)
    {
        if (LOG_ENABLE)
        {
            Log.v(getTag23Characters(tag),  buildMessage(message));
        }
    }

    /**
     * Verbose Message.
     *
     * @param tag     - Application Tag.
     * @param message - Logging message.
     * @param throwable - exception.
     */
    public static void verboseMessage(final String tag, final String message, final Throwable throwable)
    {
        if (LOG_ENABLE)
        {
            Log.v(getTag23Characters(tag), buildMessage(message),throwable);
        }
    }

    /**
     * Debug Message.
     *
     * @param tag     - Application Tag.
     * @param message - Logging message.
     */
    public static void debuggingMessage(final String tag, final String message)
    {
        if (LOG_ENABLE)
        {
            Log.d(getTag23Characters(tag), buildMessage(message));
        }
    }

    /**
     * Debug Message.
     *
     * @param tag     - Application Tag.
     * @param message - Logging message.
     * @param throwable - exception.
     */
    public static void debuggingMessage(final String tag, final String message, final Throwable throwable)
    {
        if (LOG_ENABLE)
        {
            Log.d(getTag23Characters(tag), buildMessage(message),throwable);
        }
    }

    /**
     * Default Log Level
     * Information Message. Used in catch block,
     *
     * @param tag     - Application Tag.
     * @param message - Logging message.
     */
    public static void informationMessage(final String tag, final String message)
    {
        if (LOG_ENABLE)
        {
            Log.i(getTag23Characters(tag),  buildMessage(message));
        }
    }

    /**
     * Default Log Level
     * Information Message. Used in catch block,
     *
     * @param tag     - Application Tag.
     * @param message - Logging message.
     * @param throwable - exception.
     */
    public static void informationMessage(final String tag, final String message, final Throwable throwable)
    {
        if (LOG_ENABLE)
        {
            Log.i(getTag23Characters(tag), buildMessage(message),throwable);
        }
    }

    /**
     * Warning Message.
     *
     * @param tag     - Application Tag.
     * @param message - Logging message.
     */
    public static void warningMessage(final String tag, final String message)
    {
        if (LOG_ENABLE)
        {
            Log.w(getTag23Characters(tag),  buildMessage(message));
        }
    }

    /**
     * Warning Message.
     *
     * @param tag     - Application Tag.
     * @param message - Logging message.
     * @param throwable - exception.
     */
    public static void warningMessage(final String tag, final String message, final Throwable throwable)
    {
        if (LOG_ENABLE)
        {
            Log.w(getTag23Characters(tag), buildMessage(message),throwable);
        }
    }

    /**
     * Error Message.
     *
     * @param tag     - Application Tag.
     * @param message - Logging message.
     */
    public static void errorMessage(final String tag, final String message)
    {
        if (LOG_ENABLE)
        {
            Log.e(getTag23Characters(tag),  buildMessage(message));
        }
    }

    /**
     * Error Message.
     *
     * @param tag     - Application Tag.
     * @param message - Logging message.
     * @param throwable - exception.
     */
    public static void errorMessage(final String tag, final String message, final Throwable throwable)
    {
        if (LOG_ENABLE)
        {
            Log.w(getTag23Characters(tag), buildMessage(message),throwable);
        }
    }

    /**
     *  wtf stands for "what a terrible failure" message
     *  This method is available in the Log class from Android. It is introduced in API 8 (Android 2.2) and it returns a int value.
     *
     * @param tag     - Application Tag.
     * @param message - Logging message.
     */
    public static void wtfMessage(final String tag, final String message)
    {
        if (LOG_ENABLE)
        {
            Log.wtf(getTag23Characters(tag),  buildMessage(message));
        }
    }

    /**
     *  wtf stands for "what a terrible failure" message
     *
     * @param tag     - Application Tag.
     * @param message - Logging message.
     * @param throwable - exception.
     */


    public static void wtfMessage(final String tag, final String message, final Throwable throwable)
    {
        if (LOG_ENABLE)
        {
            Log.wtf(getTag23Characters(tag),  buildMessage(message),throwable);
        }
    }

    private LogcatUtil() {
        throw new UnsupportedOperationException(
                "Should not create instance of Util class. Please use as static..");
    }
}
