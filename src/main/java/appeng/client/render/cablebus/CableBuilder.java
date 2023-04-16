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

package appeng.client.render.cablebus;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.core.AppEng;

/**
 * A helper class that builds quads for cable connections.
 */
@Environment(EnvType.CLIENT)
class CableBuilder {

    // Textures for the cable core types, one per type/color pair
    private final EnumMap<CableCoreType, EnumMap<AEColor, TextureAtlasSprite>> coreTextures;

    // Textures for rendering the actual connection cubes, one per type/color pair
    public final EnumMap<AECableType, EnumMap<AEColor, TextureAtlasSprite>> connectionTextures;

    CableBuilder(Function<Material, TextureAtlasSprite> bakedTextureGetter) {
        this.coreTextures = new EnumMap<>(CableCoreType.class);

        for (CableCoreType type : CableCoreType.values()) {
            EnumMap<AEColor, TextureAtlasSprite> colorTextures = new EnumMap<>(AEColor.class);

            for (AEColor color : AEColor.values()) {
                colorTextures.put(color, bakedTextureGetter.apply(type.getTexture(color)));
            }

            this.coreTextures.put(type, colorTextures);
        }

        this.connectionTextures = new EnumMap<>(AECableType.class);

        for (AECableType type : AECableType.VALIDCABLES) {
            EnumMap<AEColor, TextureAtlasSprite> colorTextures = new EnumMap<>(AEColor.class);

            for (AEColor color : AEColor.values()) {
                colorTextures.put(color, bakedTextureGetter.apply(getConnectionTexture(type, color)));
            }

            this.connectionTextures.put(type, colorTextures);
        }
    }

    static Material getConnectionTexture(AECableType cableType, AEColor color) {
        String textureFolder = switch (cableType) {
            case GLASS -> "part/cable/glass/";
            default -> throw new IllegalStateException("Cable type " + cableType + " does not support connections.");
        };

        return new Material(TextureAtlas.LOCATION_BLOCKS,
                new ResourceLocation(AppEng.MOD_ID, textureFolder + color.name().toLowerCase(Locale.ROOT)));
    }

    /**
     * Adds the core of a cable to the given list of quads.
     * <p>
     * The type of cable core is automatically deduced from the given cable type.
     */
    public void addCableCore(AECableType cableType, AEColor color, QuadEmitter emitter) {
        switch (cableType) {
            case GLASS:
                this.addCableCore(CableCoreType.GLASS, color, emitter);
                break;
            default:
        }
    }

    public void addCableCore(CableCoreType coreType, AEColor color, QuadEmitter emitter) {
        CubeBuilder cubeBuilder = new CubeBuilder(emitter);

        TextureAtlasSprite texture = this.coreTextures.get(coreType).get(color);
        cubeBuilder.setTexture(texture);

        switch (coreType) {
            case GLASS -> cubeBuilder.addCube(6, 6, 6, 10, 10, 10);
        }
    }

    public void addGlassConnection(Direction facing, AEColor cableColor, AECableType connectionType,
            boolean cableBusAdjacent, QuadEmitter emitter) {
        CubeBuilder cubeBuilder = new CubeBuilder(emitter);

        // We render all faces except the one on the connection side
        cubeBuilder.setDrawFaces(EnumSet.complementOf(EnumSet.of(facing)));

        // For to-machine connections, use a thicker end-cap for the connection TODO ATAKKU
        if (connectionType != AECableType.GLASS && !cableBusAdjacent) {
            TextureAtlasSprite texture = this.connectionTextures.get(AECableType.GLASS).get(cableColor);
            cubeBuilder.setTexture(texture);

            this.addBigCoveredCableSizedCube(facing, cubeBuilder);
        }

        TextureAtlasSprite texture = this.connectionTextures.get(AECableType.GLASS).get(cableColor);
        cubeBuilder.setTexture(texture);

        switch (facing) {
            case DOWN -> cubeBuilder.addCube(6, 0, 6, 10, 6, 10);
            case EAST -> cubeBuilder.addCube(10, 6, 6, 16, 10, 10);
            case NORTH -> cubeBuilder.addCube(6, 6, 0, 10, 10, 6);
            case SOUTH -> cubeBuilder.addCube(6, 6, 10, 10, 10, 16);
            case UP -> cubeBuilder.addCube(6, 10, 6, 10, 16, 10);
            case WEST -> cubeBuilder.addCube(0, 6, 6, 6, 10, 10);
        }
    }

    public void addStraightGlassConnection(Direction facing, AEColor cableColor, QuadEmitter emitter) {
        CubeBuilder cubeBuilder = new CubeBuilder(emitter);

        // We render all faces except the connection caps. We can do this because the
        // glass cable is the smallest one
        // and its ends will always be covered by something
        cubeBuilder.setDrawFaces(EnumSet.complementOf(EnumSet.of(facing, facing.getOpposite())));

        TextureAtlasSprite texture = this.connectionTextures.get(AECableType.GLASS).get(cableColor);
        cubeBuilder.setTexture(texture);

        switch (facing) {
            case DOWN, UP -> cubeBuilder.addCube(6, 0, 6, 10, 16, 10);
            case NORTH, SOUTH -> cubeBuilder.addCube(6, 6, 0, 10, 10, 16);
            case EAST, WEST -> cubeBuilder.addCube(0, 6, 6, 16, 10, 10);
        }
    }

    public void addConstrainedGlassConnection(Direction facing, AEColor cableColor, int distanceFromEdge,
            QuadEmitter emitter) {

        // Glass connections reach only 6 voxels from the edge
        if (distanceFromEdge >= 6) {
            return;
        }

        CubeBuilder cubeBuilder = new CubeBuilder(emitter);

        TextureAtlasSprite texture = this.connectionTextures.get(AECableType.GLASS).get(cableColor);
        cubeBuilder.setTexture(texture);

        switch (facing) {
            case DOWN -> cubeBuilder.addCube(6, distanceFromEdge, 6, 10, 6, 10);
            case EAST -> cubeBuilder.addCube(10, 6, 6, 16 - distanceFromEdge, 10, 10);
            case NORTH -> cubeBuilder.addCube(6, 6, distanceFromEdge, 10, 10, 6);
            case SOUTH -> cubeBuilder.addCube(6, 6, 10, 10, 10, 16 - distanceFromEdge);
            case UP -> cubeBuilder.addCube(6, 10, 6, 10, 16 - distanceFromEdge, 10);
            case WEST -> cubeBuilder.addCube(distanceFromEdge, 6, 6, 6, 10, 10);
        }
    }

    /**
     * This renders a slightly bigger covered cable connection to the specified side. This is used to connect cable
     * cores with adjacent machines that do not want to be connected to using a glass cable connection. This applies to
     * most machines (interfaces, etc.)
     */
    private void addBigCoveredCableSizedCube(Direction facing, CubeBuilder cubeBuilder) {
        switch (facing) {
            case DOWN -> cubeBuilder.addCube(5, 0, 5, 11, 4, 11);
            case EAST -> cubeBuilder.addCube(12, 5, 5, 16, 11, 11);
            case NORTH -> cubeBuilder.addCube(5, 5, 0, 11, 11, 4);
            case SOUTH -> cubeBuilder.addCube(5, 5, 12, 11, 11, 16);
            case UP -> cubeBuilder.addCube(5, 12, 5, 11, 16, 11);
            case WEST -> cubeBuilder.addCube(0, 5, 5, 4, 11, 11);
        }
    }

    // Get all textures needed for building the actual cable quads
    public static List<Material> getTextures() {
        List<Material> locations = new ArrayList<>();

        for (CableCoreType coreType : CableCoreType.values()) {
            for (AEColor color : AEColor.values()) {
                locations.add(coreType.getTexture(color));
            }
        }

        for (AECableType cableType : AECableType.VALIDCABLES) {
            for (AEColor color : AEColor.values()) {
                locations.add(getConnectionTexture(cableType, color));
            }
        }

        return locations;
    }

    public TextureAtlasSprite getCoreTexture(CableCoreType coreType, AEColor color) {
        return this.coreTextures.get(coreType).get(color);
    }
}
