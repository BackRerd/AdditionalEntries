package site.backrer.additionalentries.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import site.backrer.additionalentries.Additionalentries;
import site.backrer.additionalentries.config.ModCommonConfig;
import site.backrer.additionalentries.item.ModItems;
import site.backrer.classbioarsenal.api.AffixAPI;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Additionalentries.MODID)
public class AnvilEventHandler {
    private static final String REFORGE_TYPE_TAG = "additionalentries_reforge_type";
    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (left.isEmpty() || right.isEmpty()) {
            return;
        }

        // Check if left item is a weapon or armor from ClassBioArsenal
        boolean isWeapon = AffixAPI.isWeapon(left);
        boolean isArmor = AffixAPI.isArmor(left);

        if (!isWeapon && !isArmor) {
            return;
        }

        int reforgeType = -1; // 0 for entry, 1 for quality
        if (right.is(ModItems.ENTRY_REFORGE.get())) {
            reforgeType = 0;
        } else if (right.is(ModItems.QUALITY_REFORGE.get())) {
            reforgeType = 1;
        } else if (right.is(ModItems.WEAPON_AFFIX_GEM.get()) || right.is(ModItems.ARMOR_AFFIX_GEM.get())
                || right.is(ModItems.AUTO_WEAPON_AFFIX_GEM.get()) || right.is(ModItems.AUTO_ARMOR_AFFIX_GEM.get())) {
            // Transfer affixes from Affix Gem to weapon/armor
            ItemStack output = left.copy();

            // Validate gem type matches item type
            boolean isWeaponGem = right.is(ModItems.WEAPON_AFFIX_GEM.get())
                    || right.is(ModItems.AUTO_WEAPON_AFFIX_GEM.get());
            boolean isArmorGem = right.is(ModItems.ARMOR_AFFIX_GEM.get())
                    || right.is(ModItems.AUTO_ARMOR_AFFIX_GEM.get());
            boolean leftIsWeapon = AffixAPI.isWeapon(left);
            boolean leftIsArmor = AffixAPI.isArmor(left);

            // Check if gem type matches equipment type
            if ((isWeaponGem && !leftIsWeapon) || (isArmorGem && !leftIsArmor)) {
                return; // Mismatched types, don't allow
            }

            // Get stored affixes from gem
            java.util.Map<String, Integer> storedAffixes = site.backrer.additionalentries.item.AffixGem
                    .getStoredAffixes(right);

            if (!storedAffixes.isEmpty()) {
                // Transfer affixes to the equipment
                for (java.util.Map.Entry<String, Integer> entry : storedAffixes.entrySet()) {
                    AffixAPI.setItemAffix(output, entry.getKey(), entry.getValue());
                }

                event.setOutput(output);
                event.setCost(5); // Higher cost for gem transfer
                event.setMaterialCost(1); // Consume the gem
                return;
            }
        }

        if (reforgeType != -1) {
            ItemStack output = left.copy();
            CompoundTag tag = output.getOrCreateTag();
            tag.putInt(REFORGE_TYPE_TAG, reforgeType);

            event.setOutput(output);
            event.setCost(1); // Set a base cost
            event.setMaterialCost(1);
        }
    }

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        ItemStack result = event.getOutput();
        if (result.isEmpty() || !result.hasTag()) {
            return;
        }

        CompoundTag tag = result.getTag();
        if (tag != null && tag.contains(REFORGE_TYPE_TAG)) {
            int reforgeType = tag.getInt(REFORGE_TYPE_TAG);
            tag.remove(REFORGE_TYPE_TAG);
            if (tag.isEmpty()) {
                result.setTag(null);
            }

            double successRate = (reforgeType == 0) ? ModCommonConfig.ENTRY_REFORGE_SUCCESS_RATE.get()
                    : ModCommonConfig.QUALITY_REFORGE_SUCCESS_RATE.get();

            if (RANDOM.nextDouble() < successRate) {
                // Success: perform reroll
                boolean rerollRarity = (reforgeType == 1);
                boolean rerollAffix = (reforgeType == 0);
                AffixAPI.rerollItem(result, rerollRarity, rerollAffix);
            }
            // If failure, the item remains as a copy of the original (already handled by
            // output being left.copy())
        }
    }
}
