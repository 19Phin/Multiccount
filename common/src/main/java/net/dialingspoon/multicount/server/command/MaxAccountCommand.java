package net.dialingspoon.multicount.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.dialingspoon.multicount.Multicount;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.players.NameAndId;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MaxAccountCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // /accountmax [maxCount] <targets(optional)>
        dispatcher.register(Commands.literal("accountmax")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                .then(Commands.argument("maxCount", IntegerArgumentType.integer(0))
                        .executes(context -> execute(
                                (CommandSourceStack) context.getSource(),
                                IntegerArgumentType.getInteger(context, "maxCount"),
                                null
                        ))
                        .then(Commands.argument("targets", GameProfileArgument.gameProfile())
                                .executes(context -> execute(
                                        (CommandSourceStack) context.getSource(),
                                        IntegerArgumentType.getInteger(context, "maxCount"),
                                        GameProfileArgument.getGameProfiles(context, "targets")
                                ))
                        )
                )
        );
    }

    private static int execute(CommandSourceStack source, int maxCount, @Nullable Collection<NameAndId> targets){
        Map<String, String> map = new HashMap<>();
        Component text;
        if (targets != null) {
            if (maxCount == 0) {
                for (NameAndId target : targets) {
                    map.put(String.valueOf(target.id()), "default");
                }
                text = Component.literal("Set " + targets.size() + " player's max accounts to default");
            } else {
                String name = null;
                for (NameAndId target : targets) {
                    map.put(String.valueOf(target.id()), String.valueOf(maxCount));
                    name = target.name();
                }
                if (targets.size() == 1) {
                    text = Component.literal("Set " + name + "'s max accounts to " + maxCount);
                } else {
                    text = Component.literal("Set " + targets.size() + " player's max accounts to " + maxCount);
                }
            }
        } else {
            map.put("default_accounts", String.valueOf(maxCount));
            Multicount.configs.accountNum = maxCount;
            text = Component.literal("Set the default max accounts to " + maxCount);
        }
        Multicount.configs.setMaxAccountNum(map);
        (source).sendSuccess(() -> text, true);
        return 1;
    }
}