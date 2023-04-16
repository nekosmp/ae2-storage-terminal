
package appeng.datagen.providers.recipes;

import java.util.function.Consumer;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import appeng.api.util.AEColor;
import appeng.core.AppEng;
import appeng.core.definitions.AEParts;
import appeng.datagen.providers.tags.ConventionTags;

public class CraftingRecipes extends AE2RecipeProvider {
  public CraftingRecipes(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void buildAE2CraftingRecipes(Consumer<FinishedRecipe> consumer) {
    addCables(consumer);
  }

  // ====================================================
  // recipes/network/cables
  // ====================================================
  private static void addCables(Consumer<FinishedRecipe> consumer) {
    for (var color : AEColor.VALID_COLORS) {
      ShapedRecipeBuilder.shaped(AEParts.GLASS_CABLE.item(color), 8)
          .pattern("aaa")
          .pattern("aba")
          .pattern("aaa")
          .define('a', AEParts.GLASS_CABLE.item(AEColor.TRANSPARENT))
          .define('b', ConventionTags.dye(color.dye))
          .unlockedBy("has_dyes/black", has(ConventionTags.dye(color.dye)))
          .unlockedBy("has_fluix_glass_cable", has(AEParts.GLASS_CABLE.item(AEColor.TRANSPARENT)))
          .save(consumer, AppEng.makeId("network/cables/glass_" + color.registryPrefix));
    }
    ShapelessRecipeBuilder.shapeless(AEParts.GLASS_CABLE.item(AEColor.TRANSPARENT))
                .requires(ConventionTags.GLASS_CABLE)
                .requires(ConventionTags.CAN_REMOVE_COLOR)
                .unlockedBy("has_glass_cable", has(ConventionTags.GLASS_CABLE))
                .save(consumer, AppEng.makeId("network/cables/glass_fluix_clean"));
  }
}
