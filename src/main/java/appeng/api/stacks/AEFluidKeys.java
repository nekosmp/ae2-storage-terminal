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

package appeng.api.stacks;

import java.util.Objects;
import java.util.stream.Stream;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import appeng.core.AppEng;
import appeng.core.localization.GuiText;

final class AEFluidKeys extends AEKeyType {
    private static final ResourceLocation ID = AppEng.makeId("f");

    static final AEFluidKeys INSTANCE = new AEFluidKeys();

    private AEFluidKeys() {
        super(ID, AEFluidKey.class, GuiText.Fluids.text());
    }

    @Override
    public AEFluidKey readFromPacket(FriendlyByteBuf input) {
        Objects.requireNonNull(input);

        return AEFluidKey.fromPacket(input);
    }

    @Override
    public AEFluidKey loadKeyFromTag(CompoundTag tag) {
        return AEFluidKey.fromTag(tag);
    }

    @Override
    public int getAmountPerUnit() {
        return AEFluidKey.AMOUNT_BUCKET;
    }

    @Override
    public Stream<TagKey<?>> getTagNames() {
        return Registry.FLUID.getTagNames().map(t -> t);
    }

    @Override
    public String getUnitSymbol() {
        return "B";
    }
}
