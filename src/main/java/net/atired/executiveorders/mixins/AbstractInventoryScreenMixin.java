package net.atired.executiveorders.mixins;

import net.atired.executiveorders.init.EOMobEffectsInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractInventoryScreen.class)
public abstract class AbstractInventoryScreenMixin<T extends ScreenHandler> extends HandledScreen<T> {
    public AbstractInventoryScreenMixin(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",at=@At("HEAD"),cancellable = true)
    private void unrender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci){
        MinecraftClient client = MinecraftClient.getInstance();

        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            if (player.hasStatusEffect(EOMobEffectsInit.SCRONGBONGLED_EFFECT)) {
                client.mouse.lockCursor();
                this.applyBlur(delta);
                ci.cancel();
            }
        }

    }
}
