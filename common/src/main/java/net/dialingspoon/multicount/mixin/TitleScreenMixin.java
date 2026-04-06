package net.dialingspoon.multicount.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.dialingspoon.multicount.Multicount;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }
    @Unique
    private static final WidgetSprites UP_BUTTON_TEXTURES = new WidgetSprites(
        Identifier.fromNamespaceAndPath(Multicount.MOD_ID, "widget/move_up"),
            Identifier.fromNamespaceAndPath(Multicount.MOD_ID, "widget/move_up_highlighted")
    );
    @Unique
    private static final WidgetSprites DOWN_BUTTON_TEXTURES = new WidgetSprites(
        Identifier.fromNamespaceAndPath(Multicount.MOD_ID, "widget/move_down"),
            Identifier.fromNamespaceAndPath(Multicount.MOD_ID, "widget/move_down_highlighted")
    );

    @Inject(method = "init", at = @At("TAIL"))
    private void Init(CallbackInfo ci) {
        int l = this.height / 4 + 78;

        this.addRenderableWidget(new ImageButton(this.width / 2 - 117, l + 12, 11, 7, DOWN_BUTTON_TEXTURES, (button) -> {
            if (Multicount.accountHandler.account > 1) Multicount.accountHandler.account --;
        }, Component.literal("account down")));
        this.addRenderableWidget(new ImageButton(this.width / 2 - 117, l-12, 11, 7, UP_BUTTON_TEXTURES, (button) -> Multicount.accountHandler.account ++, Component.literal("account up")));
    }

    @Inject(method = "extractRenderState", at = @At(value = "TAIL"))
    private void renderNum(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci, @Local(ordinal = 1) float f) {
        String accountString = String.valueOf(Multicount.accountHandler.account);
        int textWidth = this.font.width(accountString);
        int xPos = (this.width - textWidth) / 2;
        graphics.text(this.font, accountString, xPos - 111, this.height / 4 + 78, ARGB.color(f, -1));
    }

}