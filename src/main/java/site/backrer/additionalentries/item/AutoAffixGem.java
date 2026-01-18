package site.backrer.additionalentries.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import site.backrer.classbioarsenal.api.AffixAPI;
import site.backrer.classbioarsenal.weapon.ConfigManager;
import site.backrer.classbioarsenal.weapon.config.AffixConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class AutoAffixGem extends Item {
    private static final Random RANDOM = new Random();
    private static final String STORED_AFFIXES_TAG = "stored_affixes";
    private static final String INITIALIZED_TAG = "affix_initialized";

    public enum GemType {
        WEAPON,
        ARMOR
    }

    private final GemType gemType;

    public AutoAffixGem(Properties pProperties, GemType gemType) {
        super(pProperties);
        this.gemType = gemType;
    }

    public GemType getGemType() {
        return gemType;
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack pStack, @Nonnull Level pLevel, @Nonnull Entity pEntity,
            int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);

        // Only initialize once when item enters inventory
        if (!pLevel.isClientSide && !isInitialized(pStack)) {
            randomizeStoredAffixes(pStack);
            setInitialized(pStack);
        }
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable Level pLevel,
            @Nonnull List<Component> pTooltipComponents, @Nonnull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        Map<String, Integer> storedAffixes = AffixGem.getStoredAffixes(pStack);
        if (!storedAffixes.isEmpty()) {
            pTooltipComponents.add(Component.translatable("tooltip.additionalentries.stored_affixes"));

            for (Map.Entry<String, Integer> entry : storedAffixes.entrySet()) {
                String affixId = entry.getKey();
                int level = entry.getValue();

                // Get affix definition to show display name
                AffixConfig.AffixEntry affixDef = gemType == GemType.WEAPON
                        ? AffixAPI.getWeaponAffixDefinition(affixId)
                        : AffixAPI.getArmorAffixDefinition(affixId);

                if (affixDef != null) {
                    String displayName = affixDef.display_name;
                    String romanLevel = toRoman(level);
                    pTooltipComponents.add(Component.literal("§a" + displayName + " " + romanLevel));
                } else {
                    pTooltipComponents.add(Component.literal("§a" + affixId + " (Lv." + level + ")"));
                }
            }
        } else {
            pTooltipComponents.add(Component.translatable("tooltip.additionalentries.auto_generate"));
        }
    }

    private void randomizeStoredAffixes(ItemStack stack) {
        // Get all available affixes based on gem type
        List<AffixConfig.AffixEntry> availableAffixes = gemType == GemType.WEAPON
                ? ConfigManager.AFFIX_CONFIG.affixes
                : ConfigManager.ARMOR_AFFIX_CONFIG.affixes;

        if (availableAffixes == null || availableAffixes.isEmpty()) {
            return;
        }

        // Select 1-3 random affixes
        int affixCount = 1 + RANDOM.nextInt(3);
        Map<String, Integer> selectedAffixes = new HashMap<>();
        List<AffixConfig.AffixEntry> pool = new ArrayList<>(availableAffixes);

        while (selectedAffixes.size() < affixCount && !pool.isEmpty()) {
            int index = RANDOM.nextInt(pool.size());
            AffixConfig.AffixEntry affix = pool.remove(index);
            int level = 1 + RANDOM.nextInt(Math.max(1, affix.max_level));
            selectedAffixes.put(affix.id, level);
        }

        // Store in NBT
        AffixGem.setStoredAffixes(stack, selectedAffixes);
    }

    private boolean isInitialized(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(INITIALIZED_TAG);
    }

    private void setInitialized(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(INITIALIZED_TAG, true);
    }

    private String toRoman(int number) {
        if (number <= 0 || number > 10)
            return String.valueOf(number);
        String[] romanNumerals = { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X" };
        return romanNumerals[number - 1];
    }
}
