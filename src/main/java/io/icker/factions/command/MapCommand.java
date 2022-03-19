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
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ChunkPos;

public class MapCommand {

    public static int show(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        ServerPlayerEntity player = source.getPlayer();
        ServerWorld world = player.getServerWorld();

        ChunkPos chunkPos = world.getChunk(player.getBlockPos()).getPos();
        String dimension = world.getRegistryKey().getValue().toString();

        Member member = Member.get(player.getUuid());
        if(member == null) {
            return 1;
        }
        Faction faction = member.getFaction();

        // Print the header of the faction map.
        new Message("---------------[").format(Formatting.GRAY)
                .add(new Message(" F MAP ").format(Formatting.AQUA))
                .add(new Message("]---------------").format(Formatting.GRAY))
                .send(player, false);

        // Create and fill an array with the faction map.
        MutableText[] rows = new MutableText[11];
        for (int x = -5; x <= 5; x++) {
            MutableText row = new LiteralText("");
            for (int z = -6; z <= 6; z++) {
                Claim claim = Claim.get(chunkPos.x + x, chunkPos.z + z, dimension);
                if (x == 0 && z == 0) {
                    row.append(new LiteralText(" ■").formatted(Formatting.YELLOW));
                } else if (claim == null) {
                    row.append(new LiteralText(" ■").formatted(Formatting.GRAY));
                } else if (faction != null && claim.getFaction().name.equals(faction.name)) {
                    row.append(new LiteralText(" ■").formatted(Formatting.GREEN));
                } else {
                    row.append(new LiteralText(" ■").formatted(Formatting.RED));
                }
            }

            rows[x + 5] = row;
        }

        // Attach the legend to the rows and send them to the player.
        for (int i = 0; i < rows.length; i++) {
            MutableText row = rows[i];
            switch (i) {
                case 0 -> row.append(new LiteralText("  ■").formatted(Formatting.GRAY)).append(" Wilderness");
                case 1 -> row.append(new LiteralText("  ■").formatted(Formatting.GREEN)).append(" Your faction");
                case 2 -> row.append(new LiteralText("  ■").formatted(Formatting.RED)).append(" Other faction(s)");
                case 3 -> row.append(new LiteralText("  ■").formatted(Formatting.YELLOW)).append(" Your position");
            }

            player.sendMessage(row, false);
        }

        // Print the footer of the faction map.
        new Message("---------------------------------------").format(Formatting.GRAY)
                .send(player, false);

        return 1;
    }
}