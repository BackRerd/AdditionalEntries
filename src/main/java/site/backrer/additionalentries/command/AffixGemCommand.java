package site.backrer.additionalentries.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import site.backrer.additionalentries.item.AffixGem;
import site.backrer.additionalentries.item.AutoAffixGem;
import site.backrer.classbioarsenal.weapon.ConfigManager;
import site.backrer.classbioarsenal.weapon.config.AffixConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AffixGemCommand {
    private static final SuggestionProvider<CommandSourceStack> AFFIX_SUGGESTIONS = (context, builder) -> {
        List<String> suggestions = new ArrayList<>();

        try {
            Player player = context.getSource().getPlayerOrException();
            ItemStack stack = player.getMainHandItem();

            if (stack.getItem() instanceof AffixGem gem) {
                if (gem.getGemType() == AffixGem.GemType.WEAPON) {
                    addWeaponAffixes(suggestions);
                } else {
                    addArmorAffixes(suggestions);
                }
            } else if (stack.getItem() instanceof AutoAffixGem gem) {
                if (gem.getGemType() == AutoAffixGem.GemType.WEAPON) {
                    addWeaponAffixes(suggestions);
                } else {
                    addArmorAffixes(suggestions);
                }
            } else {
                // Fallback: suggest both if not holding a gem (though command checks for gem
                // later)
                addWeaponAffixes(suggestions);
                addArmorAffixes(suggestions);
            }
        } catch (CommandSyntaxException e) {
            // Fallback if not a player or other error
            addWeaponAffixes(suggestions);
            addArmorAffixes(suggestions);
        }

        return SharedSuggestionProvider.suggest(suggestions, builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("affixgem")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("add")
                        .then(Commands.argument("affix", StringArgumentType.string())
                                .suggests(AFFIX_SUGGESTIONS)
                                .then(Commands.argument("level", IntegerArgumentType.integer(1))
                                        .executes(AffixGemCommand::addAffix))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("affix", StringArgumentType.string())
                                .suggests(AFFIX_SUGGESTIONS)
                                .executes(AffixGemCommand::removeAffix)))
                .then(Commands.literal("clear")
                        .executes(AffixGemCommand::clearAffixes)));
    }

    private static int addAffix(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();

        if (isValidGem(stack)) {
            String affixId = StringArgumentType.getString(context, "affix");
            int level = IntegerArgumentType.getInteger(context, "level");

            Map<String, Integer> affixes = AffixGem.getStoredAffixes(stack);
            affixes.put(affixId, level);
            AffixGem.setStoredAffixes(stack, affixes);

            String displayName = getAffixDisplayName(affixId);
            context.getSource().sendSuccess(
                    () -> Component.translatable("command.additionalentries.affix_added", displayName, toRoman(level)),
                    true);
            return 1;
        } else {
            context.getSource().sendFailure(Component.translatable("command.additionalentries.hold_gem"));
            return 0;
        }
    }

    private static int removeAffix(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();

        if (isValidGem(stack)) {
            String affixId = StringArgumentType.getString(context, "affix");

            Map<String, Integer> affixes = AffixGem.getStoredAffixes(stack);
            if (affixes.containsKey(affixId)) {
                affixes.remove(affixId);
                AffixGem.setStoredAffixes(stack, affixes);

                String displayName = getAffixDisplayName(affixId);
                context.getSource().sendSuccess(
                        () -> Component.translatable("command.additionalentries.affix_removed", displayName), true);
                return 1;
            } else {
                context.getSource().sendFailure(Component.translatable("command.additionalentries.affix_not_found"));
                return 0;
            }
        } else {
            context.getSource().sendFailure(Component.translatable("command.additionalentries.hold_gem"));
            return 0;
        }
    }

    private static int clearAffixes(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        ItemStack stack = player.getMainHandItem();

        if (isValidGem(stack)) {
            AffixGem.clearStoredAffixes(stack);
            context.getSource().sendSuccess(() -> Component.translatable("command.additionalentries.affixes_cleared"),
                    true);
            return 1;
        } else {
            context.getSource().sendFailure(Component.translatable("command.additionalentries.hold_gem"));
            return 0;
        }
    }

    private static boolean isValidGem(ItemStack stack) {
        return stack.getItem() instanceof AffixGem || stack.getItem() instanceof AutoAffixGem;
    }

    private static String getAffixDisplayName(String affixId) {
        // Try weapon affix first
        AffixConfig.AffixEntry weaponAffix = site.backrer.classbioarsenal.api.AffixAPI
                .getWeaponAffixDefinition(affixId);
        if (weaponAffix != null) {
            return weaponAffix.display_name;
        }

        // Try armor affix
        AffixConfig.AffixEntry armorAffix = site.backrer.classbioarsenal.api.AffixAPI.getArmorAffixDefinition(affixId);
        if (armorAffix != null) {
            return armorAffix.display_name;
        }

        return affixId;
    }

    private static String toRoman(int number) {
        if (number <= 0 || number > 10)
            return String.valueOf(number);
        String[] romanNumerals = { "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X" };
        return romanNumerals[number - 1];
    }

    private static void addWeaponAffixes(List<String> suggestions) {
        if (ConfigManager.AFFIX_CONFIG != null && ConfigManager.AFFIX_CONFIG.affixes != null) {
            for (AffixConfig.AffixEntry entry : ConfigManager.AFFIX_CONFIG.affixes) {
                suggestions.add(entry.id);
            }
        }
    }

    private static void addArmorAffixes(List<String> suggestions) {
        if (ConfigManager.ARMOR_AFFIX_CONFIG != null && ConfigManager.ARMOR_AFFIX_CONFIG.affixes != null) {
            for (AffixConfig.AffixEntry entry : ConfigManager.ARMOR_AFFIX_CONFIG.affixes) {
                suggestions.add(entry.id);
            }
        }
    }
}
