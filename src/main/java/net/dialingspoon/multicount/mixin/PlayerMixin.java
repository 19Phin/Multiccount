package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.interfaces.PlayerAdditions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin implements PlayerAdditions {
    public int account = 1;
    @Override
    public void setAccount(int i) {account = i;}
    @Override
    public int getAccount() {
        return account;
    }
    @Inject
            (method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        nbt.putInt("account", account);
    }
    @Inject
            (method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt,CallbackInfo info) {
        account = nbt.getInt("account");
    }
    @Inject
            (method = "copyFrom", at = @At("TAIL"))
    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive,CallbackInfo info) {
        setAccount(((PlayerAdditions)oldPlayer).getAccount());
    }
}