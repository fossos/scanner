package ovh.corail.scanner.handler;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import ovh.corail.scanner.core.Main;
import ovh.corail.scanner.packet.DamageHoldItemServerMessage;

public class PacketHandler {

	public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(Main.MOD_ID);

	public static void init() {
		int id = 0;
		INSTANCE.registerMessage(DamageHoldItemServerMessage.Handler.class, DamageHoldItemServerMessage.class, id++, Side.SERVER);
		//INSTANCE.registerMessage(ClientMessage.Handler.class, ClientMessage.class, id++, Side.CLIENT);
	}
}
