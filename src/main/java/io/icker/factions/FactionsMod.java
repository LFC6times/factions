package io.icker.factions;

import io.icker.factions.command.AutoClaimCommand;
import io.icker.factions.command.AutoUnClaimCommand;
import io.icker.factions.config.Config;
import io.icker.factions.database.Faction;
import io.icker.factions.util.Dynmap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

public class FactionsMod implements ModInitializer {
	public static Logger LOGGER = LogManager.getLogger("Factions");
	public static DynmapCommonAPI dynmapCommonAPI;
	public static MarkerAPI markerApi;
	public static MarkerSet markerSet;

	public static int ticksElapsed = 0;

	@Override
	public void onInitialize() {
		LOGGER.info("Initalized Factions-Dynmap Mod for Minecraft v1.17");
		Config.init();

		DynmapCommonAPIListener.register(new DynmapCommonAPIListener() {
			@Override
			public void apiEnabled(DynmapCommonAPI dCAPI) {
				dynmapCommonAPI = dCAPI;
				markerApi = dynmapCommonAPI.getMarkerAPI();
				markerSet = markerApi.createMarkerSet("dynmap-factions", "the dynmap factions thing", null, true);
				if(markerSet == null) {
					markerSet = markerApi.getMarkerSet("dynmap-factions");
				}
			}
		});

		ServerTickEvents.START_SERVER_TICK.register((startTick) -> {
			if(++ticksElapsed % 10 == 0) {
				AutoClaimCommand.autoClaimLoop();
				AutoUnClaimCommand.autoUnClaimLoop();
			}
		});

		ServerTickEvents.END_WORLD_TICK.register((endTick) -> {
			if(!Dynmap.factionsToUpdate.isEmpty()) {
				Faction faction = Dynmap.factionsToUpdate.get(0);
				Dynmap.factionsToUpdate.remove(0);
				Dynmap.modifyFactionInfo(faction);
			}
		});

	}
}
