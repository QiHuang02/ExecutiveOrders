package net.atired.executiveorders.client.event;

import net.atired.executiveorders.client.tooltips.ClientPalePileTooltipData;
import net.atired.executiveorders.client.tooltips.PalePileTooltipData;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import org.jetbrains.annotations.Nullable;

public class PalePileTooltipComponentEvent implements TooltipComponentCallback {
    @Override
    public @Nullable TooltipComponent getComponent(TooltipData data) {
        if(data instanceof PalePileTooltipData palePileTooltipData){
            return new ClientPalePileTooltipData(palePileTooltipData);
        }

        return null;
    }
}
