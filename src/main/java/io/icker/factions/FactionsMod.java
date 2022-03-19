package io.icker.factions;

import io.icker.factions.command.AutoClaimCommand;
import io.icker.factions.command.AutoUnClaimCommand;
import io.icker.factions.config.Config;
import io.icker.factions.database.Claim;
import io.icker.factions.database.Faction;
import io.icker.factions.util.Dynmap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.DynmapCommonAPIListener;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import java.util.*;

public class FactionsMod implements ModInitializer {
	public static Logger LOGGER = LogManager.getLogger("Factions");
	public static DynmapCommonAPI dynmapCommonAPI;
	public static MarkerAPI markerApi;
	public static MarkerSet markerSet;

	public static int ticksElapsed = 0;

	public static int completedChunks = 0;

	public static HashMap<Faction, ArrayList<Claim>> factionsAndClaims = new HashMap<>();

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
			if(ticksElapsed % 20 == 0) {
				modifyFactionInfo();
			}
		});

	}

	public static void modifyFactionInfo() {
		if(factionsAndClaims.isEmpty()) {
			return;
		}

		Optional<Faction> first = factionsAndClaims.keySet().stream().findFirst();
		Faction faction = first.get();
		LOGGER.info("Reloading " + faction.name);
		ArrayList<Claim> claims = factionsAndClaims.get(faction);

		int percentRemoved = (int) Math.ceil((claims.size() - completedChunks) * 0.9D);

		//LOGGER.info(claims.size());

		//LOGGER.info(percentRemoved);
		//LOGGER.info(completedChunks);

		// Dynmap.removeAllAreaMarkers();
		// Dynmap.removeSpecificFactionMarkers(faction);

		for(int i = (claims.size() - completedChunks) - 1; i >= (percentRemoved - 1) && i >= 0; i--) {
			if(i == claims.size() - 1) {
				Dynmap.removeSpecificFactionMarkers(faction);
			}
			//LOGGER.info("i is: " + i);
			Claim claim = claims.get(i);

			// Dynmap.removeChunkClaim(claim.x * 16, claim.z * 16, claim.x * 16 + (claim.x < 0 ? -16 : 16), claim.z * 16 + (claim.z < 0 ? -16 : 16), faction);
			Dynmap.newChunkClaim(claim.x * 16, claim.z * 16, claim.x * 16 + (claim.x < 0 ? -16 : 16), claim.z * 16 + (claim.z < 0 ? -16 : 16), faction);
			completedChunks++;
		}

		if(claims.size() == completedChunks) {
			factionsAndClaims.remove(faction);
			completedChunks = 0;
			LOGGER.info("Reloaded " + faction);
		}
		/*
        ArrayList<Claim> claims = faction.getClaims();
        for(Claim claim : claims) {
            removeChunkClaim(claim.x * 16, claim.z * 16, claim.x * 16 + (claim.x < 0 ? -16 : 16), claim.z * 16 + (claim.z < 0 ? -16 : 16), faction);
            newChunkClaim(claim.x * 16, claim.z * 16, claim.x * 16 + (claim.x < 0 ? -16 : 16), claim.z * 16 + (claim.z < 0 ? -16 : 16), faction);
        }
        */
	}
}
