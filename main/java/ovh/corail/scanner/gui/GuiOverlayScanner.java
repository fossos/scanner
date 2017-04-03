package ovh.corail.scanner.gui;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import ovh.corail.scanner.core.Helper;
import ovh.corail.scanner.core.Main;
import ovh.corail.scanner.handler.ConfigurationHandler;
import ovh.corail.scanner.handler.PacketHandler;
import ovh.corail.scanner.item.ItemScanner;
import ovh.corail.scanner.packet.DamageHoldItemServerMessage;

public class GuiOverlayScanner extends Gui {
	protected static Minecraft mc;
	protected static FontRenderer fontRenderer;
	protected static ScaledResolution scaled;
	protected static int width, height, guiLeft, guiTop;
	protected static int guiWidth = 128;
	protected static int guiHeight = 128;
	protected static ResourceLocation textureJauge = new ResourceLocation(Main.MOD_ID + ":textures/gui/jauge.png"); 
	protected static ResourceLocation textureScanner = new ResourceLocation(Main.MOD_ID + ":textures/items/scanner.png"); 
	private static long lastUpdate = -1;
	private static long tick = Long.MIN_VALUE;
	private static long lastTick = Long.MIN_VALUE;
	private static final long tickTime = 100;
	protected static int detectFound = 0;
	protected static int lastProgress = 0;
	protected static ItemStack target = ItemStack.EMPTY;
	protected static boolean discharged;
	protected static boolean messageDischarged = false;

	public GuiOverlayScanner(Minecraft mc) {
		this.mc = mc;
		this.fontRenderer = mc.fontRenderer;
		this.scaled = new ScaledResolution(mc);
        this.width = scaled.getScaledWidth();
        this.height = scaled.getScaledHeight();
        this.guiLeft = width - guiWidth + 10;
        this.guiTop = height - guiHeight + 60;
        ItemStack holdItem = mc.player.getHeldItemMainhand();
        this.target = ItemScanner.getTarget(holdItem);
        this.discharged = holdItem.getItemDamage() + ConfigurationHandler.damageAmount >= holdItem.getMaxDamage();
        if (discharged) {
        	lastProgress = 0;
        } else {
        	messageDischarged = false;
        }
        temporize();
        drawScreen();
	}
	
	private void temporize() {
        /** temporize the updates */
		long currentUpdate = mc.getSystemTime();
		if (lastUpdate == -1) {
			lastUpdate = currentUpdate;
			tick++;
		} else if (currentUpdate-lastUpdate > tickTime) {
			lastUpdate += tickTime;
			tick++;
		}
		if (lastTick != tick) {
			updateData();
			lastTick = tick;
		}
	}
	
	protected void updateData() {
		/** update the jauge depending on detectFound and lastProgress */
		if (tick%5==0) {
			EntityPlayer player = mc.player;
			BlockPos playerPos = player.getPosition().up();
			Vec3d posVec3d = player.getPositionVector().addVector(0d, 1d, 0d);
			Vec3d lookVec3d = player.getLookVec();
			Vec3i lookVec3i = new Vec3i((int) Math.round(lookVec3d.xCoord), (int) Math.round(lookVec3d.yCoord), (int) Math.round(lookVec3d.zCoord));
			EnumFacing facing = EnumFacing.getFacingFromVector((float)lookVec3d.xCoord, (float)lookVec3d.yCoord, (float)lookVec3d.zCoord);
			BlockPos nearestPos = playerPos.add(lookVec3i.getX(), lookVec3i.getY(), lookVec3i.getZ());

			int radius = 2;
			int depthMax = 5;
			BlockPos currentPos = playerPos;
			Set<BlockPos> blockPosList = new HashSet<BlockPos>();
			/** list a line of blocks facing the player */
			detectFound = 0;
			if (!target.isEmpty() && !discharged) {
				for (int depth = 1 ; depth <= depthMax ; depth++) {
					currentPos = currentPos.add(lookVec3i.getX(), lookVec3i.getY(), lookVec3i.getZ());
					BlockPos start, end;
					if (radius == 0) {
						blockPosList.add(currentPos);
						continue;
					}
					/** add radius of blocks around that block */
					if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
						start = currentPos.south(radius).west(radius);
						end = currentPos.north(radius).east(radius);
					} else {
						start = currentPos.down(radius).west(radius);
						end = currentPos.up(radius).east(radius);
					}
					blockPosList.addAll(Lists.newArrayList(BlockPos.getAllInBox(start, end)));
				}
				for (BlockPos pos : blockPosList) {
					IBlockState state = mc.world.getBlockState(pos);
					// TODO could check oredict similar ore
					if (Helper.areItemEqual(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)), target)) {
						detectFound++;
					}
				}
			}
			// TODO Achievment for some kinds/count of blocks found */
		}
		/** allow to draw slowly the jauge progress */
		if (tick%2==0 && lastProgress != detectFound) {
			lastProgress = (lastProgress < detectFound ? lastProgress+1 : lastProgress-1);
		}
		/** damage the scanner */
		if (!discharged && ConfigurationHandler.damageAmount > 0 && tick%(ConfigurationHandler.timeForDamage/100)==0) {
			ItemStack scanner = mc.player.getHeldItemMainhand();
			PacketHandler.INSTANCE.sendToServer(new DamageHoldItemServerMessage(ConfigurationHandler.damageAmount));
			scanner = Helper.damageItem(scanner, ConfigurationHandler.damageAmount);
			mc.player.setHeldItem(EnumHand.MAIN_HAND, scanner);

		}
		if (discharged && !messageDischarged) {
			Helper.sendMessage("message.scanner.discharged", mc.player, true);
			messageDischarged = true;
		}
		// TODO sounds to this player depending on detectFound
	}
	
	protected void drawScreen() {
		/** draw background */
		mc.renderEngine.bindTexture(textureScanner);
		GlStateManager.scale(0.5f, 0.5f, 0.5f);
		this.drawTexturedModalRect(guiLeft*2, guiTop*2, 0, 0, 256, 256);
		GlStateManager.scale(2f, 2f, 2f);
		/** draw the jauge */
		mc.renderEngine.bindTexture(textureJauge);
		GlStateManager.scale(0.2f, 0.2f, 0.2f);
		int x = guiLeft+40;
		int y = guiTop;
		for (int i = 0 ; i < 12 ; i++) {	
			int textureX = lastProgress<3?0:(lastProgress<6?40:(lastProgress<9?80:120));
			this.drawTexturedModalRect((x*5)+(i*20), (y*5)+150, 5+(i<lastProgress?textureX:160), 0, 35, 54);
		}
		GlStateManager.scale(2.5f, 2.5f, 2.5f);
		/** draw text on gui */
		if (discharged) {
			drawString(fontRenderer, Helper.getTranslation("statut.scanner.discharged"), x*2, (y+15)*2, Color.RED.getRGB());
		} else if (!target.isEmpty()) {
			drawString(fontRenderer, Helper.getTranslation("statut.scanner.scanning"), x*2, (y+15)*2, Color.YELLOW.getRGB());
			drawString(fontRenderer, target.getDisplayName(), x*2, (y+22)*2, Color.YELLOW.getRGB());
		} else {
			drawString(fontRenderer, Helper.getTranslation("statut.scanner.waiting1"), x*2, (y+15)*2, Color.CYAN.getRGB());
			drawString(fontRenderer, Helper.getTranslation("statut.scanner.waiting2"), x*2, (y+22)*2, Color.CYAN.getRGB());
		}
		GlStateManager.scale(2f, 2f, 2f);
	}

}
