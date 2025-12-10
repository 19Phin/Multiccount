package net.dialingspoon.multicount.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.dialingspoon.multicount.server.interfaces.PlayerAdditions;
import net.dialingspoon.multicount.server.interfaces.PlayerManagerAdditions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;

public class LanAccountCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher) {
        // /account [account number(optional)]
        return CommandManager.literal("account")
                        .then(CommandManager.argument("account number", IntegerArgumentType.integer(1))
                                .executes(ctx -> run(ctx.getSource(), getInteger(ctx, "account number"))))
                        .executes(ctx -> run(ctx.getSource(), -1)); // Executes the command without an account number
    }

    private static int run(ServerCommandSource source, int i) throws CommandSyntaxException{
        ServerPlayerEntity getplayer = source.getPlayer();
        if (getplayer == null) throw new SimpleCommandExceptionType(Text.literal("Account command must be run by player")).create();
        if (i != -1) {
            if (!getplayer.getUuidAsString().equals(MinecraftClient.getInstance().player.getUuidAsString())) {
                if (((PlayerAdditions) getplayer).getAccount() == i) {
                    throw new SimpleCommandExceptionType(Text.literal("That is the current account!")).create();
                } else {
                    // Kick to reload
                    ((PlayerManagerAdditions) source.getServer().getPlayerManager()).setAccount(((PlayerAdditions) getplayer).getAccount(), (i));
                    getplayer.networkHandler.disconnect(Text.literal("Switching accounts, please re-log."));
                }
            } else {
                throw new SimpleCommandExceptionType(Text.literal("Lan server source cannot use account command\nPlease log out and change on title screen")).create();
            }
        } else {
            Text text = Text.literal("Current account: " + ((PlayerAdditions) getplayer).getAccount());
            (source).sendFeedback(() -> text, false);
        }
        return 1;
    }
}