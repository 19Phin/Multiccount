package net.dialingspoon.multicount;

import net.dialingspoon.multicount.util.SingleplayerAccountHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.MinecraftClient;

import java.util.UUID;

public class MulticountClient implements ClientModInitializer {
	public static SingleplayerAccountHandler accountHandler;

	@Override
	public void onInitializeClient() {

		// Get the player's GameProfile
		accountHandler = new SingleplayerAccountHandler(MinecraftClient.getInstance().getSession().getUuid());

		// Copy account data to save file on world close
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			Multicount.accountStates.setValue(UUID.fromString(accountHandler.uuid), accountHandler.account);
			accountHandler.saveAccount(server);
		});
	}
}
