package net.dialingspoon.multicount.server;

import net.dialingspoon.multicount.server.command.AccountCommand;
import net.dialingspoon.multicount.server.command.MaxAccountCommand;
import net.dialingspoon.multicount.server.command.MaxAccountQueryCommand;
import net.dialingspoon.multicount.server.config.ModConfigs;
import net.dialingspoon.multicount.server.util.Updater;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class MulticountServer implements DedicatedServerModInitializer {
	public static ModConfigs configs;

	@Override
	public void onInitializeServer() {
		// Setup data from configs
		configs = new ModConfigs();

		// Check if updated from pre-1.0.1
		ServerLifecycleEvents.SERVER_STARTED.register(Updater::checkAndUpdateDataFormat);
		// Register command, and max if specified
		CommandRegistrationCallback.EVENT.register(AccountCommand::register);
		CommandRegistrationCallback.EVENT.register(MaxAccountQueryCommand::register);
		if (configs.command) CommandRegistrationCallback.EVENT.register(MaxAccountCommand::register);
	}
}
