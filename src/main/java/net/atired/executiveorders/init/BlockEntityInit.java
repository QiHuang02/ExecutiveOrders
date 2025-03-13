package net.atired.executiveorders.init;

import com.mojang.datafixers.types.Type;
import net.atired.executiveorders.ExecutiveOrders;
import net.atired.executiveorders.enemies.blockentity.MonolithBlockEntity;
import net.atired.executiveorders.enemies.blockentity.VitricCampfireBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

public class BlockEntityInit {
    public static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, ExecutiveOrders.id(path), blockEntityType);
    }
    public static final BlockEntityType<VitricCampfireBlockEntity> VITRIC_CAMPFIRE_ENTITY_TYPE = register(
            "vitric_campfire",
            BlockEntityType.Builder.create(VitricCampfireBlockEntity::new, BlocksInit.VITRIC_CAMPFIRE).build()
    );
    public static final BlockEntityType<MonolithBlockEntity> MONOLITH_ENTITY_TYPE = register(
            "monolith",
            BlockEntityType.Builder.create(MonolithBlockEntity::new, BlocksInit.MONOLITH).build()
    );
    public static void initialize() {
    }
}
