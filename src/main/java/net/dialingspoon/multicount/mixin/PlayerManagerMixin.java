package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.interfaces.AdvancementAdditions;
import net.dialingspoon.multicount.interfaces.PlayerManagerAdditions;
import net.dialingspoon.multicount.interfaces.StatHandlerAdditions;
import net.dialingspoon.multicount.interfaces.WorldSaveAdditions;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.world.WorldSaveHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin implements PlayerManagerAdditions {
	@Shadow
	private @Final MinecraftServer server;
	@Shadow
	private @Final Map<UUID, ServerStatHandler> statisticsMap;
	@Shadow
	private @Final Map<UUID, PlayerAdvancementTracker> advancementTrackers;
	@Shadow
	private @Final WorldSaveHandler saveHandler;
	@Override
	public WorldSaveHandler getSaveHandler(){return saveHandler;}
	@Inject
			(method = "savePlayerData", at = @At("TAIL"))
	private void savePlayerData(ServerPlayerEntity player, CallbackInfo info) {
		if(((WorldSaveAdditions)this.saveHandler).getAccount(true) != -1) {

			ServerStatHandler serverStatHandler2 = (ServerStatHandler) this.statisticsMap.get(player.getUuid());
			if (serverStatHandler2 != null) {
				((StatHandlerAdditions)serverStatHandler2).saveSecond(((WorldSaveAdditions)this.saveHandler).getAccount(true),((WorldSaveAdditions)this.saveHandler).getAccount(false));
			}

			PlayerAdvancementTracker playerAdvancementTracker2 = (PlayerAdvancementTracker) this.advancementTrackers.get(player.getUuid());
			if (playerAdvancementTracker2 != null) {
				((AdvancementAdditions)playerAdvancementTracker2).saveSecond(((WorldSaveAdditions)this.saveHandler).getAccount(true),((WorldSaveAdditions)this.saveHandler).getAccount(false));
			}
			((WorldSaveAdditions)this.saveHandler).setAccount((byte) -1, (byte) -1);
		}
	}
}

