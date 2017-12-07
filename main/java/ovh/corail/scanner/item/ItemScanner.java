package ovh.corail.scanner.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ovh.corail.scanner.core.Helper;
import ovh.corail.scanner.core.Main;
import ovh.corail.scanner.core.NBTStackHelper;
import ovh.corail.scanner.core.ScannerManager;
import ovh.corail.scanner.gui.GuiOverlayScanner;
import ovh.corail.scanner.handler.ClientEventHandler;
import ovh.corail.scanner.handler.ConfigurationHandler;

public class ItemScanner extends Item {
	protected final String name = "scanner";
	
	public ItemScanner() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(Main.tabScanner);
		setMaxStackSize(1);
		setMaxDamage(0);
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		items.add(rechargeScanner(new ItemStack(this)));
	}
	
	@Override 
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
		list.add(TextFormatting.WHITE + Helper.getTranslation("item." + name + ".desc"));
		int energy = getEnergy(stack);
		list.add((energy > 0 ? TextFormatting.WHITE : TextFormatting.RED) + Helper.getTranslation("item.scanner.energyLeft") + " = " + energy);
		list.add(TextFormatting.BLUE + Helper.getTranslation("item." + name + ".use"));
	}
	
	public boolean isStackValid(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() instanceof ItemScanner;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		return isSearching(player, stack);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItemMainhand();
		if (getEnergy(stack) > 0 && hasTarget(stack)) {
			/** start casting */
			player.setActiveHand(EnumHand.MAIN_HAND);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return super.onItemRightClick(world, player, hand);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
		if (world.isRemote || !(entity instanceof EntityPlayer)) { return; }
		EntityPlayer player = (EntityPlayer)entity;
		if (!player.isCreative()) {
			setEnergy(stack, timeLeft);
		}
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
		if (world.isRemote || !(entity instanceof EntityPlayer)) { return stack; }
		EntityPlayer player = (EntityPlayer)entity;
		if (!player.isCreative()) {
			setEnergy(stack, 0);
		}
		Helper.sendMessage("message.scanner.discharged", player, true);
		return stack;
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase entity, int timeLeft) {
		if (!(entity instanceof EntityPlayer)) { return; }
		EntityPlayer player = (EntityPlayer)entity;
		World world = player.world;
		if (entity.world.isRemote) {
			GuiOverlayScanner currentGui = ClientEventHandler.currentGui;
			if (currentGui == null) { return; }
			if (ConfigurationHandler.enableScannerSound && timeLeft % 3 == 0) {	
				float pitch = (float)currentGui.detectFound/12f; 
				player.world.playSound(player, player.getPosition(), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("minecraft",ConfigurationHandler.favoriteSound)), SoundCategory.PLAYERS, 0.5f, pitch);
			}
			return;
		}
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItemMainhand();
		if (!world.isRemote && player.isSneaking()) {
			IBlockState targetState = world.getBlockState(pos);
			Block targetBlock = targetState.getBlock();
			if (ScannerManager.getInstance().canSelectBlock(targetBlock.getRegistryName().toString()+":"+targetBlock.getMetaFromState(targetState))) {
				setTarget(stack, targetState);
				Helper.sendMessage(Helper.getTranslation("message.scanner.select", TextFormatting.YELLOW + "[" + targetBlock.getLocalizedName()) + "]", player);
				return EnumActionResult.SUCCESS;
			} else {
				Helper.sendMessage("message.scanner.cantSelect", player, true);
				return EnumActionResult.FAIL;
			}	
		}
		return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return getEnergy(stack);
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW; 
	}
	
	public boolean isDischarged(ItemStack stack) {
		if (!isStackValid(stack)) { return true; }
		return getEnergy(stack) <= 0;
	}
	
	public int getEnergy(ItemStack stack) {
		if (!isStackValid(stack)) { return 0; }
		int val = NBTStackHelper.getInteger(stack, "energy");
		return val > 0 ? val : 0;
	}
	
	public ItemStack setEnergy(ItemStack stack, int value) {
		if (!isStackValid(stack)) { return stack; }
		return NBTStackHelper.setInteger(stack, "energy", value);
	}
	
	public ItemStack rechargeScanner(ItemStack stack) {
		return setEnergy(stack, ConfigurationHandler.batteryEnergy);
	}
	
	public boolean isSearching(EntityPlayer player, ItemStack stack) {
		return player.isHandActive() && getEnergy(stack) > 0 && hasTarget(stack);
	}
	
	public boolean hasTarget(ItemStack stack) {
		return !getTarget(stack).isEmpty();
	}
	
	public ItemStack getTarget(ItemStack stack) {
		if (!isStackValid(stack)) { return ItemStack.EMPTY; }
		String stringStack = NBTStackHelper.getString(stack, "target");
		return Helper.StringToItemStack(stringStack, false);
	}
	
	public ItemStack setTarget(ItemStack stack, IBlockState targetState) {
		if (!isStackValid(stack)) { return stack; }
		String targetString = null;
		if (targetState == null || targetState.getBlock() == Blocks.AIR) {
			targetString = "";
		} else {
			targetString = targetState.getBlock().getRegistryName().toString() + ":" + targetState.getBlock().getMetaFromState(targetState);
		}
		return NBTStackHelper.setString(stack, "target", targetString);
	}
}
