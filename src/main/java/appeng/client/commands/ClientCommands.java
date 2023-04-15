package appeng.client.commands;

import java.util.List;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

import appeng.core.AEConfig;

public final class ClientCommands {

    public static final List<CommandBuilder> DEBUG_COMMANDS = List.of(
            ClientCommands::highlightGuiAreas);

    private ClientCommands() {
    }

    @FunctionalInterface
    public interface CommandBuilder {
        void build(LiteralArgumentBuilder<FabricClientCommandSource> builder);
    }


    private static void highlightGuiAreas(LiteralArgumentBuilder<FabricClientCommandSource> builder) {
        builder.then(ClientCommandManager.literal("highlight_gui_areas").executes(context -> {
            var src = context.getSource();
            var toggle = !AEConfig.instance().isShowDebugGuiOverlays();
            AEConfig.instance().setShowDebugGuiOverlays(toggle);
            AEConfig.instance().save();
            src.sendFeedback(Component.literal("GUI Overlays: " + toggle));
            return 0;
        }));
    }
}
