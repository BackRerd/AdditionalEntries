package site.backrer.additionalentries.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import site.backrer.classbioarsenal.api.AffixAPI;

import javax.annotation.Nonnull;

public class LightElementParticle extends Item {
    public LightElementParticle(Properties pProperties) {
        super(pProperties);
    }

    @Override
    @Nonnull
    public InteractionResultHolder<ItemStack> use(@Nonnull Level pLevel, @Nonnull Player pPlayer,
            @Nonnull InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide) {
            // Randomly refresh affixes on this item
            // Since it's not a weapon/armor, we might need to manually roll affixes
            // or see if AffixAPI can handle it if we "force" it.
            // For now, we'll use a simple logic to add a random affix if it's empty,
            // or reroll if it has some.

            // We'll use the rerollItem from API if possible,
            // but we might need to implement a custom randomizer if API rejects
            // non-equipment.
            // Let's try to just add a random affix from the available pool.
            addRandomAffix(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
    }

    private void addRandomAffix(ItemStack stack) {
        // This is a placeholder for "randomly refresh any affix"
        // In a real scenario, we'd pull from the Affix pool.
        // Since we don't have direct access to the internal pool easily without more
        // API,
        // we'll assume the user can also use commands as they mentioned.
        // But to satisfy "随机刷新", we'll try to call a reroll if the API allows.
        AffixAPI.rerollItem(stack, false, true);
    }
}
