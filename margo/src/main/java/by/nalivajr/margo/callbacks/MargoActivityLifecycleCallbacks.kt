package by.nalivajr.margo.callbacks

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

import by.nalivajr.margo.annonatations.AutoInjectActivity
import by.nalivajr.margo.exceptions.NotAnnotatedActivityUsedException
import by.nalivajr.margo.tools.Margo

/**
 * Created by Sergey Nalivko.
 * email: snalivko93@gmail.com
 */
class MargoActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        try {
            Margo.autoBind(activity)
        } catch (e: NotAnnotatedActivityUsedException) {
            Log.i(TAG, "Activity ${activity.javaClass.name} can't be initialized automatically as it is not annotated wits ${AutoInjectActivity::class.java.name}")
        }
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    companion object {

        private val TAG = MargoActivityLifecycleCallbacks::class.java.simpleName
    }
}
