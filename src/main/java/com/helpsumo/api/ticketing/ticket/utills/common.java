package com.helpsumo.api.ticketing.ticket.utills;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

public class common implements Application.ActivityLifecycleCallbacks {
    private int fw = 0;

    public common() {
    }

    public void onActivityCreated(Activity arg0, Bundle arg1) {
    }

    public void onActivityDestroyed(Activity arg0) {
    }

    public void onActivityPaused(Activity arg0) {
    }

    public void onActivityResumed(Activity arg0) {
    }

    public void onActivitySaveInstanceState(Activity arg0, Bundle arg1) {
    }

    public void onActivityStarted(Activity activity) {
        if (this.fw == 0) {
            Log.w("ActivityLifecycle", "Registering new init");
        }
        ++this.fw;
    }

    public void onActivityStopped(Activity arg0) {
        --this.fw;
        if (this.fw < 0) {
            this.fw = 0;
        }

    }
}

