package net.sparklypower.bedrockconverter.serializable.java

import kotlinx.serialization.Serializable

@Serializable
data class JavaModelConfig(
    val model: Model
) {
    @Serializable
    data class Model(
        val type: String,
        val property: String,
        val entries: List<Entry>,
        val fallback: ModelDetail
    )

    @Serializable
    data class Entry(
        val threshold: Int,
        val model: ModelDetail
    )

    @Serializable
    data class ModelDetail(
        val type: String,
        val model: String
    )
}