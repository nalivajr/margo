package by.nalivajr.margo.tools

import kotlin.reflect.KMutableProperty1


internal class PropertyDescriptor<F, S> (
        val id: Int,
        val required: Boolean = false,
        val prop: KMutableProperty1<F, S>
)