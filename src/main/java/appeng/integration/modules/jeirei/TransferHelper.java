package appeng.integration.modules.jeirei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import appeng.core.localization.ItemModText;
import appeng.menu.me.items.CraftingTermMenu;

public class TransferHelper {
    // Colors for the slot highlights
    public static final int BLUE_SLOT_HIGHLIGHT_COLOR = 0x400000ff;
    public static final int RED_SLOT_HIGHLIGHT_COLOR = 0x66ff0000;
    // Colors for the buttons
    public static final int BLUE_PLUS_BUTTON_COLOR = 0x804545FF;
    public static final int ORANGE_PLUS_BUTTON_COLOR = 0x80FFA500;

    public static List<Component> createCraftingTooltip(CraftingTermMenu.MissingIngredientSlots missingSlots) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(ItemModText.MOVE_ITEMS.text());
        if (missingSlots.anyMissing()) {
            tooltip.add(ItemModText.MISSING_ITEMS.text().withStyle(ChatFormatting.RED));
        }
        return tooltip;
    }
}
