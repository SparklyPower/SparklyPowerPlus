package net.sparklypower.bedrockconverter

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    for ((index, arg) in args.withIndex()) {
        println("args[$index]: $arg")
    }

    val inputPackFolder = File(args[0])
    val inputAssetsFolder = File(inputPackFolder, "assets")
    val outputPackFolder = File(args[1])

    // outputPackFolder.deleteRecursively()
    outputPackFolder.mkdirs()

    // STUFF THAT CANNOT BE REMAPPED:
    // Rain (I don't know where is the texture)

    // ===[ NAIVE FILE REMAPPER ]===
    val fileRemap = mapOf(
        // Armor
        "minecraft\\textures\\models\\armor\\chainmail_layer_1.png" to "textures\\models\\armor\\chain_1.png",

        // Items
        "minecraft\\textures\\item\\chainmail_chestplate.png" to "textures\\items\\chainmail_chestplate.png",

        // Signs
        "minecraft\\textures\\entity\\signs\\oak.png" to "textures\\entity\\sign.png",
        "minecraft\\textures\\entity\\signs\\acacia.png" to "textures\\entity\\sign_acacia.png",
        "minecraft\\textures\\entity\\signs\\birch.png" to "textures\\entity\\sign_birch.png",
        "minecraft\\textures\\entity\\signs\\jungle.png" to "textures\\entity\\sign_jungle.png",
        "minecraft\\textures\\entity\\signs\\spruce.png" to "textures\\entity\\sign_spruce.png",
        "minecraft\\textures\\entity\\signs\\crimson.png" to "textures\\entity\\sign_crimson.png",
        "minecraft\\textures\\entity\\signs\\warped.png" to "textures\\entity\\sign_warped.png",
        "minecraft\\textures\\entity\\signs\\dark_oak.png" to "textures\\entity\\sign_darkoak.png",
        "minecraft\\textures\\entity\\signs\\mangrove.png" to "textures\\entity\\mangrove_sign.png",

        // Blocks
        "minecraft\\textures\\block\\brown_mushroom.png" to "textures\\blocks\\mushroom_brown.png",

        // GUI
        "minecraft\\textures\\gui\\icons.png" to "textures\\gui\\icons.png",
    )

    // Remap files
    for ((input, output) in fileRemap) {
        File(outputPackFolder, output).parentFile.mkdirs()
        File(inputAssetsFolder, input).copyTo(File(outputPackFolder, output))
    }

    // ===[ BASIC FONTS REMAPPER ]===
    val javaFontsFolder = File(inputAssetsFolder, "minecraft\\textures\\font")
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
            ImageIO.read(File(inputAssetsFolder, "minecraft\\textures\\painting\\${path}.png")),
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

    val bedrockPaintingsFolder = File(outputPackFolder, "textures\\painting\\")
    bedrockPaintingsFolder.mkdirs()
    ImageIO.write(
        paintingSource,
        "png",
        File(bedrockPaintingsFolder, "kz.png")
    )

    // ===[ SOUNDS REMAPPER ]===
    // Copy the definitions
    val javaSounds = Json.decodeFromString<Map<String, MCJavaSoundDefinition>>(File(inputAssetsFolder, "minecraft\\sounds.json").readText())
    val beSounds = MCBedrockSoundDefinitions(
        "1.14.0",
        javaSounds.mapValues { // it is actually the same JSON format lmao
            it.value.copy(
                sounds = it.value.sounds.map {
                    it.copy(
                        name = "sounds/${it.name.replace(":", "/")}" // Replace the namespace to a path, example: "sparklypower:" to "sparklypower/"
                    )
                }
            )
        }
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
    File(inputAssetsFolder, "sparklypower\\sounds\\").copyRecursively(beSparklySoundsFolder)
}