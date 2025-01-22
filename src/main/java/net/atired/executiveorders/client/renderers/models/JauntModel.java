package net.atired.executiveorders.client.renderers.models;

import net.atired.executiveorders.enemies.custom.JauntEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.util.math.MathHelper;

public class JauntModel<T extends JauntEntity> extends ZombieEntityModel<T> {
    public JauntModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public void setAngles(T hostileEntity, float f, float g, float h, float i, float j) {
        super.setAngles(hostileEntity, f, g, h, i, j);
        this.leftLeg.pitch = MathHelper.lerp(this.leaningPitch/4, this.leftLeg.pitch, 0.1F * MathHelper.cos(f * 0.33333334F + 3.1415927F));
        this.rightLeg.pitch = MathHelper.lerp(this.leaningPitch/4, this.rightLeg.pitch, 0.1F * MathHelper.cos(f * 0.33333334F));
    }

    @Override
    public boolean isAttacking(JauntEntity entity) {
        return false;
    }
}
