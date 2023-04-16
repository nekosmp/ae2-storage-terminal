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

package appeng.parts.networking;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import appeng.api.config.SecurityPermissions;
import appeng.api.implementations.parts.ICablePart;
import appeng.api.networking.GridFlags;
import appeng.api.networking.GridHelper;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import appeng.api.networking.IManagedGridNode;
import appeng.api.parts.BusSupport;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartItem;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.core.definitions.AEParts;
import appeng.items.parts.ColoredPartItem;
import appeng.parts.AEBasePart;

public class CablePart extends AEBasePart implements ICablePart {

    private static final IGridNodeListener<CablePart> NODE_LISTENER = new NodeListener<>() {
        @Override
        public void onInWorldConnectionChanged(CablePart nodeOwner, IGridNode node) {
            super.onInWorldConnectionChanged(nodeOwner, node);
            nodeOwner.markForUpdate();
        }
    };

    private Set<Direction> connections = Collections.emptySet();

    public CablePart(ColoredPartItem<?> partItem) {
        super(partItem);
        this.getMainNode()
                .setFlags(GridFlags.PREFERRED)
                .setInWorldNode(true)
                .setExposedOnSides(EnumSet.allOf(Direction.class));
        this.getMainNode().setGridColor(partItem.getColor());
    }

    @Override
    protected IManagedGridNode createMainNode() {
        return GridHelper.createManagedNode(this, NODE_LISTENER);
    }

    @Override
    public BusSupport supportsBuses() {
        return BusSupport.CABLE;
    }

    @Override
    public AEColor getCableColor() {
        if (getPartItem() instanceof ColoredPartItem<?>coloredPartItem) {
            return coloredPartItem.getColor();
        }
        return AEColor.TRANSPARENT;
    }

    @Override
    public AECableType getCableConnectionType() {
        return AECableType.GLASS;
    }

    @Override
    public float getCableConnectionLength(AECableType cable) {
        if (cable == this.getCableConnectionType()) {
            return 4;
        } else if (cable.ordinal() >= this.getCableConnectionType().ordinal()) {
            return -1;
        } else {
            return 8;
        }
    }

    @Override
    public boolean changeColor(AEColor newColor, Player who) {
        if (this.getCableColor() != newColor) {
            IPartItem<?> newPart = null;

            if (this.getCableConnectionType() == AECableType.GLASS) {
                newPart = AEParts.GLASS_CABLE.item(newColor);
            }

            boolean hasPermission = true;

            var grid = getMainNode().getGrid();
            if (grid != null) {
                hasPermission = grid.getSecurityService().hasPermission(who, SecurityPermissions.BUILD);
            }

            if (newPart != null && hasPermission) {
                if (isClientSide()) {
                    return true;
                }

                setPartItem(newPart);

                getMainNode().setGridColor(getCableColor());
                getHost().markForUpdate();
                getHost().markForSave();
                return true;
            }
        }
        return false;
    }

    @Override
    public void setExposedOnSides(EnumSet<Direction> sides) {
        this.getMainNode().setExposedOnSides(sides);
    }

    @Override
    public boolean isConnected(Direction side) {
        return this.getConnections().contains(side);
    }

    public void markForUpdate() {
        this.getHost().markForUpdate();
    }

    @Override
    public void getBoxes(IPartCollisionHelper bch) {
        updateConnections();

        bch.addBox(6.0, 6.0, 6.0, 10.0, 10.0, 10.0);

        final IPartHost ph = this.getHost();
        if (ph != null) {
            for (Direction dir : Direction.values()) {
                var p = ph.getPart(dir);
                if (p != null) {
                    var dist = p.getCableConnectionLength(this.getCableConnectionType());

                    if (dist > 8) {
                        continue;
                    }

                    switch (dir) {
                        case DOWN:
                            bch.addBox(6.0, dist, 6.0, 10.0, 6.0, 10.0);
                            break;
                        case EAST:
                            bch.addBox(10.0, 6.0, 6.0, 16.0 - dist, 10.0, 10.0);
                            break;
                        case NORTH:
                            bch.addBox(6.0, 6.0, dist, 10.0, 10.0, 6.0);
                            break;
                        case SOUTH:
                            bch.addBox(6.0, 6.0, 10.0, 10.0, 10.0, 16.0 - dist);
                            break;
                        case UP:
                            bch.addBox(6.0, 10.0, 6.0, 10.0, 16.0 - dist, 10.0);
                            break;
                        case WEST:
                            bch.addBox(dist, 6.0, 6.0, 6.0, 10.0, 10.0);
                            break;
                        default:
                    }
                }
            }
        }

        for (Direction of : this.getConnections()) {
            switch (of) {
                case DOWN:
                    bch.addBox(6.0, 0.0, 6.0, 10.0, 6.0, 10.0);
                    break;
                case EAST:
                    bch.addBox(10.0, 6.0, 6.0, 16.0, 10.0, 10.0);
                    break;
                case NORTH:
                    bch.addBox(6.0, 6.0, 0.0, 10.0, 10.0, 6.0);
                    break;
                case SOUTH:
                    bch.addBox(6.0, 6.0, 10.0, 10.0, 10.0, 16.0);
                    break;
                case UP:
                    bch.addBox(6.0, 10.0, 6.0, 10.0, 16.0, 10.0);
                    break;
                case WEST:
                    bch.addBox(0.0, 6.0, 6.0, 6.0, 10.0, 10.0);
                    break;
                default:
            }
        }
    }

    protected void updateConnections() {
        if (!isClientSide()) {
            var n = this.getGridNode();
            if (n != null) {
                this.setConnections(n.getConnectedSides());
            } else {
                this.setConnections(Collections.emptySet());
            }
        }
    }

    @Override
    public void writeToStream(FriendlyByteBuf data) {
        super.writeToStream(data);

        int connectedSidesPacked = 0;
        var n = getGridNode();
        if (n != null) {
            for (var entry : n.getInWorldConnections().entrySet()) {
                var side = entry.getKey().ordinal();
                connectedSidesPacked |= 1 << side;
            }
        }
        data.writeByte((byte) connectedSidesPacked);
    }

    @Override
    public boolean readFromStream(FriendlyByteBuf data) {
        var changed = super.readFromStream(data);

        int connectedSidesPacked = data.readByte();
        // Save previous state for change-detection
        var previousConnections = this.getConnections();
    
        var connections = EnumSet.noneOf(Direction.class);
        for (var d : Direction.values()) {
            boolean conOnSide = (connectedSidesPacked & (1 << d.ordinal())) != 0;
            if (conOnSide) {
                connections.add(d);
            }
        }
        this.setConnections(connections);

        return changed || !previousConnections.equals(this.getConnections());
    }

    @Override
    public void writeVisualStateToNBT(CompoundTag data) {
        super.writeVisualStateToNBT(data);

        updateConnections();

        var connectionsTag = new ListTag();
        for (var connection : connections) {
            connectionsTag.add(StringTag.valueOf(connection.getSerializedName()));
        }
        data.put("connections", connectionsTag);
    }

    @Override
    public void readVisualStateFromNBT(CompoundTag data) {
        super.readVisualStateFromNBT(data);

        // Restore adjacent connections
        var connections = EnumSet.noneOf(Direction.class);
        var connectionsTag = data.getList("connections", Tag.TAG_STRING);
        for (var connectionTag : connectionsTag) {
            var side = Direction.byName(connectionTag.getAsString());
            if (side != null) {
                connections.add(side);
            }
        }
        setConnections(connections);
    }

    Set<Direction> getConnections() {
        return this.connections;
    }

    void setConnections(Set<Direction> connections) {
        this.connections = connections;
    }

}
