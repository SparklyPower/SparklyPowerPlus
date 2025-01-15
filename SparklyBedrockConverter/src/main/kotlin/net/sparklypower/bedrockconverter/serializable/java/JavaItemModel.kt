package net.sparklypower.bedrockconverter.serializable.java

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class JavaItemModel(
    val parent: String,
    val textures: JsonObject? = null,
    val display: JsonObject? = null,
    val overrides: List<Override>
) {
    @Serializable
    data class Override(
        val predicate: Predicate,
        val model: String
    ) {
        @Serializable
        data class Predicate(
            @SerialName("custom_model_data") val customModelData: Int
        )
    }
}