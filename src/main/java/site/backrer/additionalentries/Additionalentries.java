package site.backrer.additionalentries;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import site.backrer.additionalentries.config.ModCommonConfig;
import site.backrer.additionalentries.item.ModCreativeTabs;
import site.backrer.additionalentries.item.ModItems;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Additionalentries.MODID)
public class Additionalentries {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "additionalentries";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Additionalentries() {
        ModCommonConfig.register();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register event handlers
        MinecraftForge.EVENT_BUS.register(site.backrer.additionalentries.event.WeaknessAffixHandler.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent event) {
        site.backrer.additionalentries.command.AffixGemCommand.register(event.getDispatcher());
    }
}
