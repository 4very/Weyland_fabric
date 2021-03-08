package sommea.weyland;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import sommea.weyland.systems.HUD;

@Environment(EnvType.CLIENT)
public class Weyland implements ModInitializer {
	public static final String MOD_ID = "weyland";
	public static Logger LOGGER = LogManager.getLogger();

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_ID + "] " + message);
    }

	public HUD hud;

	@Override
	public void onInitialize() {

		hud = new HUD();
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		System.out.println("Hello Fabric world!");
	}
}
