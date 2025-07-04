package net.atired.executiveorders.client.tooltips;

import com.mojang.blaze3d.systems.RenderSystem;
import net.atired.executiveorders.ExecutiveOrders;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;

public class ClientPalePileTooltipData implements TooltipComponent {
    private List<ItemStack> stacks = List.of();
    private static final Identifier BLOB = ExecutiveOrders.id("hud/blob");
    private static final Identifier BLOB2 = ExecutiveOrders.id("hud/blob2");

    public ClientPalePileTooltipData(PalePileTooltipData stacksFrom){
        stacks = stacksFrom.getStacks();
    }
    @Override
    public int getHeight() {
        return 24;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 72;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {

        TooltipComponent.super.drawText(textRenderer, x, y, matrix, vertexConsumers);
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        if(this.stacks.isEmpty()||this.stacks.getFirst().isEmpty())
            return;
        int i = 0;
        long time = MinecraftClient.getInstance().world.getTime();
        int amount = 0;
        float delta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
        for(ItemStack stack : this.stacks){
            amount+=stack.getCount();
        }
        int timed = (int) (time/16);
        timed%=3;
        for(ItemStack stack : this.stacks){
            for(int j = 0; j < stack.getCount(); j++){
                context.getMatrices().push();

                float sin = (float) Math.sin((time+delta)/16+i*2)*3;
                float cosin = (float) Math.cos((time+delta)/16+i*2)*0.5f;
                MatrixStack.Entry entry = context.getMatrices().peek();
                RenderSystem.setShaderColor(1f,1f,1f,0.2f);
                Quaternionf quaternionf = new Quaternionf().rotateXYZ(0,0,-(time+delta)/10);
                Vec3d dir = new Vec3d(0,0,sin).rotateY(-(time+delta)/10);
                entry.getPositionMatrix().rotateAround(quaternionf,x+6*i+8,y+8,400.0F-i);
                entry.getPositionMatrix().translate((float) dir.x, (float) dir.z,0);
                context.drawGuiTexture(BLOB2,16,48,0,timed*16, x+6*i, y,1,16,16);
                entry.getPositionMatrix().translate((float) -dir.x, (float) -dir.z,0);
                quaternionf = new Quaternionf().rotateXYZ(0,0,(time+delta)/10+(time+delta+i*6)/20);
                entry.getPositionMatrix().rotateAround(quaternionf,x+6*i+8,y+8,400.0F-i);
                context.drawGuiTexture(BLOB, x+6*i, y,2, 16, 16);
                quaternionf = new Quaternionf().rotateXYZ(0,0,-(time+delta+i*6)/20);
                entry.getPositionMatrix().rotateAround(quaternionf,x+6*i+8,y+8,400.0F-i);

                 quaternionf = new Quaternionf().rotateXYZ(0,0,-sin/10);
                entry.getPositionMatrix().rotateAround(quaternionf,x+6*i+8,y+8,400.0F-i);
                entry.getPositionMatrix().translate(cosin,sin,0);
                RenderSystem.setShaderColor(1f-((float) i /amount)/2,1f-((float) i /amount)/2,1f,1f-((float) i /amount)/1.1f);
                entry.getPositionMatrix().translate(0,0,-i);
                context.drawItem(stack,x+6*i,y);
                RenderSystem.setShaderColor(1f,1f,1f,1f);
                entry.getPositionMatrix().translate(-cosin,-sin,i);

                i+=1;
                quaternionf = new Quaternionf().rotateXYZ(0,0,sin/10);
                entry.getPositionMatrix().rotateAround(quaternionf,x+6*i+8,y+8,400.0F-i);

                context.getMatrices().pop();
            }

        }


        TooltipComponent.super.drawItems(textRenderer, x, y, context);
    }
}
