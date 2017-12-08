package ovh.corail.scanner.gui;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import ovh.corail.scanner.core.Helper;
import ovh.corail.scanner.core.Main;
import ovh.corail.scanner.core.ModProps;
import ovh.corail.scanner.handler.ConfigurationHandler;

public class GuiOverlayScanner extends Gui {
	public static Minecraft mc;
	protected static FontRenderer fontRenderer;
	protected static ScaledResolution scaled;
	protected static int width, height, guiLeft, guiTop;
	protected static int guiWidth = 128;
	protected static int guiHeight = 128;
	protected static ResourceLocation textureJauge = new ResourceLocation(ModProps.MOD_ID + ":textures/gui/jauge.png"); 
	protected static ResourceLocation textureScanner = new ResourceLocation(ModProps.MOD_ID + ":textures/items/scanner.png"); 
	private static long lastUpdate = -1;
	private static long tick = Long.MIN_VALUE;
	private static long lastTick = Long.MIN_VALUE;
	private static final long tickTime = 100;
	public static int detectFound = 0;
	protected static int lastProgress = 0;
	protected final ItemStack scanner;
	protected final boolean isDischarged;
	protected final boolean isSearching;
	protected final ItemStack target;
	public static Set<BlockPos> blockPosList = new HashSet<BlockPos>();
	public static Set<BlockPos> foundList = new HashSet<BlockPos>();

	public GuiOverlayScanner(Minecraft mc) {
		this.mc = mc;
		this.fontRenderer = mc.fontRenderer;
		this.scaled = new ScaledResolution(mc);
        this.width = scaled.getScaledWidth();
        this.height = scaled.getScaledHeight();
        this.guiLeft = width - guiWidth + 10;
        this.guiTop = height - guiHeight + 60;
        scanner = mc.player.getHeldItemMainhand();
        isDischarged = Main.scanner.isDischarged(scanner);
        isSearching = Main.scanner.isSearching(mc.player, scanner);
        target = Main.scanner.getTarget(scanner);
        if (Main.scanner.getEnergy(scanner) <= 0) { lastProgress = 0; }
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
		if (tick%10==0) {
			EntityPlayer player = mc.player;
			detectFound = 0;
			blockPosList.clear();
			if (isSearching && !isDischarged) {
				blockPosList = Helper.findBlockPossesInSphericalCone(player, 20, ConfigurationHandler.scanRange);
				foundList.clear();
				for (BlockPos pos : blockPosList) {
					if (!mc.world.isBlockLoaded(pos)) { continue; }
					if (mc.world.isAirBlock(pos)) { continue; }
					IBlockState state = mc.world.getBlockState(pos);
					ItemStack currentStack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
					if (Helper.isSimilarOredict(currentStack, target)) {
						foundList.add(pos);
						detectFound++;
					}
				}
			}
		}
		/** allow to draw slowly the jauge progress */
		if (tick%2==0 && lastProgress != detectFound) {
			lastProgress = (lastProgress < detectFound ? lastProgress+1 : lastProgress-1);
		}
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
		if (isDischarged) {
			drawString(fontRenderer, Helper.getTranslation("statut.scanner.discharged"), x*2, (y+15)*2, Color.RED.getRGB());
		} else if (isSearching) {
			drawString(fontRenderer, Helper.getTranslation("statut.scanner.scanning"), x*2, (y+15)*2, Color.YELLOW.getRGB());
		} else {
			drawString(fontRenderer, Helper.getTranslation("statut.scanner.waiting"), x*2, (y+15)*2, Color.CYAN.getRGB());
		}
		if (!target.isEmpty()) {
			drawString(fontRenderer, target.getDisplayName(), x*2, (y+22)*2, Color.YELLOW.getRGB());
		} else {
			drawString(fontRenderer, Helper.getTranslation("statut.scanner.noTarget"), x*2, (y+22)*2, Color.CYAN.getRGB());
		}
		GlStateManager.scale(2f, 2f, 2f);
	}
}
