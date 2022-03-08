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

public class AutoUnClaimCommand {
    public static Map<ServerPlayerEntity, ChunkPos> playerToChunk = new HashMap<>();

    public static void addPlayer(ServerPlayerEntity player) {
        // playerToFaction.put(player, faction);
        playerToChunk.put(player, player.getServerWorld().getChunk(player.getBlockPos()).getPos());
    }

    public static void removePlayer(ServerPlayerEntity player) {
        playerToChunk.remove(player);
        // playerToFaction.remove(player);
    }

    public static void autoUnClaimLoop() {
        for(Map.Entry<ServerPlayerEntity, ChunkPos> temp : playerToChunk.entrySet()) {
            ChunkPos chunkPos = temp.getKey().getServerWorld().getChunk(temp.getKey().getBlockPos()).getPos();
            if (chunkPos != temp.getValue()) {
                unClaim(temp.getKey());
                playerToChunk.replace(temp.getKey(), chunkPos);
            }
        }
    }

    public static void unClaim(ServerPlayerEntity player) {
        Member member = Member.get(player.getUuid());
        ChunkPos chunkPos = player.getServerWorld().getChunk(player.getBlockPos()).getPos();
        String dimension = player.getServerWorld().getRegistryKey().getValue().toString();

        Claim existingClaim = Claim.get(chunkPos.x, chunkPos.z, dimension);
        if(member == null) {
            return;
        }
        if (existingClaim == null) {
            new Message("You cannot unclaim a chunk you do not own.").fail().send(player, false);
        } else if(!Objects.equals(existingClaim.getFaction().name, member.getFaction().name)) {
            new Message("Another faction owns this chunk").fail().send(player, false);
        } else {
            Faction faction = member.getFaction();
            if(faction == null) {
                return;
            }
            existingClaim.remove();

            new Message("%s removed claim at chunk (%d, %d)", player.getName().asString(), existingClaim.x, existingClaim.z).send(faction);
            Dynmap.removeChunkClaim(chunkPos, faction);
        }
    }

    public static int startAutoUnClaim(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        addPlayer(context.getSource().getPlayer());
        return 0;
    }

    public static int stopAutoUnClaim(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        removePlayer(context.getSource().getPlayer());
        return 1;
    }

}
