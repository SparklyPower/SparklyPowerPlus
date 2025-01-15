package net.sparklypower.bedrockconverter

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.sparklypower.bedrockconverter.serializable.java.JavaItemModel
import net.sparklypower.bedrockconverter.serializable.java.JavaModelConfig
import java.io.File

// Converts Java Edition 1.21.3 predicates to 1.21.4 predicates
fun main() {
    val vanillaModelsFolder = File("C:\\Users\\leona\\AppData\\Roaming\\.minecraft\\resourcepacks\\SparklyPowerPlus\\assets\\minecraft\\models\\item")
    val outputFolder = File("C:\\Users\\leona\\AppData\\Roaming\\.minecraft\\resourcepacks\\SparklyPowerPlus\\assets\\minecraft\\items")

    for (file in vanillaModelsFolder.listFiles()) {
        if (file.extension == "json") {
            println(file.name)

            if (file.nameWithoutExtension != "carrot_on_a_stick" && file.nameWithoutExtension != "light_gray_stained_glass" && file.nameWithoutExtension != "light_gray_stained_glass_pane_parent") {
                val model = Json.decodeFromString<JavaItemModel>(file.readText())

                val pred = model.overrides.map {
                    JavaModelConfig.Entry(
                        it.predicate.customModelData,
                        JavaModelConfig.ModelDetail(
                            "model",
                            it.model
                        )
                    )
                }

                val newConfig = JavaModelConfig(
                    JavaModelConfig.Model(
                        type = "range_dispatch",
                        property = "custom_model_data",
                        pred,
                        JavaModelConfig.ModelDetail("model", "minecraft:item/${file.nameWithoutExtension}"),
                    )
                )

                File(outputFolder, file.name).writeText(
                    Json {
                        prettyPrint = true
                    }.encodeToString(
                        newConfig
                    )
                )
            }
        }
    }
}