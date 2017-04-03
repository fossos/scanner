package ovh.corail.scanner.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.scanner.core.Helper;

public class DamageHoldItemServerMessage implements IMessage {
	private int damage;
	private BlockPos currentPos;

	public DamageHoldItemServerMessage() {
	}

	public DamageHoldItemServerMessage(int damage) {
		this.damage = damage;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.damage = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.damage);
	}
	
	public static class Handler implements IMessageHandler<DamageHoldItemServerMessage, IMessage> {
		@Override
		public IMessage onMessage(final DamageHoldItemServerMessage message, final MessageContext ctx) {
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().player.world;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					/** damage hold item */
					EntityPlayerMP player = ctx.getServerHandler().player;
					if (player != null) {
						ItemStack stack = player.getHeldItemMainhand();
						player.setHeldItem(EnumHand.MAIN_HAND, Helper.damageItem(stack, message.damage));
					}
				}
			});
			return null;
		}
	}
}
