package by.nalivajr.margo.annonatations

/**
 * Created by Siarhei Naliuka
 * email: snaliuka@exadel.com
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Saveable(
        /**
         * Optional. Key will be generated as class name + field name
         */
        val key: String = "",
        val required: Boolean = false
)