package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.Multicount;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Mixin(PlayerList.class)
public abstract class SingleplayerPlayerListMixin {

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "loadPlayerData", at = @At("RETURN"), cancellable = true)
    private void multicount$loadSelectedSingleplayerAccount(NameAndId nameAndId, CallbackInfoReturnable<Optional<CompoundTag>> cir) {
        if (!(this.server instanceof IntegratedServer) || !this.server.isSingleplayerOwner(nameAndId)) {
            return;
        }

        WorldData worldData = this.server.getWorldData();
        UUID baseUuid = worldData.getSinglePlayerUUID();
        if (baseUuid == null) {
            baseUuid = nameAndId.id();
        }

        File worldRoot = this.server.getWorldPath(LevelResource.ROOT).toFile();
        CompoundTag accountData = Multicount.accountHandler.getAccount(worldRoot, baseUuid.toString());
        cir.setReturnValue(Optional.ofNullable(accountData));
    }
}
