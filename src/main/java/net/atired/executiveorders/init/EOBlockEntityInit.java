package net.atired.executiveorders.init;

import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.enemies.blockentity.BrushableBedrockEntity;
import net.atired.executiveorders.enemies.blockentity.MonolithBlockEntity;
import net.atired.executiveorders.enemies.blockentity.VitricCampfireBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class EOBlockEntityInit {
    public static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, ExecutiveOrders.id(path), blockEntityType);
    }
    public static final BlockEntityType<BrushableBedrockEntity> BRUSHABLE_BLOCK_BEDROCK = register(
            "brushable_block_bedrock", BlockEntityType.Builder.create(BrushableBedrockEntity::new, EOBlocksInit.SUSPICIOUS_BEDROCK).build()
    );
    public static final BlockEntityType<VitricCampfireBlockEntity> VITRIC_CAMPFIRE_ENTITY_TYPE = register(
            "vitric_campfire",
            BlockEntityType.Builder.create(VitricCampfireBlockEntity::new, EOBlocksInit.VITRIC_CAMPFIRE).build()
    );
    public static final BlockEntityType<MonolithBlockEntity> MONOLITH_ENTITY_TYPE = register(
            "monolith",
            BlockEntityType.Builder.create(MonolithBlockEntity::new, EOBlocksInit.MONOLITH).build()
    );
    public static void initialize() {
    }
}
