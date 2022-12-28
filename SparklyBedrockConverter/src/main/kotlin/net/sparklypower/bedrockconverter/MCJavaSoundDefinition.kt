package net.sparklypower.bedrockconverter

import kotlinx.serialization.Serializable

@Serializable
data class MCJavaSoundDefinition(
    val category: String,
    val sounds: List<MCJavaSound>
) {
    @Serializable
    data class MCJavaSound(
        val name: String,
        val stream: Boolean
    )
}