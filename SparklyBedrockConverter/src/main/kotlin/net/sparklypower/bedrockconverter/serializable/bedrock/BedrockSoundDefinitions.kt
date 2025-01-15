package net.sparklypower.bedrockconverter.serializable.bedrock

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.sparklypower.bedrockconverter.serializable.java.JavaSoundDefinition

@Serializable
data class BedrockSoundDefinitions(
    @SerialName("format_version")
    val formatVersion: String,
    // It is actually the same lmao
    @SerialName("sound_definitions")
    val soundDefinitions: Map<String, JavaSoundDefinition>
)