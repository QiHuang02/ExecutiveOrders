package net.atired.executiveorders.client.renderers.entity;


import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.client.renderers.renderfeatures.VitrifiedOverlayFeatureRenderer;
import net.atired.executiveorders.enemies.custom.VitrifiedEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.SkeletonOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class VitrifiedRenderer extends SkeletonEntityRenderer<VitrifiedEntity> {
    private static final Identifier VITRIFIED_SKELETON_LOCATION = ExecutiveOrders.id("textures/entity/vitrified/vitrified.png");
    private static final List<Identifier> VITRIFIED_CLOTHES_LOCATION =  List.of(ExecutiveOrders.id("textures/entity/vitrified/vitrified_overlay.png"),ExecutiveOrders.id("textures/entity/vitrified/vitrified_overlay_1.png"),ExecutiveOrders.id("textures/entity/vitrified/vitrified_overlay_3.png"),ExecutiveOrders.id("textures/entity/vitrified/vitrified_overlay_2.png"));


    public VitrifiedRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.addFeature(new VitrifiedOverlayFeatureRenderer<>(this,context.getModelLoader(), EntityModelLayers.STRAY_OUTER,VITRIFIED_CLOTHES_LOCATION));
    }

    public static List<Identifier> getVitrifiedClothesLocation() {
        return VITRIFIED_CLOTHES_LOCATION;
    }

    @Override
    protected float getShadowRadius(VitrifiedEntity mobEntity) {
        if(mobEntity.getTargetId()!=-1)
            return super.getShadowRadius(mobEntity);
        else
            return 0;
    }


    @Override
    public void render(VitrifiedEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.getTargetId() == -1)
        {
            matrixStack.push();
            this.model.handSwingProgress = this.getHandSwingProgress(livingEntity, g);
            this.model.riding = livingEntity.hasVehicle();
            this.model.child = livingEntity.isBaby();
            float h = MathHelper.lerpAngleDegrees(g, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
            float j = MathHelper.lerpAngleDegrees(g, livingEntity.prevHeadYaw, livingEntity.headYaw);
            float k = j - h;
            float l;
            if (livingEntity.hasVehicle()) {
                Entity var11 = livingEntity.getVehicle();
                if (var11 instanceof LivingEntity) {
                    LivingEntity livingEntity2 = (LivingEntity) var11;
                    h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
                    k = j - h;
                    l = MathHelper.wrapDegrees(k);
                    if (l < -85.0F) {
                        l = -85.0F;
                    }

                    if (l >= 85.0F) {
                        l = 85.0F;
                    }

                    h = j - l;
                    if (l * l > 2500.0F) {
                        h += l * 0.2F;
                    }

                    k = j - h;
                }
            }

            float m = MathHelper.lerp(g, livingEntity.prevPitch, livingEntity.getPitch());
            if (shouldFlipUpsideDown(livingEntity)) {
                m *= -1.0F;
                k *= -1.0F;
            }

            k = MathHelper.wrapDegrees(k);
            float n;
            if (livingEntity.isInPose(EntityPose.SLEEPING)) {
                Direction direction = livingEntity.getSleepingDirection();
                if (direction != null) {
                    n = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1F;
                    matrixStack.translate((float) (-direction.getOffsetX()) * n, 0.0F, (float) (-direction.getOffsetZ()) * n);
                }
            }

            l = livingEntity.getScale();
            matrixStack.scale(l, l, l);
            n = this.getAnimationProgress(livingEntity, g);
            this.setupTransforms(livingEntity, matrixStack, n, h, g, l);
            matrixStack.scale(-1.0F, -1.0F, 1.0F);
            this.scale(livingEntity, matrixStack, g);
            matrixStack.translate(0.0F, -1.501F, 0.0F);
            float o = 0.0F;
            float p = 0.0F;
            if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
                o = livingEntity.limbAnimator.getSpeed(g);
                p = livingEntity.limbAnimator.getPos(g);
                if (livingEntity.isBaby()) {
                    p *= 3.0F;
                }

                if (o > 1.0F) {
                    o = 1.0F;
                }
            }

            this.model.animateModel(livingEntity, p, o, g);
            this.model.setAngles(livingEntity, p, o, n, k, m);
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            boolean bl = true;
            boolean bl2 = false;
            boolean bl3 = minecraftClient.hasOutline(livingEntity);
            RenderLayer renderLayer = this.getRenderLayer(livingEntity, bl, bl2, bl3);
            if (renderLayer != null) {
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
                int q = getOverlay(livingEntity, this.getAnimationCounter(livingEntity, g));
                this.model.render(matrixStack, vertexConsumer, i, q,ColorHelper.Argb.fromFloats(0.1f,1,0.95f,0.9f));
            }
            matrixStack.pop();
            super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
        else
            super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(VitrifiedEntity abstractSkeletonEntity) {
        return VITRIFIED_SKELETON_LOCATION;
    }
}