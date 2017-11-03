package ovh.corail.scanner.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ovh.corail.scanner.core.Helper;
import ovh.corail.scanner.core.Main;
import ovh.corail.scanner.core.ScannerManager;
import ovh.corail.scanner.gui.GuiOverlayScanner;

public class ItemScanner extends Item {
	private static final String name = "scanner";
	
	public ItemScanner() {
		super();
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(Main.tabScanner);
		setMaxStackSize(1);
		setMaxDamage(5000);
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		/** TODO could be right click holding to scan and another key to select target */
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState targetState = world.getBlockState(pos);
		Block targetBlock = targetState.getBlock();
		// TODO could deny some blocks
		if (ScannerManager.getInstance().canSelectBlock(targetBlock.getRegistryName().toString()+":"+targetBlock.getMetaFromState(targetState))) {
			setTarget(player.getHeldItemMainhand(), targetState);
		} else {
			if (world.isRemote) {
				Helper.sendMessage("message.scanner.cantSelect", player, true);
			}
			setTarget(player.getHeldItemMainhand(), null);
		}
		return EnumActionResult.FAIL;//super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
	
	public static ItemStack getTarget(ItemStack stack) {
		if (stack.hasTagCompound()) {
			String stringStack = stack.getTagCompound().getString("target");
			return Helper.StringToItemStack(stringStack, false);
		}
		return ItemStack.EMPTY;
	}
	
	/** add a compound if needed */
	private static boolean checkCompound(ItemStack stack) {
		if (stack.getItem() == Main.scanner) {
			if (!stack.hasTagCompound()) {
				stack.setTagCompound(new NBTTagCompound());
			}
			return true;
		}
		return false;
	}
	
	/** write the compound of the itemStack */
	private static boolean setTarget(ItemStack stack, IBlockState targetState) {
		String targetString = null;
		if (targetState == null) {
			targetString = ItemStack.EMPTY.getUnlocalizedName() + ":" + ItemStack.EMPTY.getMetadata();
		} else {
			Block targetBlock = targetState.getBlock();
			targetString = targetBlock.getRegistryName().toString() + ":" + targetBlock.getMetaFromState(targetState);
		}
		if (checkCompound(stack)) {
			NBTTagCompound compound = stack.getTagCompound();
			compound.setString("target", targetString);
			return true;
		} else {
			return false;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
		list.add(TextFormatting.WHITE + Helper.getTranslation("item." + name + ".desc"));
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		Helper.grantAdvancement(player, "tutorial/create_scanner");
	}
	
	@SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
		/** draw after experience is rendered */
		if (event.getType() != ElementType.EXPERIENCE) { return; }
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player == null || player.getHeldItemMainhand() == null || player.getHeldItemMainhand().getItem() != this) {
			return;
		}
		new GuiOverlayScanner(Minecraft.getMinecraft());
	}
	
	@SubscribeEvent
	public void onRenderHands(RenderSpecificHandEvent event) {
		/** don't draw item in first person */
		if (event.getItemStack().getItem().equals(this)) {
			event.setCanceled(true);
		}
	}
}
