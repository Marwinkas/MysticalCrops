package net.marwinka.mysticalcrops.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.marwinka.mysticalcrops.init.ModBlockEntities;
import net.marwinka.mysticalcrops.recipe.InfusionTableRecipe;
import net.marwinka.mysticalcrops.screen.InfusionTableScreenHandler;
import net.marwinka.mysticalcrops.util.inventory.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class InfusionTableEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    public InfusionTableEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INFUSION_TABLE, pos, state);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                switch (index) {
                    case 0:
                        return InfusionTableEntity.this.progress;
                    case 1:
                        return InfusionTableEntity.this.maxProgress;
                    default:
                        return 0;
                }
            }
            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        InfusionTableEntity.this.progress = value;
                        break;
                    case 1:
                        InfusionTableEntity.this.maxProgress = value;
                        break;
                }
            }

            public int size() {
                return 2;
            }
        };
    }

    public int progress = 0;
    public int maxProgress = 100;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction side) {
        if (!stack.isStackable()) {
            return slot == 0;
        }
        if (stack.isStackable() && this.getStack(1).getCount() <= this.getStack(2).getCount() && this.getStack(1).getCount() <= this.getStack(3).getCount() && this.getStack(1).getCount() <= this.getStack(4).getCount()) {
            return slot == 1;
        }
        if (stack.isStackable() && this.getStack(2).getCount() <= this.getStack(1).getCount() && this.getStack(2).getCount() <= this.getStack(3).getCount() && this.getStack(2).getCount() <= this.getStack(4).getCount()) {
            return slot == 2;
        }
        if (stack.isStackable() && this.getStack(3).getCount() <= this.getStack(2).getCount() && this.getStack(3).getCount() <= this.getStack(1).getCount() && this.getStack(3).getCount() <= this.getStack(4).getCount()) {
            return slot == 3;
        }
        if (stack.isStackable() && this.getStack(4).getCount() <= this.getStack(2).getCount() && this.getStack(4).getCount() <= this.getStack(3).getCount() && this.getStack(4).getCount() <= this.getStack(1).getCount()) {
            return slot == 4;
        }
        return false;
    }
    public void resetProgress() {
        this.progress = 0;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new InfusionTableScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    protected final PropertyDelegate propertyDelegate;

    public void setInventory(DefaultedList<ItemStack> inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            this.inventory.set(i, inventory.get(i));
        }
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("progress", this.progress);
        if(this.getStack(1) != null){
            getRenderStack1 = this.getStack(1);
        }
        else{
            getRenderStack1 = new ItemStack(Blocks.AIR);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.progress = nbt.getInt("progress");
        if(this.getStack(1) != null){
            getRenderStack1 = this.getStack(1);
        }
        else{
            getRenderStack1 = new ItemStack(Blocks.AIR);
        }
    }
    public ItemStack getItem(int index) {
        if(world != null) world.tickEntity(Entity::tick,this.getWorld().getEntityById(0));
        return index >= 0 && index < this.inventory.size() ? this.inventory.get(index) : ItemStack.EMPTY;
    }

    private boolean canInsertAmountIntoOutputSlot(SimpleInventory inventory) {
        return inventory.getStack(5).getCount() < inventory.getStack(5).getMaxCount();
    }
    private static boolean canInsertItemIntoOutputSlot(SimpleInventory inventory, Item output) {
        return inventory.getStack(5).getItem() == output || inventory.getStack(5).isEmpty();
    }
    public void tick() {


        if(this.getStack(1) != null){
            getRenderStack1 = this.getStack(1);
        }
        else{
            getRenderStack1 = new ItemStack(Blocks.AIR);
        }

        if (!this.world.isClient) {

            SimpleInventory inventory = new SimpleInventory(this.size());
            for (int i = 0; i < this.size(); i++) {
                inventory.setStack(i, this.getStack(i));
            }
            Optional<InfusionTableRecipe> recipe = this.getWorld().getRecipeManager()
                    .getFirstMatch(InfusionTableRecipe.Type.INFUSION, inventory, this.getWorld());

            if (recipe.isPresent() && canInsertAmountIntoOutputSlot(inventory) && canInsertItemIntoOutputSlot(inventory, recipe.get().getOutput().getItem())) {
                this.progress++;
                if (this.progress >= this.maxProgress) {

                    if (this.getStack(0).getItem().isDamageable()) {
                        this.getStack(0).setDamage(this.getStack(0).getDamage() + 1);
                        if (this.getStack(0).getDamage() >= this.getStack(0).getItem().getMaxDamage() && this.getStack(0).getItem().isDamageable()) {
                            this.removeStack(0, 1);
                        }
                    }

                    for (int i = 1; i < this.size(); i++) {
                        if (i != 5) this.removeStack(i, 1);
                        else this.setStack(5, new ItemStack(recipe.get().getOutput().getItem(), this.getStack(5).getCount() + 1));
                    }
                    resetProgress();
                    this.markDirty();
                }
            } else {
                resetProgress();
            }
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.world instanceof ServerWorld world) {
            world.getChunkManager().markForUpdate(this.pos);
        }
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction side) {
        return slot == 5;
    }

    public ItemStack getRenderStack() {
        return this.getStack(1);
    }
    public ItemStack getRenderStack1;
    public ItemStack getRenderStack2() {
        return this.getStack(3);
    }
    public ItemStack getRenderStack3() {
        return this.getStack(4);
    }
    public ItemStack getRenderStack4() {
        return this.getStack(5);
    }
}