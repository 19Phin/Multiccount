package net.dialingspoon.multicount.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.world.PlayerSaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PlayerSaveHandler.class)
public abstract class PlayerSaveHandlerMixin {

    @Inject(method = "loadPlayerData(Lnet/minecraft/server/PlayerConfigEntry;Ljava/lang/String;)Ljava/util/Optional;", at = @At(value = "HEAD"), cancellable = true)
    private void returnEmpty(PlayerConfigEntry playerConfigEntry, String extension, CallbackInfoReturnable<Optional<NbtCompound>> cir){
        if (extension.equals(".dat_old")) cir.setReturnValue(Optional.empty());
    }
}