package net.dialingspoon.multicount.fabric;

import net.dialingspoon.multicount.Multicount;
import net.dialingspoon.multicount.util.AccountStates;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedDataType;

public class MulticountFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		Identifier priorityPhase = Identifier.fromNamespaceAndPath(Multicount.MOD_ID, "default");
		ServerLifecycleEvents.SERVER_STARTED.addPhaseOrdering(priorityPhase, Event.DEFAULT_PHASE);
		ServerLifecycleEvents.SERVER_STARTED.register(priorityPhase, server ->
				Multicount.accountStates = server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(
						new SavedDataType<>(Multicount.MOD_ID, AccountStates::new, AccountStates.CODEC, DataFixTypes.LEVEL)
				));
	}
}
