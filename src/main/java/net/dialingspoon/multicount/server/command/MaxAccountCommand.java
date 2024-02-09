package net.dialingspoon.multicount.server.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.dialingspoon.multicount.server.MulticountServer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MaxAccountCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        // /accountmax [maxCount] <targets(optional)>
        dispatcher.register(CommandManager.literal("accountmax")
                .requires(source -> source.hasPermissionLevel(3))
                .then(CommandManager.argument("maxCount", IntegerArgumentType.integer(0))
                        .executes(context -> execute(
                                (ServerCommandSource) context.getSource(),
                                IntegerArgumentType.getInteger(context, "maxCount"),
                                null
                        ))
                        .then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile())
                                .executes(context -> execute(
                                        (ServerCommandSource) context.getSource(),
                                        IntegerArgumentType.getInteger(context, "maxCount"),
                                        GameProfileArgumentType.getProfileArgument(context, "targets")
                                ))
                        )
                )
        );
    }

    private static int execute(ServerCommandSource source, int maxCount, @Nullable Collection<GameProfile> targets){
        Map<String, String> map = new HashMap<>();
        Text text;
        // If player is specfied
        if (targets != null) {
            // If 0 set player's value to default
            if (maxCount == 0) {
                for (GameProfile target : targets) {
                    map.put(String.valueOf(target.getId()), "default");
                }
                text = Text.literal("Set " + targets.size() + " player's max accounts to default");
            } else {
                // Else set to new value
                String name = null;
                for (GameProfile target : targets) {
                    map.put(String.valueOf(target.getId()), String.valueOf(maxCount));
                    name = target.getName();
                }
                if (targets.size() == 1) {
                    text = Text.literal("Set " + name + "'s max accounts to " + maxCount);
                } else {
                    text = Text.literal("Set " + targets.size() + " player's max accounts to " + maxCount);
                }
            }
        } else {
            // If not, set default to value
            map.put("default_accounts", String.valueOf(maxCount));
            MulticountServer.configs.accountNum = maxCount;
            text = Text.literal("Set the default max accounts to " + maxCount);
        }
        MulticountServer.configs.setMaxAccountNum(map);
        (source).sendFeedback(text, true);
        return 1;
    }
}