package ovh.corail.scanner.core;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class NBTStackHelper {
	
	private static boolean checkCompound(ItemStack stack, boolean addCompound) {
		if (stack.isEmpty()) { return false; }
		/** add a compound if needed */
		if (addCompound && !stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.hasTagCompound();
	}
	
	private static boolean checkCompound(ItemStack stack) {
		return checkCompound(stack, false);
	}

	public static ItemStack setString(ItemStack stack, String keyName, String keyValue) {
		if (checkCompound(stack, true)) {
			stack.getTagCompound().setString(keyName, keyValue);
		}
		return stack;
	}
	
	public static String getString(ItemStack stack, String keyName) {
		if (checkCompound(stack)) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound.hasKey(keyName)) { return compound.getString(keyName); }
		}
		return "";
	}
	
	public static ItemStack setBoolean(ItemStack stack, String keyName, boolean keyValue) {
		if (checkCompound(stack, true)) {
			stack.getTagCompound().setBoolean(keyName, keyValue);
		}
		return stack;
	}
	
	public static boolean getBoolean(ItemStack stack, String keyName) {
		if (checkCompound(stack)) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound.hasKey(keyName)) { return compound.getBoolean(keyName); }
		}
		return false;
	}
	
	
	public static ItemStack setInteger(ItemStack stack, String keyName, int keyValue) {
		if (checkCompound(stack, true)) {
			stack.getTagCompound().setInteger(keyName, keyValue);
		}
		return stack;
	}
	
	public static int getInteger(ItemStack stack, String keyName) {
		if (checkCompound(stack)) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound.hasKey(keyName)) { return compound.getInteger(keyName); }
		}
		return Integer.MIN_VALUE;
	}
	
	public static ItemStack setLong(ItemStack stack, String keyName, long keyValue) {
		if (checkCompound(stack, true)) {
			stack.getTagCompound().setLong(keyName, keyValue);
		}
		return stack;	
	}
	
	public static long getLong(ItemStack stack, String keyName) {
		if (checkCompound(stack)) {
			NBTTagCompound compound = stack.getTagCompound();
			if (compound.hasKey(keyName)) { return compound.getLong(keyName); }
		}
		return Long.MIN_VALUE;
	}
	
	public static ItemStack setBlockPos(ItemStack stack, String keyName, BlockPos keyValue) {
		return setLong(stack, keyName, keyValue.toLong());
	}
	
	public static BlockPos getBlockPos(ItemStack stack, String keyName) {
		long longValue = getLong(stack, keyName);
		return longValue == Long.MIN_VALUE ? BlockPos.ORIGIN : BlockPos.fromLong(longValue);
	}
	
	public static boolean removeKeyName(ItemStack stack, String keyName) {
		if (hasKeyName(stack, keyName)) {
			stack.getTagCompound().removeTag(keyName);
			return true;
		}
		return false;
	}
	
	public static boolean hasKeyName(ItemStack stack, String keyName) {
		if (checkCompound(stack)) {
			return stack.getTagCompound().hasKey(keyName);
		}
		return false;
	}
	
}
