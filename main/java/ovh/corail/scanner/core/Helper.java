package ovh.corail.scanner.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class Helper {
	
	public static ItemStack damageItem(ItemStack stack, int amount) {
		boolean broke = stack.getItemDamage() + amount > stack.getMaxDamage();
		if (!broke) {
			stack.setItemDamage(stack.getItemDamage() + amount);
			return stack;
		}
		return ItemStack.EMPTY;
	}
	
	public static boolean grantAdvancement(EntityPlayer player, String name) {
		return grantAdvancement(player, ModProps.MOD_ID, name);
	}
	
	public static boolean grantAdvancement(EntityPlayer player, String domain, String name) {
		if (player == null) { return false; }
		if (player.world.isRemote) { return true; }
		PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
		EntityPlayerMP player_mp = playerList.getPlayerByUUID(player.getUniqueID());
		AdvancementManager am = player_mp.getServerWorld().getAdvancementManager();
		Advancement advancement = am.getAdvancement(new ResourceLocation(domain, name));
		if (advancement == null) { return false; }
		AdvancementProgress advancementprogress = player_mp.getAdvancements().getProgress(advancement);
		if (!advancementprogress.isDone()) {
			for (String criteria : advancementprogress.getRemaningCriteria()) {
				player_mp.getAdvancements().grantCriterion(advancement, criteria);
			}
		}
		return true;
	}
	
	public static boolean saveAsJson(File file, Set<?> list) {
		if (file.exists()) { file.delete(); }
		try {
			if (file.createNewFile()) {
				FileWriter fw = new FileWriter(file);
				fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(list));
				fw.close();
				return true;
			}
		} catch (IOException e) { e.printStackTrace(); }
		return false;
	}
	
	public static Set<?> loadAsJson(File file, Type token) {
		Set<?> list = null;
		try {
			list = new Gson().fromJson(new BufferedReader(new FileReader(file)), token);
		} catch (Exception e) { e.printStackTrace(); }
		return list;
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
		ForgeRegistries.BLOCKS.register(block);
		ForgeRegistries.ITEMS.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	private static void register(Item item) {
		ForgeRegistries.ITEMS.register(item);
	}
}
