package nl.enjarai.showmeyourskin.client.cursed;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

public class AlwaysGlintingStack extends ItemStack {
    public AlwaysGlintingStack(ItemConvertible item) {
        super(item);
    }

    @Override
    public boolean hasGlint() {
        return true;
    }
}
