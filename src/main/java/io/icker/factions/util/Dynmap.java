package io.icker.factions.util;

import io.icker.factions.FactionsMod;
import io.icker.factions.database.Ally;
import io.icker.factions.database.Enemy;
import io.icker.factions.database.Faction;
import net.minecraft.util.math.ChunkPos;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Dynmap {
    public static void newChunkClaim(ChunkPos chunkPos, Faction faction) {
        MarkerSet markerSet = FactionsMod.markerSet;
        /*
        if(markerAPI.getMarkerSet("factions-dynmap") != null) {
            markerSet = markerAPI.getMarkerSet("factions-dynmap");
        } else {
            markerSet = markerAPI.createMarkerSet("dynmap-factions", "factions-dynmap", null, false);
        }
        */
        if(markerSet == null) {
            FactionsMod.LOGGER.info("it's null bruh");
        }

        int chunkX1 = chunkPos.getStartX(), chunkZ1 = chunkPos.getStartZ(), chunkX2 = chunkPos.getEndX() + 1, chunkZ2 = chunkPos.getEndZ() + 1;
        String factionInfo = "Name: " + faction.name + "<br>"
                + "Description: " + faction.description + "<br>"
                + "Power: " + faction.power + "<br>"
                + "Number of members: " + faction.getMembers().size() + "<br>"
                + "Allies: " + Ally.getAllies(faction.name).stream().map(ally -> ally.target).collect(Collectors.joining(", ")) + "<br>"
                + "Enemies: " + Enemy.getEnemies(faction.name).stream().map(enemy -> enemy.target).collect(Collectors.joining(", ")) + "<br>"
                + "";
        String areaMarkerId = faction.name + "-" + chunkX1 + "." + chunkZ1 + ";" + chunkX2 + "." + chunkZ2;
        AreaMarker areaMarker = markerSet.createAreaMarker(areaMarkerId, factionInfo, true, "world", new double[]{chunkX1, chunkX2}, new double[]{chunkZ1, chunkZ2}, true);
        int color = faction.color.getColorValue();
        FactionsMod.LOGGER.info(areaMarkerId);
        areaMarker.setFillStyle(areaMarker.getFillOpacity(), color);
        areaMarker.setLineStyle(areaMarker.getLineWeight(), areaMarker.getLineOpacity(), color);
    }

    public static void removeChunkClaim(ChunkPos chunkPos, Faction faction) {
        int chunkX1 = chunkPos.getStartX(), chunkZ1 = chunkPos.getStartZ(), chunkX2 = chunkPos.getEndX() + 1, chunkZ2 = chunkPos.getEndZ() + 1;
        String idToRemove = faction.name + "-" + chunkX1 + "." + chunkZ1 + ";" + chunkX2 + "." + chunkZ2;

        MarkerSet markerSet = FactionsMod.markerSet;
        Set<AreaMarker> areaMarkers = markerSet.getAreaMarkers();

        for(AreaMarker areaMarker : areaMarkers) {
            FactionsMod.LOGGER.info(areaMarker.getMarkerID());
            FactionsMod.LOGGER.info(idToRemove);
            FactionsMod.LOGGER.info(areaMarker.getMarkerID().equals(idToRemove));
            if(areaMarker.getMarkerID().equals(idToRemove)) {
                areaMarker.deleteMarker();
                FactionsMod.LOGGER.info("found it");
                break;
            }
        }
    }

    public static void removeChunkClaim(int chunkX1, int chunkZ1, int chunkX2, int chunkZ2, Faction faction) {
        String idToRemove = faction.name + "-" + chunkX1 + "." + chunkZ1 + ";" + chunkX2 + "." + chunkZ2;

        MarkerSet markerSet = FactionsMod.markerSet;
        Set<AreaMarker> areaMarkers = markerSet.getAreaMarkers();

        for(AreaMarker areaMarker : areaMarkers) {
            FactionsMod.LOGGER.info(areaMarker.getMarkerID());
            FactionsMod.LOGGER.info(idToRemove);
            FactionsMod.LOGGER.info(areaMarker.getMarkerID().equals(idToRemove));
            if(areaMarker.getMarkerID().equals(idToRemove)) {
                areaMarker.deleteMarker();
                FactionsMod.LOGGER.info("found it");
                break;
            }
        }
    }
}
