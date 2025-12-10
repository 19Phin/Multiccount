package net.dialingspoon.multicount.server.command;

import com.mojang.brigadier.CommandDispatcher;
import net.dialingspoon.multicount.Multicount;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.players.NameAndId;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MaxAccountQueryCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // /accountmax query <targets(optional)>
        dispatcher.register(Commands.literal("accountmax")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.literal("query")
                        .executes(context -> execute((CommandSourceStack) context.getSource(), null))
                        .then(Commands.argument("targets", GameProfileArgument.gameProfile())
                                .executes(context -> execute((CommandSourceStack) context.getSource(), GameProfileArgument.getGameProfiles(context, "targets")))
                        )
                )
        );
    }

    private static int execute(CommandSourceStack source, @Nullable Collection<NameAndId> targets){
        Component text;
        if (targets == null) {
            text = Component.literal("The default account maximum is " + Multicount.configs.accountNum);
        } else {
            StringBuilder accounts = new StringBuilder();
            for (NameAndId target : targets) {
                String targetmax = Multicount.configs.configsList.get(String.valueOf(target.id()));
                if (targetmax.equals("default")) accounts.append(target.name() + " is allowed the default maximum accounts\n");
                else accounts.append(target.name() + " is allowed " + targetmax + " accounts\n");
            }
            accounts.deleteCharAt(accounts.length() - 1);
            text = Component.literal(accounts.toString());
        }
        (source).sendSuccess(() -> text, false);
        return 1;
    }
}