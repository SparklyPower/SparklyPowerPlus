package net.sparklypower.bedrockconverter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MCBedrockSoundDefinitions(
    @SerialName("format_version")
    val formatVersion: String,
    // It is actually the same lmao
    @SerialName("sound_definitions")
    val soundDefinitions: Map<String, MCJavaSoundDefinition>
)