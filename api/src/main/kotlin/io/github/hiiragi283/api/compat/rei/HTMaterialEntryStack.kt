package io.github.hiiragi283.api.compat.rei

import io.github.hiiragi283.api.extension.HTColor
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialTooltipContext
import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.property.HTPropertyHolder
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.EntryStack
import me.shedaniel.rei.api.fractions.Fraction
import me.shedaniel.rei.api.widgets.Tooltip
import me.shedaniel.rei.impl.AbstractEntryStack
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.texture.Sprite
import net.minecraft.client.texture.SpriteAtlasTexture
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import net.minecraft.util.math.Matrix4f
import java.awt.Color
import java.util.*
import java.util.concurrent.ThreadLocalRandom

private val IGNORE_AMOUNT = Fraction.of(
    ThreadLocalRandom.current().nextLong(),
    ThreadLocalRandom.current().nextLong(Long.MAX_VALUE),
).simplify()

@Suppress("UnstableApiUsage")
data class HTMaterialEntryStack(
    private val materialKey: HTMaterialKey,
    private var amount: Fraction = IGNORE_AMOUNT,
) : AbstractEntryStack() {
    constructor(materialKey: HTMaterialKey, amount: Long) : this(materialKey, Fraction.ofWhole(amount))

    private val material: HTPropertyHolder by lazy(materialKey::get)

    private var isEmptyMaterial: Boolean = false

    override fun getIdentifier(): Optional<Identifier> = Optional.empty()

    override fun getType(): EntryStack.Type = EntryStack.Type.RENDER

    override fun getAccurateAmount(): Fraction = amount

    override fun setAmount(amount: Fraction) {
        this.amount = if (amount == IGNORE_AMOUNT) IGNORE_AMOUNT else max(amount, Fraction.empty())
        if (isEmpty) {
            isEmptyMaterial = true
        }
    }

    private fun max(f1: Fraction, f2: Fraction): Fraction = if (f1 > f2) f1 else f2

    override fun isEmpty(): Boolean = (amount != IGNORE_AMOUNT && !amount.isGreaterThan(Fraction.empty())) || isEmptyMaterial

    override fun copy(): EntryStack {
        val copy: HTMaterialEntryStack = copy(materialKey = materialKey, amount = amount)
        for (entry: Short2ObjectMap.Entry<Any> in settings.short2ObjectEntrySet()) {
            copy.setting(EntryStack.Settings.getById(entry.shortKey), entry.value)
        }
        return copy
    }

    override fun getObject(): Any = materialKey

    override fun equalsIgnoreTagsAndAmount(stack: EntryStack): Boolean = equalsIgnoreAmount(stack)

    override fun equalsIgnoreTags(stack: EntryStack): Boolean = equalsAll(stack)

    override fun equalsIgnoreAmount(stack: EntryStack): Boolean = if (stack.type != EntryStack.Type.RENDER) {
        false
    } else {
        (stack as? HTMaterialEntryStack)?.materialKey == materialKey
    }

    override fun equalsAll(stack: EntryStack): Boolean = if (stack.type != EntryStack.Type.RENDER) {
        false
    } else {
        (stack as? HTMaterialEntryStack)?.materialKey == materialKey &&
            (
                amount == IGNORE_AMOUNT ||
                    stack.accurateAmount == IGNORE_AMOUNT ||
                    amount == stack.accurateAmount
            )
    }

    override fun getTooltip(mouse: Point): Tooltip? = if (!get(EntryStack.Settings.TOOLTIP_ENABLED).get() || isEmpty) {
        null
    } else {
        Tooltip.create(
            buildList {
                add(asFormattedText())
                if (!amount.isLessThan(Fraction.empty()) && amount != IGNORE_AMOUNT) {
                    val amountTooltip: String =
                        get(EntryStack.Settings.Fluid.AMOUNT_TOOLTIP).apply(this@HTMaterialEntryStack)
                    addAll(amountTooltip.split("\n").map(::LiteralText))
                }
                if (MinecraftClient.getInstance().options.advancedItemTooltips) {
                    add(materialKey.translatedText.formatted(Formatting.DARK_GRAY))
                }
                addAll(get(EntryStack.Settings.TOOLTIP_APPEND_EXTRA).apply(this@HTMaterialEntryStack))
                HTMaterialTooltipContext(materialKey, material).appendTooltips(this)
            },
        )
    }

    @Suppress("DEPRECATION")
    override fun render(
        matrices: MatrixStack,
        bounds: Rectangle,
        mouseX: Int,
        mouseY: Int,
        delta: Float,
    ) {
        if (!get(EntryStack.Settings.RENDER).get()) return
        val sprite: Sprite = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
            .apply(Identifier("block/white_concrete"))
        val color: Color = material.getOrDefault(HTMaterialProperties.COLOR, HTColor.WHITE)
        val alpha = 255
        val red: Int = color.red
        val green: Int = color.green
        val blue: Int = color.blue
        MinecraftClient.getInstance().textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)
        val tessellator: Tessellator = Tessellator.getInstance()
        val buffer: BufferBuilder = tessellator.buffer
        val matrix: Matrix4f = matrices.peek().model
        buffer.begin(7, VertexFormats.POSITION_TEXTURE_COLOR)
        buffer.vertex(matrix, bounds.maxX.toFloat(), bounds.y.toFloat(), z.toFloat())
            .texture(sprite.maxU, sprite.minV)
            .color(red, green, blue, alpha).next()
        buffer.vertex(matrix, bounds.x.toFloat(), bounds.y.toFloat(), z.toFloat())
            .texture(sprite.minU, sprite.minV)
            .color(red, green, blue, alpha).next()
        buffer.vertex(matrix, bounds.x.toFloat(), bounds.maxY.toFloat(), z.toFloat())
            .texture(sprite.minU, sprite.maxV)
            .color(red, green, blue, alpha).next()
        buffer.vertex(matrix, bounds.maxX.toFloat(), bounds.maxY.toFloat(), z.toFloat())
            .texture(sprite.maxU, sprite.maxV)
            .color(red, green, blue, alpha).next()
        tessellator.draw()
    }

    override fun asFormattedText(): Text = materialKey.translatedText
}
