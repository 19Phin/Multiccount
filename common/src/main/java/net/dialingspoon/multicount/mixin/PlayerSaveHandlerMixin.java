package net.dialingspoon.multicount.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PlayerDataStorage.class)
public abstract class PlayerSaveHandlerMixin {

    @Inject(method = "load(Lnet/minecraft/server/players/NameAndId;Ljava/lang/String;)Ljava/util/Optional;", at = @At(value = "HEAD"), cancellable = true)
    private void returnEmpty(NameAndId playerConfigEntry, String extension, CallbackInfoReturnable<Optional<CompoundTag>> cir){
        if (extension.equals(".dat_old")) cir.setReturnValue(Optional.empty());
    }
}