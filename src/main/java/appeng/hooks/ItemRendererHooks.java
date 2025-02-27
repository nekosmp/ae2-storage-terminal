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
package appeng.hooks;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

import appeng.api.client.AEStackRendering;
import appeng.api.stacks.AmountFormat;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.me.common.StackSizeRenderer;

public final class ItemRendererHooks {

    private ItemRendererHooks() {
    }

    /**
     * This hook will exchange the rendered item model for encoded patterns to the item being crafted by them if shift
     * is held.
     */
    public static boolean onRenderGuiItemModel(ItemRenderer renderer, ItemStack stack, int x, int y) {
        var minecraft = Minecraft.getInstance();
        var unwrapped = GenericStack.unwrapItemStack(stack);
        if (unwrapped != null) {
            AEStackRendering.drawInGui(
                    minecraft,
                    new PoseStack(),
                    x,
                    y,
                    (int) renderer.blitOffset,
                    unwrapped.what());

            if (unwrapped.amount() > 0) {
                String amtText = unwrapped.what().formatAmount(unwrapped.amount(), AmountFormat.SLOT);
                Font font = minecraft.font;
                StackSizeRenderer.renderSizeLabel(font, x, y, amtText, false);
            }

            return true;
        }

        return false;
    }
}
