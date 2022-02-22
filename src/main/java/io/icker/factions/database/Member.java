package io.icker.factions.database;

import java.util.UUID;

public class Member {
    public final UUID uuid;
    private final String factionName;
    private Rank rank;

    public static Member get(UUID uuid) {
        Query query = new Query("SELECT faction, rank FROM Member WHERE uuid = ?;")
            .set(uuid)
            .executeQuery();

        if (!query.success) return null;
        return new Member(uuid, query.getString("faction"), Rank.valueOf(query.getString("rank").toUpperCase()));
    }

    public static Member add(UUID uuid, String faction) {
        return add(uuid, faction, Rank.CIVILIAN);
    }

    public static Member add(UUID uuid, String faction, Rank rank) {
        Query query = new Query("INSERT INTO Member VALUES (?, ?, ?);")
                .set(uuid, faction, rank.name().toLowerCase())
                .executeUpdate();
        if (!query.success) return null;
        return new Member(uuid, faction, rank);
    }

    public Member(UUID uuid, String faction) {
        this(uuid, faction, Rank.CIVILIAN);
    }

    public Member(UUID uuid, String faction, Rank rank) {
        this.uuid = uuid;
        this.factionName = faction;
        this.rank = rank;
    }

    public Faction getFaction() {
        return Faction.get(factionName);
    }

    public Rank getRank() {
        return rank;
    }

    public void updateRank(Rank rank) {
        new Query("UPDATE Member SET rank = ? WHERE uuid = ?;")
                .set(rank.name().toLowerCase(), uuid)
                .executeUpdate();
    }

    public void remove() {
        new Query("DELETE FROM Member WHERE uuid = ?;")
            .set(uuid)
            .executeUpdate();
    }

    public enum Rank {
        OWNER,
        CO_OWNER,
        OFFICER,
        CIVILIAN;
    }
}