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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import appeng.api.ids.AEItemIds;
import appeng.api.stacks.AEKeyType;
import appeng.api.upgrades.Upgrades;
import appeng.api.util.AEColor;
import appeng.core.AEConfig;
import appeng.core.AppEng;
import appeng.core.CreativeTab;
import appeng.items.materials.MaterialItem;
import appeng.items.misc.WrappedGenericStack;
import appeng.items.tools.BiometricCardItem;
import appeng.items.tools.WirelessCraftingTerminalItem;
import appeng.items.tools.WirelessTerminalItem;
import appeng.menu.me.common.MEStorageMenu;

/**
 * Internal implementation for the API items
 */
@SuppressWarnings("unused")
public final class AEItems {

    // spotless:off
    private static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();
    
    public static final ItemDefinition<WirelessTerminalItem> WIRELESS_TERMINAL = item("Wireless Terminal", AEItemIds.WIRELESS_TERMINAL, p -> new WirelessTerminalItem(p.stacksTo(1)));
    public static final ItemDefinition<WirelessTerminalItem> WIRELESS_CRAFTING_TERMINAL = item("Wireless Crafting Terminal", AEItemIds.WIRELESS_CRAFTING_TERMINAL, p -> new WirelessCraftingTerminalItem(p.stacksTo(1)));
    public static final ItemDefinition<BiometricCardItem> BIOMETRIC_CARD = item("Biometric Card", AEItemIds.BIOMETRIC_CARD, p -> new BiometricCardItem(p.stacksTo(1)));
    public static final ItemDefinition<WrappedGenericStack> WRAPPED_GENERIC_STACK = item("Wrapped Generic Stack", AEItemIds.WRAPPED_GENERIC_STACK, WrappedGenericStack::new);
    public static final ItemDefinition<Item> INVERTER_CARD = item("Inverter Card", AEItemIds.INVERTER_CARD, Upgrades::createUpgradeCardItem);
    public static final ItemDefinition<Item> CAPACITY_CARD = item("Capacity Card", AEItemIds.CAPACITY_CARD, Upgrades::createUpgradeCardItem);
    public static final ItemDefinition<Item> FUZZY_CARD = item("Fuzzy Card", AEItemIds.FUZZY_CARD, Upgrades::createUpgradeCardItem);
    
    // spotless:on

    public static List<ItemDefinition<?>> getItems() {
        return Collections.unmodifiableList(ITEMS);
    }

    private static <T extends Item> ColoredItemDefinition<T> createColoredItems(String name,
            Map<AEColor, ResourceLocation> ids,
            BiFunction<FabricItemSettings, AEColor, T> factory) {
        var colors = new ColoredItemDefinition<T>();
        for (var entry : ids.entrySet()) {
            String fullName;
            if (entry.getKey() == AEColor.TRANSPARENT) {
                fullName = name;
            } else {
                fullName = entry.getKey().getEnglishName() + " " + name;
            }
            colors.add(entry.getKey(), entry.getValue(),
                    item(fullName, entry.getValue(), p -> factory.apply(p, entry.getKey())));
        }
        return colors;
    }

    static <T extends Item> ItemDefinition<T> item(String name, ResourceLocation id,
            Function<FabricItemSettings, T> factory) {
        return item(name, id, factory, CreativeTab.INSTANCE);
    }

    static <T extends Item> ItemDefinition<T> item(String name, ResourceLocation id,
            Function<FabricItemSettings, T> factory,
            CreativeModeTab group) {

        FabricItemSettings p = new FabricItemSettings().group(group);

        T item = factory.apply(p);

        ItemDefinition<T> definition = new ItemDefinition<>(name, id, item);

        if (group == CreativeTab.INSTANCE) {
            CreativeTab.add(definition);
        }

        ITEMS.add(definition);

        return definition;
    }

    // Used to control in which order static constructors are called
    public static void init() {
    }

}
