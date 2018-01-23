package by.nalivajr.margo.annonatations

/**
 * Analogue to [InnerView] named same as ButterKnife's annotation
 * to make transition easier
 */
@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class BindView(
        /**
         * @return the id of the view to be found in root view
         */
        val value: Int,
        /**
         * True if the view is required and need to show toast if not found
         */
        val required: Boolean = false
)