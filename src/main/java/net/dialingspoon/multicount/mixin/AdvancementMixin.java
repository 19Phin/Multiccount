package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.Multicount;
import net.dialingspoon.multicount.interfaces.AdvancementAdditions;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.io.*;

@Mixin(PlayerAdvancementTracker.class)
public abstract class AdvancementMixin implements AdvancementAdditions {
    @Shadow
    private @Final File advancementFile;
    @Override
    public void saveSecond(int next, int old) {
        try{
            //uuid.dat[old]
            File file1 = new File(advancementFile.getParentFile(), advancementFile.getName() + old);
            //uuid.dat[new]
            File file2 = new File(advancementFile.getParentFile(), advancementFile.getName() + next);
            if (!file2.exists()){
                file2 = File.createTempFile(advancementFile.getName(), ".json", advancementFile.getParentFile());
            }
            Util.backupAndReplace(advancementFile, file2, file1);
        }catch (Exception var2) {
            Multicount.LOGGER.error("Couldn't switch player advancements in {" + advancementFile.toString() + "}", var2);
        }
    }
}

