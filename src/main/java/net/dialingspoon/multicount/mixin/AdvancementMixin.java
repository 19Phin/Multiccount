package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.interfaces.AdvancementAdditions;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.*;
import java.nio.charset.Charset;

@Mixin(PlayerAdvancementTracker.class)
public abstract class AdvancementMixin implements AdvancementAdditions {
    @Shadow
    private @Final File advancementFile;
    @Shadow
    private static @Final Logger LOGGER;
    @Shadow
    private ServerPlayerEntity owner;
    @Shadow
    private @Final PlayerManager playerManager;
    @Override
    public void saveSecond(byte next, byte old) {
        try{
            //uuid.dat(old)
            File file1 = new File(advancementFile.getParentFile(), "" + advancementFile.getName() + old);
            //uuid.dat(new)
            File file2 = new File(advancementFile.getParentFile(), "" + advancementFile.getName() + next);
            if (!file2.exists()){
                file2 = File.createTempFile(advancementFile.getName(), ".json", advancementFile.getParentFile());
            }
            Util.backupAndReplace(advancementFile, file2, file1);
        }catch (Exception var2) {
            LOGGER.error("Couldn't Switch stats", var2);
        }
    }
}

