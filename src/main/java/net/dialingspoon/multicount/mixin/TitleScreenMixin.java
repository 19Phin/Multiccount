package net.dialingspoon.multicount.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.dialingspoon.multicount.Multicount;
import net.dialingspoon.multicount.MulticountClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }
    @Unique
    private static final ButtonTextures UP_BUTTON_TEXTURES = new ButtonTextures(
        Identifier.of(Multicount.MOD_ID, "widget/move_up"),
            Identifier.of(Multicount.MOD_ID, "widget/move_up_highlighted")
    );
    @Unique
    private static final ButtonTextures DOWN_BUTTON_TEXTURES = new ButtonTextures(
        Identifier.of(Multicount.MOD_ID, "widget/move_down"),
            Identifier.of(Multicount.MOD_ID, "widget/move_down_highlighted")
    );


    // On init set account from persistent state
    @Inject(method = "init", at = @At("TAIL"))
    private void Init(CallbackInfo ci) {
        // Add arrow buttons to title screen
        int l = this.height / 4 + 78;
        this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 117, l + 12, 11, 7, DOWN_BUTTON_TEXTURES, (button) -> {
            if (MulticountClient.accountHandler.account > 1) MulticountClient.accountHandler.account --;
        }, Text.literal("account down")));
        this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 117, l-12, 11, 7, UP_BUTTON_TEXTURES, (button) -> MulticountClient.accountHandler.account ++, Text.literal("account up")));
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
    private void renderNum(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci, @Local(ordinal = 1) float f) {
        // Add account number to title screen
        String accountString = String.valueOf(MulticountClient.accountHandler.account);
        int textWidth = this.textRenderer.getWidth(accountString); // Calculate the width of the text
        int xPos = (this.width - textWidth) / 2; // Calculate the x-position to center the text
        context.drawTextWithShadow(this.textRenderer, accountString, xPos - 111, this.height / 4 + 78, ColorHelper.withAlpha(f, -1));
    }

}