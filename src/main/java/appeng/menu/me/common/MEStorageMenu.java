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

package appeng.menu.me.common;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import appeng.api.config.Actionable;
import appeng.api.config.Setting;
import appeng.api.config.Settings;
import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.implementations.menuobjects.IPortableTerminal;
import appeng.api.networking.IGridNode;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.ConfigValuePacket;
import appeng.core.sync.packets.MEInteractionPacket;
import appeng.core.sync.packets.MEInventoryUpdatePacket;
import appeng.helpers.InventoryAction;
import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.interaction.StackInteractions;
import appeng.util.ConfigManager;
import appeng.util.IConfigManagerListener;
import appeng.util.Platform;

/**
 * @see MEStorageScreen
 */
public class MEStorageMenu extends AEBaseMenu
        implements IConfigManagerListener, IConfigurableObject, IMEInteractionHandler {

    public static final MenuType<MEStorageMenu> TYPE = MenuTypeBuilder
            .<MEStorageMenu, ITerminalHost>create(MEStorageMenu::new, ITerminalHost.class)
            .build("item_terminal");

    public static final MenuType<MEStorageMenu> WIRELESS_TYPE = MenuTypeBuilder
            .<MEStorageMenu, IPortableTerminal>create(MEStorageMenu::new, IPortableTerminal.class)
            .build("wirelessterm");

    private final IConfigManager clientCM;
    private final ITerminalHost host;
    /**
     * The number of active crafting jobs in the network. -1 means unknown and will hide the label on the screen.
     */
    @GuiSync(100)
    public int activeCraftingJobs = -1;

    private IConfigManagerListener gui;
    private IConfigManager serverCM;

    // This is null on the client-side and can be null on the server too
    @Nullable
    protected final MEStorage storage;

    private final IncrementalUpdateHelper updateHelper = new IncrementalUpdateHelper();

    /**
     * A grid connection is optional for a screen showing the content of a {@link MEStorage}, because inventories like
     * portable cells are not grid connected.
     */
    @Nullable
    private IGridNode networkNode;

    /**
     * The repository of entries currently known on the client-side. This is maintained by the screen associated with
     * this menu and will only be non-null on the client-side.
     */
    @Nullable
    private IClientRepo clientRepo;

    /**
     * The last set of craftables sent to the client.
     */
    private KeyCounter previousAvailableStacks = new KeyCounter();

    public MEStorageMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        this(menuType, id, ip, host, true);
    }

    protected MEStorageMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host, boolean bindInventory) {
        super(menuType, id, ip, host);

        this.host = host;
        this.clientCM = new ConfigManager(this);

        this.clientCM.registerSetting(Settings.SORT_BY, SortOrder.NAME);
        this.clientCM.registerSetting(Settings.SORT_DIRECTION, SortDir.ASCENDING);

        if (isServerSide()) {
            this.serverCM = host.getConfigManager();

            this.storage = host.getInventory();
            if (this.storage == null) {
                this.setValidMenu(false);
            }
        } else {
            this.storage = null;
        }

        setupUpgrades(host.getUpgrades());

        if (bindInventory) {
            this.createPlayerInventorySlots(ip);
        }
    }

    @Nullable
    public IGridNode getNetworkNode() {
        return this.networkNode;
    }

    public boolean isKeyVisible(AEKey key) {
        return true;
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            // Close the screen if the backing network inventory has changed
            if (this.storage != this.host.getInventory()) {
                this.setValidMenu(false);
                return;
            }

            for (var set : this.serverCM.getSettings()) {
                var sideLocal = this.serverCM.getSetting(set);
                var sideRemote = this.clientCM.getSetting(set);

                if (sideLocal != sideRemote) {
                    set.copy(serverCM, clientCM);
                    sendPacketToClient(new ConfigValuePacket(set, serverCM));
                }
            }

            var availableStacks = storage == null ? new KeyCounter() : storage.getAvailableStacks();

            // This is currently not supported/backed by any network service
            var requestables = new KeyCounter();

            try {
                // Available changes
                previousAvailableStacks.removeAll(availableStacks);
                previousAvailableStacks.removeZeros();
                previousAvailableStacks.keySet().forEach(updateHelper::addChange);

                if (updateHelper.hasChanges()) {
                    var builder = MEInventoryUpdatePacket
                            .builder(containerId, updateHelper.isFullUpdate());
                    builder.setFilter(this::isKeyVisible);
                    builder.addChanges(updateHelper, availableStacks, requestables);
                    builder.buildAndSend(this::sendPacketToClient);
                    updateHelper.commitChanges();
                }

            } catch (Exception e) {
                AELog.warn(e, "Failed to send incremental inventory update to client");
            }
            previousAvailableStacks = availableStacks;

            super.broadcastChanges();
        }

    }

    protected boolean showsCraftables() {
        return true;
    }

    @Override
    public void onServerDataSync() {
        super.onServerDataSync();
    }

    @Override
    public void onSettingChanged(IConfigManager manager, Setting<?> setting) {
        if (this.getGui() != null) {
            this.getGui().onSettingChanged(manager, setting);
        }
    }

    @Override
    public IConfigManager getConfigManager() {
        if (isServerSide()) {
            return this.serverCM;
        }
        return this.clientCM;
    }

    /**
     * Checks that the inventory monitor is connected, a power source exists and that it is powered.
     */
    protected final boolean canInteractWithGrid() {
        return this.storage != null;
    }

    @Override
    public final void handleInteraction(long serial, InventoryAction action) {
        if (isClientSide()) {
            NetworkHandler.instance().sendToServer(new MEInteractionPacket(containerId, serial, action));
            return;
        }

        // Do not allow interactions if there's no monitor or no power
        if (!canInteractWithGrid()) {
            return;
        }

        ServerPlayer player = (ServerPlayer) this.getPlayerInventory().player;

        // Serial -1 is used to target empty virtual slots, which only allows the player to put
        // items under their cursor into the network inventory
        if (serial == -1) {
            handleNetworkInteraction(player, null, action);
            return;
        }

        AEKey stack = getStackBySerial(serial);
        if (stack == null) {
            // This can happen if the client sent the request after we removed the item, but before
            // the client knows about it (-> network delay).
            return;
        }

        handleNetworkInteraction(player, stack, action);
    }

    protected void handleNetworkInteraction(ServerPlayer player, @Nullable AEKey clickedKey, InventoryAction action) {

        // Interacting with the network is not possible if there's no network.
        if (this.storage == null) {
            return;
        }

        if (action == InventoryAction.PICKUP_OR_SET_DOWN && StackInteractions.isKeySupported(clickedKey)) {
            action = InventoryAction.FILL_ITEM;
        }

        if (action == InventoryAction.SPLIT_OR_PLACE_SINGLE) {
            if (StackInteractions.getContainedStack(getCarried()) != null) {
                action = InventoryAction.EMPTY_ITEM;
            }
        }

        if (action == InventoryAction.FILL_ITEM) {
            tryFillContainerItem(clickedKey, false);
        } else if (action == InventoryAction.SHIFT_CLICK) {
            tryFillContainerItem(clickedKey, true);
        } else if (action == InventoryAction.EMPTY_ITEM) {
            handleEmptyHeldItem((what, amount, mode) -> StorageHelper.insert(storage, what, amount,
                    getActionSource(), mode));
        }

        // Handle interactions where the player wants to put something into the network
        if (clickedKey == null) {
            if (action == InventoryAction.SPLIT_OR_PLACE_SINGLE || action == InventoryAction.ROLL_DOWN) {
                putCarriedItemIntoNetwork(true);
            } else if (action == InventoryAction.PICKUP_OR_SET_DOWN) {
                putCarriedItemIntoNetwork(false);
            }
            return;
        }

        if (!(clickedKey instanceof AEItemKey clickedItem)) {
            return;
        }

        switch (action) {
            case SHIFT_CLICK:
                moveOneStackToPlayer(clickedItem);
                break;

            case ROLL_DOWN: {
                // Insert 1 of the carried stack into the network (or at least try to), regardless of what we're
                // hovering in the network inventory.
                var carried = getCarried();
                if (!carried.isEmpty()) {
                    var what = AEItemKey.of(carried);
                    var inserted = StorageHelper.insert(storage, what, 1, this.getActionSource());
                    if (inserted > 0) {
                        getCarried().shrink(1);
                    }
                }
            }
                break;
            case ROLL_UP:
            case PICKUP_SINGLE: {
                // Extract 1 of the hovered stack from the network (or at least try to), and add it to the carried item
                var item = getCarried();

                if (!item.isEmpty()) {
                    if (item.getCount() >= item.getMaxStackSize()) {
                        return; // Max stack size reached
                    }
                    if (!clickedItem.matches(item)) {
                        return; // Not stackable
                    }
                }

                var extracted = StorageHelper.extract(storage, clickedItem, 1,
                        this.getActionSource());
                if (extracted > 0) {
                    if (item.isEmpty()) {
                        setCarried(clickedItem.toStack());
                    } else {
                        // we checked beforehand that max stack size was not reached
                        item.grow(1);
                    }
                }
            }
                break;
            case PICKUP_OR_SET_DOWN: {
                if (!getCarried().isEmpty()) {
                    putCarriedItemIntoNetwork(false);
                } else {
                    var extracted = StorageHelper.extract(
                            storage,
                            clickedItem,
                            clickedItem.getItem().getMaxStackSize(),
                            this.getActionSource());
                    if (extracted > 0) {
                        setCarried(clickedItem.toStack((int) extracted));
                    } else {
                        setCarried(ItemStack.EMPTY);
                    }
                }
            }
                break;
            case SPLIT_OR_PLACE_SINGLE:
                if (!getCarried().isEmpty()) {
                    putCarriedItemIntoNetwork(true);
                } else {
                    var extracted = storage.extract(
                            clickedItem,
                            clickedItem.getItem().getMaxStackSize(),
                            Actionable.SIMULATE,
                            this.getActionSource());

                    if (extracted > 0) {
                        // Half
                        extracted = extracted + 1 >> 1;
                        extracted = StorageHelper.extract(storage, clickedItem, extracted,
                                this.getActionSource());
                    }

                    if (extracted > 0) {
                        setCarried(clickedItem.toStack((int) extracted));
                    } else {
                        setCarried(ItemStack.EMPTY);
                    }
                }

                break;
            case CREATIVE_DUPLICATE:
                if (player.getAbilities().instabuild) {
                    var is = clickedItem.toStack();
                    is.setCount(is.getMaxStackSize());
                    setCarried(is);
                }
                break;
            case MOVE_REGION:
                final int playerInv = player.getInventory().items.size();
                for (int slotNum = 0; slotNum < playerInv; slotNum++) {
                    if (!moveOneStackToPlayer(clickedItem)) {
                        break;
                    }
                }
                break;
            default:
                AELog.warn("Received unhandled inventory action %s from client in %s", action, getClass());
                break;
        }
    }

    private void tryFillContainerItem(@org.jetbrains.annotations.Nullable AEKey clickedKey, boolean moveToPlayer) {
        // Special handling for fluids to facilitate filling water/lava buckets which are often
        // needed for crafting and placement in-world.
        boolean grabbedEmptyBucket = false;
        if (getCarried().isEmpty() && clickedKey instanceof AEFluidKey fluidKey
                && fluidKey.getFluid().getBucket() != Items.AIR) {
            // This costs no energy, but who cares...
            if (storage != null
                    && storage.extract(AEItemKey.of(Items.BUCKET), 1, Actionable.MODULATE, getActionSource()) >= 1) {
                var bucket = Items.BUCKET.getDefaultInstance();
                setCarried(bucket);
                grabbedEmptyBucket = true;
            }
        }

        var carriedBefore = getCarried().getItem();

        handleFillingHeldItem(
                (amount, mode) -> StorageHelper.extract(storage, clickedKey, amount,
                        getActionSource(), mode),
                clickedKey);

        // If we grabbed an empty bucket, and after trying to fill it, it's still empty, put it back!
        if (grabbedEmptyBucket && getCarried().is(Items.BUCKET)) {
            var inserted = storage.insert(AEItemKey.of(getCarried()), getCarried().getCount(), Actionable.MODULATE,
                    getActionSource());
            var newCarried = getCarried().copy();
            newCarried.shrink(Ints.saturatedCast(inserted));
            setCarried(newCarried);
        }
        // If the player was holding shift, whatever has been filled should be moved to the player inv
        // To detect the fill operation actually filling, and not moving excess into the inv itself
        // We just compare against the carried item from before the fill operation.
        if (moveToPlayer && !getCarried().is(carriedBefore)) {
            if (getPlayer().addItem(getCarried())) {
                setCarried(ItemStack.EMPTY);
            }
        }
    }

    protected void putCarriedItemIntoNetwork(boolean singleItem) {
        var heldStack = getCarried();

        var what = AEItemKey.of(heldStack);
        if (what == null) {
            return;
        }

        var amount = heldStack.getCount();
        if (singleItem) {
            amount = 1;
        }

        var inserted = StorageHelper.insert(storage, what, amount,
                this.getActionSource());
        setCarried(Platform.getInsertionRemainder(heldStack, inserted));
    }

    private boolean moveOneStackToPlayer(AEItemKey stack) {
        ItemStack myItem = stack.toStack();

        var playerInv = getPlayerInventory();
        var slot = playerInv.getSlotWithRemainingSpace(myItem);
        int toExtract;
        if (slot != -1) {
            // Try to fill up existing slot with item
            toExtract = myItem.getMaxStackSize() - playerInv.getItem(slot).getCount();
        } else {
            slot = playerInv.getFreeSlot();
            if (slot == -1) {
                return false; // No more free space
            }
            toExtract = myItem.getMaxStackSize();
        }
        if (toExtract <= 0) {
            return false;
        }

        var extracted = StorageHelper.extract(storage, stack, toExtract, getActionSource());
        if (extracted == 0) {
            return false; // No items available
        }

        var itemInSlot = playerInv.getItem(slot);
        if (itemInSlot.isEmpty()) {
            playerInv.setItem(slot, stack.toStack((int) extracted));
        } else {
            itemInSlot.grow((int) extracted);
        }
        return true;
    }

    @Nullable
    protected final AEKey getStackBySerial(long serial) {
        return updateHelper.getBySerial(serial);
    }

    private IConfigManagerListener getGui() {
        return this.gui;
    }

    public void setGui(IConfigManagerListener gui) {
        this.gui = gui;
    }

    @Nullable
    public IClientRepo getClientRepo() {
        return clientRepo;
    }

    public void setClientRepo(@Nullable IClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    /**
     * Try to transfer an item stack into the grid.
     */
    @Override
    protected ItemStack transferStackToMenu(ItemStack input) {
        if (!canInteractWithGrid()) {
            // Allow non-grid slots to be use
            return super.transferStackToMenu(input);
        }

        var key = AEItemKey.of(input);
        if (key == null || !isKeyVisible(key)) {
            return input;
        }

        var inserted = StorageHelper.insert(storage,
                key, input.getCount(),
                this.getActionSource());
        return Platform.getInsertionRemainder(input, inserted);
    }

    /**
     * Checks if the terminal has a given amount of the requested item. Used to determine for REI/JEI if a recipe is
     * potentially craftable based on the available items.
     */
    public boolean hasItemType(ItemStack itemStack, int amount) {
        var clientRepo = getClientRepo();

        if (clientRepo != null) {
            for (var stack : clientRepo.getAllEntries()) {
                if (AEItemKey.matches(stack.getWhat(), itemStack)) {
                    if (stack.getStoredAmount() >= amount) {
                        return true;
                    }
                    amount -= stack.getStoredAmount();
                }
            }
        }

        return false;
    }

    /**
     * @return The stacks available in the storage as determined the last time this menu was ticked.
     */
    protected final KeyCounter getPreviousAvailableStacks() {
        Preconditions.checkState(isServerSide());
        return previousAvailableStacks;
    }

    public ITerminalHost getHost() {
        return host;
    }
}
