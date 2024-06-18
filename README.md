# HT Materials: Robustum Edition

![Requires Fabric API](https://i.imgur.com/Ol1Tcf8.png)
![Requires Fabric Language Kotlin](https://i.imgur.com/c1DH9VL.png)

This is a simple Minecraft mod for **Fabric+1.16.5** that provides _**Material System**_ and its usages

## Material System

The Material System was invented to handle Items and Tags more generally by decomposing them into `HTMaterial`
and `HTShape`.

### HTMaterial

- Represents the material of objects: _Iron_, _Gold_, _Copper_, _Stone_, _Wood_, ...
- Retrieved from `HTMaterialsAPI.INSTANCE.getMaterialRegistry().get(HTMaterialKey)`
  - Call `HTMaterial.empty(HTMaterialKey)` then returns empty material, which only has empty parameters (**Not Null !!**)
- Has several parameters
  - Composition: Provides its components, color, formula, and molar mass
  - Flags: Collection of flags
  - Properties: Be able to hold any objects, like Vanilla's `Component`
  - Type: Determines a model for material items and default shape of the material

![Material Info](image/material_info.png)

### HTShape

- Represents the shape of objects: _Ingot_, _Nugget_, _Plate_, _Gear_, _Rod_, ...
- Retrieved from `HTMaterialsAPI.INSTANCE.getShapeRegistry().get(String)` or call `new HTShape(String)`
- In contrast to `HTMaterial`, has only one parameter `name`
- Provides `Identifier` or `Tag<Item>` based on its name and given `HTMaterialKey`

![Material System](image/material_system.png)


## Material Contents

- Material System is used not only grouping existing items/blocks/fluids but also generate them

### Items

- Material Items are non-functional, but useful for recipe ingredients

![Material Contents](image/material_contents.png)

### Blocks

- There are two default types by default
  - Storage Block
    - Supports 2x2 and 3x3 format
  - Ore
    - No world generation
    - Drops `Raw XX Chunk` (Fortune and Silk Touch is valid)
      - 1x Metal `Raw Chunk` are smelt into 1x `XX Ingot` by `Furnace`
      - 1x Gem `Raw Chunk` are polished into 1x `XX Gem` by `Grinding Stone`
      - 1x Any `Raw Chunk` are pulverized into 2x `XX Dust` by mod compat

![Grinding Recipe](image/grinding_recipe.png)

### Fluids

- By default, Material Fluids are _Virtual_
  - Cannot place in world and has no buckets
  - Like `Forge Fluid`

## Item Unification

### Material Dictionary

- Crafted from 1x `Iron Ingot` and 1x `Book`
- Converts input item into others with the same `HTMaterial` and `HTShape`

![Material Dictionary](image/material_dictionary.png)

### Material Library

- Crafted from 6x `#minecraft:planks` and 3x `Material Dictionary`
- Converts inserted item by right-click or using `Hopper`/`Pipes`, then drops the result in front of bookshelf

![Material Library](image/material_library.png)

### _Experimental: Item Picking Conversion_ 

- Disabled by default; Enabled from config
- Converts ALL items player picked

## How to create Addon

1. Add new entrypoint `ht_materials` in `fabric.mod.json`
2. Implement [HTMaterialsPlugin](src/api/kotlin/io/github/hiiragi283/htms/api/HTMaterialsAPI.kt)

## Example

- [fabric.mod.json](src/test/resources/fabric.mod.json)
- [HTMaterialsTestPlugin](src/test/java/io/github/hiiragi283/htms/test/HTMaterialsTestPlugin.java)

## Credits

- @Ko_no ... Translator (since [Ragi Materials](https://github.com/Hiiragi283/RagiMaterials))
- @turtton ... Refined material system (since [Ragi Materials](https://github.com/Hiiragi283/RagiMaterials))
- @toliner ... Helped to import codes in [my repository](https://github.com/Hiiragi283/HT-Materials) to [Robustum's repository](https://github.com/Robustum/HT-Materials)
- Many people who helped me learn modding