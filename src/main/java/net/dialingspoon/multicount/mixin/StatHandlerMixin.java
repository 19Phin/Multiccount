package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.interfaces.StatHandlerAdditions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@Mixin(ServerStatHandler.class)
public abstract class StatHandlerMixin implements StatHandlerAdditions {
    @Shadow
    private @Final File file;
    @Shadow
    private static @Final Logger LOGGER;
    @Shadow
    private @Final MinecraftServer server;
    @Override
    public void saveSecond(byte next, byte old) {
        try{
            //uuid.dat(old)
            File file1 = new File(file.getParentFile(), "" + file.getName() + old);
            //uuid.dat(new)
            File file2 = new File(file.getParentFile(), "" + file.getName() + next);
            if (!file2.exists()){
                file2 = File.createTempFile(file.getName(), ".json", file.getParentFile());
            }
            Util.backupAndReplace(file, file2, file1);
        }catch (Exception var2) {
            LOGGER.error("Couldn't Switch stats", var2);
        }
    }
}