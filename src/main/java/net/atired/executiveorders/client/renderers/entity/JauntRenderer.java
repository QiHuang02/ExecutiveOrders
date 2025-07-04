package net.atired.executiveorders.client.renderers.entity;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.client.renderers.models.JauntModel;
import net.atired.executiveorders.client.renderers.renderfeatures.JauntEyesFeatureRenderer;
import net.atired.executiveorders.enemies.custom.JauntEntity;
import net.atired.executiveorders.misc.EOgetDatNoise;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.EndermanEyesFeatureRenderer;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;


public class JauntRenderer extends ZombieBaseEntityRenderer<JauntEntity, JauntModel<JauntEntity>> {
    private static final Identifier JAUNT_LOCATION = ExecutiveOrders.id("textures/entity/juant.png");

    @Override
    public Identifier getTexture(JauntEntity entity) {
        return JAUNT_LOCATION;
    }

    public JauntRenderer(EntityRendererFactory.Context context) {
        super(context, new JauntModel(context.getPart(EntityModelLayers.ZOMBIE)), new JauntModel(context.getPart(EntityModelLayers.ZOMBIE_INNER_ARMOR)), new JauntModel(context.getPart(EntityModelLayers.ZOMBIE_OUTER_ARMOR)));
        this.addFeature(new JauntEyesFeatureRenderer(this));
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(JauntEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        translucent = true;
        return super.getRenderLayer(entity, showBody, translucent, showOutline);
    }

    @Override
    public void render(JauntEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {

        matrixStack.translate(livingEntity.laspos.x-livingEntity.getLerpedPos(g).x,livingEntity.laspos.y-livingEntity.getLerpedPos(g).y,livingEntity.laspos.z-livingEntity.getLerpedPos(g).z);
        if(livingEntity.isVolatile()&&EOgetDatNoise.class!=null){
            float noisyx = EOgetDatNoise.sampleNoise3D((float)(livingEntity.getX()+Math.sin(livingEntity.getWorld().getTime()/8f+g)),(livingEntity.getWorld().getTime()+g)/4f,0f,10f)/3f;
            float noisyz = EOgetDatNoise.sampleNoise3D(0,(livingEntity.getWorld().getTime()+g)/4f,(float)(livingEntity.getZ()+Math.cos(livingEntity.getWorld().getTime()/8f+g)),10f)/3f;
            matrixStack.translate(noisyx,0,noisyz);
            super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, 0);
            matrixStack.translate(noisyx*-3,0,noisyz*-3);

        }
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
