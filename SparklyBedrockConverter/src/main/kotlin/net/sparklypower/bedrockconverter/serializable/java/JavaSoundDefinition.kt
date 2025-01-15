package net.sparklypower.bedrockconverter.serializable.java

import kotlinx.serialization.Serializable

@Serializable
data class JavaSoundDefinition(
    val category: String,
    val sounds: List<MCJavaSound>
) {
    @Serializable
    data class MCJavaSound(
        val name: String,
        val stream: Boolean
    )
}