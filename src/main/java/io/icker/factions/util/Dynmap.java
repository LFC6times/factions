package io.icker.factions.util;

import io.icker.factions.FactionsMod;
import io.icker.factions.database.Ally;
import io.icker.factions.database.Enemy;
import io.icker.factions.database.Faction;
import net.minecraft.util.math.ChunkPos;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

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

        int chunkX1 = chunkPos.getStartX(), chunkZ1 = chunkPos.getStartZ(), chunkX2 = chunkPos.getEndX(), chunkZ2 = chunkPos.getEndZ();
        String factionInfo = "Name: " + faction.name + "\n"
                + "Description: " + faction.description + "\n"
                + "Power: " + faction.power + "\n"
                + "Number of members: " + faction.getMembers().size() + "\n"
                + "Allies: " + Ally.getAllies(faction.name).stream().map(ally -> ally.target).collect(Collectors.joining(", ")) + "\n"
                + "Enemies: " + Enemy.getEnemies(faction.name).stream().map(enemy -> enemy.target).collect(Collectors.joining(", "));

        AreaMarker areaMarker = markerSet.createAreaMarker(null, factionInfo, false, "world", new double[]{chunkX1, chunkX2}, new double[]{chunkZ1, chunkZ2}, true);
        int color = faction.color.getColorValue();

        areaMarker.setFillStyle(areaMarker.getFillOpacity(), color);
        areaMarker.setLineStyle(areaMarker.getLineWeight(), areaMarker.getLineOpacity(), color);
    }

    
}
