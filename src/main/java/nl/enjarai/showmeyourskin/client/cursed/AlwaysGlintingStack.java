package nl.enjarai.showmeyourskin.client.cursed;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;

public class AlwaysGlintingStack extends ItemStack {
    public AlwaysGlintingStack(ItemConvertible item) {
        super(item);
    }

    @Override
    public boolean isIn(TagKey<Item> tag) {
        return tag == ItemTags.TRIMMABLE_ARMOR;
    }

    @Override
    public boolean hasGlint() {
        return true;
    }
}
