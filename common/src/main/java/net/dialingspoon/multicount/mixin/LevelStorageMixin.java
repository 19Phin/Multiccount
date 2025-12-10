package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.Multicount;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.nio.file.Path;

@Mixin(LevelStorageSource.class)
public abstract class LevelStorageMixin {

    @Redirect(method = "readLevelDataTagFixed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorageSource;readLevelDataTagRaw(Ljava/nio/file/Path;)Lnet/minecraft/nbt/CompoundTag;"))
    private static CompoundTag readModifiedCompressed(Path path) throws IOException {
        CompoundTag fileNbt = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
        CompoundTag playerData = Multicount.accountHandler.getAccount(path.getParent().toFile());

        CompoundTag dataCompound = fileNbt.getCompound("Data").orElseThrow();
        if (playerData != null) {
            dataCompound.put("Player", playerData);
        } else {
            dataCompound.remove("Player");
        }

        return fileNbt;
    }
}