package io.github.hiiragi283.api.module

import io.github.hiiragi283.api.extension.checkNotNull
import io.github.hiiragi283.api.extension.findEntrypointContainer

sealed class HTApiHolder<T : Any>(val type: HTModuleType, private val clazz: Class<T>) {
    val apiInstance: T by lazy {
        if (!type.isLoaded) {
            throw IllegalStateException("Module: ${type.modName} is not loaded!")
        }
        findEntrypointContainer(type.modId, type.apiKey, clazz)
            ?.entrypoint
            .checkNotNull { "Could not find api instance of ${clazz.canonicalName}!" }
    }

    data object Engineering

    data object Material : HTApiHolder<HTMaterialsAPI>(HTModuleType.MATERIAL, HTMaterialsAPI::class.java)

    data object Storage

    data object Tool
}