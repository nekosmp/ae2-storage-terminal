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

public enum PlayerMessages implements LocalizationEnum {
    ClickToShowDetails("Click to show details"),
    CommunicationError("Error Communicating with Network."),
    DeviceNotLinked("Device is not linked."),
    MissingBlankPatterns("Not enough blank pattern to restore patterns (missing %d)."),
    MissingUpgrades("Not enough %s to restore upgrades (missing %d)."),
    InvalidMachine("Could not restore configuration for an incompatible device."),
    InvalidMachinePartiallyRestored("Partially restored configuration for an incompatible device: %s."),
    LastTransition("Last Transition:"),
    LastTransitionUnknown("Last Transition unknown"),
    LoadedSettings("Loaded device configuration from memory card."),
    MachineNotPowered("Machine is not powered."),
    MinecraftProfile("Minecraft profile (%s)"),
    Origin("Origin"),
    OutOfRange("Wireless Out Of Range."),
    Owner("Owner"),
    PlayerConnected("%s [Connected]"),
    PlayerDisconnected("%s [Disconnected]"),
    RegionFile("Region file"),
    Size("Size"),
    Source("Source"),
    SourceLink("%s - %s to %s"),
    StationCanNotBeLocated("Station can not be located."),
    Unknown("Unknown"),
    UnknownAE2Player("Unknown AE2 Player (%s)"),
    When("When"),
    isNowLocked("Monitor is now Locked."),
    isNowUnlocked("Monitor is now Unlocked."),
    UnsupportedUpgrade("This upgrade is not supported by this machine."),
    MaxUpgradesOfTypeInstalled("No further upgrade cards of this type can be installed."),
    MaxUpgradesInstalled("The upgrade capacity of this machine has been reached."),
    UnknownHotkey("Unknown Hotkey: "),
    SpecialThanks("Special thanks to %s");

    private final String englishText;

    PlayerMessages(String englishText) {
        this.englishText = englishText;
    }

    @Override
    public String getEnglishText() {
        return englishText;
    }

    @Override
    public String getTranslationKey() {
        return "chat.ae2." + name();
    }
}
