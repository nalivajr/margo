package by.nalivajr.margo.tools;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Siarhei Naliuka
 * email: snaliuka@exadel.com
 */

public class Margoja {

    /**
     * Finds and injects all views, annotated with {@link by.nalivajr.margo.annonatations.InnerView}
     * or {@link by.nalivajr.margo.annonatations.BindView}
     * and also assigns supported listeners
     *
     * @param activity the target object
     */
    public void bind(Activity activity) {
        Margo.INSTANCE.bind(activity);
    }

    /**
     * Finds and injects all views, annotated with {@link by.nalivajr.margo.annonatations.InnerView}
     * or {@link by.nalivajr.margo.annonatations.BindView}
     * and also assigns supported listeners
     *
     * @param view the target object
     */
    public void bind(View view) {
        Margo.INSTANCE.bind(view);
    }

    /**
     * Finds and injects all views, annotated with {@link by.nalivajr.margo.annonatations.InnerView}
     * or {@link by.nalivajr.margo.annonatations.BindView}
     * and also assigns supported listeners
     *
     * @param view the target object
     */
    public void bind(Fragment fragment, View view) {
        Margo.INSTANCE.bind(fragment, view);
    }

    /**
     * Finds and injects all views, annotated with {@link by.nalivajr.margo.annonatations.InnerView}
     * or {@link by.nalivajr.margo.annonatations.BindView}
     * and also assigns supported listeners
     *
     * @param view the target object
     */
    public void bind(Object target, View view) {
        Margo.INSTANCE.bind(target, view);
    }

    /**
     * Sets content view to activity annottaed with {@link by.nalivajr.margo.annonatations.AutoInjectActivity}
     * Finds and injects all views, annotated with {@link by.nalivajr.margo.annonatations.InnerView}
     * or {@link by.nalivajr.margo.annonatations.BindView}
     * and also assigns supported listeners doing it recursively
     *
     * @param activity the activity to setContent view
     */
    public void autoBind(Activity activity) {
        Margo.INSTANCE.autoBind(activity);
    }

    /**
     * Sets content view to activity. Finds and injects all views, annotated with {@link by.nalivajr.margo.annonatations.InnerView}
     * or {@link by.nalivajr.margo.annonatations.BindView}
     * and also assigns supported listeners doing it recursively
     *
     * @param activity the activity to setContent view
     * @param layoutId the id of layout resource
     * @return the view, set as a content
     */
    public void autoBind(Activity activity, int layoutId, boolean recursive) {
        Margo.INSTANCE.autoBind(activity, layoutId, recursive);
    }

    /**
     * Allows to save into bundle the data from fields annotated with {@link by.nalivajr.margo.annonatations.Saveable}
     */
    public static void saveState(Object target, Bundle bundle) {
        Margo.INSTANCE.saveState(target, bundle);
    }

    /**
     * Allows to restore from the bundle the data to fields annotated with {@link by.nalivajr.margo.annonatations.Saveable}
     *
     * Warning: Bundle allows to put
     * - {@link Bundle#putIntegerArrayList}
     * - {@link Bundle#putStringArrayList}
     * - {@link Bundle#putCharSequenceArrayList}
     * - {@link Bundle#putParcelableArrayList}
     *
     * But for now library does not support them because of complexity with generics. Will be supported in future
     */
    public static void restoreState(Object target, Bundle bundle) {
        Margo.INSTANCE.restoreState(target, bundle);
    }

    /**
     * Creates layout for fragment. Finds and injects all views, annotated with {@link by.nalivajr.margo.annonatations.InnerView}
     * or {@link by.nalivajr.margo.annonatations.BindView}
     * and also assigns supported listeners
     *
     * @param fragment the fragment to create view
     * @param context  the context
     * @return the view, set as a root view
     */
    public static View autoBindFragment(Fragment fragment, Context context) {
        return Margo.INSTANCE.autoBindFragment(fragment, context);
    }

    /**
     * Creates layout for fragment. Finds and injects all views, annotated with {@link by.nalivajr.margo.annonatations.InnerView}
     * or {@link by.nalivajr.margo.annonatations.BindView}
     * and also assigns supported listeners
     *
     * @param fragment the fragment to create view
     * @param context  the context
     * @return the view, set as a root view
     */
    public static View autoBindFragment(Context context, Fragment fragment, int layoutId, boolean recursive) {
        return Margo.INSTANCE.autoBindFragment(context, fragment, layoutId, recursive);
    }

    /**
     * Finds and injects all views, annotated with {@link by.nalivajr.margo.annonatations.InnerView}
     * or {@link by.nalivajr.margo.annonatations.BindView}
     * and also assigns supported listeners
     *
     * @param view      target view
     * @param recursive if true then all sub views in hierarchy will be initialized too
     */
    public static void autoBindView(View view, boolean recursive) {
        Margo.INSTANCE.autoBindView(view, recursive);
    }
}
