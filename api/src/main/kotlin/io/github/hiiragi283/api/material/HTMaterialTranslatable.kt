package io.github.hiiragi283.api.material

import net.minecraft.text.TranslatableText

interface HTMaterialTranslatable {
    val translationKey: String

    fun getTranslatedName(materialKey: HTMaterialKey): String = getTranslatedText(materialKey).string

    fun getTranslatedText(materialKey: HTMaterialKey): TranslatableText = TranslatableText(translationKey, materialKey.translatedName)
}
