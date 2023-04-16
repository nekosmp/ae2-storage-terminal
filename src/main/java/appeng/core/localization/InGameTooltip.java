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

package appeng.core.localization;

/**
 * Texts used for in-game tooltip mods like WAILA, TOP, Jade, WTHIT, etc.
 */
public enum InGameTooltip implements LocalizationEnum {
    Charged("%d%% charged"),
    Contains("Contains: %s"),
    Crafting("Crafting: %s"),
    DeviceOffline("Device Offline"),
    DeviceOnline("Device Online"),
    EnchantedWith("Enchanted with:"),
    ErrorControllerConflict("Error: Controller Conflict"),
    Locked("Locked"),
    NetworkBooting("Network Booting"),
    Showing("Showing"),
    Stored("Stored: %s / %s"),
    Unlocked("Unlocked");

    private final String englishText;

    InGameTooltip(String englishText) {
        this.englishText = englishText;
    }

    @Override
    public String getTranslationKey() {
        return "waila.ae2." + name();
    }

    @Override
    public String getEnglishText() {
        return englishText;
    }

}
