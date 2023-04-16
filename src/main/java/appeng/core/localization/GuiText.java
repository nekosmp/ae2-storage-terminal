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

package appeng.core.localization;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;

public enum GuiText implements LocalizationEnum {
    inventory(null, "container"), // mc's default Inventory localization.
    AdjacentToDifferentMachines("Adjacent to Different Machines"),
    And("and"),
    Or("or"),
    AttachedTo("Attached to: %s"),
    Automatic("Automatic"),
    Black("Black"),
    Blank("Blank"),
    Blue("Blue"),
    Brown("Brown"),
    BytesUsed("%s Bytes Used"),
    CanBeEnchanted("Can be enchanted"),
    Cancel("Cancel"),
    CantStoreItems("Can't Store Contents!"),
    Clean("Clean"),
    CompatibleUpgrade("%s (%s)"),
    CompatibleUpgrades("Compatible Upgrades:"),
    Config("Config"),
    CopyMode("Copy Mode"),
    CopyModeDesc("Controls if the contents of the configuration pane are cleared when you remove the cell."),
    CraftingLockIsLocked("Crafting is locked"),
    CraftingLockIsUnlocked("Crafting is unlocked"),
    CraftingTerminal("Crafting Terminal"),
    Crafts("Crafts"),
    Cyan("Cyan"),
    Deprecated("Deprecated"),
    Empty("Empty"),
    Encoded("Encoded"),
    Excluded("Excluded"),
    ExternalStorage("External Storage (%s)"),
    Fluids("Fluids"),
    Fluix("Fluix"),
    FormationPlane("Formation Plane"),
    FromStorage("Available: %s"),
    Fuzzy("Fuzzy"),
    RestoredGenericSettingUpgrades("upgrades"),
    RestoredGenericSettingSettings("settings"),
    RestoredGenericSettingConfigInv("config inventory"),
    RestoredGenericSettingPriority("priority"),
    Gray("Gray"),
    Green("Green"),
    Included("Included"),
    IncreasedEnergyUseFromEnchants("Enchants increase energy use"),
    Installed("Installed: %s"),
    IntrinsicEnchant("Always has at least %s"),
    InvalidNumber("Please enter a number or a mathematical expression e.g. : 3*4"),
    Items("Items"),
    // Used in a terminal to indicate that an item is craftable
    LargeFontCraft("+"),
    LevelEmitter("ME Level Emitter"),
    LightBlue("Light Blue"),
    LightGray("Light Gray"),
    Lime("Lime"),
    Linked("Linked"),
    Lumen("Lumen"),
    MENetworkStorage("ME Network Storage"),
    Magenta("Magenta"),
    Missing("Missing: %s"),
    MultipleOutputs("%1$d%% second, %2$d%% third output."),
    MysteriousQuote("\"Through others we become ourselves.\""),
    NetworkDetails("Network Details (%d Channels)"),
    NetworkTool("Network Tool"),
    Next("Next"),
    No("No"),
    NoPermissions("No Permissions Selected"),
    NoSecondOutput("No Secondary Output"),
    Nothing("Nothing"),
    NotSoMysteriousQuote("\"So far, no matter how close.\""),
    NumberGreaterThanMaxValue("Please enter a number less than or equal to %s"),
    NumberLessThanMinValue("Please enter a number greater than or equal to %s"),
    NumberNonInteger("Must be whole number"),
    OCTunnel("OpenComputers"),
    Of("of"),
    OfSecondOutput("%1$d%% Chance for second output."),
    Orange("Orange"),
    PartialPlan("Partial Plan (Missing Ingredients)"),
    Partitioned("Partitioned"),
    Pink("Pink"),
    Precise("Precise"),
    PressureTunnel("Pressure"),
    Priority("Priority"),
    PriorityExtractionHint("Extraction: Lower priority first"),
    PriorityInsertionHint("Insertion: Higher priority first"),
    Produces("Produces"),
    Purple("Purple"),
    Red("Red"),
    ReturnInventory("Return Inventory"),
    SCSInvalid("SCS Size: Invalid"),
    SCSSize("SCS Size: %sx%sx%s"),
    Scheduled("Scheduled: %s"),
    Security("Security Term"),
    SecurityCardEditor("Biometric Card Editor"),
    SelectAmount("Select Amount"),
    SelectedCraftingCPU("Crafting CPU: %s"),
    SerialNumber("Serial Number: %s"),
    Set("Set"),
    ShowingOf("Showing %d of %d"),
    // Used in a terminal to indicate that an item is craftable
    SmallFontCraft("Craft"),
    Start("Start"),
    StorageBus("Storage Bus"),
    StorageBusFluids("Fluid Storage Bus"),
    StorageCells("ME Storage Cells"),
    WirelessTerminals("Wireless Terminals"),
    SearchPlaceholder("Search..."),
    SearchSettingsTitle("Search Settings"),
    SearchSettingsUseInternalSearch("Use AE"),
    SearchSettingsUseExternalSearch("Use %s"),
    SearchSettingsSearchTooltips("Search in tooltips"),
    SearchSettingsRememberSearch("Remember last search"),
    SearchSettingsAutoFocus("Auto-Focus on open"),
    SearchSettingsSyncWithExternal("Sync with %s search"),
    SearchSettingsClearExternal("Clear %s search on open"),
    SearchSettingsReplaceWithExternal("Replace with %s search"),
    SearchTooltip("Search in Name"),
    SearchTooltipIncludingTooltips("Search in Name and Tooltip"),
    SearchTooltipModId("Use @ to search by mod (@ae2)"),
    SearchTooltipItemId("Use * to search by id (*cell)"),
    SearchTooltipTag("Use # to search by tag (#ores)"),
    StorageCellTooltipUpgrades("Upgrades:"),
    Stored("Stored"),
    StoredEnergy("Stored Energy"),
    StoredFluids("Stored Fluids"),
    StoredItems("Stored Items"),
    StoredSize("Stored Size: %dx%dx%d"),
    Stores("Stores"),
    Substitute("Using Substitutions:"),
    TankAmount("Amount: %d"),
    TankCapacity("Capacity: %d"),
    TankBucketCapacity("Can Store up to %d Buckets"),
    Terminal("Terminal"),
    TerminalSettingsTitle("Terminal Settings"),
    TerminalSettingsClearGridOnClose("Automatically clear terminal grid on close (if applicable)"),
    Types("Types"),
    Unattached("Unattached"),
    Unformatted("Unformatted"),
    Unlinked("Unlinked"),
    White("White"),
    Wireless("Wireless Access Point"),
    WirelessTerminal("Wireless Term"),
    With("with"),
    Yellow("Yellow"),
    Yes("Yes");

    private final String root;

    @Nullable
    private final String englishText;

    private final Component text;

    GuiText(@Nullable String englishText) {
        this.root = "gui.ae2";
        this.englishText = englishText;
        this.text = Component.translatable(getTranslationKey());
    }

    GuiText(@Nullable String englishText, String r) {
        this.root = r;
        this.englishText = englishText;
        this.text = Component.translatable(getTranslationKey());
    }

    @Nullable
    public String getEnglishText() {
        return englishText;
    }

    @Override
    public String getTranslationKey() {
        return this.root + '.' + name();
    }

    public String getLocal() {
        return text.getString();
    }
}
