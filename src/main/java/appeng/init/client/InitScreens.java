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

import java.util.IdentityHashMap;
import java.util.Map;

import com.google.common.annotations.VisibleForTesting;

import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.PriorityScreen;
import appeng.client.gui.implementations.SecurityStationScreen;
import appeng.client.gui.implementations.StorageBusScreen;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.me.items.CraftingTermScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.style.StyleManager;
import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.PriorityMenu;
import appeng.menu.implementations.SecurityStationMenu;
import appeng.menu.implementations.StorageBusMenu;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.me.items.WirelessCraftingTermMenu;

/**
 * The server sends the client a menu identifier, which the client then maps onto a screen using {@link MenuScreens}.
 * This class registers our screens.
 */
public final class InitScreens {

    @VisibleForTesting
    static final Map<MenuType<?>, String> MENU_STYLES = new IdentityHashMap<>();

    private InitScreens() {
    }

    public static void init() {
        register(StorageBusMenu.TYPE, StorageBusScreen::new, "/screens/storage_bus.json");
        register(PriorityMenu.TYPE, PriorityScreen::new, "/screens/priority.json");

        // Terminals
        InitScreens.<MEStorageMenu, MEStorageScreen<MEStorageMenu>>register(
                MEStorageMenu.TYPE,
                MEStorageScreen::new,
                "/screens/terminals/terminal.json");            
        InitScreens.<MEStorageMenu, MEStorageScreen<MEStorageMenu>>register(
                MEStorageMenu.WIRELESS_TYPE,
                MEStorageScreen::new,
                "/screens/terminals/wireless_terminal.json");
        register(SecurityStationMenu.TYPE,
                SecurityStationScreen::new,
                "/screens/terminals/security_station.json");
        InitScreens.<CraftingTermMenu, CraftingTermScreen<CraftingTermMenu>>register(
                CraftingTermMenu.TYPE,
                CraftingTermScreen::new,
                "/screens/terminals/crafting_terminal.json");
        InitScreens.<WirelessCraftingTermMenu, CraftingTermScreen<WirelessCraftingTermMenu>>register(
                WirelessCraftingTermMenu.TYPE,
                CraftingTermScreen::new,
                "/screens/terminals/crafting_terminal.json");
    }

    /**
     * Registers a screen for a given menu and ensures the given style is applied after opening the screen.
     */
    public static <M extends AEBaseMenu, U extends AEBaseScreen<M>> void register(MenuType<M> type,
            StyledScreenFactory<M, U> factory,
            String stylePath) {
        MENU_STYLES.put(type, stylePath);
        ScreenRegistry.<M, U>register(type, (menu, playerInv, title) -> {
            var style = StyleManager.loadStyleDoc(stylePath);

            return factory.create(menu, playerInv, title, style);
        });
    }

    /**
     * A type definition that matches the constructors of our screens, which take an additional {@link ScreenStyle}
     * argument.
     */
    @FunctionalInterface
    public interface StyledScreenFactory<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
        U create(T t, Inventory pi, Component title, ScreenStyle style);
    }

}
