package net.atired.executiveorders.enemies.blockentity;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.atired.executiveorders.blocks.SuspiciousBedrockBlock;
import net.atired.executiveorders.enemies.custom.JauntEntity;
import net.atired.executiveorders.init.EOBlockEntityInit;
import net.atired.executiveorders.init.EOBlocksInit;
import net.atired.executiveorders.init.EOEntityTypeInit;
import net.atired.executiveorders.init.EOItemsInit;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;
import org.slf4j.Logger;

import java.util.Objects;

public class BrushableBedrockEntity extends BlockEntity {
    public static final Item[] ITEMS = new Item[]{Items.AMETHYST_SHARD,EOBlocksInit.BEDROCK_LEAVES.asItem(),Items.POPPED_CHORUS_FRUIT,Items.OAK_PLANKS,Items.PETRIFIED_OAK_SLAB,EOItemsInit.DIGGERSHARK,EOItemsInit.RIPPERSHARK,EOItemsInit.NIGHTMARE_FUEL,Items.BEDROCK};
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String LOOT_TABLE_NBT_KEY = "LootTable";
    private static final String LOOT_TABLE_SEED_NBT_KEY = "LootTableSeed";
    private static final String HIT_DIRECTION_NBT_KEY = "hit_direction";
    private static final String ITEM_NBT_KEY = "item";
    private static final int field_42806 = 10;
    private static final int field_42807 = 40;
    private static final int field_42808 = 10;
    private int brushesCount;
    private long nextDustTime;
    private long nextBrushTime;
    private ItemStack item = ItemStack.EMPTY;
    @Nullable
    private Direction hitDirection;
    @Nullable
    private RegistryKey<LootTable> lootTable;
    private long lootTableSeed;

    public BrushableBedrockEntity(BlockPos pos, BlockState state) {
        super(EOBlockEntityInit.BRUSHABLE_BLOCK_BEDROCK,pos,state);
    }

    public boolean brush(long worldTime, PlayerEntity player, Direction hitDirection) {
        if (this.hitDirection == null) {
            this.hitDirection = hitDirection;
        }

        this.nextDustTime = worldTime + 40L;
        if (worldTime >= this.nextBrushTime && this.world instanceof ServerWorld) {
            this.nextBrushTime = worldTime + 10L;
            this.generateItem(player);
            int i = this.getDustedLevel();
            if (++this.brushesCount >= 10) {
                this.finishBrushing(player);
                return true;
            } else {
                this.world.scheduleBlockTick(this.getPos(), this.getCachedState().getBlock(), 2);
                int j = this.getDustedLevel();
                if (i != j) {
                    BlockState blockState = this.getCachedState();
                    BlockState blockState2 = blockState.with(Properties.DUSTED, Integer.valueOf(j)).with(Properties.LIT,j>1);
                    this.world.setBlockState(this.getPos(), blockState2, Block.NOTIFY_ALL);
                }

                return false;
            }
        } else {
            return false;
        }

    }

    public void generateItem(PlayerEntity player) {
        if (this.world != null && !this.world.isClient() && this.world.getServer() != null && this.item.isEmpty()) {
            this.item = new ItemStack(ITEMS[player.getRandom().nextInt(ITEMS.length)]);
        }
    }

    private void finishBrushing(PlayerEntity player) {

        for (int i = 0; i < 5; i++) {
            Vec3d vec3d = this.pos.toBottomCenterPos().add(Math.random()/2-0.25,1.1,Math.random()/2-0.25);
            world.addParticle(ParticleTypes.PORTAL, vec3d.x, vec3d.y, vec3d.z, Math.random()/4-0.25, 0.2, Math.random()/4-0.25);
            world.addParticle(ParticleTypes.REVERSE_PORTAL, vec3d.x, vec3d.y, vec3d.z, Math.random()/4-0.25, 0.4, Math.random()/4-0.25);
        }
        if (this.world != null && this.world.getServer() != null) {
            this.spawnItem(player);
            BlockState blockState = this.getCachedState();
            this.world.syncWorldEvent(WorldEvents.BLOCK_FINISHED_BRUSHING, this.getPos(), Block.getRawIdFromState(blockState));
            Block block2;
            if (this.getCachedState().getBlock() instanceof SuspiciousBedrockBlock brushableBlock) {
                block2 = brushableBlock.getBaseBlock();
            } else {
                block2 = Blocks.AIR;
            }

            this.world.setBlockState(this.pos, block2.getDefaultState(), Block.NOTIFY_ALL);
        }
    }

    private void spawnItem(PlayerEntity player) {
        if (this.world != null && this.world.getServer() != null) {
            this.generateItem(player);
            Direction direction = (Direction) Objects.requireNonNullElse(this.hitDirection, Direction.UP);
            if (this.item.getItem() == Items.BEDROCK) {
                JauntEntity jauntEntity = new JauntEntity(EOEntityTypeInit.JAUNT,this.world);
                Vec3d vec3d = this.pos.toBottomCenterPos().add(0,1.1,0);
                jauntEntity.setPos(vec3d.x,vec3d.y, vec3d.z);
                jauntEntity.setVelocity(new Vec3d(0,0,0).offset(direction, 0.7));
                this.world.spawnEntity(jauntEntity);
                this.item = ItemStack.EMPTY;
            }
            else if (!this.item.isEmpty()) {
                double d = (double) EntityType.ITEM.getWidth();
                double e = 1.0 - d;
                double f = d / 2.0;

                Vec3d vec3d = this.pos.toBottomCenterPos().add(-0.5,0.5,-0.5).offset(direction, 0.8);
                double g = (double)vec3d.getX() + 0.5 * e + f;
                double h = (double)vec3d.getY() + (double)(EntityType.ITEM.getHeight() / 2.0F);
                double i = (double)vec3d.getZ() + 0.5 * e + f;
                ItemEntity itemEntity = new ItemEntity(this.world, g, h, i, this.item.split(this.world.random.nextInt(21) + 10));
                itemEntity.setVelocity(new Vec3d(0,0,0).offset(direction, 0.3));
                this.world.spawnEntity(itemEntity);
                this.item = ItemStack.EMPTY;
            }
        }
    }

    public void scheduledTick() {
        if (this.world != null) {
            if (this.brushesCount != 0 && this.world.getTime() >= this.nextDustTime) {
                int i = this.getDustedLevel();
                this.brushesCount = Math.max(0, this.brushesCount - 2);
                int j = this.getDustedLevel();
                if (i != j) {
                    this.world.setBlockState(this.getPos(), this.getCachedState().with(Properties.DUSTED, Integer.valueOf(j)).with(Properties.LIT,j>1), Block.NOTIFY_ALL);
                }

                int k = 4;
                this.nextDustTime = this.world.getTime() + 4L;
            }

            if (this.brushesCount == 0) {
                this.hitDirection = null;
                this.nextDustTime = 0L;
                this.nextBrushTime = 0L;
            } else {
                this.world.scheduleBlockTick(this.getPos(), this.getCachedState().getBlock(), 2);
            }
        }
    }

    private boolean readLootTableFromNbt(NbtCompound nbt) {
        if (nbt.contains("LootTable", NbtElement.STRING_TYPE)) {
            this.lootTable = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(nbt.getString("LootTable")));
            this.lootTableSeed = nbt.getLong("LootTableSeed");
            return true;
        } else {
            return false;
        }
    }

    private boolean writeLootTableToNbt(NbtCompound nbt) {
        if (this.lootTable == null) {
            return false;
        } else {
            nbt.putString("LootTable", this.lootTable.getValue().toString());
            if (this.lootTableSeed != 0L) {
                nbt.putLong("LootTableSeed", this.lootTableSeed);
            }

            return true;
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbtCompound = super.toInitialChunkDataNbt(registryLookup);
        if (this.hitDirection != null) {
            nbtCompound.putInt("hit_direction", this.hitDirection.ordinal());
        }

        if (!this.item.isEmpty()) {
            nbtCompound.put("item", this.item.encode(registryLookup));
        }

        return nbtCompound;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (!this.readLootTableFromNbt(nbt) && nbt.contains("item")) {
            this.item = (ItemStack)ItemStack.fromNbt(registryLookup, nbt.getCompound("item")).orElse(ItemStack.EMPTY);
        } else {
            this.item = ItemStack.EMPTY;
        }

        if (nbt.contains("hit_direction")) {
            this.hitDirection = Direction.values()[nbt.getInt("hit_direction")];
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.writeLootTableToNbt(nbt) && !this.item.isEmpty()) {
            nbt.put("item", this.item.encode(registryLookup));
        }
    }

    public void setLootTable(RegistryKey<LootTable> lootTable, long seed) {
        this.lootTable = lootTable;
        this.lootTableSeed = seed;
    }

    private int getDustedLevel() {
        if (this.brushesCount == 0) {
            return 0;
        } else if (this.brushesCount < 3) {
            return 1;
        } else {
            return this.brushesCount < 6 ? 2 : 3;
        }
    }

    @Nullable
    public Direction getHitDirection() {
        return this.hitDirection;
    }

    public ItemStack getItem() {
        return this.item;
    }

}
