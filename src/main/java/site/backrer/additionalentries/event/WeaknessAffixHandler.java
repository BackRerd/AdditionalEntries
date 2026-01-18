package site.backrer.additionalentries.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import site.backrer.additionalentries.Additionalentries;
import site.backrer.classbioarsenal.api.AffixAPI;

import java.util.Random;

/**
 * Event handler for the Weakness armor affix.
 * Applies weakness debuff to attackers when player is hurt.
 */
@Mod.EventBusSubscriber(modid = Additionalentries.MODID)
public class WeaknessAffixHandler {

    private static final String WEAKNESS_AFFIX_ID = "WEAKNESS";
    private static final Random RANDOM = new Random();

    /**
     * Handles weakness application when a player is hurt.
     * Checks all armor pieces for WEAKNESS affix and calculates total application
     * chance.
     * If successful, applies weakness debuff to the attacker.
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onLivingHurt(LivingHurtEvent event) {
        // Only process for player entities being hurt
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // Only process on server side
        if (player.level().isClientSide()) {
            return;
        }

        // Check if there is an attacker
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) {
            return;
        }

        // Calculate total weakness application chance from all armor pieces
        double totalWeaknessChance = 0.0;

        // Check each armor slot
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() != EquipmentSlot.Type.ARMOR) {
                continue;
            }

            ItemStack armor = player.getItemBySlot(slot);
            if (armor.isEmpty()) {
                continue;
            }

            // Check if this armor piece is valid and has the WEAKNESS affix
            if (AffixAPI.isArmor(armor)) {
                double weaknessValue = AffixAPI.getArmorAffixValue(armor, WEAKNESS_AFFIX_ID);
                if (weaknessValue > 0.0) {
                    totalWeaknessChance += weaknessValue;
                }
            }
        }

        // If no weakness chance, return early
        if (totalWeaknessChance <= 0.0) {
            return;
        }

        // Cap weakness chance at 80% to prevent guaranteed application
        totalWeaknessChance = Math.min(totalWeaknessChance, 80.0);

        // Roll for weakness application
        double roll = RANDOM.nextDouble() * 100.0;

        if (roll < totalWeaknessChance) {
            // Weakness application successful!
            ServerLevel level = (ServerLevel) player.level();

            // Calculate weakness duration and amplifier based on chance
            int duration = (int) (60 + totalWeaknessChance * 2); // 3-7 seconds
            int amplifier = totalWeaknessChance > 50.0 ? 1 : 0; // Weakness II if chance > 50%

            // Apply weakness effect to attacker
            attacker.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS,
                    duration,
                    amplifier,
                    false,
                    true,
                    true));

            // Spawn weakness particles around attacker
            for (int i = 0; i < 10; i++) {
                double offsetX = (RANDOM.nextDouble() - 0.5) * 1.0;
                double offsetY = RANDOM.nextDouble() * 1.5;
                double offsetZ = (RANDOM.nextDouble() - 0.5) * 1.0;

                level.sendParticles(
                        ParticleTypes.ENTITY_EFFECT,
                        attacker.getX() + offsetX,
                        attacker.getY() + offsetY,
                        attacker.getZ() + offsetZ,
                        1,
                        0.5, 0.5, 0.5,
                        0.0);
            }

            // Play debuff sound
            level.playSound(
                    null,
                    attacker.getX(),
                    attacker.getY(),
                    attacker.getZ(),
                    SoundEvents.BREWING_STAND_BREW,
                    SoundSource.PLAYERS,
                    0.5f,
                    0.8f);
        }
    }
}
