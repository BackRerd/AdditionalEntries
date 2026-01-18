package site.backrer.additionalentries.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import site.backrer.additionalentries.Additionalentries;

public class ModItems {
        public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
                        Additionalentries.MODID);

        public static final RegistryObject<Item> ENTRY_REFORGE = ITEMS.register("entry_reforge",
                        () -> new Item(new Item.Properties()));

        public static final RegistryObject<Item> QUALITY_REFORGE = ITEMS.register("quality_reforge",
                        () -> new Item(new Item.Properties()));

        // Creative-only gems (manual refresh)
        public static final RegistryObject<Item> WEAPON_AFFIX_GEM = ITEMS.register("weapon_affix_gem",
                        () -> new AffixGem(new Item.Properties().stacksTo(1), AffixGem.GemType.WEAPON));

        public static final RegistryObject<Item> ARMOR_AFFIX_GEM = ITEMS.register("armor_affix_gem",
                        () -> new AffixGem(new Item.Properties().stacksTo(1), AffixGem.GemType.ARMOR));

        // Survival-friendly gems (auto-refresh on pickup)
        public static final RegistryObject<Item> AUTO_WEAPON_AFFIX_GEM = ITEMS.register("auto_weapon_affix_gem",
                        () -> new AutoAffixGem(new Item.Properties().stacksTo(1), AutoAffixGem.GemType.WEAPON));

        public static final RegistryObject<Item> AUTO_ARMOR_AFFIX_GEM = ITEMS.register("auto_armor_affix_gem",
                        () -> new AutoAffixGem(new Item.Properties().stacksTo(1), AutoAffixGem.GemType.ARMOR));

        public static void register(IEventBus eventBus) {
                ITEMS.register(eventBus);
        }
}
