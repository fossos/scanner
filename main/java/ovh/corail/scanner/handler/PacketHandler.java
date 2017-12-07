package ovh.corail.scanner.handler;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import ovh.corail.scanner.core.ModProps;
import ovh.corail.scanner.packet.UpdateSharedDataMessage;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(ModProps.MOD_ID);

	public static void init() {
		int id = 0;
		INSTANCE.registerMessage(UpdateSharedDataMessage.Handler.class, UpdateSharedDataMessage.class, id++, Side.CLIENT);
	}
}
