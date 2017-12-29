package ovh.corail.scanner.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class Helper {
	public static boolean isSimilarOredict(ItemStack stack1, ItemStack stack2) {
		if (stack1.isEmpty() || stack2.isEmpty()) { return false; }
        int[] oreList1 = OreDictionary.getOreIDs(stack1);
        if (oreList1.length == 0) {
        	return stack1.getItem() == stack2.getItem() && stack1.getMetadata() == stack2.getMetadata();
        }
        int[] oreList2 = OreDictionary.getOreIDs(stack2);
        for (int oreId1 : oreList1) {
            for (int oreId2 : oreList2) {
                if (oreId1 == oreId2) {
                    return true;
                }
            }
        }
        return false;
    }
	
    public static Set<BlockPos> findBlockPossesInSphericalCone(EntityPlayer player, int coneAngle, double reach) {

        Vec3d originPosition = player.getPositionVector().addVector(-0.5, 0.5, -0.5); //shift position to check for middlepoints of blocks instead of BlockPosses (that are in their minimal corner).
        Vec3d lookVector = player.getLookVec(); //I assume this will return a Vector with three factors <= 1.0
        Vec3d targetPosition = originPosition.addVector(lookVector.x * reach, lookVector.y * reach, lookVector.z * reach);

        //minimise `squareroot(square(coneX * x - blockX) + square(coneY * x - blockY) + square(coneZ * x - blockZ))` by changing x, 
        //which is the only variable the rest already has a value by now using a and b from the abc method.
        double a = Math.pow(lookVector.x, 2) + Math.pow(lookVector.y, 2) + Math.pow(lookVector.z, 2); //the sum of three squares is always positive, so the mentioned formula has a minimum

        Set<BlockPos> investigatedPositions = new HashSet<BlockPos>(); //contains all positions which have been evaluated whether they are in the "cone" or not
        Set<BlockPos> validPositions = new HashSet<BlockPos>(); //contains all positions found to be within the "cone"
        List<BlockPos> unExploredPositions = new ArrayList<BlockPos>(); //contains valid positions of which the neighbours have not been investigated yet.

        for (double i = 0; i < reach; i += 0.5) { //walk along the cone's guideline from the "bottom" of the cone to the "top" of the cone, a.k.a. the @code{originPosition}
            BlockPos startingPos = new BlockPos(
                    originPosition.x + lookVector.x * (reach - i),
                    originPosition.y + lookVector.y * (reach - i),
                    originPosition.z + lookVector.z * (reach - i));

            if (investigatedPositions.contains(startingPos)) {
                //do nothing
            } else {
                investigatedPositions.add(startingPos);
                if (isPosInSphere(startingPos, originPosition, reach)) { // && isPosInCone(startingPos, originPosition, coneX, coneY, coneZ, a, coneAngle / 2)) {
                    validPositions.add(startingPos);
                    unExploredPositions.add(startingPos);
                }
            }

            while (!unExploredPositions.isEmpty()) {
                BlockPos oldPosition = unExploredPositions.get(0);
                unExploredPositions.remove(oldPosition);

                //explore every position directly around
                for (int offsetX = -1; offsetX <= 1; offsetX++) {
                    for (int offsetY = -1; offsetY <= 1; offsetY++) {
                        for (int offsetZ = -1; offsetZ <= 1; offsetZ++) {
                            if (offsetX == 0 && offsetY == 0 && offsetZ == 0) {
                                //do nothing
                            } else {
                                BlockPos newPosition = oldPosition.add(offsetX, offsetY, offsetZ);
                                if (investigatedPositions.contains(newPosition)) { //todo investigatedPositions can become pretty large, slowing down lookup time. Can this be remedied? A List type that will always be searched from back to front on contains(), perhaps?
                                    //do nothing
                                } else {
                                    investigatedPositions.add(newPosition);
                                    if (isPosInSphere(newPosition, originPosition, reach) && isPosInCone(newPosition, originPosition, lookVector.x, lookVector.y, lookVector.z, a, ((double) coneAngle) / 2.0)) {
                                        validPositions.add(newPosition);
                                        unExploredPositions.add(newPosition);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return validPositions;
    }

    public static boolean isPosInSphere(BlockPos pos, Vec3d center, double radius) {
        return Math.sqrt(Math.pow(((double) pos.getX()) - center.x, 2) 
        	+ Math.pow(((double) pos.getY())- center.y, 2)
        	+ Math.pow(((double) pos.getZ()) - center.z, 2)) < radius;
    }

    //coneVector should be the direction of the look Vector, not the absolute position
    //slopeAngle must be lower than 90.0
    public static boolean isPosInCone(BlockPos pos, Vec3d topPoint, double coneX, double coneY, double coneZ, double a, double slopeAngle) {

        Vec3d blockVector = new Vec3d(pos).subtract(topPoint); //transform BlockPos to its relative position
        double blockX = blockVector.x;
        double blockY = blockVector.y;
        double blockZ = blockVector.z;
        

        //minimise `squareroot(square(coneX * x - blockX) + square(coneY * x - blockY) + square(coneZ * x - blockZ))` by changing x, 
        //which is the only variable the rest already has a value by now using a and b from the abc method. a comes as a parameter.
        double b = -2 * (blockX * coneX + blockY * coneY + blockZ * coneZ);
        double c = Math.pow(blockX, 2) + Math.pow(blockY, 2) + Math.pow(blockZ, 2);

        double x = (-1 * b) / (2 * a);
        if (x < 0) { //block is behind the player
            return false;
        }
        double y = Math.sqrt(-1 * (Math.pow(b, 2) - 4 * a * c) / (4 * a)); //this is the shortest distance between the BlockPos and the guide line of the cone
                
        //double distance = Math.sqrt(Math.pow(coneX, 2) + Math.pow(coneY, 2) + Math.pow(coneZ, 2)) * x;
        double coneRadiusAtDistance = x * Math.tan(slopeAngle);
        
        return y <= coneRadiusAtDistance;
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
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(parts[0], parts[1]));
			if (block != null) {
				return new ItemStack(block, Integer.valueOf(parts[2]), Integer.valueOf(parts[3]));
			}
		}
		return ItemStack.EMPTY;

	}
	
	public static ItemStack StringToItemStack(String stringStack, boolean withStackSize) {
		if (withStackSize) { return StringToItemStack(stringStack); }
		String[] parts = stringStack.split(":");
		if (parts.length == 3) {
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(parts[0], parts[1]));
			if (block != null) {
				return new ItemStack(block, 1, Integer.valueOf(parts[2]));
			}
		}
		return ItemStack.EMPTY;
	}
	
	public static void sendMessage(String message, EntityPlayer player) {
		sendMessage(message, player, false);
	}
	
	public static void sendMessage(String message, EntityPlayer player, boolean translate) {
		if (player != null && !player.world.isRemote) {
			if (translate) {
				message = getTranslation(message);
			}
			player.sendMessage(new TextComponentString(message));
		}
	}
	
	public static String getTranslation(String message, Object... format) {
		return I18n.translateToLocalFormatted(message, format);
	}
	
	public static void sendLog(String message) {
		if (Main.logger != null) {
			Main.logger.log(Level.INFO, message);
		} else {
			System.out.println(message);
		}
	}

	public static void registerEncodedRecipes() {
		ItemStack scanner = Main.scanner.rechargeScanner(new ItemStack(Main.scanner));
		/** create scanner */
		ForgeRegistries.RECIPES.register(new ShapedOreRecipe(null, scanner, new Object[] {
			"2 2", "010", "000",
			'0', "ingotIron",
			'1', Main.battery,
			'2', Blocks.REDSTONE_TORCH
		}).setRegistryName(new ResourceLocation(ModProps.MOD_ID, "create_scanner")));
		/** recharge scanner */
		RecipeSorter.register("scanner:recharge_scanner", RechargeScannerRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
		ForgeRegistries.RECIPES.register(new RechargeScannerRecipe(scanner, new Object[] {
			new ItemStack(Main.scanner), Main.battery
		}).setRegistryName(new ResourceLocation(ModProps.MOD_ID, "recharge_scanner")));		
	}
}
