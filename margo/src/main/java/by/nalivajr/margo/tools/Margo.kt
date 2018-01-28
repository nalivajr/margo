package by.nalivajr.margo.tools

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.LayoutRes
import android.support.annotation.NonNull
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import by.nalivajr.margo.annonatations.*
import by.nalivajr.margo.exceptions.NotAnnotatedActivityUsedException
import by.nalivajr.margo.exceptions.NotAnnotatedFragmentUsedException
import by.nalivajr.margo.tools.models.ListenerDescriptor
import by.nalivajr.margo.tools.models.PropertyDescriptor
import java.io.Serializable
import java.lang.reflect.Modifier

object Margo {

    private val listenerAnnotations = arrayOf(OnClick::class, OnTextChanged::class, OnCheckChanged::class)
    private val TAG = Margo::class.java.name

    /**
     * Inflates and returns inflated view, without attaching it to any view group
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : View> inflate(context: Context, @LayoutRes layoutId: Int)
            = LayoutInflater.from(context).inflate(layoutId, null) as T

    /**
     * Inflates and returns inflated view, allowing attach it to parent view group
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : View> inflate(@NonNull parent: ViewGroup, @LayoutRes layoutId: Int, attachToRoot: Boolean = false)
            = LayoutInflater.from(parent.context).inflate(layoutId, parent, attachToRoot) as T

    /**
     * Inflates and returns inflated view, allowing attach it to parent view group and bind it
     */
    fun <T : View> inflateAndBind(@NonNull parent: ViewGroup, @LayoutRes layoutId: Int, attachToRoot: Boolean = false) : T {
        val view : T = inflate(parent, layoutId, attachToRoot)
        bind(view)
        return view
    }

    /**
     * Inflates and returns inflated view, allowing attach it to parent view group and bind it
     */
    fun <T : View> inflateAndBind(@NonNull context: Context, @LayoutRes layoutId: Int) : T {
        val view : T = inflate(context, layoutId)
        bind(view)
        return view
    }

    /**
     * Finds and injects all views, annotated with [InnerView] or [BindView]
     * and also assigns supported listeners
     *
     * @param activity the target activity
     * @return the view, set as a content
     */
    fun bind(activity: Activity) {
        bindWithDetector(activity, activity, activity::findViewById)
    }

    /**
     * Finds and injects all views, annotated with [InnerView] or [BindView]
     * and also assigns supported listeners
     *
     * @param view the target view
     * @return the view, set as a content
     */
    fun bind(view: View) {
        bindWithDetector(view, view.context, view::findViewById)
    }

    /**
     * Finds and injects all views, annotated with [InnerView] or [BindView]
     * and also assigns supported listeners
     *
     * @param view the target fragmet
     * @return the view, set as a content
     */
    fun bind(fragment: Fragment, rootView: View) {
        bindWithDetector(fragment, rootView.context, rootView::findViewById)
    }

    /**
     * Finds and injects all views, annotated with [InnerView] or [BindView]
     * and also assigns supported listeners
     *
     * @param view the target object
     * @return the view, set as a content
     */
    fun bind(target: Any, rootView: View) {
        bindWithDetector(target, rootView.context, rootView::findViewById)
    }

    /**
     * Sets content view to activity. Finds and injects all views, annotated with [InnerView] or [BindView]
     * and also assigns supported listeners
     *
     * @param activity the activity to setContent view
     * @return the view, set as a content
     * @throws NotAnnotatedActivityUsedException if activity is not annotated with [AutoInjectActivity]
     */
    @Throws(NotAnnotatedActivityUsedException::class)
    fun autoBind(activity: Activity): View {
        val annotation = activity.javaClass.getAnnotation(AutoInjectActivity::class.java)
                ?: throw NotAnnotatedActivityUsedException()
        val id = annotation.layoutId
        val recursive = annotation.recursive
        return autoBind(activity, id, recursive)
    }

    /**
     * Creates layout for fragment. Finds and injects all views, annotated with [InnerView] or [BindView]
     * and also assigns supported listeners
     *
     * @param fragment the fragment to create view
     * @param context  the context
     * @return the view, set as a root view
     */
    fun autoBindFragment(fragment: Fragment, context: Context): View {
        val annotation = fragment.javaClass.getAnnotation(AutoInjectFragment::class.java)
                ?: throw NotAnnotatedFragmentUsedException()
        val layoutId = annotation.layoutId
        val recursive = annotation.recursive
        return autoBindFragment(context, fragment, layoutId, recursive)
    }

    /**
     * Creates layout for fragment. Finds and injects all views, annotated with [InnerView] or [BindView]
     * and also assigns supported listeners
     *
     * @param fragment the fragment to create view
     * @param context  the context
     * @return the view, set as a root view
     */
    fun autoBindFragment(context: Context, fragment: Fragment, layoutId: Int, recursive: Boolean): View {
        val view = LayoutInflater.from(context).inflate(layoutId, null)

        bind(fragment, view)
        autoBindView(view, recursive)
        return view
    }

    /**
     * Sets content view to activity. Finds and injects all views, annotated with [InnerView] or [BindView]
     * and also assigns supported listeners
     *
     * @param activity the activity to setContent view
     * @param layoutId the id of layout resource
     * @return the view, set as a content
     */
    fun autoBind(activity: Activity, layoutId: Int, recursive: Boolean): View {
        val root = activity.layoutInflater.inflate(layoutId, null)
        activity.setContentView(root)
        bind(activity)
        autoBindView(root, recursive)
        return root
    }

    /**
     * Finds and injects all views, annotated with [InnerView] or [BindView]
     * and also assigns supported listeners
     *
     * @param view      target view
     * @param recursive if true then all sub views in hierarchy will be initialized too
     */
    fun autoBindView(view: View?, recursive: Boolean) {
        var doDeepRecursive = recursive
        if (view == null) {
            return
        }

        if (ViewGroup::class.java.isAssignableFrom(view.javaClass)) {
            val autoView = view.javaClass.getAnnotation(AutoInjectView::class.java)
            val v = view as ViewGroup?
            if (autoView != null) {
                val layoutId = autoView.layoutId
                LayoutInflater.from(view.context).inflate(layoutId, v)
                doDeepRecursive = autoView.recursive
            }

            var i = 0
            while (i < v!!.childCount && doDeepRecursive) {
                val child = v.getChildAt(i)
                autoBindView(child, doDeepRecursive)
                i++
            }
        }

        val fields = view.javaClass.declaredFields
        for (field in fields) {
            bind(view)
            if (View::class.java.isAssignableFrom(field.type) && field.type.getAnnotation(AutoInjectView::class.java) != null) {
                field.isAccessible = true
                try {
                    val v = field.get(view) as View
                    autoBindView(v, doDeepRecursive)
                } catch (e: IllegalAccessException) {
                    Log.w(TAG, "Could not get access", e)
                }

                field.isAccessible = false
            }
        }
    }


    private fun bindWithDetector(target: Any, context: Context, viewDetector: (Int) -> View?) {
        bindFields(target, context, viewDetector)
        bindListenersJava(target, viewDetector, context)
    }

    private fun bindFields(target: Any, context: Context, viewDetector: (Int) -> View?) {
        val declaredMemberProperties = target::class.java.declaredFields
        declaredMemberProperties
                .map {
                    val field = it
                    val errorMessage = "Can not bind view to not modifiable field ${field.name} at ${target.javaClass.name}"
                    (it.getAnnotation(InnerView::class.java))?.let {
                        if (Modifier.isFinal(field.modifiers)) {
                            Log.w(TAG, errorMessage)
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            return@map null;
                        }
                        return@map PropertyDescriptor(it.value, it.required, field)
                    }
                    (it.getAnnotation(BindView::class.java))?.let {
                        if (Modifier.isFinal(field.modifiers)) {
                            Log.w(TAG, errorMessage)
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            return@map null;
                        }
                        return@map PropertyDescriptor(it.value, it.required, field)
                    }
                }
                .forEach {
                    if (it == null) {
                        return@forEach
                    }
                    val view = viewDetector.invoke(it.id)
                    if (it.required && view == null) {
                        val errorMessage = "Can not find required view for field ${it.field.name} at ${target.javaClass.name}"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        Log.w(TAG, errorMessage)
                        return@forEach
                    }
                    val initialAccessibility = it.field.isAccessible;
                    it.field.isAccessible = true
                    it.field.set (target, view)
                    it.field.isAccessible = initialAccessibility
                }
    }

    private fun bindListenersJava(target: Any, viewDetector: (Int) -> View?, context: Context) {
        val declaredMemberMethods = target::class.java.declaredMethods
        declaredMemberMethods
                .flatMap {
                    val func = it
                    val allAnnos = it.annotations
                    val res = mutableListOf<ListenerDescriptor>()
                    allAnnos.forEach {
                        if (listenerAnnotations.contains(it.annotationClass)) {
                            val anno = it;
                            val descriptors = when (it) {
                                is OnClick -> it.value.map { ListenerDescriptor(it, (anno as OnClick).required, func, anno) }.toList()
                                is OnCheckChanged -> it.value.map { ListenerDescriptor(it, (anno as OnCheckChanged).required, func, anno) }.toList()
                                is OnTextChanged -> it.value.map { ListenerDescriptor(it, (anno as OnTextChanged).required, func, anno) }.toList()
                                else -> emptyList()
                            }
                            res.addAll(descriptors)
                        }
                    }
                    return@flatMap res
                }
                .forEach {
                    val view = viewDetector.invoke(it.id)
                    if (it.required && view == null) {
                        val errorMessage = "Can not find required view for field ${it.func.name} at ${target.javaClass.name}"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        Log.w(TAG, errorMessage)
                        return@forEach
                    }
                    val listenerDesc: ListenerDescriptor = it
                    when (it.anno) {
                        is OnClick -> bindOnClick(view, listenerDesc, target)
                        is OnCheckChanged -> bindOnCheckChanged(context, view, listenerDesc, target)
                        is OnTextChanged -> bindOnTextChanged(context, view, listenerDesc, target)
                    }
                }
    }

    private fun bindOnClick(view: View?, listenerDesc: ListenerDescriptor, target: Any) {
        val func = listenerDesc.func
        val initialAccess = func.isAccessible

        view?.setOnClickListener {
            func.isAccessible = true
            val parameterTypes = func.parameterTypes
            if (parameterTypes.size > 0 && View::class.java.isAssignableFrom(parameterTypes[0])) {
                func.invoke(target, view)
            } else {
                func.invoke(target)
            }
            func.isAccessible = initialAccess
        }
    }

    private fun bindOnCheckChanged(context: Context, view: View?, listenerDesc: ListenerDescriptor, target: Any) {
        if (view is CompoundButton? == false) {
            val errorMessage = "Can not bind OnCheckChanged listener for method ${listenerDesc.func.name} at ${target.javaClass.name} as target view is not a CompoundButton"
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            Log.w(TAG, errorMessage)
            return
        }
        (view as CompoundButton?)?.setOnCheckedChangeListener { btn, checked ->
            val func = listenerDesc.func
            var bound = false;
            val initialAccess = func.isAccessible
            func.isAccessible = true
            val parameters = func.parameterTypes
            if (parameters.size == 0) {
                func.invoke(target)
                bound = true
            } else if (parameters.size == 1) {
                if (View::class.java.isAssignableFrom(parameters[0])) {
                    func.invoke(target, btn)
                    bound = true
                } else if (parameters[0] == Boolean::class.java ) {
                    func.invoke(target, checked)
                    bound = true
                }
            } else if (parameters.size == 2) {
                if (View::class.java.isAssignableFrom(parameters[0]) && parameters[1] == Boolean::class.java ) {
                    func.invoke(target, btn, checked)
                    bound = true
                } else if (View::class.java.isAssignableFrom(parameters[1]) && parameters[0] == Boolean::class.java ) {
                    func.invoke(target, checked, btn)
                    bound = true
                }
            }
            if (bound == false) {
                val errorMessage = "Can not bind OnCheckChanged listener for method ${listenerDesc.func.name} at ${target.javaClass.name} as signature is not appropriate"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                Log.w(TAG, errorMessage)
            }
            func.isAccessible = initialAccess
        }
    }

    private fun bindOnTextChanged(context: Context, view: View?, listenerDesc: ListenerDescriptor, target: Any) {
        if (view is TextView? == false) {
            val errorMessage = "Can not bind OnCheckChanged listener for method ${listenerDesc.func.name} at ${target.javaClass.name} as target view is not a EditText"
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            Log.w(TAG, errorMessage)
            return
        }
        (view as TextView?)?.addTextChangedListener(object : TextWatcher {
            private var oldText : CharSequence? = null;
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                oldText = s
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val func = listenerDesc.func
                var bound = false
                val initialAccess = func.isAccessible
                val parameters = func.parameterTypes
                if (parameters == null || parameters.size == 0) {
                    func.invoke(target)
                    bound = true
                } else if (parameters.size == 1 && View::class.java.isAssignableFrom(parameters[0])) {
                    func.invoke(target, view)
                    bound = true
                } else if (parameters.size == 2 && View::class.java.isAssignableFrom(parameters[0]) && CharSequence::class.java.isAssignableFrom(parameters[1])) {
                    func.invoke(target, view, s?.toString())
                    bound = true
                } else if (parameters.size == 3 && View::class.java.isAssignableFrom(parameters[0]) && CharSequence::class.java.isAssignableFrom(parameters[1]) && CharSequence::class.java.isAssignableFrom(parameters[2])) {
                    func.invoke(target, view, s?.toString(), oldText?.toString())
                    bound = true
                }
                if (bound == false) {
                    val errorMessage = "Can not bind OnCheckChanged listener for method ${listenerDesc.func.name} at ${target.javaClass.name} as signature is not appropriate"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    Log.w(TAG, errorMessage)
                }
                func.isAccessible = initialAccess
            }
        })
    }

    /**
     * Allows to save into bundle the data from fields annotated with [Saveable]
     */
    fun saveState(target: Any, bundle: Bundle?) {
        val dest = bundle ?: Bundle()
        target.javaClass.declaredFields
                .forEach {
                    val annotation = it.getAnnotation(Saveable::class.java) ?: return@forEach
                    var key = annotation.key
                    if (key.isBlank()) {
                        key = "${target.javaClass.name}.${it.name}"
                    }
                    val accessibility = it.isAccessible
                    it.isAccessible = true
                    val value = it.get(target)
                    it.isAccessible = accessibility
                    when(value) {
                        null -> dest.remove(key)
                        is Boolean -> dest.putBoolean(key, value)
                        is Byte -> dest.putByte(key, value)
                        is ByteArray -> dest.putByteArray(key, value)
                        is Char -> dest.putChar(key, value)
                        is CharArray -> dest.putCharArray(key, value)
                        is Short -> dest.putShort(key, value)
                        is ShortArray -> dest.putShortArray(key, value)
                        is Int -> dest.putInt(key, value)
                        is IntArray -> dest.putIntArray(key, value)
                        is Long -> dest.putLong(key, value)
                        is LongArray -> dest.putLongArray(key, value)
                        isArrayListOfType(value, Int::class.java) -> dest.putIntegerArrayList(key, value as ArrayList<Int>)
                        is Float -> dest.putFloat(key, value)
                        is FloatArray -> dest.putFloatArray(key, value)
                        is Double -> dest.putDouble(key, value)
                        is DoubleArray -> dest.putDoubleArray(key, value)
                        is String -> dest.putString(key, value)
                        isArrayOfType(value, String::class.java) -> dest.putStringArray(key, value as Array<out String>)
                        isArrayListOfType(value, String::class.java) -> dest.putStringArrayList(key, value as ArrayList<String>)
                        is CharSequence -> dest.putCharSequence(key, value)
                        isArrayOfType(value, CharSequence::class.java) -> dest.putCharSequenceArray(key, value as Array<out CharSequence>)
                        isArrayListOfType(value, CharSequence::class.java) -> dest.putCharSequenceArrayList(key, value as ArrayList<CharSequence>)
                        is Bundle -> dest.putBundle(key, dest)
                        is Binder ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                dest.putBinder(key, value)
                            }
                        is Parcelable -> dest.putParcelable(key, value)
                        isArrayOfType(value, Parcelable::class.java) -> dest.putParcelableArray(key, value as Array<out Parcelable>?)
                        isArrayListOfType(value as ArrayList<*>, Parcelable::class.java) -> dest.putParcelableArrayList(key, value as ArrayList<out Parcelable>?)
                        is Serializable -> dest.putSerializable(key, value)
                    }
                }
    }

    /**
     * Allows to restore from the bundle the data to fields annotated with [Saveable]
     *
     * Warning: Bundle allows to put
     * - [Bundle.putIntegerArrayList]
     * - [Bundle.putStringArrayList]
     * - [Bundle.putCharSequenceArrayList]
     * - [Bundle.putParcelableArrayList]
     *
     * But for now library does not support them because of complexity with generics. Will be supported in future
     */
    fun restoreState(target: Any, bundle: Bundle?) {
        if (bundle == null) {
            return
        }
        target.javaClass.declaredFields
                .forEach {
                    val annotation = it.getAnnotation(Saveable::class.java) ?: return@forEach
                    var key = annotation.key
                    if (key.isBlank()) {
                        key = "${target.javaClass.name}.${it.name}"
                    }
                    val data: Any? = when(it.type) {
                        Boolean::class.java -> bundle.getBoolean(key)
                        Byte::class.java -> bundle.getByte(key)
                        ByteArray::class.java -> bundle.getByteArray(key)
                        Char::class.java -> bundle.getChar(key)
                        CharArray::class.java -> bundle.getCharArray(key)
                        Short::class.java -> bundle.getShort(key)
                        ShortArray::class.java -> bundle.getShortArray(key)
                        Int::class.java -> bundle.getInt(key)
                        IntArray::class.java -> bundle.getIntArray(key)
                        Long::class.java -> bundle.getLong(key)
                        LongArray::class.java -> bundle.getLongArray(key)
                        Float::class.java -> bundle.getFloat(key)
                        FloatArray::class.java -> bundle.getFloatArray(key)
                        Double::class.java -> bundle.getDouble(key)
                        DoubleArray::class.java -> bundle.getDoubleArray(key)
                        String::class.java -> bundle.getString(key)
                        Parcelable::class.java -> bundle.getParcelable(key)
//                        ArrayList::class.java -> {
//                            bundle.getIntegerArrayList(key)
//                            bundle.getStringArrayList(key)
//                            bundle.getCharSequenceArrayList(key)
//                        }
                        Array<String>::class.java -> bundle.getStringArray(key)
                        Array<CharSequence>::class.java -> bundle.getCharSequenceArray(key)
                        Array<Parcelable>::class.java -> bundle.getParcelableArray(key)
                        Bundle::class.java -> bundle.getBundle(key)
                        is Binder ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                bundle.getBinder(key)
                            } else {
                                null
                            }
                        Serializable::class.java -> bundle.getSerializable(key)
                        else -> null
                    }
                    val accessibility = it.isAccessible
                    it.isAccessible = true
                    it.set(target, data)
                    it.isAccessible = accessibility
                }
    }

    private fun isArrayListOfType(list: Any, cls: Class<*>): Boolean {
        if ((list is ArrayList<*>) == false) {
            return false
        }
        for (item in list as ArrayList<*>) {
            if (item != null && item.javaClass != cls) {
                return false
            }
        }
        return true
    }

    private fun isArrayOfType(array: Any, cls: Class<*>): Boolean {
        if ((array is Array<*>) == false) {
            return false
        }
        for (item in array as Array<*>) {
            if (item != null && item.javaClass != cls) {
                return false
            }
        }
        return true
    }
}