package rs.neko.nsmp.ae2;

import com.simibubi.create.content.decoration.encasing.CasingBlock;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;

import appeng.api.ids.AEBlockIds;
import appeng.api.ids.AEItemIds;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import appeng.items.materials.MaterialItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class CreateCompat {
  public static class Ids {
    // Item ids
    public static final ResourceLocation INCOMPLETE_ALLOY = AEItemIds.id("incomplete_alloy");
    public static final ResourceLocation INCOMPLETE_CABLE = AEItemIds.id("incomplete_cable");
    public static final ResourceLocation INCOMPLETE_TERMINAL = AEItemIds.id("incomplete_terminal");

    public static final ResourceLocation CRYSTALLINE_ALLOY = AEItemIds.id("crystalline_alloy");
    public static final ResourceLocation WIRELESS_ANTENNA = AEItemIds.id("wireless_antenna");

    // Block ids
    public static final ResourceLocation CRYSTALLINE_CASING = AEBlockIds.id("crystalline_casing");
  }

  // Register items    
  public static final ItemDefinition<SequencedAssemblyItem> INCOMPLETE_ALLOY = AEItems.item("Incomplete Crystalline Alloy", Ids.INCOMPLETE_ALLOY, SequencedAssemblyItem::new);
  public static final ItemDefinition<SequencedAssemblyItem> INCOMPLETE_CABLE = AEItems.item("Incomplete Crystalline Cables", Ids.INCOMPLETE_CABLE, SequencedAssemblyItem::new);
  public static final ItemDefinition<SequencedAssemblyItem> INCOMPLETE_TERMINAL = AEItems.item("Incomplete Terminal", Ids.INCOMPLETE_TERMINAL, SequencedAssemblyItem::new);
  
  public static final ItemDefinition<MaterialItem> CRYSTALLINE_ALLOY = AEItems.item("Crystalline Alloy", Ids.CRYSTALLINE_ALLOY, MaterialItem::new);
  public static final ItemDefinition<MaterialItem> WIRELESS_ANTENNA = AEItems.item("Wireless Antenna", Ids.WIRELESS_ANTENNA, MaterialItem::new);
  
  // Register blocks
  public static final BlockDefinition<CasingBlock> CRYSTALLINE_CASING = AEBlocks.block("Crystalline Casing", Ids.CRYSTALLINE_CASING, () -> new CasingBlock(Properties.of(Material.AMETHYST, MaterialColor.TERRACOTTA_ORANGE)));

  public static void init() {}
}
