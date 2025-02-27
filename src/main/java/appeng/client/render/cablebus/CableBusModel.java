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
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

import appeng.api.parts.PartModelsInternal;
import appeng.api.util.AEColor;
import appeng.client.render.BasicUnbakedModel;
import appeng.core.AELog;
import appeng.core.AppEng;

/**
 * The built-in model for the cable bus block.
 */
@Environment(EnvType.CLIENT)
public class CableBusModel implements BasicUnbakedModel {

    @Override
    public Collection<ResourceLocation> getDependencies() {
        PartModelsInternal.freeze();
        var models = new ArrayList<>(PartModelsInternal.getModels());
        return models;
    }

    @Override
    public Stream<Material> getAdditionalTextures() {
        return CableBuilder.getTextures().stream();
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter,
            ModelState modelState, ResourceLocation resourceLocation) {
        Map<ResourceLocation, BakedModel> partModels = this.loadPartModels(bakery, modelState);

        CableBuilder cableBuilder = new CableBuilder(spriteGetter);

        // This should normally not be used, but we *have* to provide a particle texture
        // or otherwise damage models will
        // crash
        TextureAtlasSprite particleTexture = cableBuilder.getCoreTexture(CableCoreType.GLASS, AEColor.TRANSPARENT);

        return new CableBusBakedModel(cableBuilder, partModels, particleTexture);
    }

    private Map<ResourceLocation, BakedModel> loadPartModels(ModelBakery bakery, ModelState transformIn) {
        ImmutableMap.Builder<ResourceLocation, BakedModel> result = ImmutableMap.builder();

        for (ResourceLocation location : PartModelsInternal.getModels()) {
            BakedModel bakedModel = bakery.bake(location, transformIn);
            if (bakedModel == null) {
                AELog.warn("Failed to bake part model {}", location);
            } else {
                result.put(location, bakedModel);
            }
        }

        return result.build();
    }
}
