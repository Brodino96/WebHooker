package dev.brodino.webhooker.fabric;

import dev.brodino.webhooker.Webhooker;
import net.fabricmc.api.ModInitializer;

public final class WebhookerFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Webhooker.init();
    }
}
