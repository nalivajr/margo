package by.nalivajr.margo.tools.models

import java.lang.reflect.Field


internal class PropertyDescriptor(
        val id: Int,
        val required: Boolean = false,
        val field: Field
)