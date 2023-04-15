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

import static appeng.core.definitions.AEItems.item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.resources.ResourceLocation;

import appeng.api.ids.AEPartIds;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.api.util.AEColor;
import appeng.core.AppEng;
import appeng.items.parts.ColoredPartItem;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import appeng.parts.networking.GlassCablePart;
import appeng.parts.reporting.CraftingTerminalPart;
import appeng.parts.reporting.ItemTerminalPart;
import appeng.parts.storagebus.StorageBusPart;

/**
 * Internal implementation for the API parts
 */
@SuppressWarnings("unused")
public final class AEParts {
    public static final List<ColoredItemDefinition<?>> COLORED_PARTS = new ArrayList<>();

    public static final ColoredItemDefinition<ColoredPartItem<GlassCablePart>> GLASS_CABLE = constructColoredDefinition("ME Glass Cable", "glass_cable", GlassCablePart.class, GlassCablePart::new);
    public static final ItemDefinition<PartItem<StorageBusPart>> STORAGE_BUS = createPart("ME Storage Bus", AEPartIds.STORAGE_BUS, StorageBusPart.class, StorageBusPart::new);
    public static final ItemDefinition<PartItem<CraftingTerminalPart>> CRAFTING_TERMINAL = createPart("ME Crafting Terminal", AEPartIds.CRAFTING_TERMINAL, CraftingTerminalPart.class, CraftingTerminalPart::new);
    public static final ItemDefinition<PartItem<ItemTerminalPart>> TERMINAL = createPart("ME Terminal", AEPartIds.TERMINAL, ItemTerminalPart.class, ItemTerminalPart::new);

    private static <T extends IPart> ItemDefinition<PartItem<T>> createPart(
            String englishName,
            ResourceLocation id,
            Class<T> partClass,
            Function<IPartItem<T>, T> factory) {

        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return item(englishName, id, props -> new PartItem<>(props, partClass, factory));
    }

    private static <T extends IPart> ColoredItemDefinition<ColoredPartItem<T>> constructColoredDefinition(
            String nameSuffix,
            String idSuffix,
            Class<T> partClass,
            Function<ColoredPartItem<T>, T> factory) {

        PartModels.registerModels(PartModelsHelper.createModels(partClass));

        var definition = new ColoredItemDefinition<ColoredPartItem<T>>();
        for (AEColor color : AEColor.values()) {
            var id = color.registryPrefix + '_' + idSuffix;
            var name = color.englishName + " " + nameSuffix;

            var itemDef = item(name, AppEng.makeId(id),
                    props -> new ColoredPartItem<>(props, partClass, factory, color));

            definition.add(color, AppEng.makeId(id), itemDef);
        }

        COLORED_PARTS.add(definition);

        return definition;
    }

    // Used to control in which order static constructors are called
    public static void init() {
    }

}
