package by.nalivajr.margosample;

import android.app.Application;

import by.nalivajr.margo.callbacks.MargoActivityLifecycleCallbacks;

/**
 * Created by Siarhei Naliuka
 * email: snaliuka@exadel.com
 */

public class MargoTestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new MargoActivityLifecycleCallbacks());
    }
}
