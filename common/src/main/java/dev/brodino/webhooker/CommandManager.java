package dev.brodino.webhooker;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Set;

public class CommandManager {

    private static final SuggestionProvider<CommandSourceStack> AVAILABLE_CHANNELS = ((context, builder) -> {
        Set<String> configuredChannels = Webhooker.CONFIG.getChannelList().keySet();
        for (String channel : configuredChannels) {
            builder.suggest(channel);
        }

        return builder.buildFuture();
    });

    private static final SuggestionProvider<CommandSourceStack> AVAILABLE_TAGS = ((context, builder) -> {
        Set<String> configuredChannels = Webhooker.CONFIG.getTaggableList().keySet();
        for (String channel : configuredChannels) {
            builder.suggest(channel);
        }

        return builder.buildFuture();
    });

    public static void initialize(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(Webhooker.MOD_ID)
            .requires(src -> src.hasPermission(2))
            .then(getReloadCommand())
            .then(getSendCommand())
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> getReloadCommand() {
        return Commands.literal("reload")
            .executes((CommandContext<CommandSourceStack> context) -> {

                if (!Webhooker.CONFIG.reload()) {
                    Webhooker.LOGGER.error("Failed to reload config");
                    context.getSource().sendSystemMessage(Component.literal("Failed to reload config"));
                    return 0;
                }

                Webhooker.LOGGER.error("Config reloaded");
                context.getSource().sendSystemMessage(Component.literal("Config reloaded"));
                return 1;
            }
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> getSendCommand() {
        return Commands.literal("send")
            .then(Commands.argument("sender", StringArgumentType.word())
                .then(Commands.argument("message", StringArgumentType.string())
                    .then(Commands.argument("channel", StringArgumentType.word())
                        .suggests(AVAILABLE_CHANNELS)
                        .executes((CommandContext<CommandSourceStack> context) -> {
                            Discord.sendMessage(
                                StringArgumentType.getString(context, "sender"),
                                StringArgumentType.getString(context, "message"),
                                new String[]{StringArgumentType.getString(context, "channel")},
                                null
                            );
                            return 1;
                        })

                        .then(Commands.argument("tags", StringArgumentType.greedyString())
                            .suggests(AVAILABLE_TAGS)
                            .executes((CommandContext<CommandSourceStack> context) -> {
                                Discord.sendMessage(
                                    StringArgumentType.getString(context, "sender"),
                                    StringArgumentType.getString(context, "message"),
                                    new String[]{StringArgumentType.getString(context, "channel")},
                                    StringArgumentType.getString(context, "tags").split(" ")
                                );
                                return 1;
                            }
                        )
                    )
                )
            )
        );
    }
}
