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

package appeng.init.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.BlockDefinition;

/**
 * Initializes which layers specific blocks render in.
 */
@Environment(EnvType.CLIENT)
public final class InitRenderTypes {

    /**
     * List of blocks that should render in the cutout layer.
     */
    private static final BlockDefinition<?>[] CUTOUT_BLOCKS = {
            AEBlocks.SECURITY_STATION,
            AEBlocks.WIRELESS_ACCESS_POINT,
    };

    private InitRenderTypes() {
    }

    public static void init() {
        for (var definition : CUTOUT_BLOCKS) {
            BlockRenderLayerMap.INSTANCE.putBlock(definition.block(), RenderType.cutout());
        }

        // Cable bus multiblock renders in all layers
        // TODO FABRIC 117 Fabric does not support rendering into multiple render layers simultaneously.
        BlockRenderLayerMap.INSTANCE.putBlock(AEBlocks.CABLE_BUS.block(), RenderType.cutout());
    }

}
