package appeng.datagen.providers.models;

import static appeng.core.AppEng.makeId;

import java.util.Locale;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.api.util.AEColor;
import appeng.core.AppEng;
import appeng.core.definitions.AEParts;
import appeng.core.definitions.ColoredItemDefinition;

public class CableModelProvider extends AE2BlockStateProvider {
    public CableModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, AppEng.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        buildCableItems(AEParts.GLASS_CABLE, "item/glass_cable_base", "part/cable/glass/");

    }

    private void buildCableItems(ColoredItemDefinition cable, String baseModel, String textureBase) {
        for (AEColor color : AEColor.values()) {
            itemModels().withExistingParent(
                    cable.id(color).getPath(),
                    makeId(baseModel)).texture("base", makeId(textureBase + color.name().toLowerCase(Locale.ROOT)));
        }
    }
}
