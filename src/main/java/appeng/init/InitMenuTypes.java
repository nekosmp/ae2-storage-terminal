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

package appeng.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

import appeng.menu.implementations.PriorityMenu;
import appeng.menu.implementations.SecurityStationMenu;
import appeng.menu.implementations.StorageBusMenu;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.me.items.WirelessCraftingTermMenu;

public final class InitMenuTypes {
    private static final Map<ResourceLocation, MenuType<?>> REGISTRATION_QUEUE = new HashMap<>();

    private InitMenuTypes() {
    }

    public static void init(Registry<MenuType<?>> registry) {
        registerAll(registry,
                CraftingTermMenu.TYPE,
                MEStorageMenu.TYPE,
                MEStorageMenu.WIRELESS_TYPE,
                PriorityMenu.TYPE,
                SecurityStationMenu.TYPE,
                StorageBusMenu.TYPE,
                WirelessCraftingTermMenu.TYPE);
    }

    private static void registerAll(Registry<MenuType<?>> registry, MenuType<?>... types) {
        // Flush the registration queue. Calling the static ctor of each menu class will have
        // filled it.
        for (var entry : REGISTRATION_QUEUE.entrySet()) {
            Registry.register(registry, entry.getKey(), entry.getValue());
        }
        REGISTRATION_QUEUE.clear();

        // Fabric registers the container types at creation time, we just do this
        // to ensure all static CTORs are called in a predictable manner
        for (var type : types) {
            if (registry.getResourceKey(type).isEmpty()) {
                throw new IllegalStateException("Menu Type " + type + " is not registered");
            }
        }
    }

    public static void queueRegistration(ResourceLocation id, MenuType<?> menuType) {
        if (REGISTRATION_QUEUE.put(id, menuType) != null) {
            throw new IllegalStateException("Duplicate menu id: " + id);
        }
    }
}
