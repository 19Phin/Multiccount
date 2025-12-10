package net.dialingspoon.multicount;

import net.dialingspoon.multicount.server.config.ModConfigs;
import net.dialingspoon.multicount.util.AccountStates;
import net.dialingspoon.multicount.util.SingleplayerAccountHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Multicount {
	public static final String MOD_ID = "multicount";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static AccountStates accountStates;
	public static SingleplayerAccountHandler accountHandler = new SingleplayerAccountHandler();
	public static ModConfigs configs;
}
