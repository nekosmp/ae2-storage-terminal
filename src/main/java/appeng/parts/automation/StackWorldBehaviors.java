package appeng.parts.automation;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.behaviors.StackExportStrategy;
import appeng.api.behaviors.StackImportStrategy;
import appeng.api.stacks.AEKeyType;
import appeng.api.storage.AEKeyFilter;
import appeng.util.CowMap;

public final class StackWorldBehaviors {
    private static final CowMap<AEKeyType, StackImportStrategy.Factory> importStrategies = CowMap.identityHashMap();
    private static final CowMap<AEKeyType, StackExportStrategy.Factory> exportStrategies = CowMap.identityHashMap();
    private static final CowMap<AEKeyType, ExternalStorageStrategy.Factory> externalStorageStrategies = CowMap
            .identityHashMap();

    static {
        registerExternalStorageStrategy(AEKeyType.items(), FabricExternalStorageStrategy::createItem);
        registerExternalStorageStrategy(AEKeyType.fluids(), FabricExternalStorageStrategy::createFluid);
    }

    private StackWorldBehaviors() {
    }

    public static void registerImportStrategy(AEKeyType type, StackImportStrategy.Factory factory) {
        importStrategies.putIfAbsent(type, factory);
    }

    public static void registerExportStrategy(AEKeyType type, StackExportStrategy.Factory factory) {
        exportStrategies.putIfAbsent(type, factory);
    }

    public static void registerExternalStorageStrategy(AEKeyType type, ExternalStorageStrategy.Factory factory) {
        externalStorageStrategies.putIfAbsent(type, factory);
    }

    /**
     * {@return filter matching any key for which there is an import strategy}
     */
    public static AEKeyFilter hasImportStrategyFilter() {
        return what -> importStrategies.getMap().containsKey(what.getType());
    }

    /**
     * {@return filter matching any key for which there is an export strategy}
     */
    public static AEKeyFilter hasExportStrategyFilter() {
        return what -> exportStrategies.getMap().containsKey(what.getType());
    }

    public static Map<AEKeyType, ExternalStorageStrategy> createExternalStorageStrategies(ServerLevel level,
            BlockPos fromPos, Direction fromSide) {
        var strategies = new IdentityHashMap<AEKeyType, ExternalStorageStrategy>(
                externalStorageStrategies.getMap().size());
        for (var entry : externalStorageStrategies.getMap().entrySet()) {
            strategies.put(entry.getKey(), entry.getValue().create(level, fromPos, fromSide));
        }
        return strategies;
    }
}
