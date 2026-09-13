package best.aps.carpetaddition;

import best.aps.carpetaddition.command.CommandRegistrar;
import best.aps.carpetaddition.command.SpectatorReturnPoints;
import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.utils.Translations;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class CarpetAPSAdditionExtension implements CarpetExtension {
    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(CarpetAPSAdditionSettings.class);
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return Translations.getTranslationFromResourcePath("assets/%s/lang/%s.json".formatted(CarpetAPSAddition.MOD_ID, lang));
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext access) {
        CommandRegistrar.register(dispatcher);
    }

    @Override
    public void onPlayerLoggedIn(ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        server.execute(() -> {
            if (player.hasDisconnected()) {
                return;
            }
            SpectatorReturnPoints.onPlayerLoggedIn(server, player);
        });
    }
}
