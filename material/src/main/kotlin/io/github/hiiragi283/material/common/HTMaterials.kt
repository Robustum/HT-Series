package io.github.hiiragi283.material.common

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import io.github.hiiragi283.api.event.HTMaterialEvent
import io.github.hiiragi283.api.event.HTTagEvents
import io.github.hiiragi283.api.extension.*
import io.github.hiiragi283.api.fluid.phase.HTFluidPhase
import io.github.hiiragi283.api.fluid.phase.HTMaterialFluidManager
import io.github.hiiragi283.api.fluid.phase.HTPhasedMaterial
import io.github.hiiragi283.api.item.shape.*
import io.github.hiiragi283.api.material.HTMaterial
import io.github.hiiragi283.api.material.HTMaterialKey
import io.github.hiiragi283.api.material.HTMaterialRegistry
import io.github.hiiragi283.api.material.property.HTMaterialProperties
import io.github.hiiragi283.api.module.*
import io.github.hiiragi283.api.recipe.HTGrindingRecipe
import io.github.hiiragi283.api.resource.HTRuntimeDataRegistry
import io.github.hiiragi283.api.resource.recipe.HTShapedRecipeBuilder
import io.github.hiiragi283.api.resource.recipe.HTShapelessRecipeBuilder
import io.github.hiiragi283.material.common.block.HTMaterialLibraryBlock
import io.github.hiiragi283.material.common.item.MaterialDictionaryItem
import io.github.hiiragi283.material.common.screen.MaterialDictionaryScreenHandler
import io.github.hiiragi283.material.impl.HTMaterialsAPIImpl
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.Fluid
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.recipe.Ingredient
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.tag.ItemTags
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Rarity
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.registry.Registry
import net.minecraft.world.World
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.jvm.optionals.getOrNull

object HTMaterials : ModInitializer, DedicatedServerModInitializer {
    lateinit var screenHandlerType: ScreenHandlerType<MaterialDictionaryScreenHandler>
    //    Init    //

    override fun onInitialize() {
        HTLogger.log { it.info("=== List ===") }
        HTPluginHolder.Material.forEach { plugin ->
            HTLogger.log { logger -> logger.info("${plugin::class.qualifiedName} - Priority: ${plugin.priority}") }
        }
        HTLogger.log { it.info("============") }

        initEntries()
        registerEvents()

        registerShapes()
        registerMaterials()

        registerResources()

        reloadFluidMaterialManager(false)
        reloadItemMaterialManager(false)

        HTLogger.log { it.info("HT Materials Initialized!") }
    }

    private fun registerShapes() {
        val map: MutableMap<HTShapeKey, HTShape> = mutableMapOf()
        val builder = HTShapeRegistry.Builder(map)
        HTPluginHolder.Material.forEach { it.registerShape(builder) }
        HTMaterialsAPIImpl.shapeRegistry = HTShapeRegistry(map)
        HTLogger.log { it.info("HTShapeRegistry initialized!") }
    }

    private fun registerMaterials() {
        val materialMap: MutableMap<HTMaterialKey, MutableMap<TypedIdentifier<*>, Any>> = mutableMapOf()
        val builder = HTMaterialRegistry.Builder(materialMap)
        HTPluginHolder.Material.forEach { it.registerMaterial(builder) }

        HTMaterialsAPIImpl.materialRegistry = HTMaterialRegistry.create(materialMap)
        HTLogger.log { it.info("HTMaterialRegistry initialized!") }

        HTMaterialsAPIImpl.materialRegistry.blocks.forEach { (material: HTMaterial, shape: HTShape, block: Block) ->
            Registry.register(Registry.BLOCK, shape.getId(material), block)
        }
        HTMaterialsAPIImpl.materialRegistry.fluids.forEach { (material: HTMaterial, phase: HTFluidPhase, fluid: Fluid) ->
            Registry.register(Registry.FLUID, phase.getId(material), fluid)
        }
        HTMaterialsAPIImpl.materialRegistry.items.forEach { (material: HTMaterial, shape: HTShape, item: Item) ->
            Registry.register(Registry.ITEM, shape.getId(material), item)
        }
        HTLogger.log { it.info("Material Contents registered!") }
    }

    private fun initEntries() {
        // ItemGroup
        HTMaterialsAPIImpl.itemGroup = FabricItemGroupBuilder.build(
            HTModuleType.MATERIAL.id("material"),
        ) { HTMaterialsAPIImpl.iconItem.defaultStack }

        // Item
        HTMaterialsAPIImpl.iconItem = Registry.ITEM.register(
            HTMaterialsAPI.RegistryKeys.ICON,
            createItem { group(HTApiHolder.Material.apiInstance.itemGroup).rarity(Rarity.EPIC) },
        )
        HTMaterialsAPIImpl.dictionaryItem = Registry.ITEM.register(
            HTMaterialsAPI.RegistryKeys.DICTIONARY,
            MaterialDictionaryItem,
        )
        // Block
        HTMaterialsAPIImpl.libraryBlock = Registry.BLOCK.register(
            HTMaterialsAPI.RegistryKeys.LIBRARY,
            HTMaterialLibraryBlock,
        )
        Registry.register(
            Registry.ITEM,
            HTModuleType.MATERIAL.id("material_library"),
            createBlockItem(HTMaterialLibraryBlock) {
                group(HTApiHolder.Material.apiInstance.itemGroup).rarity(Rarity.EPIC)
            },
        )

        // ScreenHandlerType
        screenHandlerType = ScreenHandlerRegistry.registerSimple(
            HTModuleType.MATERIAL.id("material_dictionary"),
            ::MaterialDictionaryScreenHandler,
        )

        Registry.register(
            Registry.RECIPE_SERIALIZER,
            HTModuleType.MATERIAL.id("grinding"),
            HTGrindingRecipe.Serializer,
        )

        // Recipe Type
        Registry.register(
            Registry.RECIPE_TYPE,
            HTModuleType.MATERIAL.id("grinding"),
            HTGrindingRecipe.Type,
        )
    }

    private fun registerResources() {
        // Loot Tables
        HTRuntimeDataRegistry.addBlockLootTable(HTMaterialLibraryBlock)
        // Recipes
        HTRuntimeDataRegistry.addRecipe(
            HTShapelessRecipeBuilder()
                .inputs(2) {
                    set(0, Ingredient.ofItems(Items.BOOK))
                    set(1, Ingredient.ofItems(Items.IRON_INGOT))
                }
                .output { HTMaterialsAPIImpl.dictionaryItem.defaultStack }
                .build(HTModuleType.MATERIAL.id("material_dictionary")),
        )
        HTRuntimeDataRegistry.addRecipe(
            HTShapedRecipeBuilder()
                .inputs(3, 3) {
                    (0..2).forEach {
                        set(it, Ingredient.fromTag(ItemTags.PLANKS))
                    }
                    (3..5).forEach {
                        set(it, Ingredient.ofItems(HTMaterialsAPIImpl.dictionaryItem))
                    }
                    (6..8).forEach {
                        set(it, Ingredient.fromTag(ItemTags.PLANKS))
                    }
                }
                .output { HTMaterialsAPIImpl.libraryBlock.asItem().defaultStack }
                .build(HTModuleType.MATERIAL.id("material_library")),
        )
    }

    private fun registerEvents() {
        HTTagEvents.UPDATED.register { _, isClient: Boolean ->
            reloadFluidMaterialManager(isClient)
            reloadItemMaterialManager(isClient)
        }

        HTTagEvents.ITEM.register { handler ->
            HTApiHolder.Material.apiInstance.run {
                forEachDirectPart { (material: HTMaterial, shape: HTShape) ->
                    handler.getOrCreate(material, shape)
                }
                materialItemManager.forEach { item: Item, shapedMaterial: HTShapedMaterial.Lazy ->
                    handler.add(shapedMaterial.materialKey, shapedMaterial.shape, item)
                }
            }
        }

        HTMaterialEvent.MODIFY_BUILDER.register { builder ->
            if (HTMaterialProperties.blockContent(HTShapeKeys.BLOCK) !in builder) {
                builder.removeProperty(HTMaterialProperties.STORAGE_BLOCK_RECIPE)
            }
        }

        UseBlockCallback.EVENT.register { player: PlayerEntity, world: World, hand: Hand, result: BlockHitResult ->
            if (hand == Hand.OFF_HAND) return@register ActionResult.PASS
            if (!player.isSneaking) return@register ActionResult.PASS
            if (!world.getBlockState(result.blockPos).isOf(Blocks.GRINDSTONE)) return@register ActionResult.PASS
            val stack: ItemStack = player.getStackInHand(hand)
            val inventory = SimpleInventory(stack)
            val matchingRecipe: HTGrindingRecipe = world.recipeManager
                .getFirstMatch(HTGrindingRecipe.Type, inventory, world)
                .getOrNull()
                ?: return@register ActionResult.PASS
            dropStackAt(player, matchingRecipe.craft(inventory))
            player.unlockRecipes(listOf(matchingRecipe))
            stack.decrement(1)
            player.playSound(SoundEvents.BLOCK_GRINDSTONE_USE, SoundCategory.BLOCKS, 1.0f, 1.0f)
            ActionResult.SUCCESS
        }

        /*if (HTMaterialsAPI.CONFIG.experimental.enableExplosionInWater) {
            // Explodes items with HTExplosionProperty in water
            ServerTickEvents.START_WORLD_TICK.register { world ->
                for (entity: ItemEntity in world.getAllEntitiesByType(EntityType.ITEM, Entity::isSubmergedInWater)) {
                    val stack: ItemStack = entity.stack
                    val item: Item = stack.item
                    val part: HTPart.Typed<Item> = HTApiHolder.Material.apiInstance.partManager[item] ?: continue
                    val material: HTMaterial = part.materialOrNull ?: continue
                    val alkaliExplosionProperty: HTExplosionProperty =
                        material[HTMaterialProperties.EXPLOSION] ?: continue
                    val attacker: LivingEntity? = null
                    world.createExplosion(
                        entity,
                        DamageSource.explosion(attacker),
                        null,
                        entity.x,
                        entity.y,
                        entity.z,
                        alkaliExplosionProperty.power,
                        true,
                        Explosion.DestructionType.DESTROY,
                    )
                    entity.remove()
                }
            }
        }

        if (HTMaterialsAPI.CONFIG.experimental.enableItemPickingUnification) {
            // Unify drop item when player picked
            HTPlayerEvents.PICKUP.register(HTApiHolder.Material.apiInstance.partManager::unifyStack)
        }*/

        HTLogger.log { it.info("Events registered!") }
    }

    private fun reloadFluidMaterialManager(isClient: Boolean) {
        if (isClient) return
        // registration
        val entryMap: MutableMap<HTMaterialFluidManager.Entry, HTPhasedMaterial.Lazy> = mutableMapOf()
        val builder = HTMaterialFluidManager.Builder(entryMap)
        HTPluginHolder.Material.forEach { it.bindMaterialWithFluid(builder) }
        // reload
        val fluidToPhased: MutableMap<Fluid, HTPhasedMaterial.Lazy> = mutableMapOf()
        val phasedToFluids: Table<HTMaterialKey, HTFluidPhase, MutableSet<Fluid>> = HashBasedTable.create()
        val unificationBlacklist: MutableSet<Fluid> = hashSetOf()
        entryMap.forEach { (entry: HTMaterialFluidManager.Entry, shapedMaterial: HTPhasedMaterial.Lazy) ->
            val (materialKey: HTMaterialKey, phase: HTFluidPhase) = shapedMaterial
            entry.values.forEach { fluid: Fluid ->
                fluidToPhased[fluid] = shapedMaterial
                if (!phasedToFluids.contains(materialKey, phase)) {
                    phasedToFluids.put(materialKey, phase, mutableSetOf())
                }
                phasedToFluids.get(materialKey, phase).add(fluid)
                if (!entry.unification) unificationBlacklist.add(fluid)
            }
        }
        // initialization
        HTMaterialsAPIImpl.materialFluidManager = HTMaterialFluidManager(
            fluidToPhased,
            phasedToFluids,
        )
        HTLogger.log { it.info("HTMaterialFluidManager reloaded!") }
    }

    private fun reloadItemMaterialManager(isClient: Boolean) {
        if (isClient) return
        // registration
        val entryMap: MutableMap<HTMaterialItemManager.Entry, HTShapedMaterial.Lazy> = mutableMapOf()
        val builder = HTMaterialItemManager.Builder(entryMap)
        HTPluginHolder.Material.forEach { it.bindMaterialWithItem(builder) }
        // reload
        val itemToShaped: MutableMap<Item, HTShapedMaterial.Lazy> = mutableMapOf()
        val shapedToItems: Table<HTMaterialKey, HTShapeKey, MutableSet<Item>> = HashBasedTable.create()
        val unificationBlacklist: MutableSet<Item> = hashSetOf()
        entryMap.forEach { (entry: HTMaterialItemManager.Entry, shapedMaterial: HTShapedMaterial.Lazy) ->
            val (materialKey: HTMaterialKey, shapeKey: HTShapeKey) = shapedMaterial
            entry.values.forEach { item: Item ->
                itemToShaped[item] = shapedMaterial
                if (!shapedToItems.contains(materialKey, shapeKey)) {
                    shapedToItems.put(materialKey, shapeKey, mutableSetOf())
                }
                shapedToItems.get(materialKey, shapeKey).add(item)
                if (!entry.unification) unificationBlacklist.add(item)
            }
        }
        // initialization
        HTMaterialsAPIImpl.materialItemManager = HTMaterialItemManager(
            itemToShaped,
            shapedToItems,
            unificationBlacklist,
        )
        HTLogger.log { it.info("HTMaterialItemManager reloaded!") }
    }

    //    Post Init    //

    override fun onInitializeServer() {
        HTPluginHolder.Material.forEach {
            it.afterMaterialRegistration(HTApiHolder.Material.apiInstance, false)
        }
    }
}
