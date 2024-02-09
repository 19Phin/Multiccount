package net.dialingspoon.multicount.mixin;

import net.dialingspoon.multicount.MulticountClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }
    @Unique
    Identifier ARROWS = new Identifier("textures/gui/resource_packs.png");


    // On init set account from persistent state
    @Inject(method = "init", at = @At("TAIL"))
    private void Init(CallbackInfo ci) {
        // Add arrow buttons to title screen
        int l = this.height / 4 + 78;
        addDrawableChild(new TexturedButtonWidget(this.width / 2 - 117, l + 12, 11, 8, 82, 20, 32, ARROWS, 256, 256, (button) -> {
            if (MulticountClient.accountHandler.account > 1) MulticountClient.accountHandler.account --;
        }, Text.literal("account down")));
        addDrawableChild(new TexturedButtonWidget(this.width / 2 - 117, l-12, 11, 8, 114, 4, 32, ARROWS, 256, 256, (button) -> {
            MulticountClient.accountHandler.account ++;
        }, Text.literal("account up")));
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;drawStringWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void renderNum(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, float f, int i, int j, int k, float g, int l) {
        // Add account number to title screen
        String accountString = String.valueOf(MulticountClient.accountHandler.account);
        int textWidth = this.textRenderer.getWidth(accountString); // Calculate the width of the text
        int xPos = (this.width - textWidth) / 2; // Calculate the x-position to center the text
        drawStringWithShadow(matrices, this.textRenderer, accountString, xPos - 111, this.height / 4 + 78, 16777215 | l);
    }

}