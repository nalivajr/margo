package by.nalivajr.margo.annonatations

/**
 * Created by Sergey Nalivko.
 * email: snalivko93@gmail.com
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class InnerView(
        /**
         * @return the id of the view to be found in root view
         */
        val value: Int,
        /**
         * True if the view is required and need to show toast if not found
         */
        val required: Boolean = false
)