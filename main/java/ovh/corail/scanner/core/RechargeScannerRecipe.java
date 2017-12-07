package ovh.corail.scanner.core;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import ovh.corail.scanner.handler.ConfigurationHandler;

public class RechargeScannerRecipe extends ShapelessOreRecipe {
	public RechargeScannerRecipe(ItemStack res, Object... recipe) {
		super(null, res, recipe);
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack scanner = ItemStack.EMPTY;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (Main.scanner.isStackValid(stack) && Main.scanner.getEnergy(stack) < ConfigurationHandler.batteryEnergy) {
				scanner = inv.getStackInSlot(i);
				return Main.scanner.rechargeScanner(scanner.copy());
			}
		}
		return ItemStack.EMPTY;
	}
}
