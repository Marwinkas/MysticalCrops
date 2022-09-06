package net.marwinka.mysticalcrops.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTier2 extends Item {
    public ItemTier2(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(Text.translatable("item.mysticalcrops.essence_tier_2.tooltip") );
        super.appendTooltip(stack, world, tooltip, context);
    }
}
