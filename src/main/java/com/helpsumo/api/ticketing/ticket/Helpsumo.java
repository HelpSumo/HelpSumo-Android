package com.helpsumo.api.ticketing.ticket;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.helpsumo.api.ticketing.ticket.Activities.Article;
import com.helpsumo.api.ticketing.ticket.Activities.Faq;
import com.helpsumo.api.ticketing.ticket.Activities.TicketList;
import com.helpsumo.api.ticketing.ticket.utills.common;
import com.helpsumo.api.ticketing.ticket.utills.version;

import java.util.concurrent.atomic.AtomicBoolean;

public class Helpsumo extends Activity {
    private static volatile Helpsumo INSTANCE;
    private final Context context;
    private common lifecycleCallbacks;
    static String editor;
    private static AtomicBoolean lifecycleCallbacksRegistered = new AtomicBoolean(false);

    private Helpsumo(Context context) {
        this.context = context.getApplicationContext();

    }
    private Context getContext() {
        return this.context;
    }

    public static Helpsumo getInstance(@NonNull Context context) {
        if (context == null) {
            throw new IllegalArgumentException("getInstance() requires a valid context");
        } else {
            if (INSTANCE == null) {
                Class var1 = Helpsumo.class;
                synchronized (Helpsumo.class) {
                    if (INSTANCE == null) {
                        INSTANCE = new Helpsumo(context);
                    }
                }
            }
            return INSTANCE;
        }
    }

    public void init(@NonNull HelpsumoConfig helpsumoConfig) {
        if (!version.ea()) {
            Log.w("HELPSUMO_WARNING", "Helpsumo is not supported in this version of OS");
        } else {
            try {
                validateHelpsumoConfig(helpsumoConfig);
            }catch(RuntimeException e){
                e.printStackTrace();
            }
            if (!lifecycleCallbacksRegistered.get()) {
                if (version.dW()) {
                    if (this.lifecycleCallbacks == null) {
                        this.lifecycleCallbacks = new common();
                        ((Application) this.context).registerActivityLifecycleCallbacks(this.lifecycleCallbacks);
                        lifecycleCallbacksRegistered.set(true);
                    }
                } else {
                    Log.w("HELPSUMO", "Device is running pre ICS OS");
                    lifecycleCallbacksRegistered.set(true);
                }
            }
            Log.w("HELPSUMO", "Helpsumo init completed for app " + helpsumoConfig.getAppKey());
        }
    }

    private static void validateHelpsumoConfig(HelpsumoConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Helpsumo.init() requires a valid HelpsumoConfig instance");
        } else if (az(config.getAppKey())) {
            throw new IllegalArgumentException("Helpsumo.init() requires a valid HelpsumoConfig instance - Api Key is missing");
        }
    }

    public static boolean az(String var0) {
        return var0 == null || var0.length() == 0 || var0.trim().isEmpty();
    }

    private static boolean hasSavedConfig(Context context) {
        context.getApplicationContext();
        return !az(editor);
    }

    public static void showTickets(@NonNull Context activityContext, String apikey) {
        if (!version.ea()) {
            Log.w("HELPSUMO_WARNING", "Helpsumo is not supported in this version of OS");
        } else if (activityContext == null) {
            throw new IllegalArgumentException("showTicket() requires a valid context");
        }  else {
            Intent i = new Intent(activityContext, TicketList.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("Apikey", apikey);
            activityContext.startActivity(i);
        }
    }

    public static void showFAQs(@NonNull Context activityContext, String apikey) {
        if (!version.ea()) {
            Log.w("HELPSUMO_WARNING", "Helpsumo is not supported in this version of OS");
        } else if (activityContext == null) {
            throw new IllegalArgumentException("showTicket() requires a valid context");
        } else {
            Intent i = new Intent(activityContext, Faq.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("Apikey", apikey);
            activityContext.startActivity(i);
        }
    }

    public static void showArticles(@NonNull Context activityContext, String apikey) {
        if (!version.ea()) {
            Log.w("HELPSUMO_WARNING", "Helpsumo is not supported in this version of OS");
        } else if (activityContext == null) {
            throw new IllegalArgumentException("showTicket() requires a valid context");
        } else {
            Intent i = new Intent(activityContext, Article.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("Apikey", apikey);
            activityContext.startActivity(i);
        }
    }
}
