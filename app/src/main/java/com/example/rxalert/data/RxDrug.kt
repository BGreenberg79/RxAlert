package com.example.rxalert.data

/**
 * Simplified representation of a drug result returned from RxNorm.
 */
data class RxDrug(
    val rxcui: String,
    val name: String,
    val synonym: String?,
    val type: String
) {
    val isBrand: Boolean
        get() = type.contains("B", ignoreCase = true)

    val displayType: String
        get() = if (isBrand) "Brand" else "Generic"

    val relatedName: String?
        get() = synonym?.takeIf { it.isNotBlank() && it != name }
}