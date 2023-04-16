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

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import appeng.api.util.AEColor;
import appeng.core.definitions.AEParts;
import appeng.datagen.providers.IAE2DataProvider;

public class ItemTagsProvider extends net.minecraft.data.tags.ItemTagsProvider implements IAE2DataProvider {

    public ItemTagsProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagsProvider) {
        super(dataGenerator, blockTagsProvider);
    }

    @Override
    protected void addTags() {
        for (AEColor color : AEColor.values()) {
            tag(ConventionTags.GLASS_CABLE).add(AEParts.GLASS_CABLE.item(color));
        }

        tag(ConventionTags.dye(DyeColor.WHITE)).add(Items.WHITE_DYE);
        tag(ConventionTags.dye(DyeColor.ORANGE)).add(Items.ORANGE_DYE);
        tag(ConventionTags.dye(DyeColor.MAGENTA)).add(Items.MAGENTA_DYE);
        tag(ConventionTags.dye(DyeColor.LIGHT_BLUE)).add(Items.LIGHT_BLUE_DYE);
        tag(ConventionTags.dye(DyeColor.YELLOW)).add(Items.YELLOW_DYE);
        tag(ConventionTags.dye(DyeColor.LIME)).add(Items.LIME_DYE);
        tag(ConventionTags.dye(DyeColor.PINK)).add(Items.PINK_DYE);
        tag(ConventionTags.dye(DyeColor.GRAY)).add(Items.GRAY_DYE);
        tag(ConventionTags.dye(DyeColor.LIGHT_GRAY)).add(Items.LIGHT_GRAY_DYE);
        tag(ConventionTags.dye(DyeColor.CYAN)).add(Items.CYAN_DYE);
        tag(ConventionTags.dye(DyeColor.PURPLE)).add(Items.PURPLE_DYE);
        tag(ConventionTags.dye(DyeColor.BLUE)).add(Items.BLUE_DYE);
        tag(ConventionTags.dye(DyeColor.BROWN)).add(Items.BROWN_DYE);
        tag(ConventionTags.dye(DyeColor.GREEN)).add(Items.GREEN_DYE);
        tag(ConventionTags.dye(DyeColor.RED)).add(Items.RED_DYE);
        tag(ConventionTags.dye(DyeColor.BLACK)).add(Items.BLACK_DYE);

        tag(ConventionTags.CAN_REMOVE_COLOR).add(Items.WATER_BUCKET, Items.SNOWBALL);
    }
}
