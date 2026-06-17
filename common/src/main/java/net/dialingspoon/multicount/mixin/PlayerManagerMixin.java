package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.Multicount;
import net.dialingspoon.multicount.server.interfaces.PlayerManagerAdditions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.LevelResource;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(PlayerList.class)
public abstract class PlayerManagerMixin implements PlayerManagerAdditions {

	@Shadow @Final private MinecraftServer server;
	@Unique
	private final Map<UUID, int[]> pendingAccountSwaps = new ConcurrentHashMap<>();

	@Override
	public void setAccount(UUID playerUuid, int current, int to) {
		pendingAccountSwaps.put(playerUuid, new int[]{current, to});
	}

	@Inject(method = "save", at = @At("TAIL"))
	private void changePlayerData(ServerPlayer player, CallbackInfo info) {
		int[] accountSwap = pendingAccountSwaps.remove(player.getUUID());
		if (accountSwap != null) {
			int oldAccount = accountSwap[0];
			int newAccount = accountSwap[1];
			Multicount.accountStates.setValue(player.getUUID(), newAccount);

			File playerData = new File(server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile(), player.getStringUUID() + ".dat");
			rotateAccounts(playerData, oldAccount, newAccount);
			File advancements = new File(server.getWorldPath(LevelResource.PLAYER_ADVANCEMENTS_DIR).toFile(), player.getStringUUID() + ".json");
			rotateAccounts(advancements, oldAccount, newAccount);
			File stats = new File(server.getWorldPath(LevelResource.PLAYER_STATS_DIR).toFile(), player.getStringUUID() + ".json");
			rotateAccounts(stats, oldAccount, newAccount);
		}
	}

	@Unique
	private void rotateAccounts(File file, int oldAccount, int newAccount){

		try {
			File oldAccountFile = new File(file.getPath() + oldAccount);
			File newAccountFile = new File(file.getPath() + newAccount);

			Files.copy(file.toPath(), oldAccountFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			if (newAccountFile.exists()) {
				Files.copy(newAccountFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} else {
				Files.copy(file.toPath(), new File(file.getPath() + "_old").toPath(), StandardCopyOption.REPLACE_EXISTING);
				Files.deleteIfExists(file.toPath());
			}
		} catch (IOException e) {
			Multicount.LOGGER.error("Couldn't switch player data in {" + file.getPath() + "}", e);
		}
	}
}

