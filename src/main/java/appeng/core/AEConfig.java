/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2015, AlgorithmX2, All rights reserved.
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

package appeng.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

import appeng.api.config.Settings;
import appeng.api.config.TerminalStyle;
import appeng.api.networking.pathing.ChannelMode;
import appeng.core.config.BooleanOption;
import appeng.core.config.ConfigFileManager;
import appeng.core.config.ConfigSection;
import appeng.core.config.ConfigValidationException;
import appeng.core.config.DoubleOption;
import appeng.core.config.EnumOption;
import appeng.core.config.IntegerOption;
import appeng.core.settings.TickRates;
import appeng.util.EnumCycler;

public final class AEConfig {

    public static final String CLIENT_CONFIG_PATH = "ae2/client.json";
    public static final String COMMON_CONFIG_PATH = "ae2/common.json";
    public final ClientConfig CLIENT;
    public final ConfigFileManager clientConfigManager;
    public final CommonConfig COMMON;
    public final ConfigFileManager commonConfigManager;

    AEConfig(Path configDir) {
        ConfigSection clientRoot = ConfigSection.createRoot();
        CLIENT = new ClientConfig(clientRoot);
        clientConfigManager = createConfigFileManager(clientRoot, configDir, CLIENT_CONFIG_PATH);

        ConfigSection commonRoot = ConfigSection.createRoot();
        COMMON = new CommonConfig(commonRoot);
        commonConfigManager = createConfigFileManager(commonRoot, configDir, COMMON_CONFIG_PATH);

        syncClientConfig();
        syncCommonConfig();
    }

    private static ConfigFileManager createConfigFileManager(ConfigSection commonRoot, Path configDir,
            String filename) {
        var configFile = configDir.resolve(filename);
        ConfigFileManager result = new ConfigFileManager(commonRoot, configFile);
        if (!Files.exists(configFile)) {
            result.save(); // Save a default file
        } else {
            try {
                result.load();
            } catch (ConfigValidationException e) {
                AELog.error("Failed to load AE2 Config. Making backup", e);

                // Backup and delete config files to reset them
                makeBackupAndReset(configDir, filename);
            }

            // Re-save immediately to write-out new defaults
            try {
                result.save();
            } catch (Exception e) {
                AELog.warn(e);
            }
        }
        return result;
    }

    // Default Energy Conversion Rates
    private static final double DEFAULT_TR_EXCHANGE = 2.0;

    // Config instance
    private static AEConfig instance;

    public static void load(Path configFolder) {
        if (instance != null) {
            throw new IllegalStateException("Config is already loaded");
        }
        instance = new AEConfig(configFolder);
    }

    private static void makeBackupAndReset(Path configFolder, String configFile) {
        var backupFile = configFolder.resolve(configFile + ".bak");
        var originalFile = configFolder.resolve(configFile);
        try {
            Files.move(originalFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            AELog.warn("Failed to backup config file %s: %s!", originalFile, e);
        }
    }

    // Misc
    private boolean enableEffects;
    private boolean useLargeFonts;
    private boolean useColoredCraftingStatus;
    private boolean disableColoredCableRecipesInJEI;

    private void syncClientConfig() {
        this.disableColoredCableRecipesInJEI = CLIENT.disableColoredCableRecipesInJEI.get();
        this.enableEffects = CLIENT.enableEffects.get();
        this.useLargeFonts = CLIENT.useLargeFonts.get();
        this.useColoredCraftingStatus = CLIENT.useColoredCraftingStatus.get();
    }

    private void syncCommonConfig() {
        for (TickRates tr : TickRates.values()) {
            tr.setMin(COMMON.tickRateMin.get(tr).get());
            tr.setMax(COMMON.tickRateMax.get(tr).get());
        }

        AELog.setCraftingLogEnabled(COMMON.craftingLog.get());
        AELog.setDebugLogEnabled(COMMON.debugLog.get());
        AELog.setGridLogEnabled(COMMON.gridLog.get());
    }

    public static AEConfig instance() {
        return instance;
    }

    public boolean isSearchTooltips() {
        return CLIENT.searchTooltips.get();
    }

    public void setSearchTooltips(boolean enable) {
        CLIENT.searchTooltips.set(enable);
    }

    public boolean isSearchModNameInTooltips() {
        return CLIENT.searchModNameInTooltips.get();
    }

    public void setSearchModNameInTooltips(boolean enable) {
        CLIENT.searchModNameInTooltips.set(enable);
    }

    public boolean isUseExternalSearch() {
        return CLIENT.useExternalSearch.get();
    }

    public void setUseExternalSearch(boolean enable) {
        CLIENT.useExternalSearch.set(enable);
    }

    public boolean isClearExternalSearchOnOpen() {
        return CLIENT.clearExternalSearchOnOpen.get();
    }

    public void setClearExternalSearchOnOpen(boolean enable) {
        CLIENT.clearExternalSearchOnOpen.set(enable);
    }

    public boolean isRememberLastSearch() {
        return CLIENT.rememberLastSearch.get();
    }

    public void setRememberLastSearch(boolean enable) {
        CLIENT.rememberLastSearch.set(enable);
    }

    public boolean isAutoFocusSearch() {
        return CLIENT.autoFocusSearch.get();
    }

    public void setAutoFocusSearch(boolean enable) {
        CLIENT.autoFocusSearch.set(enable);
    }

    public boolean isSyncWithExternalSearch() {
        return CLIENT.syncWithExternalSearch.get();
    }

    public void setSyncWithExternalSearch(boolean enable) {
        CLIENT.syncWithExternalSearch.set(enable);
    }

    public TerminalStyle getTerminalStyle() {
        return CLIENT.terminalStyle.get();
    }

    public void setTerminalStyle(TerminalStyle setting) {
        CLIENT.terminalStyle.set(setting);
    }

    public void save() {
    }

    public boolean isDebugToolsEnabled() {
        return COMMON.debugTools.get();
    }

    public boolean isEnableEffects() {
        return this.enableEffects;
    }

    public boolean isUseLargeFonts() {
        return this.useLargeFonts;
    }

    public boolean isUseColoredCraftingStatus() {
        return this.useColoredCraftingStatus;
    }

    public boolean isDisableColoredCableRecipesInJEI() {
        return this.disableColoredCableRecipesInJEI;
    }

    public boolean isShowDebugGuiOverlays() {
        return CLIENT.debugGuiOverlays.get();
    }

    public void setShowDebugGuiOverlays(boolean enable) {
        CLIENT.debugGuiOverlays.set(enable);
    }

    public boolean isSecurityAuditLogEnabled() {
        return COMMON.securityAuditLog.get();
    }

    public boolean isBlockUpdateLogEnabled() {
        return COMMON.blockUpdateLog.get();
    }

    public boolean isPacketLogEnabled() {
        return COMMON.packetLog.get();
    }

    public boolean isChunkLoggerTraceEnabled() {
        return COMMON.chunkLoggerTrace.get();
    }

    public boolean serverOpsIgnoreSecurity() {
        return COMMON.serverOpsIgnoreSecurity.get();
    }

    public ChannelMode getChannelMode() {
        return COMMON.channels.get();
    }

    public void setChannelModel(ChannelMode mode) {
        COMMON.channels.set(mode);
    }

    public int getPathfindingStepsPerTick() {
        return COMMON.pathfindingStepsPerTick.get();
    }

    /**
     * @return True if an in-world preview of parts and facade placement should be shown when holding one in hand.
     */
    public boolean isPlacementPreviewEnabled() {
        return CLIENT.showPlacementPreview.get();
    }

    // Tooltip settings

    /**
     * Show upgrade inventory in tooltips of storage cells and similar devices.
     */
    public boolean isTooltipShowCellUpgrades() {
        return CLIENT.tooltipShowCellUpgrades.get();
    }

    /**
     * Show part of the content in tooltips of storage cells and similar devices.
     */
    public boolean isTooltipShowCellContent() {
        return CLIENT.tooltipShowCellContent.get();
    }

    /**
     * How much of the content to show in storage cellls and similar devices.
     */
    public int getTooltipMaxCellContentShown() {
        return CLIENT.tooltipMaxCellContentShown.get();
    }

    public boolean isPinAutoCraftedItems() {
        return CLIENT.pinAutoCraftedItems.get();
    }

    public void setPinAutoCraftedItems(boolean enabled) {
        CLIENT.pinAutoCraftedItems.set(enabled);
    }

    public boolean isNotifyForFinishedCraftingJobs() {
        return CLIENT.notifyForFinishedCraftingJobs.get();
    }

    public void setNotifyForFinishedCraftingJobs(boolean enabled) {
        CLIENT.notifyForFinishedCraftingJobs.set(enabled);
    }

    public boolean isClearGridOnClose() {
        return CLIENT.clearGridOnClose.get();
    }

    public void setClearGridOnClose(boolean enabled) {
        CLIENT.clearGridOnClose.set(enabled);
    }

    public int getTerminalMargin() {
        return CLIENT.terminalMargin.get();
    }

    // Setters keep visibility as low as possible.

    private static class ClientConfig {

        // Misc
        public final BooleanOption enableEffects;
        public final BooleanOption useLargeFonts;
        public final BooleanOption useColoredCraftingStatus;
        public final BooleanOption disableColoredCableRecipesInJEI;
        public final BooleanOption debugGuiOverlays;
        public final BooleanOption showPlacementPreview;
        public final BooleanOption notifyForFinishedCraftingJobs;

        // Terminal Settings
        public final EnumOption<TerminalStyle> terminalStyle;
        public final BooleanOption pinAutoCraftedItems;
        public final BooleanOption clearGridOnClose;
        public final IntegerOption terminalMargin;

        // Search Settings
        public final BooleanOption searchTooltips;
        public final BooleanOption searchModNameInTooltips;
        public final BooleanOption useExternalSearch;
        public final BooleanOption clearExternalSearchOnOpen;
        public final BooleanOption syncWithExternalSearch;
        public final BooleanOption rememberLastSearch;
        public final BooleanOption autoFocusSearch;

        // Tooltip settings
        public final BooleanOption tooltipShowCellUpgrades;
        public final BooleanOption tooltipShowCellContent;
        public final IntegerOption tooltipMaxCellContentShown;

        public ClientConfig(ConfigSection root) {
            var client = root.subsection("client");
            this.disableColoredCableRecipesInJEI = client.addBoolean("disableColoredCableRecipesInJEI", true);
            this.enableEffects = client.addBoolean("enableEffects", true);
            this.useLargeFonts = client.addBoolean("useTerminalUseLargeFont", false);
            this.useColoredCraftingStatus = client.addBoolean("useColoredCraftingStatus", true);
            this.debugGuiOverlays = client.addBoolean("showDebugGuiOverlays", false, "Show debugging GUI overlays");
            this.showPlacementPreview = client.addBoolean("showPlacementPreview", true,
                    "Show a preview of part and facade placement");
            this.notifyForFinishedCraftingJobs = client.addBoolean("notifyForFinishedCraftingJobs", true,
                    "Show toast when long-running crafting jobs finish.");

            var terminals = root.subsection("terminals");
            this.terminalStyle = terminals.addEnum("terminalStyle", TerminalStyle.SMALL);
            this.pinAutoCraftedItems = terminals.addBoolean("pinAutoCraftedItems", true,
                    "Pin items that the player auto-crafts to the top of the terminal");
            this.clearGridOnClose = client.addBoolean("clearGridOnClose", false,
                    "Automatically clear the crafting/encoding grid when closing the terminal");
            this.terminalMargin = client.addInt("terminalMargin", 25,
                    "The vertical margin to apply when sizing terminals. Used to make room for centered item mod search bars");

            // Search Settings
            var search = root.subsection("search");
            this.searchTooltips = search.addBoolean("searchTooltips", true,
                    "Should tooltips be searched. Performance impact");
            this.searchModNameInTooltips = search.addBoolean("searchModNameInTooltips", false,
                    "Should the mod name be included when searching in tooltips.");
            this.useExternalSearch = search.addBoolean("useExternalSearch", false,
                    "Replaces AEs own search with the search of REI or JEI");
            this.clearExternalSearchOnOpen = search.addBoolean("clearExternalSearchOnOpen", true,
                    "When using useExternalSearch, clears the search when the terminal opens");
            this.syncWithExternalSearch = search.addBoolean("syncWithExternalSearch", true,
                    "When REI/JEI is installed, automatically set the AE or REI/JEI search text when either is changed while the terminal is open");
            this.rememberLastSearch = search.addBoolean("rememberLastSearch", true,
                    "Remembers the last search term and restores it when the terminal opens");
            this.autoFocusSearch = search.addBoolean("autoFocusSearch", false,
                    "Automatically focuses the search field when the terminal opens");

            var tooltips = root.subsection("tooltips");
            this.tooltipShowCellUpgrades = tooltips.addBoolean("showCellUpgrades", true,
                    "Show installed upgrades in the tooltips of storage cells, color applicators and matter cannons");
            this.tooltipShowCellContent = tooltips.addBoolean("showCellContent", true,
                    "Show a preview of the content in the tooltips of storage cells, color applicators and matter cannons");
            this.tooltipMaxCellContentShown = tooltips.addInt("maxCellContentShown", 5, 1, 32,
                    "The maximum number of content entries to show in the tooltip of storage cells, color applicators and matter cannons");
        }

    }

    private static class CommonConfig {

        // Misc
        public final BooleanOption debugTools;
        public final BooleanOption serverOpsIgnoreSecurity;
        public final EnumOption<ChannelMode> channels;
        public final IntegerOption pathfindingStepsPerTick;

        // Logging
        public final BooleanOption securityAuditLog;
        public final BooleanOption blockUpdateLog;
        public final BooleanOption packetLog;
        public final BooleanOption craftingLog;
        public final BooleanOption debugLog;
        public final BooleanOption gridLog;
        public final BooleanOption chunkLoggerTrace;

        public final Map<TickRates, IntegerOption> tickRateMin = new HashMap<>();
        public final Map<TickRates, IntegerOption> tickRateMax = new HashMap<>();

        public CommonConfig(ConfigSection root) {

            ConfigSection general = root.subsection("general");
            debugTools = general.addBoolean("unsupportedDeveloperTools", false);
            serverOpsIgnoreSecurity = general.addBoolean("serverOpsIgnoreSecurity", true,
                    "Server operators are not restricted by ME security terminal settings.");
            channels = general.addEnum("channels", ChannelMode.DEFAULT,
                    "Changes the channel capacity that cables provide in AE2.");
            pathfindingStepsPerTick = general.addInt("pathfindingStepsPerTick", 4,
                    1, 1024,
                    "The number of pathfinding steps that are taken per tick and per grid that is booting. Lower numbers will mean booting takes longer, but less work is done per tick.");

            var logging = root.subsection("logging");
            securityAuditLog = logging.addBoolean("securityAuditLog", false);
            blockUpdateLog = logging.addBoolean("blockUpdateLog", false);
            packetLog = logging.addBoolean("packetLog", false);
            craftingLog = logging.addBoolean("craftingLog", false);
            debugLog = logging.addBoolean("debugLog", false);
            gridLog = logging.addBoolean("gridLog", false);
            chunkLoggerTrace = logging.addBoolean("chunkLoggerTrace", false,
                    "Enable stack trace logging for the chunk loading debug command");

            ConfigSection tickrates = root.subsection("tickRates",
                    " Min / Max Tickrates for dynamic ticking, most of these components also use sleeping, to prevent constant ticking, adjust with care, non standard rates are not supported or tested.");
            for (TickRates tickRate : TickRates.values()) {
                tickRateMin.put(tickRate, tickrates.addInt(tickRate.name() + "Min", tickRate.getDefaultMin()));
                tickRateMax.put(tickRate, tickrates.addInt(tickRate.name() + "Max", tickRate.getDefaultMax()));
            }
        }

    }

}
