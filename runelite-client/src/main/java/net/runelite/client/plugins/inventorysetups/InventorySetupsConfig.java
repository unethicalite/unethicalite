/*
 * Copyright (c) 2019, dillydill123 <https://github.com/dillydill123>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.inventorysetups;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;

import static net.runelite.client.plugins.inventorysetups.InventorySetupsPlugin.CONFIG_KEY_COMPACT_MODE;
import static net.runelite.client.plugins.inventorysetups.InventorySetupsPlugin.CONFIG_KEY_HIDE_BUTTON;
import static net.runelite.client.plugins.inventorysetups.InventorySetupsPlugin.CONFIG_KEY_MANUAL_BANK_FILTER;
import static net.runelite.client.plugins.inventorysetups.InventorySetupsPlugin.CONFIG_KEY_SORTING_MODE;

@ConfigGroup(InventorySetupsPlugin.CONFIG_GROUP)
public interface InventorySetupsConfig extends Config
{

	@ConfigSection(
			name = "Default Options",
			description = "Default options for new setups",
			position = 0
	)
	String defaultSection = "defaultSection";

	@ConfigSection(
			name = "Hotkey Options",
			description = "Options for hot keys",
			position = 1
	)
	String hotkeySection = "hotkeysection";

	@ConfigItem(
			keyName = "bankFilter",
			name = "Default Filter Bank",
			description = "Configures the default setting for bank filtering in new setups",
			position = 1,
			section = defaultSection
	)
	default boolean bankFilter()
	{
		return false;
	}

	@ConfigItem(
			keyName = "highlightStackDifference",
			name = "Default Highlight Stack Difference",
			description = "Configures the default setting for highlighting stack differences in new setups",
			position = 1,
			hidden = true,
			section = defaultSection
	)
	default boolean highlightStackDifferenceOld()
	{
		return false;
	}

	@ConfigItem(
			keyName = "highlightStackDifferenceEnum",
			name = "Default Highlight Stack Difference",
			description = "Configures the default setting for highlighting stack differences in new setups",
			position = 1,
			hidden = true,
			section = defaultSection
	)
	default InventorySetupsStackCompareID highlightStackDifference()
	{
		return InventorySetupsStackCompareID.None;
	}

	@ConfigItem(
			keyName = "highlightVarianceDifference",
			name = "Default Highlight Variation Difference",
			description = "Configures the default setting for highlighting variations in new setups",
			position = 2,
			hidden = true,
			section = defaultSection
	)
	default boolean highlightVariationDifference()
	{
		return false;
	}

	@ConfigItem(
			keyName = "highlightUnorderedDifference",
			name = "Default Highlight Unordered Difference",
			description = "Configures the default setting for unordered highlighting in new setups",
			position = 2,
			section = defaultSection
	)
	default boolean highlightUnorderedDifference()
	{
		return false;
	}

	@ConfigItem(
			keyName = "highlightDifference",
			name = "Default Highlight",
			description = "Configures the default setting for highlighting differences in new setups",
			position = 4,
			section = defaultSection
	)
	default boolean highlightDifference()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
			keyName = "highlightColor",
			name = "Default Highlight Color",
			description = "Configures the default highlighting color in new setups",
			position = 5,
			section = defaultSection
	)
	default Color highlightColor()
	{
		return Color.RED;
	}

	@ConfigItem(
			keyName = "fuzzy",
			name = "Default Fuzzy",
			description = "Configures the default setting for fuzziness in new setups",
			position = 6,
			section = defaultSection
	)
	default boolean fuzzy()
	{
		return false;
	}

	@ConfigItem(
			keyName = "stackCompare",
			name = "Default Stack Compare",
			description = "Configures the default setting for stack compare in new setups",
			position = 7,
			section = defaultSection
	)
	default InventorySetupsStackCompareID stackCompareType()
	{
		return InventorySetupsStackCompareID.None;
	}

	@ConfigItem(
			keyName = "returnToSetupsHotkey",
			name = "Return To Setups Hotkey",
			description = "Configures the hotkey for returning to setups",
			position = 6,
			section = hotkeySection
	)
	default Keybind returnToSetupsHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "filterBankHotkey",
			name = "Filter Bank Hotkey",
			description = "Configures the hotkey for filtering all items in the bank",
			position = 7,
			section = hotkeySection
	)
	default Keybind filterBankHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "filterBankInventoryOnlyHotkey",
			name = "Filter Inventory Hotkey",
			description = "Configures the hotkey for filtering the inventory in the bank",
			position = 8,
			section = hotkeySection
	)
	default Keybind filterInventoryHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "filterBankEquipmentOnlyHotkey",
			name = "Filter Equipment Hotkey",
			description = "Configures the hotkey for filtering the equipment in the bank",
			position = 9,
			section = hotkeySection
	)
	default Keybind filterEquipmentHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "filterBankAddItemstOnlyHotkey",
			name = "Filter Additional Items Hotkey",
			description = "Configures the hotkey for filtering the additional items in the bank",
			position = 10,
			section = hotkeySection
	)
	default Keybind filterAddItemsHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = CONFIG_KEY_COMPACT_MODE,
			name = "Compact Mode",
			description = "Configures the setup panels to be compact",
			position = 11
	)
	default boolean compactMode()
	{
		return false;
	}

	@ConfigItem(
			keyName = CONFIG_KEY_SORTING_MODE,
			name = "Sorting Mode",
			description = "Configures the sorting of setups",
			position = 12
	)
	default InventorySetupsSortingID sortingMode()
	{
		return InventorySetupsSortingID.DEFAULT;
	}

	@ConfigItem(
			keyName = CONFIG_KEY_HIDE_BUTTON,
			name = "Hide Help Button",
			description = "Hide the help button",
			position = 13
	)
	default boolean hideButton()
	{
		return false;
	}

	@ConfigItem(
			keyName = "disableBankTabBar",
			name = "Disable Bank Tab Separator",
			description = "Stops the thin bank tab separator from removing the bank filter when clicked",
			position = 14
	)
	default boolean disableBankTabBar()
	{
		return false;
	}

	@ConfigItem(
			keyName = "removeBankTabSeparator",
			name = "Remove Bank Tab Separator",
			description = "Removes the thin bank tab separators from the bank filter",
			position = 15
	)
	default boolean removeBankTabSeparator()
	{
		return false;
	}

	@ConfigItem(
			keyName = "requireActivePanelFilter",
			name = "Require Active Panel for Filtering",
			description = "Only allow filtering if the Inventory Setups panel is active",
			position = 16
	)
	default boolean requireActivePanelFilter()
	{
		return false;
	}

	@ConfigItem(
			keyName = "showWornItemsFilter",
			name = "Show Worn Items Filter",
			description = "Determines which setups show up when right clicking the show worn items menu",
			position = 17
	)
	default InventorySetupsShowWornItemsFilterID showWornItemsFilter()
	{
		return InventorySetupsShowWornItemsFilterID.All;
	}

	@ConfigItem(
			keyName = CONFIG_KEY_MANUAL_BANK_FILTER,
			name = "Manual Bank Filter",
			description = "Disable automatic bank filtering when opening the bank",
			position = 18
	)
	default boolean manualBankFilter()
	{
		return false;
	}

}
