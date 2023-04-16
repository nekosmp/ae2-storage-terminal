package appeng.core.localization;

import java.util.Locale;

/**
 * Texts for the integrations with item-mods like REI or JEI.
 */
public enum ItemModText implements LocalizationEnum {
    // Recipe transfer handling
    MISSING_ID("Cannot identify recipe"),
    MISSING_ITEMS("Missing items will be skipped"),
    INCOMPATIBLE_RECIPE("Incompatible recipe"),
    NO_OUTPUT("Recipe has no output"),
    RECIPE_TOO_LARGE("Recipe larger than 3x3"),
    NO_ITEMS("Found no compatible items"),
    WILL_CRAFT("Will craft unavailable items"),
    CTRL_CLICK_TO_CRAFT("CTRL + click to craft unavailable items"),
    HAS_ENCODED_INGREDIENTS("Highlighted elements are already craftable"),
    MOVE_ITEMS("Move items"),

    RIGHT_CLICK("Right-Click"),
    SHIFT_RIGHT_CLICK("Shift+Right-Click"),
    CONSUMED("Consumed"),
    FLOWING_FLUID_NAME("%s (flowing)"),
    ;

    private final String englishText;

    ItemModText(String englishText) {
        this.englishText = englishText;
    }

    @Override
    public String getEnglishText() {
        return englishText;
    }

    @Override
    public String getTranslationKey() {
        return "ae2.rei_jei_integration." + name().toLowerCase(Locale.ROOT);
    }
}
