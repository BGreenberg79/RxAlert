package com.example.rxalert.network

import com.google.gson.annotations.SerializedName

data class RxNormResponse(
    @SerializedName("drugGroup") val drugGroup: DrugGroup?
)

data class DrugGroup(
    @SerializedName("conceptGroup") val conceptGroup: List<ConceptGroup>?
)

data class ConceptGroup(
    @SerializedName("tty") val tty: String?,
    @SerializedName("conceptProperties") val conceptProperties: List<ConceptProperty>?
)

data class ConceptProperty(
    @SerializedName("rxcui") val rxcui: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("synonym") val synonym: String?,
    @SerializedName("tty") val tty: String?
)