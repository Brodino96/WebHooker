package dev.brodino.webhooker;

import net.fabricmc.api.ModInitializer;
import dev.brodino.webhooker.Config;

public class Webhooker implements ModInitializer {

    public static final Config CONFIG = Config.createAndLoad();

    @Override
    public void onInitialize() {
        Discord.initialize();
    }

}
