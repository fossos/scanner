package ovh.corail.scanner.handler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import ovh.corail.scanner.core.Helper;
import ovh.corail.scanner.core.Main;
import ovh.corail.scanner.packet.UpdateSharedDataMessage;

public class EventHandler {
	@SubscribeEvent
	public void onCraft(ItemCraftedEvent event) {
		if (Main.scanner.isStackValid(event.crafting)) {
			IInventory inv = event.craftMatrix;
			boolean has_battery = false;
			for (int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (!stack.isEmpty() && stack.getItem() == Main.battery) {
					has_battery = true;
					break;
				}
			}
			if (has_battery) {
				Helper.grantAdvancement(event.player, "tutorial/recharge_scanner");
			} else {
				Helper.grantAdvancement(event.player, "tutorial/create_scanner");
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onPlayerLogued(EntityJoinWorldEvent event) {
		if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityPlayerMP)) { return; }
		EntityPlayerMP player = (EntityPlayerMP)event.getEntity();
		PacketHandler.INSTANCE.sendTo(new UpdateSharedDataMessage(ConfigurationHandler.scanRange, ConfigurationHandler.batteryEnergy), player);
	}
}
