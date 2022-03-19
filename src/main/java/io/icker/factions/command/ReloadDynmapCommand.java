package io.icker.factions.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.icker.factions.FactionsMod;
import io.icker.factions.database.Faction;
import io.icker.factions.util.Dynmap;
import net.minecraft.server.command.ServerCommandSource;

public class ReloadDynmapCommand {
    public static int reloadDynmap(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        for(Faction faction : Faction.all()) {
            FactionsMod.LOGGER.info(faction.name + " has been added to list");
            Dynmap.addFactionsToUpdate(faction);
        }
        Dynmap.removeAllAreaMarkers();
        return 1;
    }
}
