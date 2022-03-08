package io.icker.factions.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.icker.factions.database.Claim;
import io.icker.factions.database.Faction;
import io.icker.factions.database.Member;
import io.icker.factions.util.Dynmap;
import io.icker.factions.util.Message;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.ChunkPos;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AutoClaimCommand {
    public static Map<ServerPlayerEntity, ChunkPos> playerToChunk = new HashMap<>();

    public static void addPlayer(ServerPlayerEntity player) {
        // playerToFaction.put(player, faction);
        playerToChunk.put(player, player.getServerWorld().getChunk(player.getBlockPos()).getPos());
    }

    public static void removePlayer(ServerPlayerEntity player) {
        playerToChunk.remove(player);
        // playerToFaction.remove(player);
    }

    public static void autoClaimLoop() {
        for(Map.Entry<ServerPlayerEntity, ChunkPos> temp : playerToChunk.entrySet()) {
            ChunkPos chunkPos = temp.getKey().getServerWorld().getChunk(temp.getKey().getBlockPos()).getPos();
            if (chunkPos != temp.getValue()) {
                claim(temp.getKey());
                playerToChunk.replace(temp.getKey(), chunkPos);
            }
        }
    }

    public static void claim(ServerPlayerEntity player) {
        Member member = Member.get(player.getUuid());
        ChunkPos chunkPos = player.getServerWorld().getChunk(player.getBlockPos()).getPos();
        String dimension = player.getServerWorld().getRegistryKey().getValue().toString();

        Claim existingClaim = Claim.get(chunkPos.x, chunkPos.z, dimension);
        if(member == null) {
            return;
        }
        if (existingClaim == null) {
            Faction faction = member.getFaction();
            if(faction == null) {
                return;
            }
            faction.addClaim(chunkPos.x, chunkPos.z, dimension);
            new Message("%s claimed chunk (%d, %d)", player.getName().asString(), chunkPos.x, chunkPos.z).send(faction);
            Dynmap.newChunkClaim(chunkPos, faction);
        } else {
            String owner = Objects.equals(existingClaim.getFaction().name, member.getFaction().name) ? "Your" : "Another";
            new Message(owner + " faction already owns this chunk").fail().send(player, false);
        }
    }

    public static int startAutoClaim(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        addPlayer(context.getSource().getPlayer());
        return 0;
    }

    public static int stopAutoClaim(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        removePlayer(context.getSource().getPlayer());
        return 1;
    }

}
