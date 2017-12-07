package ovh.corail.scanner.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import ovh.corail.scanner.core.Helper;
import ovh.corail.scanner.core.Main;

public class ItemBattery extends Item {
	private final String name = "battery";
	
	public ItemBattery() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(Main.tabScanner);
		setMaxStackSize(64);
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		Helper.grantAdvancement(player, "tutorial/create_battery");
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
		list.add(TextFormatting.WHITE + Helper.getTranslation("item." + name + ".desc"));
	}
}
