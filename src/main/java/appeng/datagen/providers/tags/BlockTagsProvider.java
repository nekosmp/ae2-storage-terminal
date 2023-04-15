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

package appeng.datagen.providers.tags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.BlockDefinition;
import appeng.datagen.providers.IAE2DataProvider;

public class BlockTagsProvider extends net.minecraft.data.tags.BlockTagsProvider implements IAE2DataProvider {
    public BlockTagsProvider(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void addTags() {

        // Only provide amethyst in the budding tag since that's the one we use; the other tags are for other mods
        tag(ConventionTags.BUDDING_BLOCKS_BLOCKS)
                .add(Blocks.BUDDING_AMETHYST);

        tag(ConventionTags.TERRACOTTA_BLOCK).add(
                Blocks.TERRACOTTA,
                Blocks.WHITE_TERRACOTTA,
                Blocks.ORANGE_TERRACOTTA,
                Blocks.MAGENTA_TERRACOTTA,
                Blocks.LIGHT_BLUE_TERRACOTTA,
                Blocks.YELLOW_TERRACOTTA,
                Blocks.LIME_TERRACOTTA,
                Blocks.PINK_TERRACOTTA,
                Blocks.GRAY_TERRACOTTA,
                Blocks.LIGHT_GRAY_TERRACOTTA,
                Blocks.CYAN_TERRACOTTA,
                Blocks.PURPLE_TERRACOTTA,
                Blocks.BLUE_TERRACOTTA,
                Blocks.BROWN_TERRACOTTA,
                Blocks.GREEN_TERRACOTTA,
                Blocks.RED_TERRACOTTA,
                Blocks.BLACK_TERRACOTTA);

        addEffectiveTools();

        addConventionTags();
    }

    private void addEffectiveTools() {
        Map<BlockDefinition<?>, List<TagKey<Block>>> specialTags = new HashMap<>();
        var defaultTags = List.of(BlockTags.MINEABLE_WITH_PICKAXE);

        for (var block : AEBlocks.getBlocks()) {
            for (var desiredTag : specialTags.getOrDefault(block, defaultTags)) {
                tag(desiredTag).add(block.block());
            }
        }

    }

    /**
     * Add convention tags that would normally be provided by the Platform but need to be added manually on Fabric.
     */
    private void addConventionTags() {
        tag(ConventionTags.STAINED_GLASS_BLOCK)
                .add(
                        Blocks.WHITE_STAINED_GLASS,
                        Blocks.ORANGE_STAINED_GLASS,
                        Blocks.MAGENTA_STAINED_GLASS,
                        Blocks.LIGHT_BLUE_STAINED_GLASS,
                        Blocks.YELLOW_STAINED_GLASS,
                        Blocks.LIME_STAINED_GLASS,
                        Blocks.PINK_STAINED_GLASS,
                        Blocks.GRAY_STAINED_GLASS,
                        Blocks.LIGHT_GRAY_STAINED_GLASS,
                        Blocks.CYAN_STAINED_GLASS,
                        Blocks.PURPLE_STAINED_GLASS,
                        Blocks.BLUE_STAINED_GLASS,
                        Blocks.BROWN_STAINED_GLASS,
                        Blocks.GREEN_STAINED_GLASS,
                        Blocks.RED_STAINED_GLASS,
                        Blocks.BLACK_STAINED_GLASS);
    }

    private TagsProvider.TagAppender<Block> tag(String name) {
        return tag(TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(name)));
    }
}
