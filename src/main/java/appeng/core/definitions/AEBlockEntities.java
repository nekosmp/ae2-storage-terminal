/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2021, TeamAppliedEnergistics, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.core.definitions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraft.world.level.block.state.BlockState;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.blockentity.misc.SecurityStationBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.blockentity.networking.ControllerBlockEntity;
import appeng.blockentity.networking.WirelessBlockEntity;
import appeng.core.AppEng;

@SuppressWarnings("unused")
public final class AEBlockEntities {

    private static final Map<ResourceLocation, BlockEntityType<?>> BLOCK_ENTITY_TYPES = new HashMap<>();

    public static final BlockEntityType<WirelessBlockEntity> WIRELESS_ACCESS_POINT = create("wireless_access_point",
            WirelessBlockEntity.class, WirelessBlockEntity::new, AEBlocks.WIRELESS_ACCESS_POINT);
    public static final BlockEntityType<SecurityStationBlockEntity> SECURITY_STATION = create("security_station",
            SecurityStationBlockEntity.class, SecurityStationBlockEntity::new, AEBlocks.SECURITY_STATION);
    public static final BlockEntityType<CableBusBlockEntity> CABLE_BUS = create("cable_bus", CableBusBlockEntity.class,
            CableBusBlockEntity::new, AEBlocks.CABLE_BUS);
    public static final BlockEntityType<ControllerBlockEntity> CONTROLLER = create("controller",
            ControllerBlockEntity.class, ControllerBlockEntity::new, AEBlocks.CONTROLLER);

    public static Map<ResourceLocation, BlockEntityType<?>> getBlockEntityTypes() {
        return ImmutableMap.copyOf(BLOCK_ENTITY_TYPES);
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    private static <T extends AEBaseBlockEntity> BlockEntityType<T> create(String shortId,
            Class<T> entityClass,
            BlockEntityFactory<T> factory,
            BlockDefinition<? extends AEBaseEntityBlock<?>>... blockDefinitions) {
        Preconditions.checkArgument(blockDefinitions.length > 0);

        ResourceLocation id = AppEng.makeId(shortId);

        var blocks = Arrays.stream(blockDefinitions)
                .map(BlockDefinition::block)
                .toArray(AEBaseEntityBlock[]::new);

        AtomicReference<BlockEntityType<T>> typeHolder = new AtomicReference<>();
        BlockEntityType.BlockEntitySupplier<T> supplier = (blockPos, blockState) -> factory.create(typeHolder.get(),
                blockPos, blockState);
        var type = Builder.of(supplier, blocks).build(null);
        typeHolder.set(type); // Makes it available to the supplier used above
        BLOCK_ENTITY_TYPES.put(id, type);

        AEBaseBlockEntity.registerBlockEntityItem(type, blockDefinitions[0].asItem());

        // If the block entity classes implement specific interfaces, automatically register them
        // as tickers with the blocks that create that entity.
        BlockEntityTicker<T> serverTicker = null;
        if (ServerTickingBlockEntity.class.isAssignableFrom(entityClass)) {
            serverTicker = (level, pos, state, entity) -> {
                ((ServerTickingBlockEntity) entity).serverTick();
            };
        }
        BlockEntityTicker<T> clientTicker = null;
        if (ClientTickingBlockEntity.class.isAssignableFrom(entityClass)) {
            clientTicker = (level, pos, state, entity) -> {
                ((ClientTickingBlockEntity) entity).clientTick();
            };
        }

        for (var block : blocks) {
            AEBaseEntityBlock<T> baseBlock = (AEBaseEntityBlock<T>) block;
            baseBlock.setBlockEntity(entityClass, type, clientTicker, serverTicker);
        }

        return type;
    }

    @FunctionalInterface
    interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }

}
