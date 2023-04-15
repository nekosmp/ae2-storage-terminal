package appeng.integration.modules.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import appeng.api.integrations.jei.IngredientConverters;
import appeng.client.gui.AEBaseScreen;
import appeng.core.AEConfig;
import appeng.core.AppEng;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.integration.abstraction.JEIFacade;
import appeng.integration.modules.jei.transfer.UseCraftingRecipeTransfer;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.me.items.WirelessCraftingTermMenu;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final ResourceLocation TEXTURE = AppEng.makeId("textures/guis/jei.png");

    private static final ResourceLocation ID = new ResourceLocation(AppEng.MOD_ID, "core");

    public JEIPlugin() {
        IngredientConverters.register(new ItemIngredientConverter());
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // Allow vanilla crafting recipe transfer from JEI to crafting terminal
        registration.addRecipeTransferHandler(
                new UseCraftingRecipeTransfer<>(CraftingTermMenu.class, CraftingTermMenu.TYPE,
                        registration.getTransferHelper()),
                RecipeTypes.CRAFTING);
        registration.addRecipeTransferHandler(
                new UseCraftingRecipeTransfer<>(WirelessCraftingTermMenu.class, WirelessCraftingTermMenu.TYPE,
                        registration.getTransferHelper()),
                RecipeTypes.CRAFTING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        var craftingTerminal = AEParts.CRAFTING_TERMINAL.stack();
        registration.addRecipeCatalyst(craftingTerminal, RecipeTypes.CRAFTING);

        var wirelessCraftingTerminal = AEItems.WIRELESS_CRAFTING_TERMINAL.stack();
        registration.addRecipeCatalyst(wirelessCraftingTerminal, RecipeTypes.CRAFTING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(AEBaseScreen.class,
                new IGuiContainerHandler<AEBaseScreen<?>>() {
                    @Override
                    public List<Rect2i> getGuiExtraAreas(AEBaseScreen<?> screen) {
                        return screen.getExclusionZones();
                    }

                    @Nullable
                    @Override
                    public Object getIngredientUnderMouse(AEBaseScreen<?> screen, double mouseX, double mouseY) {
                        // The following code allows the player to show recipes involving fluids in AE fluid terminals
                        // or AE fluid tanks shown in fluid interfaces and other UI.
                        var stack = screen.getStackUnderMouse(mouseX, mouseY);
                        if (stack != null) {
                            return GenericEntryStackHelper.stackToIngredient(stack);
                        }

                        return null;
                    }

                    @Override
                    public Collection<IGuiClickableArea> getGuiClickableAreas(AEBaseScreen<?> screen, double mouseX,
                            double mouseY) {
                        return Collections.emptyList();
                    }
                });

        registration.addGhostIngredientHandler(AEBaseScreen.class, new GhostIngredientHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        JEIFacade.setInstance(new JeiRuntimeAdapter(jeiRuntime));
        this.hideDebugTools(jeiRuntime);
    }

    private void hideDebugTools(IJeiRuntime jeiRuntime) {
        if (!AEConfig.instance().isDebugToolsEnabled()) {
            Collection<ItemStack> toRemove = new ArrayList<>();

            // We use the internal API here as exception as debug tools are not part of the public one by design.
            toRemove.add(AEBlocks.DEBUG_CUBE_GEN.stack());
            toRemove.add(AEBlocks.DEBUG_CHUNK_LOADER.stack());
            toRemove.add(AEBlocks.DEBUG_ENERGY_GEN.stack());
            toRemove.add(AEBlocks.DEBUG_ITEM_GEN.stack());
            toRemove.add(AEBlocks.DEBUG_PHANTOM_NODE.stack());

            toRemove.add(AEItems.DEBUG_CARD.stack());
            toRemove.add(AEItems.DEBUG_ERASER.stack());

            jeiRuntime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,
                    toRemove);
        }

    }

    // Copy-pasted from JEI since it doesn't seem to expose these
    public static void drawHoveringText(PoseStack poseStack, List<Component> textLines, int x, int y) {
        var minecraft = Minecraft.getInstance();
        var screen = minecraft.screen;
        if (screen == null) {
            return;
        }

        screen.renderTooltip(poseStack, textLines, Optional.empty(), x, y);
    }
}
