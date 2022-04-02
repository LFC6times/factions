package io.icker.factions.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.icker.factions.database.Claim;
import io.icker.factions.database.Faction;
import io.icker.factions.database.Member;
import io.icker.factions.util.Message;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

public class MapCommand {

    public static int show(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        ServerPlayerEntity player = source.getPlayer();
        ServerWorld world = player.getServerWorld();

        ChunkPos chunkPos = world.getChunk(player.getBlockPos()).getPos();
        String dimension = world.getRegistryKey().getValue().toString();

        Member member = Member.get(player.getUuid());
        Faction faction = member == null ? null : member.getFaction();

        // Print the header of the faction map.
        new Message("---------------[").format(Formatting.GRAY)
                .add(new Message(" F MAP ").format(Formatting.AQUA))
                .add(new Message("]---------------").format(Formatting.GRAY))
                .send(player, false);

        Map<String, Formatting> factions = new HashMap<>();

        // Create and fill an array with the faction map.
        MutableText[] rows = new MutableText[11];
        for (int z = -5; z <= 5; z++) {
            MutableText row = new LiteralText("");
            for (int x = -6; x <= 6; x++) {
                Claim claim = Claim.get(chunkPos.x + x, chunkPos.z + z, dimension);
                if (x == 0 && z == 0) {
                    row.append(new LiteralText(" ■").formatted(Formatting.YELLOW));
                } else if (claim == null) {
                    row.append(new LiteralText(" ■").formatted(Formatting.GRAY));
                } else if (faction != null && claim.getFaction().name.equals(faction.name)) {
                    row.append(new LiteralText(" ■").formatted(Formatting.GREEN));
                } else {
                    Faction owner = claim.getFaction();
                    factions.put(owner.name, owner.color);

                    row.append(new LiteralText(" ■")
                            .formatted(owner.color)
                            .styled((style)
                                    -> style.withHoverEvent(
                                    new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new LiteralText(owner.name)))
                            ));
                }
            }

            rows[z + 5] = row;
        }

        // Attach the legend to the rows and send them to the player.
        player.sendMessage(rows[0].append(new LiteralText("  ■").formatted(Formatting.GRAY)).append(" Wilderness"), false);
        player.sendMessage(rows[1].append(new LiteralText("  ■").formatted(Formatting.GREEN)).append(" Your faction"), false);
        player.sendMessage(rows[2].append(new LiteralText("  ■").formatted(Formatting.YELLOW)).append(" Your position"), false);

        int i = 3;
        for (Map.Entry<String, Formatting> entry : factions.entrySet()) {
            if (i >= rows.length)
                break;

            player.sendMessage(rows[i].append(new LiteralText("  ■").formatted(entry.getValue())).append(" " + entry.getKey()), false);
            i++;
        }

        // Send remaining rows.
        for (; i < rows.length; i++) {
            player.sendMessage(rows[i], false);
        }

        // Print the footer of the faction map.
        new Message("---------------------------------------").format(Formatting.GRAY)
                .send(player, false);

        return 1;
    }
}