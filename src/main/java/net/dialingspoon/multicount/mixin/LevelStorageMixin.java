package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.MulticountClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.nio.file.Path;

@Mixin(LevelStorage.class)
public abstract class LevelStorageMixin {

    @Redirect(method = "readLevelProperties(Ljava/nio/file/Path;Lcom/mojang/datafixers/DataFixer;)Lcom/mojang/serialization/Dynamic;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorage;readLevelProperties(Ljava/nio/file/Path;)Lnet/minecraft/nbt/NbtCompound;"))
    private static NbtCompound readModifiedCompressed(Path path) throws IOException {
        // Replace player data in .dat with custom data
        NbtCompound fileNbt = NbtIo.readCompressed(path, NbtTagSizeTracker.ofUnlimitedBytes());
        NbtCompound playerData = MulticountClient.accountHandler.getAccount(path.getParent().toFile());

        NbtCompound dataCompound = fileNbt.getCompound("Data");
        if (playerData != null) {
            dataCompound.put("Player", playerData);
        } else {
            dataCompound.remove("Player");
        }

        return fileNbt;
    }
}