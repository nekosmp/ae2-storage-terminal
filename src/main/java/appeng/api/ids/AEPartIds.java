/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 TeamAppliedEnergistics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package appeng.api.ids;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.resources.ResourceLocation;

import appeng.api.util.AEColor;

/**
 * Contains {@link net.minecraft.world.item.Item} ids for various cable bus parts defined by AE2.
 */
@SuppressWarnings("unused")
public final class AEPartIds {

    ///
    /// CABLES
    ///

    public static final ResourceLocation CABLE_GLASS_WHITE = id("white_glass_cable");
    public static final ResourceLocation CABLE_GLASS_ORANGE = id("orange_glass_cable");
    public static final ResourceLocation CABLE_GLASS_MAGENTA = id("magenta_glass_cable");
    public static final ResourceLocation CABLE_GLASS_LIGHT_BLUE = id("light_blue_glass_cable");
    public static final ResourceLocation CABLE_GLASS_YELLOW = id("yellow_glass_cable");
    public static final ResourceLocation CABLE_GLASS_LIME = id("lime_glass_cable");
    public static final ResourceLocation CABLE_GLASS_PINK = id("pink_glass_cable");
    public static final ResourceLocation CABLE_GLASS_GRAY = id("gray_glass_cable");
    public static final ResourceLocation CABLE_GLASS_LIGHT_GRAY = id("light_gray_glass_cable");
    public static final ResourceLocation CABLE_GLASS_CYAN = id("cyan_glass_cable");
    public static final ResourceLocation CABLE_GLASS_PURPLE = id("purple_glass_cable");
    public static final ResourceLocation CABLE_GLASS_BLUE = id("blue_glass_cable");
    public static final ResourceLocation CABLE_GLASS_BROWN = id("brown_glass_cable");
    public static final ResourceLocation CABLE_GLASS_GREEN = id("green_glass_cable");
    public static final ResourceLocation CABLE_GLASS_RED = id("red_glass_cable");
    public static final ResourceLocation CABLE_GLASS_BLACK = id("black_glass_cable");
    public static final ResourceLocation CABLE_GLASS_TRANSPARENT = id("fluix_glass_cable");
    public static final Map<AEColor, ResourceLocation> CABLE_GLASS = ImmutableMap.<AEColor, ResourceLocation>builder()
            .put(AEColor.WHITE, CABLE_GLASS_WHITE)
            .put(AEColor.ORANGE, CABLE_GLASS_ORANGE)
            .put(AEColor.MAGENTA, CABLE_GLASS_MAGENTA)
            .put(AEColor.LIGHT_BLUE, CABLE_GLASS_LIGHT_BLUE)
            .put(AEColor.YELLOW, CABLE_GLASS_YELLOW)
            .put(AEColor.LIME, CABLE_GLASS_LIME)
            .put(AEColor.PINK, CABLE_GLASS_PINK)
            .put(AEColor.GRAY, CABLE_GLASS_GRAY)
            .put(AEColor.LIGHT_GRAY, CABLE_GLASS_LIGHT_GRAY)
            .put(AEColor.CYAN, CABLE_GLASS_CYAN)
            .put(AEColor.PURPLE, CABLE_GLASS_PURPLE)
            .put(AEColor.BLUE, CABLE_GLASS_BLUE)
            .put(AEColor.BROWN, CABLE_GLASS_BROWN)
            .put(AEColor.GREEN, CABLE_GLASS_GREEN)
            .put(AEColor.RED, CABLE_GLASS_RED)
            .put(AEColor.BLACK, CABLE_GLASS_BLACK)
            .put(AEColor.TRANSPARENT, CABLE_GLASS_TRANSPARENT)
            .build();
    public static final ResourceLocation STORAGE_BUS = id("storage_bus");
    public static final ResourceLocation TERMINAL = id("terminal");
    public static final ResourceLocation CRAFTING_TERMINAL = id("crafting_terminal");

    private static ResourceLocation id(String id) {
        return new ResourceLocation(AEConstants.MOD_ID, id);
    }
}
