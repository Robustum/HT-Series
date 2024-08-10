package io.github.hiiragi283.api.module

import io.github.hiiragi283.api.extension.getEntrypoints
import io.github.hiiragi283.api.extension.isModLoaded
import io.github.hiiragi283.api.extension.runCatchAndLog

sealed class HTPluginHolder<T : HTPlugin>(type: HTModuleType, clazz: Class<T>) {
    private val plugins: Iterable<T> = getEntrypoints(type.pluginKey, clazz)
        .filter { isModLoaded(it.modId) }
        .sortedWith(compareBy<T> { it.priority }.thenBy { it::class.java.canonicalName })

    fun forEach(action: (T) -> Unit) {
        plugins.forEach { runCatchAndLog { action(it) } }
    }

    data object Engineering 

    data object Material : HTPluginHolder<HTPlugin.Material>(HTModuleType.MATERIAL, HTPlugin.Material::class.java)

    data object Storage

    data object Tool 
}