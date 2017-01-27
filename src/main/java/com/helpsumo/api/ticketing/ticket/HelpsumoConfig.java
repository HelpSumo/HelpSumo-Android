package com.helpsumo.api.ticketing.ticket;

import android.support.annotation.NonNull;

public class HelpsumoConfig {

    private String appKey;

    public HelpsumoConfig(@NonNull String appKey) {
        this.appKey = appKey.trim();
    }

    public void setAppkey(String appKey) {
        this.appKey = appKey.trim();
    }

    public String getAppKey() {
        return this.appKey;
    }

}

