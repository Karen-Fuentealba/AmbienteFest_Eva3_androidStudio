package com.antaedo_karfuentealba.eva3_ambientefest.data.model

// Clase principal usada por la API de Xano. Campos opcionales con valores por defecto
// para proteger contra respuestas parciales (p. ej. meta faltante).
data class XanoImage(
    val path: String? = null,
    val name: String? = null,
    val type: String? = null,
    val size: Int? = null,
    val mime: String? = null,
    val meta: ImageMeta? = null
)

// Meta puede faltar o tener campos faltantes; usar Int? con default null.
data class ImageMeta(
    val width: Int? = null,
    val height: Int? = null
)
