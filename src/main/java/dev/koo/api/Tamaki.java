package dev.koo.api;

import dev.koo.database.SQLMan;
import dev.koo.database.SQLite;
import dev.koo.utils.text.OAuth2;
import dev.koo.utils.verify.Verify;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;

import java.util.concurrent.ExecutionException;

public class Tamaki {

    private static DiscordApi api;

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        api = new DiscordApiBuilder()
                .setToken(OAuth2.getToken)
                .setIntents(Intent.GUILDS, Intent.GUILD_MEMBERS, Intent.GUILD_EMOJIS, Intent.GUILD_MESSAGE_REACTIONS, Intent.GUILD_MESSAGES)
                .login()
                .get();
        init();

    }

    public static void init() {

        api.updateActivity("test");
        // Database
        SQLite.connect();
        SQLMan.onCreate();
        // Commands

        // VerifySystem
        Verify.init();

        System.out.println("Tamaki gestartet.");
    }

    public static DiscordApi getApi() {
        return api;
    }
}
