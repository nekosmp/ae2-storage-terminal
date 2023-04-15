package appeng.datagen.providers.models;

import static appeng.core.AppEng.makeId;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

import appeng.client.render.model.BiometricCardModel;
import appeng.core.AppEng;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.ItemDefinition;
import appeng.datagen.providers.IAE2DataProvider;

public class ItemModelProvider extends net.minecraftforge.client.model.generators.ItemModelProvider
        implements IAE2DataProvider {
    public ItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, AppEng.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        flatSingleLayer(BiometricCardModel.MODEL_BASE, "item/biometric_card");
        builtInItemModel("biometric_card");

        flatSingleLayer(AEItems.CAPACITY_CARD, "item/card_capacity");
        flatSingleLayer(AEItems.FUZZY_CARD, "item/card_fuzzy");
        flatSingleLayer(AEItems.INVERTER_CARD, "item/card_inverter");
        flatSingleLayer(AEItems.NETHER_QUARTZ_WRENCH, "item/nether_quartz_wrench");
        flatSingleLayer(AEItems.WIRELESS_CRAFTING_TERMINAL, "item/wireless_crafting_terminal");
        flatSingleLayer(AEItems.WIRELESS_TERMINAL, "item/wireless_terminal");

        flatSingleLayer(AEItems.DEBUG_CARD, "item/debug_card");
        flatSingleLayer(AEItems.DEBUG_ERASER, "item/debug/eraser");
        registerEmptyModel(AEItems.WRAPPED_GENERIC_STACK);
        registerEmptyModel(AEBlocks.CABLE_BUS);
        registerHandheld();
    }

    private void registerHandheld() {
        handheld(AEItems.NETHER_QUARTZ_WRENCH);
    }

    private void handheld(ItemDefinition<?> item) {
        singleTexture(
                item.id().getPath(),
                new ResourceLocation("item/handheld"),
                "layer0",
                makeId("item/" + item.id().getPath()));
    }

    private void registerEmptyModel(ItemDefinition<?> item) {
        this.getBuilder(item.id().getPath());
    }

    private ItemModelBuilder flatSingleLayer(ItemDefinition<?> item, String texture) {
        String id = item.id().getPath();
        return singleTexture(
                id,
                mcLoc("item/generated"),
                "layer0",
                makeId(texture));
    }

    private ItemModelBuilder flatSingleLayer(ResourceLocation id, String texture) {
        return singleTexture(
                id.getPath(),
                mcLoc("item/generated"),
                "layer0",
                makeId(texture));
    }

    private ItemModelBuilder builtInItemModel(String name) {
        var model = getBuilder("item/" + name);
        var loaderId = AppEng.makeId("item/" + name);
        model.customLoader((bmb, efh) -> new CustomLoaderBuilder<ItemModelBuilder>(loaderId, bmb, efh) {});
        return model;
    }
}
