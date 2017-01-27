package com.helpsumo.api.ticketing.ticket.utills;

import android.os.Build;

public class version {
    public static boolean ea() {
        return Build.VERSION.SDK_INT >= 10;
    }

    public static boolean dU() {
        return Build.VERSION.SDK_INT == 10;
    }

    public static boolean dV() {
        return Build.VERSION.SDK_INT >= 11;
    }

    public static boolean dW() {
        return Build.VERSION.SDK_INT >= 14;
    }

    public static boolean dX() {
        return Build.VERSION.SDK_INT >= 17;
    }

    public static boolean dY() {
        return Build.VERSION.SDK_INT >= 19;
    }

    public static boolean dZ() {
        return Build.VERSION.SDK_INT >= 23;
    }

    public static boolean ed() {
        return Build.VERSION.SDK_INT <= 23;
    }

    public static boolean ee() {
        return Build.VERSION.SDK_INT > 23;
    }
}
