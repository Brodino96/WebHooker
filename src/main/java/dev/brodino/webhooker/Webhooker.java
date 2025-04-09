package dev.brodino.webhooker;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Webhooker implements ModInitializer {
    public static final String MOD_ID = "webhooker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final Config CONFIG = Config.createAndLoad();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing WebHooker");
        try {
            Discord.initialize();
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Discord webhooks", e);
        }
    }
}
