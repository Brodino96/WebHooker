package dev.brodino.webhooker;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Webhooker {
    public static final String MOD_ID = "webhooker";
    public static final Logger LOGGER = LoggerFactory.getLogger(Webhooker.MOD_ID);
    public static final Config CONFIG = new Config();

    public static void init() {
        Webhooker.LOGGER.info("Initializing WebHooker");
        Discord.initialize();

        CommandRegistrationEvent.EVENT.register((dispatcher, _s, _c) -> {
            Commands.initialize(dispatcher);
        });
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
}
