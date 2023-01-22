package net.dialingspoon.multicount;

import net.dialingspoon.multicount.command.AccountCommand;
import net.dialingspoon.multicount.config.ModConfigs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Multicount implements ModInitializer {
	public static final String MOD_ID = "multicount";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModConfigs.registerConfigs();

		CommandRegistrationCallback.EVENT.register(AccountCommand::register);
	}
}
