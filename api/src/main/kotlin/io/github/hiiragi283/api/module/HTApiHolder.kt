package io.github.hiiragi283.api.module

import io.github.hiiragi283.api.extension.findEntrypointContainer

sealed class HTApiHolder<T : Any>(val type: HTModuleType, private val clazz: Class<T>) {
    private lateinit var instanceCache: T

    val apiInstance: T
        get() {
            if (!::instanceCache.isInitialized) {
                if (!type.isLoaded) {
                    throw IllegalStateException("Module: ${type.modName} is not loaded!")
                }
                instanceCache = findEntrypointContainer(type.modId, type.apiKey, clazz)
                    ?.entrypoint
                    .let {
                        checkNotNull(it) { "Could not find api instance of ${clazz.canonicalName}!" }
                    }
            }
            return instanceCache
        }

    data object Engineering

    data object Material : HTApiHolder<HTMaterialsAPI>(HTModuleType.MATERIAL, HTMaterialsAPI::class.java)

    data object Storage

    data object Tool
}
