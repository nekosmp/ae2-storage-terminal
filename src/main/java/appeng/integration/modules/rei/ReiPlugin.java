/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
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

package appeng.integration.modules.rei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;

import dev.architectury.event.CompoundEventResult;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;

import appeng.api.integrations.rei.IngredientConverters;
import appeng.api.util.AEColor;
import appeng.client.gui.AEBaseScreen;
import appeng.core.AEConfig;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.integration.abstraction.REIFacade;
import appeng.integration.modules.jeirei.CompatLayerHelper;
import appeng.integration.modules.rei.transfer.UseCraftingRecipeTransfer;
import appeng.menu.me.items.CraftingTermMenu;

public class ReiPlugin implements REIClientPlugin {

    // Will be hidden if developer items are disabled in the config
    private List<Predicate<ItemStack>> developerItems;
    // Will be hidden if colored cables are hidden
    private List<Predicate<ItemStack>> coloredCables;

    public ReiPlugin() {
        if (CompatLayerHelper.IS_LOADED) {
            return;
        }

        IngredientConverters.register(new ItemIngredientConverter());
        IngredientConverters.register(new FluidIngredientConverter());

        REIFacade.setInstance(new ReiRuntimeAdapter());
    }

    @Override
    public String getPluginProviderName() {
        return "AE2";
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        if (CompatLayerHelper.IS_LOADED) {
            return;
        }

        registerWorkingStations(registry);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (CompatLayerHelper.IS_LOADED) {
            return;
        }
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        if (CompatLayerHelper.IS_LOADED) {
            return;
        }

        // Allow recipe transfer from JEI to crafting and pattern terminal
        registry.register(new UseCraftingRecipeTransfer<>(CraftingTermMenu.class));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        if (CompatLayerHelper.IS_LOADED) {
            return;
        }

        registry.registerDraggableStackVisitor(new GhostIngredientHandler());
        registry.registerFocusedStack((screen, mouse) -> {
            if (screen instanceof AEBaseScreen<?>aeScreen) {
                var stack = aeScreen.getStackUnderMouse(mouse.x, mouse.y);
                if (stack != null) {
                    for (var converter : IngredientConverters.getConverters()) {
                        var entryStack = converter.getIngredientFromStack(stack);
                        if (entryStack != null) {
                            return CompoundEventResult.interruptTrue(entryStack);
                        }
                    }
                }
            }

            return CompoundEventResult.pass();
        });
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        // Will be hidden if developer items are disabled in the config
        developerItems = ImmutableList.of(
                AEBlocks.DEBUG_CUBE_GEN::isSameAs,
                AEBlocks.DEBUG_CHUNK_LOADER::isSameAs,
                AEBlocks.DEBUG_ENERGY_GEN::isSameAs,
                AEBlocks.DEBUG_ITEM_GEN::isSameAs,
                AEBlocks.DEBUG_PHANTOM_NODE::isSameAs,

                AEItems.DEBUG_CARD::isSameAs,
                AEItems.DEBUG_ERASER::isSameAs);

        // Will be hidden if colored cables are hidden
        List<Predicate<ItemStack>> predicates = new ArrayList<>();

        for (AEColor color : AEColor.values()) {
            if (color == AEColor.TRANSPARENT) {
                continue; // Keep the Fluix variant
            }
            predicates.add(stack -> stack.getItem() == AEParts.GLASS_CABLE.item(color));
        }
        coloredCables = ImmutableList.copyOf(predicates);

        registry.removeEntryIf(this::shouldEntryBeHidden);
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        if (CompatLayerHelper.IS_LOADED) {
            return;
        }

        zones.register(AEBaseScreen.class, screen -> {
            return screen != null ? mapRects(screen.getExclusionZones()) : Collections.emptyList();
        });

    }

    private static List<Rectangle> mapRects(List<Rect2i> exclusionZones) {
        return exclusionZones.stream()
                .map(ez -> new Rectangle(ez.getX(), ez.getY(), ez.getWidth(), ez.getHeight()))
                .collect(Collectors.toList());
    }

    private void registerWorkingStations(CategoryRegistry registry) {
        ItemStack craftingTerminal = AEParts.CRAFTING_TERMINAL.stack();
        registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(craftingTerminal));

        ItemStack wirelessCraftingTerminal = AEItems.WIRELESS_CRAFTING_TERMINAL.stack();
        registry.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(wirelessCraftingTerminal));
    }

    private boolean shouldEntryBeHidden(EntryStack<?> entryStack) {
        if (entryStack.getType() != VanillaEntryTypes.ITEM) {
            return false;
        }
        ItemStack stack = entryStack.castValue();

        if (AEItems.WRAPPED_GENERIC_STACK.isSameAs(stack)
                || AEBlocks.CABLE_BUS.isSameAs(stack)) {
            return true;
        }

        if (!AEConfig.instance().isDebugToolsEnabled()) {
            for (var developerItem : developerItems) {
                if (developerItem.test(stack)) {
                    return true;
                }
            }
        }

        if (AEConfig.instance().isDisableColoredCableRecipesInJEI()) {
            for (var predicate : coloredCables) {
                if (predicate.test(stack)) {
                    return true;
                }
            }
        }

        return false;
    }

}
