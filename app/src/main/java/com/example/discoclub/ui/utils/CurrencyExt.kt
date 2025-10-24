package com.example.discoclub.ui.utils

import java.text.NumberFormat
import java.util.Locale

// Función de extensión: convierte un Long (precio) a formato CLP ($x.xxx)
fun Long.toCLP(): String =
    NumberFormat.getCurrencyInstance(Locale("es", "CL")).format(this)
