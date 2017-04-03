package ovh.corail.scanner.core;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Helper {
	
	public static ItemStack damageItem(ItemStack stack, int amount) {
		boolean broke = stack.getItemDamage() + amount > stack.getMaxDamage();
		if (!broke) {
			stack.setItemDamage(stack.getItemDamage() + amount);
			return stack;
		}
		return ItemStack.EMPTY;
	}
	
	public static String ItemStackToString(ItemStack stack, boolean withStackSize) {
		return stack.getItem().getRegistryName().toString() + (withStackSize ? ":"+stack.getCount() : "") + ":" + stack.getMetadata();
	}
	
	public static String ItemStackToString(ItemStack stack) {
		return ItemStackToString(stack, true);
	}
	
	public static ItemStack StringToItemStack(String stringStack) {
		String[] parts = stringStack.split(":");
		if (parts.length == 4) {
			Item item = Item.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1]));
			if (item != null) {
				return new ItemStack(item, Integer.valueOf(parts[2]), Integer.valueOf(parts[3]));
			}
		}
		return ItemStack.EMPTY;

	}
	
	public static ItemStack StringToItemStack(String stringStack, boolean withStackSize) {
		if (withStackSize) { return StringToItemStack(stringStack); }
		String[] parts = stringStack.split(":");
		if (parts.length == 3) {
			Item item = Item.REGISTRY.getObject(new ResourceLocation(parts[0], parts[1]));
			if (item != null) {
				return new ItemStack(item, 1, Integer.valueOf(parts[2]));
			}
		}
		return ItemStack.EMPTY;
	}

	
	public static boolean areItemEqual(ItemStack s1, ItemStack s2) {
		return s1.isItemEqual(s2) && s1.getMetadata() == s2.getMetadata() && ItemStack.areItemStackTagsEqual(s1, s2);
	}
	
	private static ItemStack copyStack(ItemStack stack, int size) {
		if (stack.isEmpty() || size == 0)
			return ItemStack.EMPTY;
		ItemStack tmp = stack.copy();
		tmp.setCount(Math.min(size, stack.getMaxStackSize()));
		return tmp;
	}
	
	public static void sendMessage(String message, EntityPlayer currentPlayer, boolean translate) {
		if (currentPlayer != null) {
			if (translate) {
				message = getTranslation(message);
			}
			currentPlayer.sendMessage(new TextComponentString(message));
		}
	}
	
	public static String getTranslation(String message) {
		return I18n.translateToLocal(message);
	}

	public static void render() {
		/** blocks */
		/** items */
		render(Main.scanner);
		render(Main.battery);
	}

	private static void render(Block block) {
		render(Item.getItemFromBlock(block), 0);
	}
	
	private static void render(Item item) {
		render(item, 0);
	}

	private static void render(Block block, int meta) {
		render(Item.getItemFromBlock(block), meta);
	}
	
	private static void render(Item item, int meta) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta,
				new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
	public static void register() {
		/** blocks */
		/** items */
		register(Main.scanner);
		register(Main.battery);
	}
	
	private static void register(Block block) {
		GameRegistry.register(block);
		GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	private static void register(Item item) {
		GameRegistry.register(item);
	}
}
