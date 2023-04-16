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

package appeng.me.service;

import appeng.api.networking.GridHelper;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.networking.IGridStorage;
import appeng.api.networking.events.GridBootingStatusChange;
import appeng.api.networking.events.GridChannelRequirementChanged;
import appeng.api.networking.pathing.ControllerState;
import appeng.api.networking.pathing.IPathingService;
import appeng.core.AEConfig;
import appeng.core.AELog;
import appeng.me.Grid;
import appeng.me.pathfinding.AdHocChannelUpdater;
import appeng.me.pathfinding.PathingCalculation;

public class PathingService implements IPathingService, IGridServiceProvider {
    static {
        GridHelper.addGridServiceEventHandler(GridChannelRequirementChanged.class,
                IPathingService.class,
                (service, event) -> {
                    ((PathingService) service).updateNodReq(event);
                });
    }

    private PathingCalculation ongoingCalculation = null;
    private final Grid grid;
    // Flag to indicate a reboot should occur next tick
    private boolean reboot = true;
    private boolean booting = false;
    private int bootingTicks = 0;

    private ControllerState controllerState = ControllerState.NO_CONTROLLER;

    public PathingService(IGrid g) {
        this.grid = (Grid) g;
    }

    @Override
    public void onServerEndTick() {
        if (this.reboot) {
            this.reboot = false;

            if (!this.booting) {
                this.booting = true;
                this.bootingTicks = 0;
                this.postBootingStatusChange();
            }

            // updateControllerState / postBootingStatusChange called above can cause the grid to be destroyed,
            // and the pivot to become null.
            if (grid.isEmpty()) {
                return;
            }

            if (this.controllerState == ControllerState.NO_CONTROLLER) {
                this.grid.getPivot().beginVisit(new AdHocChannelUpdater());
            } else {
                this.ongoingCalculation = new PathingCalculation(grid);
            }
        }

        if (this.booting) {
            // Work on remaining pathfinding work
            if (ongoingCalculation != null) { // can be null for ad-hoc or invalid controller state
                for (var i = 0; i < AEConfig.instance().getPathfindingStepsPerTick(); i++) {
                    ongoingCalculation.step();
                    if (ongoingCalculation.isFinished()) {
                        ongoingCalculation = null;
                        break;
                    }
                }
            }

            bootingTicks++;

            // Booting completes when both pathfinding completes, and the minimum boot time has elapsed
            if (ongoingCalculation == null) {
                // check for achievements

                this.booting = false;
                // Notify of channel changes AFTER we set booting to false, this ensures that any activeness check will
                // properly return true.
                this.postBootingStatusChange();
            } else if (bootingTicks == 2000) {
                AELog.warn("Booting has still not completed after %d ticks for %s", bootingTicks, grid);
            }
        }
    }

    private void postBootingStatusChange() {
        this.grid.postEvent(new GridBootingStatusChange(this.booting));
        this.grid.notifyAllNodes(IGridNodeListener.State.GRID_BOOT);
    }

    @Override
    public void removeNode(IGridNode gridNode) {
        this.repath();
    }

    @Override
    public void addNode(IGridNode gridNode) {
        this.repath();
    }

    private void updateNodReq(GridChannelRequirementChanged ev) {
        this.repath();
    }

    @Override
    public boolean isNetworkBooting() {
        return this.booting;
    }

    @Override
    public ControllerState getControllerState() {
        return this.controllerState;
    }

    @Override
    public void repath() {
        // clean up...
        this.ongoingCalculation = null;
        this.reboot = true;
    }

    @Override
    public void onSplit(IGridStorage destinationStorage) {
        populateGridStorage(destinationStorage);
    }
}
