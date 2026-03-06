package dev.brodino.webhooker.forge;

import dev.brodino.webhooker.Webhooker;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Webhooker.MOD_ID)
public final class WebhookerForge {
    public WebhookerForge() {
        EventBuses.registerModEventBus(Webhooker.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Webhooker.init();
    }
}
