package ovh.corail.scanner.handler;

import java.awt.Color;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ovh.corail.scanner.core.Main;
import ovh.corail.scanner.core.ModProps;
import ovh.corail.scanner.gui.GuiOverlayScanner;

public class ClientEventHandler {
	public static GuiOverlayScanner currentGui = null;
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		if (eventArgs.getModID().equals(ModProps.MOD_ID)) {
			ConfigurationHandler.refreshConfig();
		}
	}
	
	@SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
		/** draw after experience is rendered */
		if (event.getType() != ElementType.EXPERIENCE) { return; }
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player == null) { return; }
		ItemStack stack = player.getHeldItemMainhand();
		if (Main.scanner.isStackValid(stack)) {
			currentGui = new GuiOverlayScanner(Minecraft.getMinecraft());
		}
	}
	
	@SubscribeEvent
	public void onRenderHands(RenderSpecificHandEvent event) {
		/** don't draw item in first person */
		if (Main.scanner.isStackValid(event.getItemStack())) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		// TODO need improvements
		if (!ConfigurationHandler.highlightBlocks) { return; }
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		ItemStack stack = player.getHeldItemMainhand();
		if (currentGui == null || !Main.scanner.isStackValid(stack) || !Main.scanner.isSearching(player, stack)) { return; }
		double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
		double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
		double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();
		Set<BlockPos> list = currentGui.foundList;
		for (BlockPos p : list) {
		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.translate(-doubleX, -doubleY, -doubleZ);

		long c = (System.currentTimeMillis() / 15l) % 360l;
		Color color = Color.getHSBColor(c / 360f, 1f, 1f);		
		
		float x = p.getX(), y = p.getY(), z = p.getZ();
		// RenderHelper.enableStandardItemLighting();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder renderer = tessellator.getBuffer();
		renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
		GL11.glLineWidth(2.5f);
		GlStateManager.pushAttrib();
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		float offset = 1f;
		renderer.pos(x, y, z).endVertex();
		renderer.pos(x + offset, y, z).endVertex();

		renderer.pos(x, y, z).endVertex();
		renderer.pos(x, y + offset, z).endVertex();

		renderer.pos(x, y, z).endVertex();
		renderer.pos(x, y, z + offset).endVertex();

		renderer.pos(x + offset, y + offset, z + offset).endVertex();
		renderer.pos(x, y + offset, z + offset).endVertex();

		renderer.pos(x + offset, y + offset, z + offset).endVertex();
		renderer.pos(x + offset, y, z + offset).endVertex();

		renderer.pos(x + offset, y + offset, z + offset).endVertex();
		renderer.pos(x + offset, y + offset, z).endVertex();

		renderer.pos(x, y + offset, z).endVertex();
		renderer.pos(x, y + offset, z + offset).endVertex();

		renderer.pos(x, y + offset, z).endVertex();
		renderer.pos(x + offset, y + offset, z).endVertex();

		renderer.pos(x + offset, y, z).endVertex();
		renderer.pos(x + offset, y, z + offset).endVertex();

		renderer.pos(x + offset, y, z).endVertex();
		renderer.pos(x + offset, y + offset, z).endVertex();

		renderer.pos(x, y, z + offset).endVertex();
		renderer.pos(x + offset, y, z + offset).endVertex();

		renderer.pos(x, y, z + offset).endVertex();
		renderer.pos(x, y + offset, z + offset).endVertex();
		tessellator.draw();
		// RenderHelper.disableStandardItemLighting();
		GlStateManager.popAttrib();
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.color(1f, 1f, 1f, 1f);
		GlStateManager.popMatrix();
		
		}
	}
}
