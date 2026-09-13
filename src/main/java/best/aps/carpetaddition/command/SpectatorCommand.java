package best.aps.carpetaddition.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class SpectatorCommand {
    public static final String NAME = "spectator";
    private static final String PLAYER = "player";

    private SpectatorCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, String name) {
        dispatcher.register(Commands.literal(name)
                .executes(SpectatorCommand::toggleGameMode)
                .then(Commands.literal("tp")
                        .then(Commands.argument(PLAYER, StringArgumentType.word())
                                .suggests(SpectatorCommand::suggestPlayers)
                                .executes(SpectatorCommand::teleportToPlayer))));
    }

    private static int toggleGameMode(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        MinecraftServer server = source.getServer();
        GameType previous = player.gameMode();
        if (SpectatorReturnPoints.has(server, player)) {
            if (!SpectatorReturnPoints.restore(server, player)) {
                source.sendFailure(Component.literal("读取返回点失败"));
                return 0;
            }
            sendGameModeChange(source, player, previous);
            return 1;
        }
        // 没有返回点的旁观模式不是使用本命令进入的
        if (player.isSpectator()) {
            source.sendFailure(unavailable());
            return 0;
        }
        // 无法记录返回点时不切换游戏模式，否则玩家无法回到原位置
        if (!SpectatorReturnPoints.save(server, player)) {
            source.sendFailure(Component.literal("无法保存返回点，未切换游戏模式"));
            return 0;
        }
        player.setGameMode(GameType.SPECTATOR);
        sendGameModeChange(source, player, previous);
        return 1;
    }

    private static void sendGameModeChange(CommandSourceStack source, ServerPlayer player, GameType previous) {
        GameType gameMode = player.gameMode();
        if (gameMode != previous) {
            source.sendSuccess(() -> Component.translatable("commands.gamemode.success.self", gameMode.getLongDisplayName()), false);
        }
    }

    private static int teleportToPlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        MinecraftServer server = source.getServer();
        if (!player.isSpectator() || !SpectatorReturnPoints.has(server, player)) {
            source.sendFailure(unavailable());
            return 0;
        }
        ServerPlayer target = server.getPlayerList().getPlayerByName(StringArgumentType.getString(context, PLAYER));
        if (target == null) {
            source.sendFailure(Component.translatable("argument.entity.notfound.player"));
            return 0;
        }
        player.teleportTo(target.level(), target.getX(), target.getY(), target.getZ(), Set.of(), target.getYRot(), target.getXRot(), true);
        source.sendSuccess(() -> Component.translatable("commands.teleport.success.entity.single", player.getDisplayName(), target.getDisplayName()), false);
        return 1;
    }

    private static CompletableFuture<Suggestions> suggestPlayers(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(context.getSource().getServer().getPlayerList().getPlayers().stream()
                .map(player -> player.getGameProfile().name()), builder);
    }

    private static Component unavailable() {
        return Component.literal("你当前无法使用此命令");
    }
}
