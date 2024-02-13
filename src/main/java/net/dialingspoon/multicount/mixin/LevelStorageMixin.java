package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.MulticountClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.File;
import java.io.IOException;

@Mixin(LevelStorage.class)
public abstract class LevelStorageMixin {

    @Redirect(method = "method_29582(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/resource/DataConfiguration;Lnet/minecraft/registry/Registry;Lcom/mojang/serialization/Lifecycle;Ljava/nio/file/Path;Lcom/mojang/datafixers/DataFixer;)Lcom/mojang/datafixers/util/Pair;", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtIo;readCompressed(Ljava/io/File;)Lnet/minecraft/nbt/NbtCompound;"))
    private static NbtCompound readModifiedCompressed(File file) throws IOException {
        // Replace player data in .dat with custom data
        NbtCompound fileNbt = NbtIo.readCompressed(file);
        NbtCompound playerData = MulticountClient.accountHandler.getAccount(file.getParentFile());

        NbtCompound dataCompound = fileNbt.getCompound("Data");
        if (playerData != null) {
            dataCompound.put("Player", playerData);
        } else {
            dataCompound.remove("Player");
        }

        return fileNbt;
    }
}