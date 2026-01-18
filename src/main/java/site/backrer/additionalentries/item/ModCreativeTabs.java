package site.backrer.additionalentries.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import site.backrer.additionalentries.Additionalentries;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, Additionalentries.MODID);

    public static final RegistryObject<CreativeModeTab> ADDITIONAL_ENTRIES_TAB = CREATIVE_MODE_TABS.register(
            "additional_entries_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.QUALITY_REFORGE.get()))
                    .title(Component.translatable("creativetab.additional_entries_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.ENTRY_REFORGE.get());
                        pOutput.accept(ModItems.QUALITY_REFORGE.get());
                        pOutput.accept(ModItems.WEAPON_AFFIX_GEM.get());
                        pOutput.accept(ModItems.ARMOR_AFFIX_GEM.get());
                        pOutput.accept(ModItems.AUTO_WEAPON_AFFIX_GEM.get());
                        pOutput.accept(ModItems.AUTO_ARMOR_AFFIX_GEM.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
