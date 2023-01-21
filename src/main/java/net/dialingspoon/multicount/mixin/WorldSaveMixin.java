package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.Multicount;
import net.dialingspoon.multicount.interfaces.WorldSaveAdditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;
import net.minecraft.world.WorldSaveHandler;
import org.apache.commons.io.FileUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;

@Mixin(WorldSaveHandler.class)
public abstract class WorldSaveMixin implements WorldSaveAdditions {
	public int old_account = -1;
	public int new_account = -1;
	@Shadow
	private @Final File playerDataDir;
	@Override
	public void setAccount(int current, int to) {
		old_account = current;
		new_account = to;
	}
	@Override
	public int getAccount(boolean is_new) {
		if (is_new){
			return new_account;
		}else {
			return old_account;
		}
	}
    @Inject(method = "savePlayerData", at = @At("TAIL"))
    private void saveOther(PlayerEntity player, CallbackInfo info) {
		if(this.new_account != -1) {
			try {
				//uuid.dat
				File file = new File(this.playerDataDir, player.getUuidAsString() + ".dat");
				//uuid.dat[old]
				File file1 = new File(file.getParentFile(), file.getName() + old_account);
				//uuid.dat[new]
				File file2 = new File(file.getParentFile(), file.getName() + new_account);
				File file3 = File.createTempFile(file.getName(), ".dat", file.getParentFile());
				if(!file2.exists()){
					NbtCompound accountNum = new NbtCompound();
					accountNum.putInt("account", new_account);
					NbtIo.writeCompressed(accountNum, file3);
				}else{
					FileUtils.copyFile(file2,file3);
				}
				Util.backupAndReplace(file, file3, file1);
			} catch (IOException var11) {
				Multicount.LOGGER.error("Couldn't switch player advancements in {}", playerDataDir, var11);
			}
		}
	}
}

