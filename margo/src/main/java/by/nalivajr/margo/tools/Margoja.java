package by.nalivajr.margo.tools;

import android.app.Activity;
import android.app.Fragment;
import android.view.View;

/**
 * Created by Siarhei Naliuka
 * email: snaliuka@exadel.com
 */

public class Margoja {

    public void bind(Activity activity) {
        Margo.INSTANCE.bind(activity);
    }

    public void bind(View view) {
        Margo.INSTANCE.bind(view);
    }

    public void bind(Fragment fragment, View view) {
        Margo.INSTANCE.bind(fragment, view);
    }

    public void bind(Object target, View view) {
        Margo.INSTANCE.bind(target, view);
    }
}
