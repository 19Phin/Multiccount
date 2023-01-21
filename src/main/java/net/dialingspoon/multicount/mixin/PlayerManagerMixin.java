package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.interfaces.AdvancementAdditions;
import net.dialingspoon.multicount.interfaces.PlayerManagerAdditions;
import net.dialingspoon.multicount.interfaces.StatHandlerAdditions;
import net.dialingspoon.multicount.interfaces.WorldSaveAdditions;
import net.minecraft.advancement.PlayerAdvancementTracker;
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
	private @Final Map<UUID, ServerStatHandler> statisticsMap;
	@Shadow
	private @Final Map<UUID, PlayerAdvancementTracker> advancementTrackers;
	@Shadow
	private @Final WorldSaveHandler saveHandler;
	@Override
	public WorldSaveHandler getSaveHandler(){return saveHandler;}

	//edit the playerdata on save
	@Inject
			(method = "savePlayerData", at = @At("TAIL"))
	private void changePlayerData(ServerPlayerEntity player, CallbackInfo info) {
		int old_data = ((WorldSaveAdditions)this.saveHandler).getAccount(false);
		int new_data = ((WorldSaveAdditions)this.saveHandler).getAccount(true);

		if(new_data != -1) {
			ServerStatHandler serverStatHandler2 = this.statisticsMap.get(player.getUuid());
			((StatHandlerAdditions)serverStatHandler2).saveSecond(new_data, old_data);

			PlayerAdvancementTracker playerAdvancementTracker2 = this.advancementTrackers.get(player.getUuid());
			((AdvancementAdditions)playerAdvancementTracker2).saveSecond(new_data, old_data);

			((WorldSaveAdditions)this.saveHandler).setAccount(-1, -1);
		}
	}
}

