package io.github.hiiragi283.api.material

import net.minecraft.text.TranslatableText

interface HTMaterialTranslatable {
    val translationKey: String

    fun getTranslatedName(keyable: HTMaterialKeyable): String = getTranslatedText(keyable).string

    fun getTranslatedText(keyable: HTMaterialKeyable): TranslatableText =
        TranslatableText(translationKey, keyable.materialKey.translatedName)
}
