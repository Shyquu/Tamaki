package dev.koo.database;

public class SQLMan {

    public static void onCreate() {

        SQLite.onUpdate("CREATE TABLE IF NOT EXISTS verify(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, userid TEXT, channelid TEXT)");

    }


}
