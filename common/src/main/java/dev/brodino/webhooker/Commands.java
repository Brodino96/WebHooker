package dev.brodino.webhooker;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class Commands {

    public static void initialize(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(net.minecraft.commands.Commands.literal(Webhooker.MOD_ID)
            .requires(src -> src.hasPermission(2))
            .then(getReloadCommand())
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> getReloadCommand() {
        return net.minecraft.commands.Commands.literal("reload")
            .executes((CommandContext<CommandSourceStack> context) -> {

                if (!Webhooker.CONFIG.reload()) {
                    Webhooker.LOGGER.error("Failed to reload config");
                    context.getSource().sendSystemMessage(Component.literal("Failed to reload config"));
                    return 0;
                }

                Webhooker.LOGGER.error("Config reloaded");
                context.getSource().sendSystemMessage(Component.literal("Config reloaded"));
                return 1;
            });
    }
}
