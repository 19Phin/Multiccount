package net.dialingspoon.multicount.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.dialingspoon.multicount.server.MulticountServer;
import net.dialingspoon.multicount.server.interfaces.PlayerAdditions;
import net.dialingspoon.multicount.server.interfaces.PlayerManagerAdditions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;

public class AccountCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, boolean dedicated) {
        // /account [account number(optional)]
        serverCommandSourceCommandDispatcher.register(
                CommandManager.literal("account")
                        .then(CommandManager.argument("account number", IntegerArgumentType.integer(1))
                                .executes(ctx -> run(ctx.getSource(), getInteger(ctx, "account number"))))
                        .executes(ctx -> run(ctx.getSource(), -1)) // Executes the command without an account number
        );
    }

    private static int run(ServerCommandSource source, int i) throws CommandSyntaxException{
        ServerPlayerEntity getplayer = source.getPlayer();
        if (getplayer == null) throw new SimpleCommandExceptionType(Text.of("Account command must be run by player")).create();
        if (i != -1) {
            String configValue = MulticountServer.configs.configsList.getOrDefault(getplayer.getUuidAsString(), "default");

            if (configValue.equals("default") ? (i <= MulticountServer.configs.accountNum) : (i <= Integer.parseInt(configValue))) {
                if (((PlayerAdditions) getplayer).getAccount() == i) {
                    throw new SimpleCommandExceptionType(Text.of("That is the current account!")).create();
                } else {
                    // Kick to reload
                    ((PlayerManagerAdditions) source.getServer().getPlayerManager()).setAccount(((PlayerAdditions) getplayer).getAccount(), (i));
                    getplayer.networkHandler.disconnect(Text.of("Switching accounts, please re-log."));
                }
            } else {
                // If value is out of player's max range fail
                if (source.hasPermissionLevel(3)) {
                    throw new SimpleCommandExceptionType(Text.of("You arent allowed that many accounts. You can change the maximum in the `config/multicount.properties` file")).create();
                } else {
                    throw new SimpleCommandExceptionType(Text.of("You arent allowed that many accounts")).create();
                }
            }
        } else {
            Text text = Text.of("Current account: " + ((PlayerAdditions) getplayer).getAccount());
            (source).sendFeedback(text, false);
        }
        return 1;
    }
}