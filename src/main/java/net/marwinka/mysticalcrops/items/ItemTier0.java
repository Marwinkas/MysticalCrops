package net.marwinka.mysticalcrops.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTier0 extends Item {
    public ItemTier0(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(new TranslatableText("item.mysticalcrops.essence_tier_0.tooltip") );
        super.appendTooltip(stack, world, tooltip, context);
    }
}