package ovh.corail.scanner.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.scanner.handler.ConfigurationHandler;

public class UpdateSharedDataMessage implements IMessage {
	private int scanRange, batteryEnergy;
	
	public UpdateSharedDataMessage() {
	}
	
	public UpdateSharedDataMessage(int scanRange, int batteryEnergy) {
		this.scanRange = scanRange;
		this.batteryEnergy = batteryEnergy;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.scanRange = buf.readInt();
		this.batteryEnergy = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(scanRange);
		buf.writeInt(batteryEnergy);
	}
	
	public static class Handler implements IMessageHandler<UpdateSharedDataMessage, IMessage> {
		@Override
		public IMessage onMessage(final UpdateSharedDataMessage message, final MessageContext ctx) {
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					ConfigurationHandler.updateClient(message.scanRange, message.batteryEnergy);
 				}
			});
			return null;
		}
	}
}
