package by.nalivajr.margo.annonatations

/**
 * Allows to bind text changed event to [TextView] and it's inheritance
 *
 * Supported method signatures
 *
 * [@OnTextChanged]
 * private onMyViewTextChanged()
 *
 * [@OnTextChanged]
 * private onMyViewTextChanged(View v)
 *
 * [@OnTextChanged]
 * private onMyViewTextChanged(View v, String newText)
 *
 * [@OnTextChanged]
 * private onMyViewTextChanged(View v, String oldText, String newText)
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnTextChanged(
    vararg val value: Int,
    val required: Boolean = false
)