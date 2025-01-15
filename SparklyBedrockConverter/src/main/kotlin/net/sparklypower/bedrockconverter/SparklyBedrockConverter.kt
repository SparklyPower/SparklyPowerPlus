package net.sparklypower.bedrockconverter

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import net.sparklypower.bedrockconverter.serializable.bedrock.BedrockSoundDefinitions
import net.sparklypower.bedrockconverter.serializable.java.JavaSoundDefinition
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

private val jsonPrettyPrint = Json { prettyPrint = true }
private val JsonIgnoreUnknownKeys = Json {
    ignoreUnknownKeys = true
}

// A script to do resource pack related things
fun main(args: Array<String>) {
    val option = args[0]

    // ===[ POST CONVERSION RESOURCE PACK MODIFICATION ]===
    // Used to make things prettier to convert things that java2bedrock.sh do not handle

    // This is the root of the Java Edition resource pack
    val inputPackFolder = File(args[1])
    val inputAssetsFolder = File(inputPackFolder, "assets")

    // This is the output folder of the Bedrock resource pack
    val outputPackFolder = File(args[2])

    // And this is the output file of the Geyser mappings
    val geyserMappingsFile = File(args[3])

    outputPackFolder.mkdirs()

    // STUFF THAT CANNOT BE REMAPPED:
    // Rain (I don't know where is the texture)

    // ===[ NAIVE FILE REMAPPER ]===
    val fileRemap = mapOf(
        // Armor
        // "minecraft/textures/models/armor/chainmail_layer_1.png" to "textures/models/armor/chain_1.png",

        // Items
        // "minecraft/textures/item/chainmail_chestplate.png" to "textures/items/chainmail_chestplate.png",

        // Signs
        "minecraft/textures/entity/signs/oak.png" to "textures/entity/sign.png",
        "minecraft/textures/entity/signs/acacia.png" to "textures/entity/sign_acacia.png",
        "minecraft/textures/entity/signs/birch.png" to "textures/entity/sign_birch.png",
        "minecraft/textures/entity/signs/jungle.png" to "textures/entity/sign_jungle.png",
        "minecraft/textures/entity/signs/spruce.png" to "textures/entity/sign_spruce.png",
        "minecraft/textures/entity/signs/crimson.png" to "textures/entity/sign_crimson.png",
        "minecraft/textures/entity/signs/warped.png" to "textures/entity/sign_warped.png",
        "minecraft/textures/entity/signs/dark_oak.png" to "textures/entity/sign_darkoak.png",
        "minecraft/textures/entity/signs/mangrove.png" to "textures/entity/mangrove_sign.png",

        // Blocks
        "minecraft/textures/block/brown_mushroom.png" to "textures/blocks/mushroom_brown.png",

        // GUI
        "minecraft/textures/gui/icons.png" to "textures/gui/icons.png",
    )

    // Remap files
    for ((input, output) in fileRemap) {
        File(outputPackFolder, output).parentFile.mkdirs()
        File(inputAssetsFolder, input).copyTo(File(outputPackFolder, output))
    }

    // ===[ BASIC FONTS REMAPPER ]===
    val javaFontsFolder = File(inputAssetsFolder, "minecraft/textures/font")
    val bedrockFontsFolder = File(outputPackFolder, "font")

    javaFontsFolder.listFiles().forEach {
        if (it.name.startsWith("unicode_page_")) {
            it.copyTo(File(bedrockFontsFolder, "glyph_${it.nameWithoutExtension.substringAfterLast("_").uppercase()}.png"))
        }
    }

    // ===[ COMPLEX FONTS REMAPPER ]===

    // ===[ PAINTING REMAPPER ]===
    val paintingSource = BufferedImage(256 * 4, 256 * 4, BufferedImage.TYPE_INT_ARGB)
    val graphics = paintingSource.createGraphics()

    fun loadAndPasteAt(path: String, x: Int, y: Int) {
        graphics.drawImage(
            ImageIO.read(File(inputAssetsFolder, "minecraft/textures/painting/${path}.png")),
            x * 4,
            y * 4,
            null
        )
    }

    loadAndPasteAt("alban", 32, 0)
    loadAndPasteAt("aztec", 16, 0)
    loadAndPasteAt("aztec2", 48, 0)
    loadAndPasteAt("bomb", 64, 0)
    loadAndPasteAt("kebab", 0, 0)
    loadAndPasteAt("plant", 80, 0)
    loadAndPasteAt("wasteland", 96, 0)

    loadAndPasteAt("courbet", 32, 32)
    loadAndPasteAt("pool", 0, 32)
    loadAndPasteAt("sea", 64, 32)
    loadAndPasteAt("creebet", 128, 32)
    loadAndPasteAt("sunset", 96, 32)

    loadAndPasteAt("graham", 16, 64)
    loadAndPasteAt("wanderer", 0, 64)

    loadAndPasteAt("bust", 32, 128)
    loadAndPasteAt("match", 0, 128)
    loadAndPasteAt("stage", 64, 128)
    loadAndPasteAt("void", 96, 128)
    loadAndPasteAt("skull_and_roses", 128, 128)
    loadAndPasteAt("wither", 160, 128)

    loadAndPasteAt("donkey_kong", 192, 112)
    loadAndPasteAt("skeleton", 192, 64)
    loadAndPasteAt("fighters", 0, 96)

    loadAndPasteAt("burning_skull", 128, 192)
    loadAndPasteAt("pigscene", 64, 192)
    loadAndPasteAt("pointer", 0, 192)

    val bedrockPaintingsFolder = File(outputPackFolder, "textures/painting/")
    bedrockPaintingsFolder.mkdirs()
    ImageIO.write(
        paintingSource,
        "png",
        File(bedrockPaintingsFolder, "kz.png")
    )

    // ===[ SOUNDS REMAPPER ]===
    // Copy the definitions
    val minecraftNamespaceJavaSounds = JsonIgnoreUnknownKeys.decodeFromString<Map<String, JavaSoundDefinition>>(File(inputAssetsFolder, "minecraft/sounds.json").readText())
    val sparklyPowerNamespaceJavaSounds = JsonIgnoreUnknownKeys.decodeFromString<Map<String, JavaSoundDefinition>>(File(inputAssetsFolder, "sparklypower/sounds.json").readText())

    // it is actually the same JSON format for the sound definition lmao
    val bedrockSoundDefinitions = mutableMapOf<String, JavaSoundDefinition>()

    for (javaSoundDefinition in minecraftNamespaceJavaSounds) {
        bedrockSoundDefinitions[javaSoundDefinition.key] = javaSoundDefinition.value
            .copy(
                sounds = javaSoundDefinition.value.sounds.map {
                    it.copy(
                        name = "sounds/${it.name.replace(":", "/")}" // Replace the namespace to a path, example: "sparklypower:" to "sparklypower/"
                    )
                }
            )
    }

    for (javaSoundDefinition in sparklyPowerNamespaceJavaSounds) {
        // When storing on the sparklypower namespace, we MUST include the "sparklypower" namespace on the key
        bedrockSoundDefinitions["sparklypower:${javaSoundDefinition.key}"] = javaSoundDefinition.value
            .copy(
                sounds = javaSoundDefinition.value.sounds.map {
                    it.copy(
                        name = "sounds/${it.name.replace(":", "/")}" // Replace the namespace to a path, example: "sparklypower:" to "sparklypower/"
                    )
                }
            )
    }

    val beSounds = BedrockSoundDefinitions(
        "1.14.0",
        bedrockSoundDefinitions
    )

    val beSoundsFolder = File(outputPackFolder, "sounds")
    beSoundsFolder.mkdirs()
    File(beSoundsFolder, "sound_definitions.json")
        .writeText(
            Json.encodeToString(beSounds)
        )
    val beSparklySoundsFolder = File(beSoundsFolder, "sparklypower")
    beSparklySoundsFolder.mkdirs()

    // Copy the sounds
    File(inputAssetsFolder, "sparklypower/sounds/").copyRecursively(beSparklySoundsFolder)

    // Don't remap tools because Geyser doesn't support changing the tool speed (yet)
    val geyserMappingsJson = Json.parseToJsonElement(geyserMappingsFile.readText())
        .jsonObject
    val items = geyserMappingsJson["items"]!!.jsonObject

    geyserMappingsFile.writeText(
        Json { prettyPrint = true }.encodeToString(
            JsonObject(
                geyserMappingsJson.toMutableMap()
                    .apply {
                        this["items"] = JsonObject(
                            items.toMutableMap().apply {
                                this.remove("minecraft:diamond_pickaxe")
                                this.remove("minecraft:diamond_shovel")
                                this.remove("minecraft:golden_pickaxe")
                            }
                        )
                    }
            )
        )
    )
}