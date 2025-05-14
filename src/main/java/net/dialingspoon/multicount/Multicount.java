package net.dialingspoon.multicount;

import net.dialingspoon.multicount.util.AccountStates;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Multicount implements ModInitializer {
	public static final String MOD_ID = "multicount";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static AccountStates accountStates;

	@Override
	public void onInitialize() {
		// Initialize persistent state
		Identifier priorityPhase = new Identifier(MOD_ID, "default");
		ServerLifecycleEvents.SERVER_STARTED.addPhaseOrdering(priorityPhase, Event.DEFAULT_PHASE);
		ServerLifecycleEvents.SERVER_STARTED.register(priorityPhase, server ->
				accountStates = server.getWorld(World.OVERWORLD).getPersistentStateManager().getOrCreate(
						new PersistentState.Type<>(AccountStates::new, AccountStates::fromNbt, DataFixTypes.LEVEL), Multicount.MOD_ID
				));
	}
}
