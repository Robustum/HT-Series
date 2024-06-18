package io.github.hiiragi283.api.item.shape

class HTShapeRegistry(map: Map<HTShapeKey, HTShape> = mapOf()) : Map<HTShapeKey, HTShape> by map {
    class Builder(private val map: MutableMap<HTShapeKey, HTShape>) {
        @JvmOverloads
        fun add(key: HTShapeKey, idPath: String = "%s_${key.name}", tagPath: String = "${idPath}s") {
            key.validated = true
            map[key] = HTShape(key, idPath, tagPath)
        }
    }
}
