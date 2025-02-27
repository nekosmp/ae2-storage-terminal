package appeng.integration.modules.igtooltip;

import net.minecraft.resources.ResourceLocation;

import appeng.core.AppEng;

public final class TooltipIds {
    private TooltipIds() {
    }

    public static final ResourceLocation GRID_NODE_STATE = AppEng.makeId("grid_node_state");
    public static final ResourceLocation CRAFTING_MONITOR = AppEng.makeId("crafting_monitor");
    public static final ResourceLocation PATTERN_PROVIDER = AppEng.makeId("pattern_provider");

    public static final ResourceLocation PART_NAME = AppEng.makeId("part_name");
    public static final ResourceLocation PART_ICON = AppEng.makeId("part_icon");
    public static final ResourceLocation PART_MOD_NAME = AppEng.makeId("part_mod_name");
    public static final ResourceLocation PART_TOOLTIP = AppEng.makeId("part_tooltip");
}
