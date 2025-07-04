package net.atired.executiveorders.mixins;

import net.atired.executiveorders.init.EOMobEffectsInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {

    @Shadow @Final protected T handler;

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "keyPressed(III)Z",at=@At("HEAD"),cancellable = true)
    private void unkeyed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir){
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (player.hasStatusEffect(EOMobEffectsInit.SCRONGBONGLED_EFFECT)) {
                if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
                    this.close();
                    cir.setReturnValue(false);
                }
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
    @Inject(method = "mouseDragged",at=@At("HEAD"),cancellable = true)
    private void undragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir){
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (player.hasStatusEffect(EOMobEffectsInit.SCRONGBONGLED_EFFECT)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
    @Inject(method = "mouseClicked(DDI)Z",at=@At("HEAD"),cancellable = true)
    private void unclicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir){
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (player.hasStatusEffect(EOMobEffectsInit.SCRONGBONGLED_EFFECT)) {

                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}
