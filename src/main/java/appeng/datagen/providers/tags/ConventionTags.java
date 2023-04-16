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

package appeng.datagen.providers.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Contains various tags:
 * <ul>
 * <li>Convention tags defined by the modding API for mod-compatibility purposes.</li>
 * <li>Tags defined by AE2 itself for recipe use.</li>
 * <li>Tags provided by AE2 for mod compatibility in the convention namespace.</li>
 * </ul>
 */
public final class ConventionTags {

    private ConventionTags() {
    }

    public static final TagKey<Item> GLASS_CABLE = tag("ae2:glass_cable");
    public static final TagKey<Item> WRENCH = tag("c:wrenches");

    private static TagKey<Item> tag(String name) {
        return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(name));
    }
}
