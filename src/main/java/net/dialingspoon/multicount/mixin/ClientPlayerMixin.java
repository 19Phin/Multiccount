package net.dialingspoon.multicount.mixin;

import com.mojang.authlib.GameProfile;
import net.dialingspoon.multicount.Multicount;
import net.dialingspoon.multicount.server.interfaces.PlayerAdditions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ClientPlayerMixin extends PlayerEntity implements PlayerAdditions {
    @Unique
    public int account;

    public ClientPlayerMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Override
    public int getAccount(){
        return account;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        account = Multicount.accountStates.getValue(uuid);
    }

    // Add nbt tag
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
        account = Multicount.accountStates.getValue(uuid);
        nbt.putInt("account", account);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt,CallbackInfo info) {account = nbt.getInt("account");}

    @Inject(method = "copyFrom", at = @At("TAIL"))
    public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive,CallbackInfo info) {account = ((PlayerAdditions)oldPlayer).getAccount();}
}