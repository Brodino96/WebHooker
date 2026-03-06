package dev.brodino.webhooker;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.Commands.CommandSelection;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Webhooker {
    public static final String MOD_ID = "webhooker";
    public static final Logger LOGGER = LoggerFactory.getLogger(Webhooker.MOD_ID);
    public static final Config CONFIG = new Config();

    public static void init() {
        Webhooker.LOGGER.info("Initializing WebHooker");
        Discord.initialize();

        CommandRegistrationEvent.EVENT.register(Webhooker::registerCommands);
    }

    public static String escapeJson(String message) {
        if (message == null) {
            Webhooker.LOGGER.warn("Tried to escape an empty string");
            return "";
        }

        return message.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, CommandSelection selection) {
        dispatcher.register(Commands.literal("webhooker")
            .requires(src -> src.hasPermission(2))
            .then(Commands.literal("reloadConfig")
                .executes(context -> {
                    Webhooker.CONFIG.reload();
                    Player player = context.getSource().getPlayer();

                    if (Webhooker.CONFIG.reload()) {
                        Webhooker.LOGGER.info("Reloaded config");
                        if (player != null) {
                            player.sendSystemMessage(Component.literal("Reloaded config"));
                        }
                        return 1;
                    }

                    Webhooker.LOGGER.error("Failed to reload config");
                    if (player != null) {
                        player.sendSystemMessage(Component.literal("Reloaded config"));
                    }
                    return 0;
                })
            )
        );
    }
}
