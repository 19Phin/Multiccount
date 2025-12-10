package net.dialingspoon.multicount.fabric;

import net.dialingspoon.multicount.Multicount;
import net.dialingspoon.multicount.server.command.AccountCommand;
import net.dialingspoon.multicount.server.command.MaxAccountCommand;
import net.dialingspoon.multicount.server.command.MaxAccountQueryCommand;
import net.dialingspoon.multicount.server.config.ModConfigs;
import net.dialingspoon.multicount.server.util.Updater;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class MulticountServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		Multicount.configs = new ModConfigs();

		// Check if updated from pre-1.0.1
		ServerLifecycleEvents.SERVER_STARTED.register(Updater::checkAndUpdateDataFormat);

		CommandRegistrationCallback.EVENT.register((sCSD, ctx, sel) -> AccountCommand.register(sCSD));
		CommandRegistrationCallback.EVENT.register((sCSD, ctx, sel) -> MaxAccountQueryCommand.register(sCSD));
		if (Multicount.configs.command) CommandRegistrationCallback.EVENT.register((sCSD, ctx, sel) -> MaxAccountCommand.register(sCSD));
	}
}
