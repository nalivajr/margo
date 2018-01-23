package by.nalivajr.margo.annonatations

/**
 *
 * [@OnClick]
 * private onMyViewClicked()
 *
 * [@OnClick]
 * private onMyViewClicked(View v)
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class OnClick (
    vararg val value: Int,
    val required: Boolean = false
)