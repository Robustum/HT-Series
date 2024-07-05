package io.github.hiiragi283.api.block

import io.github.hiiragi283.api.block.entity.HTMachineBlockEntity
import io.github.hiiragi283.api.machine.HTMachineType

class HTMachineBlock(settings: Settings, machineType: () -> HTMachineType<*>) :
    HTSimpleBlockWithEntity(settings, { _ -> HTMachineBlockEntity(machineType()) })
