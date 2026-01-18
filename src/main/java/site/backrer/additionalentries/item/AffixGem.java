package site.backrer.additionalentries.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
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

public class AffixGem extends Item {
    private static final Random RANDOM = new Random();
    private static final String STORED_AFFIXES_TAG = "stored_affixes";

    public enum GemType {
        WEAPON,
        ARMOR
    }

    private final GemType gemType;

    public AffixGem(Properties pProperties, GemType gemType) {
        super(pProperties);
        this.gemType = gemType;
    }

    public GemType getGemType() {
        return gemType;
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(@Nonnull Level pLevel, @Nonnull Player pPlayer,
            @Nonnull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide) {
            // Randomly generate and store affixes
            randomizeStoredAffixes(stack);
            pPlayer.displayClientMessage(Component.translatable("message.additionalentries.affixes_refreshed"), true);
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack pStack, @Nullable Level pLevel,
            @Nonnull List<Component> pTooltipComponents, @Nonnull TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        Map<String, Integer> storedAffixes = getStoredAffixes(pStack);
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
            pTooltipComponents.add(Component.translatable("tooltip.additionalentries.right_click_generate"));
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
        setStoredAffixes(stack, selectedAffixes);
    }

    public static Map<String, Integer> getStoredAffixes(ItemStack stack) {
        Map<String, Integer> affixes = new HashMap<>();
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(STORED_AFFIXES_TAG)) {
            CompoundTag affixTag = tag.getCompound(STORED_AFFIXES_TAG);
            for (String key : affixTag.getAllKeys()) {
                affixes.put(key, affixTag.getInt(key));
            }
        }
        return affixes;
    }

    public static void setStoredAffixes(ItemStack stack, Map<String, Integer> affixes) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag affixTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : affixes.entrySet()) {
            affixTag.putInt(entry.getKey(), entry.getValue());
        }
        tag.put(STORED_AFFIXES_TAG, affixTag);
    }

    public static void clearStoredAffixes(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            tag.remove(STORED_AFFIXES_TAG);
        }
    }

    private String toRoman(int number) {
        if (number <= 0 || number > 10)
            return String.valueOf(number);
        String[] romanNumerals = { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X" };
        return romanNumerals[number - 1];
    }
}
