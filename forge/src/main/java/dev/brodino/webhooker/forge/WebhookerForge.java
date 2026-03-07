package dev.brodino.webhooker.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.brodino.webhooker.Webhooker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Webhooker.MOD_ID)
public final class WebhookerForge {
    public WebhookerForge() {
        EventBuses.registerModEventBus(Webhooker.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Webhooker.init();
    }
}
