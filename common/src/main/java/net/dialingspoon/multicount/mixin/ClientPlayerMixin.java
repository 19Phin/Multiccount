package net.dialingspoon.multicount.mixin;

import com.mojang.authlib.GameProfile;
import net.dialingspoon.multicount.Multicount;
import net.dialingspoon.multicount.server.interfaces.PlayerAdditions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ClientPlayerMixin extends Player implements PlayerAdditions {
    @Unique
    public int account;

    public ClientPlayerMixin(Level world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Override
    public int getAccount(){
        return account;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        account = Multicount.accountStates.getValue(uuid);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeCustomDataToNbt(ValueOutput view, CallbackInfo ci) {
        account = Multicount.accountStates.getValue(uuid);
        view.putInt("account", account);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readCustomDataFromNbt(ValueInput view, CallbackInfo ci) {
        view.getInt("account").ifPresent(integer -> account = integer);
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    public void copyFrom(ServerPlayer oldPlayer, boolean alive,CallbackInfo info) {account = ((PlayerAdditions)oldPlayer).getAccount();}
}