package net.dialingspoon.multicount.mixin;

import com.mojang.authlib.GameProfile;
import net.dialingspoon.multicount.Multicount;
import net.dialingspoon.multicount.server.MulticountServer;
import net.dialingspoon.multicount.server.interfaces.PlayerAdditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class PlayerMixin extends PlayerEntity implements PlayerAdditions {
    @Unique
    public int account;

    public PlayerMixin(World world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Override
    public int getAccount(){
        return account;
    }

    // On init set account from persistent state
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        if (!MulticountServer.configs.configsList.containsKey(uuidString))  MulticountServer.configs.setValue(uuidString, "default");
        account = Multicount.accountStates.getValue(uuid);
    }


    // Add nbt tags
    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void writeCustomDataToNbt(WriteView view, CallbackInfo ci) {
        view.putInt("account", account);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void readCustomDataFromNbt(ReadView view, CallbackInfo ci) {
        view.getOptionalInt("account").ifPresent(integer -> account = integer);
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive,CallbackInfo info) {account = ((PlayerAdditions)oldPlayer).getAccount();}
}