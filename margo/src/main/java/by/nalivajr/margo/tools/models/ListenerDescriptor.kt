package by.nalivajr.margo.tools.models

import java.lang.reflect.Method


internal class ListenerDescriptor(
        val id: Int,
        val required: Boolean = false,
        val func: Method,
        val anno: Annotation
)