package io.github.hiiragi283.api.module

import io.github.hiiragi283.api.extension.isModLoaded
import net.minecraft.util.Identifier

enum class HTModuleType(
    val apiKey: String,
    val pluginKey: String,
    val modId: String,
    val modName: String,
) {
    API("ht_api.api", "ht_series.api", "ht_api", "HT API"),
    ENGINEERING("ht_api.engineering", "ht_series.engineering", "ht_engineering", "HT Engineering"),
    MATERIAL("ht_api.material", "ht_series.material", "ht_materials", "HT Materials"),
    STORAGE("ht_api.storage", "ht_series.storage", "ht_storage", "HT Storage"),
    TOOL("ht_api.tool", "ht_series.tool", "ht_tools", "HT Tools"),
    ;

    val isLoaded: Boolean = isModLoaded(modId)

    fun id(path: String): Identifier = Identifier(modId, path)
}
