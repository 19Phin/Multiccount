package net.dialingspoon.multicount.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import net.dialingspoon.multicount.interfaces.PlayerAdditions;
import net.dialingspoon.multicount.interfaces.PlayerManagerAdditions;
import net.dialingspoon.multicount.interfaces.WorldSaveAdditions;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;

public class AccountCommand {
    static final Logger LOGGER = LogUtils.getLogger();
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(CommandManager.literal("account").then(CommandManager.argument("account number", IntegerArgumentType.integer(1,3)).executes(ctx -> run(ctx.getSource(), getInteger(ctx, "account number")))));
    }

    private static int run(ServerCommandSource source, int i) throws CommandSyntaxException{
        ServerPlayerEntity getplayer = source.getPlayer();
        if(getplayer == null) {
            return -1;
        }else if (((PlayerAdditions)getplayer).getAccount() == i-1) {
            throw new SimpleCommandExceptionType(Text.literal("That is the current account!")).create();
        }else {
            //playernbt=nbt
            ((WorldSaveAdditions)((PlayerManagerAdditions)getplayer.server.getPlayerManager()).getSaveHandler()).setAccount((byte)((PlayerAdditions)getplayer).getAccount(),(byte) (i-1));
            //kick to reload
            getplayer.networkHandler.disconnect(Text.literal("Switching accounts, please re-log."));
            return 1;
        }
    }
}