package io.github.hiiragi283.api.extension

import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import net.minecraft.entity.attribute.EntityAttribute
import net.minecraft.entity.attribute.EntityAttributeModifier

class EntityAttributeMapBuilder {
    companion object {
        @JvmStatic
        fun create(builderAction: EntityAttributeMapBuilder.() -> Unit): Multimap<EntityAttribute, EntityAttributeModifier> =
            EntityAttributeMapBuilder().apply(builderAction).build()
    }

    private val builder: ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> =
        ImmutableMultimap.builder()

    fun putAddition(attribute: EntityAttribute, name: String, value: Double): EntityAttributeMapBuilder = apply {
        builder.put(attribute, EntityAttributeModifier(name, value, EntityAttributeModifier.Operation.ADDITION))
    }

    fun putMultiplyBase(attribute: EntityAttribute, name: String, value: Double): EntityAttributeMapBuilder = apply {
        builder.put(attribute, EntityAttributeModifier(name, value, EntityAttributeModifier.Operation.MULTIPLY_BASE))
    }

    fun putMultiplyTotal(attribute: EntityAttribute, name: String, value: Double): EntityAttributeMapBuilder = apply {
        builder.put(attribute, EntityAttributeModifier(name, value, EntityAttributeModifier.Operation.MULTIPLY_TOTAL))
    }

    fun build(): Multimap<EntityAttribute, EntityAttributeModifier> = builder.build()
}
