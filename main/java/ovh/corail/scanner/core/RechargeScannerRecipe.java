package ovh.corail.scanner.core;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RechargeScannerRecipe extends ShapelessOreRecipe {
	public RechargeScannerRecipe(ItemStack res, Object... recipe) {
		super(res, recipe);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack scanner = ItemStack.EMPTY;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (!inv.getStackInSlot(i).isEmpty() && inv.getStackInSlot(i).getItem() == Main.scanner && inv.getStackInSlot(i).getItemDamage() > 0) {
				scanner = inv.getStackInSlot(i);
				break;
			}
		}
		if (scanner.isEmpty()) { return ItemStack.EMPTY; }
		ItemStack res = scanner.copy();
		res.setItemDamage(0);
		// TODO add achievement for recharge
		return res;
	}
}
