package net.dialingspoon.multicount.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.WorldSaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(WorldSaveHandler.class)
public abstract class WorldSaveHandlerMixin {

    @Inject(method = "loadPlayerData(Lnet/minecraft/entity/player/PlayerEntity;Ljava/lang/String;)Ljava/util/Optional;", at = @At(value = "HEAD"), cancellable = true)
    private void returnEmpty(PlayerEntity player, String extension, CallbackInfoReturnable<Optional<NbtCompound>> cir){
        if (extension.equals(".dat_old")) cir.setReturnValue(Optional.empty());
    }
}