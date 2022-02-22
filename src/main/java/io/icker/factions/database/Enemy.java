package io.icker.factions.database;

import java.util.ArrayList;
public class Enemy {
    public String target;
    public String source;

    public static Enemy get(String target, String source) {
        Query query = new Query("SELECT * FROM Enemy WHERE source = ? AND target = ?;")
                .set(source, target)
                .executeQuery();

        if (!query.success)
            return null;
        return new Enemy(query.getString("source"), query.getString("target"));
    }

    public static Enemy add(String source, String target) {
        Query query = new Query("INSERT INTO Enemies (source, target) VALUES (?, ?);")
                .set(source, target)
                .executeUpdate();

        if (!query.success)
            return null;
        return new Enemy(source, target);
    }

    public Enemy(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public void remove() {
        new Query("DELETE FROM Enemies WHERE source = ? AND target = ?;")
                .set(this.source, this.target)
                .executeUpdate();
    }

    public static void remove(String source, String target) {
        new Query("DELETE FROM Enemies WHERE source = ? AND target = ?;")
                .set(source, target)
                .executeUpdate();
    }

    public static boolean checkIfEnemy(String source, String target) {
        Query query = new Query("SELECT EXISTS(SELECT * FROM Enemies WHERE source = ? AND target = ?);")
                .set(source, target)
                .executeQuery();

        return query.exists();
    }

    public static ArrayList<Enemy> getEnemies(String source) {
        Query query = new Query("SELECT * FROM Enemies WHERE source = ?;")
                .set(source)
                .executeQuery();

        ArrayList<Enemy> enemies = new ArrayList<Enemy>();
        if (!query.success)
            return enemies;

        while (query.next()) {
            enemies.add(new Enemy(query.getString("source"), query.getString("target")));
        }
        return enemies;
    }
}