package by.nalivajr.margo.tools;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import by.nalivajr.margo.annonatations.AutoInjectActivity;
import by.nalivajr.margo.annonatations.AutoInjectFragment;
import by.nalivajr.margo.annonatations.AutoInjectView;
import by.nalivajr.margo.annonatations.BindView;
import by.nalivajr.margo.annonatations.InnerView;
import by.nalivajr.margo.exceptions.NotAnnotatedActivityUsedException;
import by.nalivajr.margo.exceptions.NotAnnotatedFragmentUsedException;

/**
 * Created by Sergey Nalivko.
 * email: snalivko93@gmail.com
 */
public final class MargoJava {

    public static final String TAG = MargoJava.class.getSimpleName();

    private MargoJava() {
    }

    /**
     * Sets content view to activity. Finds and injects all views, annotated with {@link InnerView}
     *
     * @param activity the activity to setContent view
     * @return the view, set as a content
     * @throws NotAnnotatedActivityUsedException if activity is not annotated with {@link AutoInjectActivity}
     */
    public static View setContentView(Activity activity) throws NotAnnotatedActivityUsedException {
        AutoInjectActivity annotation = activity.getClass().getAnnotation(AutoInjectActivity.class);
        if (annotation == null) {
            throw new NotAnnotatedActivityUsedException();
        }
        int id = annotation.layoutId();
        boolean recursive = annotation.recursive();
        return setContentView(activity, id, recursive);
    }

    /**
     * Creates layout for fragment. Finds and injects all views, annotated with {@link InnerView}
     *
     * @param fragment the fragment to create view
     * @param context  the context
     * @return the view, set as a root view
     */
    public static View createView(Fragment fragment, Context context) {
        AutoInjectFragment annotation = fragment.getClass().getAnnotation(AutoInjectFragment.class);
        if (annotation == null) {
            throw new NotAnnotatedFragmentUsedException();
        }
        int layoutId = annotation.layoutId();
        boolean recursive = annotation.recursive();
        return createView(context, fragment, layoutId, recursive);
    }

    /**
     * Creates layout for fragment. Finds and injects all views, annotated with {@link InnerView}
     *
     * @param fragment the fragment to create view
     * @param context  the context
     * @return the view, set as a root view
     */
    public static View createView(Context context, Fragment fragment, int layoutId, boolean recursive) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);

        Field[] fields = fragment.getClass().getDeclaredFields();
        for (Field field : fields) {
            InnerView innerViewAnnotation = field.getAnnotation(InnerView.class);
            BindView bindViewAnnotation = field.getAnnotation(BindView.class);
            Integer id = null;
            if (innerViewAnnotation != null) {
                id = innerViewAnnotation.value();
            } else if (bindViewAnnotation != null) {
                id = bindViewAnnotation.value();
            }
            if (id != null) {
                View v = view.findViewById(id);
                field.setAccessible(true);
                try {
                    field.set(fragment, v);
                } catch (IllegalAccessException e) {
                    Log.w(TAG, "Could not initialize annotated field", e);
                }
                field.setAccessible(false);
            }
        }
        initView(view, recursive);
        return view;
    }

    /**
     * Sets content view to activity. Finds and injects all views, annotated with {@link InnerView}
     * or {@link }
     *
     * @param activity the activity to setContent view
     * @param layoutId the id of layout resource
     * @return the view, set as a content
     */
    public static View setContentView(Activity activity, int layoutId, boolean recursive) {
        View root = activity.getLayoutInflater().inflate(layoutId, null);
        activity.setContentView(root);

        Field[] fields = activity.getClass().getDeclaredFields();
        for (Field field : fields) {
            InnerView innerViewAnnotation = field.getAnnotation(InnerView.class);
            BindView bindViewAnnotation = field.getAnnotation(BindView.class);
            Integer id = null;
            if (innerViewAnnotation != null) {
                id = innerViewAnnotation.value();
            } else if (bindViewAnnotation != null) {
                id = bindViewAnnotation.value();
            }
            if (id != null) {
                View v = root.findViewById(id);
                field.setAccessible(true);
                try {
                    field.set(activity, v);
                } catch (IllegalAccessException e) {
                    Log.w(TAG, "Could not initialize annotated field", e);
                }
                field.setAccessible(false);
            }
        }
        initView(root, recursive);
        return root;
    }

    /**
     * Finds and injects all views, annotated with {@link InnerView} or {@link BindView}
     * @param view      target view
     * @param recursive if true then all sub views in hierarchy will be initialized too
     */
    public static void initView(View view, boolean recursive) {
        if (view == null) {
            return;
        }

        if (ViewGroup.class.isAssignableFrom(view.getClass())) {
            AutoInjectView autoInjectView = view.getClass().getAnnotation(AutoInjectView.class);
            ViewGroup v = (ViewGroup) view;
            if (autoInjectView != null) {
                int layoutId = autoInjectView.layoutId();
                LayoutInflater.from(view.getContext()).inflate(layoutId, v);
                recursive = autoInjectView.recursive();
            }

            for (int i = 0; i < v.getChildCount() && recursive; i++) {
                View child = v.getChildAt(i);
                initView(child, recursive);
            }
        }

        Field[] fields = view.getClass().getDeclaredFields();
        for (Field field : fields) {
            InnerView innerViewAnnotation = field.getAnnotation(InnerView.class);
            BindView bindViewAnnotation = field.getAnnotation(BindView.class);
            Integer id = null;
            if (innerViewAnnotation != null) {
                id = innerViewAnnotation.value();
            } else if (bindViewAnnotation != null) {
                id = bindViewAnnotation.value();
            }
            if (id != null) {
                View v = view.findViewById(id);
                field.setAccessible(true);
                try {
                    field.set(view, v);
                } catch (IllegalAccessException e) {
                    Log.w(TAG, "Could not initialize annotated field", e);
                }
                field.setAccessible(false);
            }
            if (View.class.isAssignableFrom(field.getType()) && (field.getType().getAnnotation(AutoInjectView.class) != null)) {
                field.setAccessible(true);
                try {
                    View v = (View) field.get(view);
                    initView(v, recursive);
                } catch (IllegalAccessException e) {
                    Log.w(TAG, "Could not get access", e);
                }
                field.setAccessible(false);
            }
        }
    }
}
