package com.leon.baseapp.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import com.leon.baseapp.base.BaseApplication
import java.lang.ref.WeakReference
import java.util.*

object ActivityUtils {
    /**
     * Return whether the activity is alive.
     *
     * @param activity The activity.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isActivityAlive(activity: Activity?): Boolean {
        return activity != null && !activity.isFinishing && !activity.isDestroyed
    }

    /**
     * Return the activity by context.
     *
     * @param context The context.
     * @return the activity by context.
     */
    @JvmStatic
    fun getActivityByContext(context: Context): Activity? {
        val activity = getActivityByContextInner(context)
        return if (!isActivityAlive(activity)) null else activity
    }

    private fun getActivityByContextInner(context: Context): Activity? {
        var context: Context? = context ?: return null
        val list: MutableList<Context> = ArrayList()
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            val activity = getActivityFromDecorContext(context)
            if (activity != null) return activity
            list.add(context)
            context = context.baseContext
            if (context == null) {
                return null
            }
            if (list.contains(context)) {
                // loop context
                return null
            }
        }
        return null
    }

    private fun getActivityFromDecorContext(context: Context?): Activity? {
        if (context == null) return null
        if (context.javaClass.name == "com.android.internal.policy.DecorContext") {
            try {
                val mActivityContextField = context.javaClass.getDeclaredField("mActivityContext")
                mActivityContextField.isAccessible = true
                return (mActivityContextField[context] as WeakReference<Activity?>).get()
            } catch (ignore: Exception) {
            }
        }
        return null
    }

    /**
     * Return whether the activity exists.
     *
     * @param pkg The name of the package.
     * @param cls The name of the class.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isActivityExists(
        pkg: String,
        cls: String
    ): Boolean {
        val intent = Intent()
        intent.setClassName(pkg, cls)
        val pm = BaseApplication.instance.packageManager
        return !(pm.resolveActivity(intent, 0) == null || intent.resolveActivity(pm) == null || pm.queryIntentActivities(
            intent,
            0
        ).size == 0)
    }

    private fun getOptionsBundle(
        fragment: Fragment,
        enterAnim: Int,
        exitAnim: Int
    ): Bundle? {
        val activity = fragment.activity ?: return null
        return ActivityOptionsCompat.makeCustomAnimation(activity, enterAnim, exitAnim).toBundle()
    }

    private fun getOptionsBundle(
        context: Context,
        enterAnim: Int,
        exitAnim: Int
    ): Bundle? {
        return ActivityOptionsCompat.makeCustomAnimation(context, enterAnim, exitAnim).toBundle()
    }
}