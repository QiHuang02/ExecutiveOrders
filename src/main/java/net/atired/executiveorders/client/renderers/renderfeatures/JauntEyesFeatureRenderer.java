package net.atired.executiveorders.client.renderers.renderfeatures;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.client.renderers.models.JauntModel;
import net.atired.executiveorders.enemies.custom.JauntEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public class JauntEyesFeatureRenderer<T extends JauntEntity> extends EyesFeatureRenderer<T, JauntModel<T>> {
    private static final RenderLayer EYE_LOCATION = RenderLayer.getEyes(ExecutiveOrders.id("textures/entity/juantover.png"));

    public JauntEyesFeatureRenderer(FeatureRendererContext<T, JauntModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.getEyesTexture());
        matrices.scale(1.2f,1.2f,1.2f);
        matrices.multiply(new Quaternionf().rotationY((entity.getWorld().getTime()+entity.getId()+tickDelta)/8f));
        if(!entity.isVolatile())
        {
            ((Model)this.getContextModel()).render(matrices, vertexConsumer, 0xF00000, OverlayTexture.DEFAULT_UV);
            matrices.multiply(new Quaternionf().rotationY(1.57f));
            ((Model)this.getContextModel()).render(matrices, vertexConsumer, 0xF00000, OverlayTexture.DEFAULT_UV);
            matrices.multiply(new Quaternionf().rotationY(1.57f));
            ((Model)this.getContextModel()).render(matrices, vertexConsumer, 0xF00000, OverlayTexture.DEFAULT_UV);
            matrices.multiply(new Quaternionf().rotationY(1.57f));
            ((Model)this.getContextModel()).render(matrices, vertexConsumer, 0xF00000, OverlayTexture.DEFAULT_UV);
        }

    }

    public RenderLayer getEyesTexture() {
        return EYE_LOCATION;
    }
}
