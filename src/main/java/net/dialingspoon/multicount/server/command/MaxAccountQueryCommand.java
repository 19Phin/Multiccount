package net.dialingspoon.multicount.server.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dialingspoon.multicount.server.MulticountServer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MaxAccountQueryCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        // /accountmax query <targets(optional)>
        dispatcher.register(CommandManager.literal("accountmax")
                .requires(source -> source.hasPermissionLevel(3))
                .then(CommandManager.literal("query")
                        .executes(context -> execute((ServerCommandSource) context.getSource(), null))
                        .then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile())
                                .executes(context -> execute((ServerCommandSource) context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets")))
                        )
                )
        );
    }

    private static int execute(ServerCommandSource source, @Nullable Collection<GameProfile> targets) throws CommandSyntaxException {
        Text text;
        // If player is specfied
        if (targets == null) {
            // Get default
            text = Text.literal("The default account maximum is " + MulticountServer.configs.accountNum);
        } else {
            // Get each player's account
            StringBuilder accounts = new StringBuilder();
            for (GameProfile target : targets) {
                String targetmax = MulticountServer.configs.configsList.get(String.valueOf(target.getId()));
                if (targetmax.equals("default")) accounts.append(target.getName() + " is allowed the default maximum accounts\n");
                else accounts.append(target.getName() + " is allowed " + targetmax + " accounts\n");
            }
            accounts.deleteCharAt(accounts.length() - 1);
            text = Text.literal(accounts.toString());
        }
        (source).sendFeedback(text, false);
        return 1;
    }
}