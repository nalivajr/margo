package by.nalivajr.margo.tools

import java.lang.reflect.Field


internal class JavaPropertyDescriptor (
        val id: Int,
        val required: Boolean = false,
        val field: Field
)