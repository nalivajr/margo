package by.nalivajr.margo.tools

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import by.nalivajr.margo.annonatations.*
import java.lang.reflect.Modifier
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

/**
 * Created by Siarhei Naliuka
 * email: snaliuka@exadel.com
 */
object MarKot {

    private val listenerAnnotations = arrayOf(OnClick::class, OnTextChanged::class, OnCheckChanged::class)
    private val boolKType = Boolean::class.createType()
    private val stringKType = String::class.createType()
    private val viewKType = View::class.createType()

    fun bind(activity: Activity) {
        bindWithDetector(activity, activity, activity::findViewById)
    }

    fun bind(view: View) {
        bindWithDetector(view, view.context, view::findViewById)
    }

    fun bind(fragment: Fragment, rootView: View) {
        bindWithDetector(fragment, rootView.context, rootView::findViewById)
    }

    fun bind(target: Any, rootView: View) {
        bindWithDetector(target, rootView.context, rootView::findViewById)
    }

    private fun bindWithDetector(target: Any, context: Context, viewDetector: (Int) -> View?) {
        bindProperties(target, context, viewDetector)
        bindListeners(target, viewDetector, context)
    }

    @Deprecated("Too slow if using kotlin reflect")
    private fun bindProperties(target: Any, context: Context, viewDetector: (Int) -> View?) {
        Log.d("MARGOPERF", "Before get props")
        val declaredMemberProperties = target::class.declaredMemberProperties
        Log.d("MARGOPERF", "After get props")
        declaredMemberProperties
                .map {
                    val field = it
                    val errorMessage = "Can not bind view to not modifiable field ${field.name} at ${target.javaClass.name}"
                    (it.findAnnotation() as InnerView?)?.let {
                        if ((field is KMutableProperty1) == false) {
                            Log.w("Margo", errorMessage)
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            return@map null;
                        }
                        return@map PropertyDescriptor(it.value, it.required, field as KMutableProperty1)
                    }
                    (it.findAnnotation() as BindView?)?.let {
                        if ((field is KMutableProperty1) == false) {
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            Log.w("Margo", errorMessage)
                            return@map null;
                        }
                        return@map PropertyDescriptor(it.value, it.required, field as KMutableProperty1)
                    }
                }
                .forEach {
                    if (it == null) {
                        return@forEach
                    }
                    val view = viewDetector.invoke(it.id)
                    if (it.prop.isFinal) {
                        val errorMessage = "Can not bind view to not modifiable field ${it.prop.name} at ${target.javaClass.name}"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        Log.w("Margo", errorMessage)
                    }
                    if (it.required && view == null) {
                        val errorMessage = "Can not find required view for field ${it.prop.name} at ${target.javaClass.name}"
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        Log.w("Margo", errorMessage)
                        return@forEach
                    }
                    val initialAccessibility = it.prop.isAccessible;
                    it.prop.isAccessible = true
                    it.prop.setter.call(target, view)
                    it.prop.isAccessible = initialAccessibility
                }
        Log.d("MARGOPERF", "perform props mapping")
    }

    @Deprecated("Too slow if using kotlin reflect")
    private fun bindListeners(target: Any, viewDetector: (Int) -> View?, context: Context) {
        target::class.declaredFunctions
                .flatMap {
                    val func = it
                    val allAnnos = it.annotations
                    val res = mutableListOf<ListenerDescriptor<*>>()
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
                        Log.w("Margo", errorMessage)
                        return@forEach
                    }
                    val listenerDesc: ListenerDescriptor<*> = it
                    when (it.anno) {
                        is OnClick -> bindOnClick(view, listenerDesc, target)
                        is OnCheckChanged -> bindOnCheckChanged(context, view, listenerDesc, target)
                    }
                }
    }

    private fun bindOnClick(view: View?, listenerDesc: ListenerDescriptor<*>, target: Any) {
        val func = listenerDesc.func
        val initialAccess = func.isAccessible
        func.isAccessible = true

        var args: Array<Any?>
        if (func.parameters.size > 1 && func.parameters[1].type.isSubtypeOf(viewKType)) {
            args = arrayOf(target, view)
        } else {
            args = arrayOf(target)
        }

        view?.setOnClickListener {
            func.isAccessible = true
            func.call(*args)
            func.isAccessible = initialAccess
        }
    }

    private fun bindOnCheckChanged(context: Context, view: View?, listenerDesc: ListenerDescriptor<*>, target: Any) {
        if (view is CompoundButton? == false) {
            val errorMessage = "Can not bind OnCheckChanged listener for method ${listenerDesc.func.name} at ${target.javaClass.name} as target view is not a CompoundButton"
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            Log.w("Margo", errorMessage)
            return
        }
        (view as CompoundButton?)?.setOnCheckedChangeListener { btn, checked ->
            val func = listenerDesc.func
            var bound = false;
            val initialAccess = func.isAccessible
            func.isAccessible = true
            val parameters = func.parameters
            if (parameters.size == 1) {
                func.call(target)
                bound = true
            } else if (parameters.size == 2) {
                if (parameters[1].type.isSubtypeOf(viewKType)) {
                    func.call(target, btn)
                    bound = true
                } else if (parameters[1].type.isSubtypeOf(boolKType) ) {
                    func.call(target, checked)
                    bound = true
                }
            } else if (parameters.size == 3) {
                if (parameters[1].type.isSubtypeOf(viewKType) && parameters[2].type.isSubtypeOf(boolKType) ) {
                    func.call(target, btn, checked)
                    bound = true
                } else if (parameters[2].type.isSubtypeOf(viewKType) && parameters[1].type.isSubtypeOf(boolKType) ) {
                    func.call(target, checked, btn)
                    bound = true
                }
            }
            if (bound == false) {
                val errorMessage = "Can not bind OnCheckChanged listener for method ${listenerDesc.func.name} at ${target.javaClass.name} as signature is not appropriate"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                Log.w("Margo", errorMessage)
            }
            func.isAccessible = initialAccess
        }
    }

    private fun bindOnTextChanged(context: Context, view: View?, listenerDesc: ListenerDescriptor<*>, target: Any) {
        if (view is TextView? == false) {
            val errorMessage = "Can not bind OnCheckChanged listener for method ${listenerDesc.func.name} at ${target.javaClass.name} as target view is not a EditText"
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            Log.w("Margo", errorMessage)
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
                val parameters = func.parameters
                if (parameters.size == 1) {
                    func.call(target)
                    bound = true
                } else if (parameters.size == 2 && parameters[1].type.isSubtypeOf(viewKType)) {
                    func.call(target, view)
                    bound = true
                } else if (parameters.size == 3 && parameters[1].type.isSubtypeOf(viewKType) && parameters[2].type.isSubtypeOf(stringKType)) {
                    func.call(target, view, s?.toString())
                    bound = true
                } else if (parameters.size == 4 && parameters[1].type.isSubtypeOf(viewKType) && parameters[2].type.isSubtypeOf(stringKType) && parameters[3].type.isSubtypeOf(stringKType)) {
                    func.call(target, view, s?.toString(), oldText?.toString())
                    bound = true
                }
                if (bound == false) {
                    val errorMessage = "Can not bind OnCheckChanged listener for method ${listenerDesc.func.name} at ${target.javaClass.name} as signature is not appropriate"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    Log.w("Margo", errorMessage)
                }
                func.isAccessible = initialAccess
            }
        })
    }
}