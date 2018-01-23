package by.nalivajr.margo.annonatations

/**
 * Created by Sergey Nalivko.
 * email: snalivko93@gmail.com
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class AutoInjectFragment(
        /**
         * Specifies the resource layout id
         * @return the id of layout
         */
        val layoutId: Int,
        /**
         * If true, then all sub views in hierarchy will be checked and initialized too
         */
        val recursive: Boolean = false
)
