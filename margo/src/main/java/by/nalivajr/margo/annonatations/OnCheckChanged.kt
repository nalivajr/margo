package by.nalivajr.margo.annonatations

/**
 *
 * [@OnClick]
 * private onMyViewCheckChanged()
 *
 * [@OnClick]
 * private onMyViewCheckChanged(View v)
 *
 * [@OnClick]
 * private onMyViewCheckChanged(Boolean checked)
 *
 * [@OnClick]
 * private onMyViewCheckChanged(View v, Boolean checked)
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnCheckChanged(
    vararg val value: Int,
    val required: Boolean = false
)