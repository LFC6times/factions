package io.icker.factions.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.icker.factions.database.Ally;
import io.icker.factions.database.Enemy;
import io.icker.factions.database.Faction;
import io.icker.factions.database.Member;
import io.icker.factions.util.Dynmap;
import io.icker.factions.util.Message;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.Objects;

public class EnemyCommand {

    public static int add(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");

        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        Faction sourceFaction = Objects.requireNonNull(Member.get(player.getUuid())).getFaction();
        Faction targetFaction = Objects.requireNonNull(Member.get(target.getUuid())).getFaction();


        if (Enemy.checkIfEnemy(sourceFaction.name, targetFaction.name)) {
            new Message(targetFaction.name + " is already an enemy.").format(Formatting.RED).send(player, false);
        } else if (Objects.equals(sourceFaction.name, targetFaction.name)) {
            new Message("You can't make yourself an enemy.").format(Formatting.RED).send(player, false);
        } else if (Ally.checkIfAlly(sourceFaction.name, targetFaction.name)) {
            new Message(targetFaction.name + " is your ally. Do /factions ally remove <faction> then do /factions enemy add <faction> again.").format(Formatting.RED).send(player, false);
        } else {
            Enemy.add(sourceFaction.name, targetFaction.name);

            new Message(targetFaction.name + " is now an enemy")
                    .send(player, false);
            new Message(
                    sourceFaction.name + " has made you their enemy. Click to make them your enemy.").format(Formatting.YELLOW)
                    .click("/factions enemy add " + source.getName())
                    .send(target, false);
            Dynmap.addFactionsToUpdate(Objects.requireNonNull(Member.get(player.getUuid())).getFaction());
        }

        return 1;
    }

    public static int remove(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity target = EntityArgumentType.getPlayer(context, "player");

        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        Faction sourceFaction = Objects.requireNonNull(Member.get(player.getUuid())).getFaction();
        Faction targetFaction = Objects.requireNonNull(Member.get(target.getUuid())).getFaction();

        if (!Enemy.checkIfEnemy(sourceFaction.name, targetFaction.name)) {
            new Message(targetFaction.name + " is not your enemy.").format(Formatting.RED).send(player, false);
        } else {
            Enemy.remove(sourceFaction.name, targetFaction.name);

            new Message(target.getName().getString() + " is no longer your enemy.")
                    .send(sourceFaction);
            new Message(
                    "You are no longer enemies with " + sourceFaction.name + ". Click to remove them as an enemy.").format(Formatting.YELLOW)
                    .click("/factions enemy remove " + sourceFaction.name)
                    .send(target, false);
            Dynmap.addFactionsToUpdate(Objects.requireNonNull(Member.get(player.getUuid())).getFaction());
        }

        return 1;
    }
}