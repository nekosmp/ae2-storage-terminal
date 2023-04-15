package appeng.init;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

import team.reborn.energy.api.EnergyStorage;

import appeng.api.behaviors.GenericInternalInventory;
import appeng.api.inventories.PartApiLookup;
import appeng.blockentity.AEBaseInvBlockEntity;
import appeng.core.definitions.AEBlockEntities;
import appeng.helpers.externalstorage.GenericStackFluidStorage;
import appeng.helpers.externalstorage.GenericStackItemStorage;

public final class InitApiLookup {

    private InitApiLookup() {
    }

    public static void init() {

        // Allow forwarding of API lookups to parts for the cable bus
        PartApiLookup.addHostType(AEBlockEntities.CABLE_BUS);

        // Forward to interfaces
        initMisc();

        ItemStorage.SIDED.registerFallback((world, pos, state, blockEntity, direction) -> {
            if (blockEntity instanceof AEBaseInvBlockEntity baseInvBlockEntity) {
                return baseInvBlockEntity.getExposedInventoryForSide(direction).toStorage();
            }
            // Fall back to generic inv
            var genericInv = GenericInternalInventory.SIDED.find(world, pos, state, blockEntity, direction);
            if (genericInv != null) {
                return new GenericStackItemStorage(genericInv);
            }
            return null;
        });

        FluidStorage.SIDED.registerFallback((world, pos, state, blockEntity, direction) -> {
            // Fall back to generic inv
            var genericInv = GenericInternalInventory.SIDED.find(world, pos, state, blockEntity, direction);
            if (genericInv != null) {
                return new GenericStackFluidStorage(genericInv);
            }
            return null;
        });
    }

    private static void initMisc() {
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, context) -> {
            return blockEntity.getItemHandler();
        }, AEBlockEntities.DEBUG_ITEM_GEN);
        EnergyStorage.SIDED.registerSelf(AEBlockEntities.DEBUG_ENERGY_GEN);
    }
}
