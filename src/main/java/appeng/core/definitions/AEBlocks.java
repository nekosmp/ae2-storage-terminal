/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2015, AlgorithmX2, All rights reserved.
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

import static appeng.block.AEBaseBlock.defaultProps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockBehaviour.StateArgumentPredicate;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import appeng.api.ids.AEBlockIds;
import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.block.misc.SecurityStationBlock;
import appeng.block.networking.CableBusBlock;
import appeng.block.networking.ControllerBlock;
import appeng.block.networking.WirelessBlock;
import appeng.core.AppEng;
import appeng.core.CreativeTab;

/**
 * Internal implementation for the API blocks
 */
@SuppressWarnings("unused")
public final class AEBlocks {

    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();
    
    public static final BlockDefinition<WirelessBlock> WIRELESS_ACCESS_POINT = block("ME Wireless Access Point", AEBlockIds.WIRELESS_ACCESS_POINT, WirelessBlock::new);
    public static final BlockDefinition<SecurityStationBlock> SECURITY_STATION = block("ME Security Terminal", AEBlockIds.SECURITY_STATION, SecurityStationBlock::new);
    public static final BlockDefinition<ControllerBlock> CONTROLLER = block("ME Controller", AEBlockIds.CONTROLLER, ControllerBlock::new);
    public static final BlockDefinition<CableBusBlock> CABLE_BUS = block("AE2 Cable and/or Bus", AEBlockIds.CABLE_BUS, CableBusBlock::new);


    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }

    private static <T extends Block> BlockDefinition<T> block(String englishName, ResourceLocation id,
            Supplier<T> blockSupplier) {
        return block(englishName, id, blockSupplier, null);
    }

    private static <T extends Block> BlockDefinition<T> block(
            String englishName,
            ResourceLocation id,
            Supplier<T> blockSupplier,
            @Nullable BiFunction<Block, Item.Properties, BlockItem> itemFactory) {

        // Create block and matching item, and set factory name of both
        T block = blockSupplier.get();

        Item.Properties itemProperties = new Item.Properties();
        itemProperties.tab(CreativeTab.INSTANCE);

        BlockItem item;
        if (itemFactory != null) {
            item = itemFactory.apply(block, itemProperties);
            if (item == null) {
                throw new IllegalArgumentException("BlockItem factory for " + id + " returned null");
            }
        } else if (block instanceof AEBaseBlock) {
            item = new AEBaseBlockItem(block, itemProperties);
        } else {
            item = new BlockItem(block, itemProperties);
        }

        BlockDefinition<T> definition = new BlockDefinition<>(englishName, id, block, item);
        CreativeTab.add(definition);

        BLOCKS.add(definition);

        return definition;

    }

    // Used to control in which order static constructors are called
    public static void init() {
    }

}
