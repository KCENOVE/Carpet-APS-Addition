package best.aps.carpetaddition.command;

import best.aps.carpetaddition.CarpetAPSAddition;
import best.aps.carpetaddition.config.CommandAliasConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

import java.util.function.BiConsumer;

public final class CommandRegistrar {
    private CommandRegistrar() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        register(dispatcher, SpectatorCommand.NAME, SpectatorCommand::register);
    }

    private static void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            String name,
            BiConsumer<CommandDispatcher<CommandSourceStack>, String> command
    ) {
        command.accept(dispatcher, name);
        for (String alias : CommandAliasConfig.aliasesOf(name)) {
            if (dispatcher.getRoot().getChild(alias) == null) {
                command.accept(dispatcher, alias);
            } else {
                CarpetAPSAddition.LOGGER.warn("Command /{} already exists, ignoring alias /{} of command /{}", alias, alias, name);
            }
        }
    }
}
