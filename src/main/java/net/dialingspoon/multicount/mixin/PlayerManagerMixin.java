package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.Multicount;
import net.dialingspoon.multicount.server.interfaces.PlayerManagerAdditions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin implements PlayerManagerAdditions {

	@Shadow @Final private MinecraftServer server;
	@Unique
	int oldAccount = 0;
	@Unique
	int newAccount = 0;

	@Override
	public void setAccount(int current, int to) {
		oldAccount = current;
		newAccount = to;
	}

	@Inject(method = "savePlayerData", at = @At("TAIL"))
	private void changePlayerData(ServerPlayerEntity player, CallbackInfo info) {
		// Check if the new account is specified
		if (newAccount != 0) {
			Multicount.accountStates.setValue(player.getUuid(), newAccount);
			// Rotate accounts
			File playerData = new File(server.getSavePath(WorldSavePath.PLAYERDATA).toFile(), player.getUuidAsString() + ".dat");
			rotateAccounts(playerData);
			File advancements = new File(server.getSavePath(WorldSavePath.ADVANCEMENTS).toFile(), player.getUuidAsString() + ".json");
			rotateAccounts(advancements);
			File stats = new File(server.getSavePath(WorldSavePath.STATS).toFile(), player.getUuidAsString() + ".json");
			rotateAccounts(stats);
			// Reset values for next use
			newAccount = 0;
			oldAccount = 0;
		}
	}

	@Unique
	private void rotateAccounts(File file){

		try {
			File oldAccountFile = new File(file.getPath() + this.oldAccount);
			File newAccountFile = new File(file.getPath() + this.newAccount);

			// Copy the current file to the old account file
			Files.copy(file.toPath(), oldAccountFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			// Check if new account file exists
			if (newAccountFile.exists()) {
				// Copy contents from new account file to the original file
				Files.copy(newAccountFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} else {
				// If new account file doesn't exist, make the original file also not exist
				Files.deleteIfExists(file.toPath());
			}
		} catch (IOException e) {
			Multicount.LOGGER.error("Couldn't switch player data in {" + file.getPath() + "}", e);
		}
	}
}

