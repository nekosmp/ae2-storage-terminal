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

package appeng.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.datagen.providers.localization.LocalizationProvider;
import appeng.datagen.providers.models.BlockModelProvider;
import appeng.datagen.providers.models.CableModelProvider;
import appeng.datagen.providers.models.ItemModelProvider;
import appeng.datagen.providers.models.PartModelProvider;
import appeng.datagen.providers.tags.BlockTagsProvider;
import appeng.datagen.providers.tags.ItemTagsProvider;

public class AE2DataGenerators {

    public static void onGatherData(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        var localization = new LocalizationProvider(generator);

        // Tags
        BlockTagsProvider blockTagsProvider = new BlockTagsProvider(generator);
        generator.addProvider(true, blockTagsProvider);
        generator.addProvider(true, new ItemTagsProvider(generator, blockTagsProvider));

        // Models
        generator.addProvider(true, new BlockModelProvider(generator, existingFileHelper));
        generator.addProvider(true, new ItemModelProvider(generator, existingFileHelper));
        generator.addProvider(true, new CableModelProvider(generator, existingFileHelper));
        generator.addProvider(true, new PartModelProvider(generator, existingFileHelper));

        // Must run last
        generator.addProvider(true, localization);
    }

}
