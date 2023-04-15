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

package appeng.core.sync;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.network.FriendlyByteBuf;

import appeng.core.sync.packets.BlockTransitionEffectPacket;
import appeng.core.sync.packets.ConfigButtonPacket;
import appeng.core.sync.packets.ConfigValuePacket;
import appeng.core.sync.packets.FillCraftingGridFromRecipePacket;
import appeng.core.sync.packets.GuiDataSyncPacket;
import appeng.core.sync.packets.HotkeyPacket;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.core.sync.packets.ItemTransitionEffectPacket;
import appeng.core.sync.packets.LightningPacket;
import appeng.core.sync.packets.MEInteractionPacket;
import appeng.core.sync.packets.MEInventoryUpdatePacket;
import appeng.core.sync.packets.MatterCannonPacket;
import appeng.core.sync.packets.MockExplosionPacket;
import appeng.core.sync.packets.MouseWheelPacket;
import appeng.core.sync.packets.PartLeftClickPacket;
import appeng.core.sync.packets.SpawnEntityPacket;
import appeng.core.sync.packets.SwapSlotsPacket;
import appeng.core.sync.packets.SwitchGuisPacket;

public class BasePacketHandler {
    private static final Map<Class<? extends BasePacket>, PacketTypes> REVERSE_LOOKUP = new HashMap<>();

    public enum PacketTypes {
        SPAWN_ENTITY(SpawnEntityPacket.class, SpawnEntityPacket::new),

        INVENTORY_ACTION(InventoryActionPacket.class, InventoryActionPacket::new),

        ME_INVENTORY_UPDATE(MEInventoryUpdatePacket.class, MEInventoryUpdatePacket::new),

        ME_INTERACTION(MEInteractionPacket.class, MEInteractionPacket::new),

        CONFIG_BUTTON(ConfigButtonPacket.class, ConfigButtonPacket::new),

        LIGHTNING(LightningPacket.class, LightningPacket::new),

        MATTER_CANNON(MatterCannonPacket.class, MatterCannonPacket::new),

        MOCK_EXPLOSION(MockExplosionPacket.class, MockExplosionPacket::new),

        VALUE_CONFIG(ConfigValuePacket.class, ConfigValuePacket::new),

        ITEM_TRANSITION_EFFECT(ItemTransitionEffectPacket.class, ItemTransitionEffectPacket::new),

        BLOCK_TRANSITION_EFFECT(BlockTransitionEffectPacket.class, BlockTransitionEffectPacket::new),

        GUI_DATA_SYNC(GuiDataSyncPacket.class, GuiDataSyncPacket::new),

        CLICK(PartLeftClickPacket.class, PartLeftClickPacket::new),

        SWITCH_GUIS(SwitchGuisPacket.class, SwitchGuisPacket::new),

        SWAP_SLOTS(SwapSlotsPacket.class, SwapSlotsPacket::new),

        FILL_CRAFTING_GRID_FROM_RECIPE(FillCraftingGridFromRecipePacket.class, FillCraftingGridFromRecipePacket::new),

        MOUSE_WHEEL(MouseWheelPacket.class, MouseWheelPacket::new),

        HOTKEY(HotkeyPacket.class, HotkeyPacket::new);

        private final Function<FriendlyByteBuf, BasePacket> factory;

        PacketTypes(Class<? extends BasePacket> packetClass, Function<FriendlyByteBuf, BasePacket> factory) {
            this.factory = factory;

            REVERSE_LOOKUP.put(packetClass, this);
        }

        public static PacketTypes getPacket(int id) {
            return values()[id];
        }

        public int getPacketId() {
            return ordinal();
        }

        static PacketTypes getID(Class<? extends BasePacket> c) {
            return REVERSE_LOOKUP.get(c);
        }

        public BasePacket parsePacket(FriendlyByteBuf in) throws IllegalArgumentException {
            return this.factory.apply(in);
        }
    }
}
