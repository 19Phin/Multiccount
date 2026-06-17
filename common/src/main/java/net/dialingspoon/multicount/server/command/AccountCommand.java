package net.dialingspoon.multicount.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.dialingspoon.multicount.Multicount;
import net.dialingspoon.multicount.server.interfaces.PlayerAdditions;
import net.dialingspoon.multicount.server.interfaces.PlayerManagerAdditions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;

public class AccountCommand {
    public static void register(CommandDispatcher<CommandSourceStack> serverCommandSourceCommandDispatcher) {
        // /account [account number(optional)]
        serverCommandSourceCommandDispatcher.register(
                Commands.literal("account")
                        .then(Commands.argument("account number", IntegerArgumentType.integer(1))
                                .executes(ctx -> run(ctx.getSource(), getInteger(ctx, "account number"))))
                        .executes(ctx -> run(ctx.getSource(), -1)) // Executes the command without an account number
        );
    }

    private static int run(CommandSourceStack source, int i) throws CommandSyntaxException{
        ServerPlayer getplayer = source.getPlayer();
        if (getplayer == null) throw new SimpleCommandExceptionType(Component.literal("Account command must be run by player")).create();
        if (i != -1) {
            String configValue = Multicount.configs.configsList.getOrDefault(getplayer.getStringUUID(), "default");

            if (configValue.equals("default") ? (i <= Multicount.configs.accountNum) : (i <= Integer.parseInt(configValue))) {
                if (((PlayerAdditions) getplayer).getAccount() == i) {
                    throw new SimpleCommandExceptionType(Component.literal("That is the current account!")).create();
                } else {
                    ((PlayerManagerAdditions) source.getServer().getPlayerList())
                            .setAccount(getplayer.getUUID(), ((PlayerAdditions) getplayer).getAccount(), i);
                    getplayer.connection.disconnect(Component.literal("Switching accounts, please re-log."));
                }
            } else {
                if (source.permissions().hasPermission(Permissions.COMMANDS_OWNER)) {
                    throw new SimpleCommandExceptionType(Component.literal("You arent allowed that many accounts. You can change the maximum in the `config/multicount.properties` file")).create();
                } else {
                    throw new SimpleCommandExceptionType(Component.literal("You arent allowed that many accounts")).create();
                }
            }
        } else {
            Component text = Component.literal("Current account: " + ((PlayerAdditions) getplayer).getAccount());
            (source).sendSuccess(() -> text, false);
        }
        return 1;
    }
}