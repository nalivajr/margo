package by.nalivajr.margo.tools

import kotlin.reflect.KFunction


internal class ListenerDescriptor<R> (
        val id: Int,
        val required: Boolean = false,
        val func: KFunction<R>,
        val anno: Annotation
)