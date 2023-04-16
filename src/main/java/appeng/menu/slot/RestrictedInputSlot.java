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

package appeng.menu.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import appeng.api.features.GridLinkables;
import appeng.api.features.IGridLinkableHandler;
import appeng.api.implementations.items.IBiometricCard;
import appeng.api.implementations.items.IStorageComponent;
import appeng.api.inventories.InternalInventory;
import appeng.api.upgrades.Upgrades;
import appeng.client.gui.Icon;
import appeng.core.definitions.AEItems;

/**
 * @author AlgorithmX2
 * @author thatsIch
 * @version rv2
 * @since rv0
 */
public class RestrictedInputSlot extends AppEngSlot {

    private final PlacableItemType which;
    private boolean allowEdit = true;
    private int stackLimit = -1;

    public RestrictedInputSlot(PlacableItemType valid, InternalInventory inv, int invSlot) {
        super(inv, invSlot);
        this.which = valid;
        this.setIcon(valid.icon);
    }

    @Override
    public int getMaxStackSize() {
        if (this.stackLimit != -1) {
            return this.stackLimit;
        }
        return super.getMaxStackSize();
    }

    public Slot setStackLimit(int i) {
        this.stackLimit = i;
        return this;
    }

    private Level getLevel() {
        return getMenu().getPlayerInventory().player.getCommandSenderWorld();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (!this.getMenu().isValidForSlot(this, stack)) {
            return false;
        }

        if (stack.isEmpty()) {
            return false;
        }

        if (stack.getItem() == Items.AIR) {
            return false;
        }

        if (!super.mayPlace(stack)) {
            return false;
        }

        if (!this.isAllowEdit()) {
            return false;
        }

        // TODO: might need to check for our own patterns in some cases
        switch (this.which) {
            case STORAGE_COMPONENT:
                return stack.getItem() instanceof IStorageComponent
                        && ((IStorageComponent) stack.getItem()).isStorageComponent(stack);
            case GRID_LINKABLE_ITEM: {
                var handler = GridLinkables.get(stack.getItem());
                return handler != null && handler.canLink(stack);
            }
            case BIOMETRIC_CARD:
                return stack.getItem() instanceof IBiometricCard;
            case UPGRADES:
                return Upgrades.isUpgradeCardItem(stack);
            default:
                break;
        }

        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return this.isAllowEdit();
    }

    @Override
    public ItemStack getDisplayStack() {
        return super.getDisplayStack();
    }

    private boolean isAllowEdit() {
        return this.allowEdit;
    }

    public void setAllowEdit(boolean allowEdit) {
        this.allowEdit = allowEdit;
    }

    public enum PlacableItemType {
        STORAGE_CELLS(Icon.BACKGROUND_STORAGE_CELL),
        ORE(Icon.BACKGROUND_ORE),
        STORAGE_COMPONENT(Icon.BACKGROUND_STORAGE_COMPONENT),
        /**
         * Only allows items that have a registered {@link IGridLinkableHandler}.
         */
        GRID_LINKABLE_ITEM(Icon.BACKGROUND_WIRELESS_TERM),
        TRASH(Icon.BACKGROUND_TRASH),
        /**
         * Accepts {@link AEItems#CRAFTING_PATTERN}, {@link AEItems#PROCESSING_PATTERN},
         * {@link AEItems#SMITHING_TABLE_PATTERN} or {@link AEItems#STONECUTTING_PATTERN}.
         */
        ENCODED_AE_PATTERN(Icon.BACKGROUND_ENCODED_PATTERN),
        /**
         * Only accepts {@link AEItems#CRAFTING_PATTERN} and {@link AEItems#STONECUTTING_PATTERN}.
         */
        MOLECULAR_ASSEMBLER_PATTERN(Icon.BACKGROUND_ENCODED_PATTERN),
        /**
         * An encoded pattern from any mod (AE2 or otherwise). Delegates to AE2 API to identify such items.
         */
        POWERED_TOOL(Icon.BACKGROUND_CHARGABLE),
        SPATIAL_STORAGE_CELLS(Icon.BACKGROUND_SPATIAL_CELL),
        FUEL(Icon.BACKGROUND_FUEL),
        UPGRADES(Icon.BACKGROUND_UPGRADE),
        WORKBENCH_CELL(Icon.BACKGROUND_STORAGE_CELL),
        BIOMETRIC_CARD(Icon.BACKGROUND_BIOMETRIC_CARD),
        VIEW_CELL(Icon.BACKGROUND_VIEW_CELL),
        INSCRIBER_PLATE(Icon.BACKGROUND_PLATE),
        INSCRIBER_INPUT(Icon.BACKGROUND_INGOT),
        METAL_INGOTS(Icon.BACKGROUND_INGOT);

        public final Icon icon;

        PlacableItemType(Icon o) {
            this.icon = o;
        }
    }
}
