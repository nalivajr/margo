package by.nalivajr.margo.tools

import java.lang.reflect.Method


internal class JavaListenerDescriptor (
        val id: Int,
        val required: Boolean = false,
        val func: Method,
        val anno: Annotation
)