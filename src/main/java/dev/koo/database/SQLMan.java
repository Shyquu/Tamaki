package dev.koo.database;

public class SQLMan {

    public static void onCreate() {

        SQLite.onUpdate("CREATE TABLE IF NOT EXISTS verify(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, userid TEXT, channelid TEXT)");
        SQLite.onUpdate("CREATE TABLE IF NOT EXISTS sao(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, userid TEXT, nickname TEXT, level INTEGER, guild TEXT, status TEXT)");
    }


}
