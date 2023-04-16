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

package appeng.me.pathfinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridMultiblock;
import appeng.api.networking.IGridNode;
import appeng.me.GridConnection;
import appeng.me.GridNode;

/**
 * Calculation to assign channels starting from the controllers. Basically a BFS, with one step each tick.
 */
public class PathingCalculation {
    /**
     * The BFS queue: all the path items that need to be visited on the next tick.
     */
    private List<IPathItem> queue = new ArrayList<>();
    /**
     * Path items that are either in the queue, or have been processed already.
     */
    private final Set<IPathItem> visited = new HashSet<>();

    /**
     * Create a new pathing calculation from the passed grid.
     */
    public PathingCalculation(IGrid grid) {}

    public void step() {
        final List<IPathItem> oldOpen = this.queue;
        this.queue = new ArrayList<>();

        for (IPathItem i : oldOpen) {
            for (IPathItem pi : i.getPossibleOptions()) {
                if (!this.visited.contains(pi)) {
                    // Set BFS parent.
                    pi.setControllerRoute(i);

                    this.visited.add(pi);
                    this.queue.add(pi);
                }
            }
        }
    }

    public boolean isFinished() {
        return queue.isEmpty();
    }
}
