package dev.koo.utils.sao;

import dev.koo.api.Tamaki;
import dev.koo.database.SQLite;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class GuildSystem {

    static DiscordApi api = Tamaki.getApi();

    public static void init() {

        getUserCountInGuild();
        addUserToGuild();
        removeUserFromGuild();

    }

    public static void getUserCountInGuild() {

        api.addMessageCreateListener(event -> {

            String[] message = event.getMessageContent().split(" ");

            if(message[0].equalsIgnoreCase(".sao") && message[1].equalsIgnoreCase("dde") && message[2].equalsIgnoreCase("status")) {

                ResultSet set = SQLite.onQuery("SELECT COUNT(userid) FROM sao WHERE guild = 'dde'");
                try {
                    if(set.next()) {

                        int count = set.getInt("COUNT(userid)");
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setTitle("Status")
                                .addInlineField("Name", "DdE 1")
                                .addInlineField("Mitglieder", count + "/30")
                                .addInlineField("Freie Plätze", String.valueOf(30-count))
                                .setFooter("Falls es Probleme gibt, wende dich bitte an einen Support oder Administrator.")
                        );

                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }


            } else
            if(message[0].equalsIgnoreCase(".sao") && message[1].equalsIgnoreCase("dde2") && message[2].equalsIgnoreCase("status")) {

                ResultSet set = SQLite.onQuery("SELECT COUNT(userid) FROM sao WHERE guild = 'dde2'");
                try {
                    if(set.next()) {

                        int count = set.getInt("COUNT(userid)");
                        event.getChannel().sendMessage(new EmbedBuilder()
                                .setColor(Color.GREEN)
                                .setTitle("Status")
                                .addInlineField("Name", "DdE 2")
                                .addInlineField("Mitglieder", count + "/30")
                                .addInlineField("Andere", "coming soon")
                                .setFooter("Falls es Probleme gibt, wende dich bitte an einen Support oder Administrator.")
                        );

                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }

        });

    }

    public static void addUserToGuild() {

        api.addMessageCreateListener(event -> {

            String[] message = event.getMessageContent().split(" ");
            if (message.length == 4) {
                if (message[0].equalsIgnoreCase(".sao") && message[1].equalsIgnoreCase("dde") && message[2].equalsIgnoreCase("add")) {

                    try {
                        String numberOnly= message[3].replaceAll("[^0-9]", "");
                        User user = api.getUserById(numberOnly).get();
                        SQLite.onUpdate("INSERT INTO sao(userid, nickname, guild, status) VALUES('" + user.getIdAsString() + "', '" + user.getName() + "', 'dde', 'member')");
                        event.getChannel().sendMessage(user.getName() + " wurde zur DdE hinzugefügt.");
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                }

                if (message[0].equalsIgnoreCase(".sao") && message[1].equalsIgnoreCase("dde2") && message[2].equalsIgnoreCase("add")) {

                    try {
                        String numberOnly= message[3].replaceAll("[^0-9]", "");
                        User user = api.getUserById(numberOnly).get();
                        SQLite.onUpdate("INSERT INTO sao(userid, nickname, guild, status) VALUES('" + user.getIdAsString() + "', '" + user.getName() + "', 'dde2', 'member')");
                        event.getChannel().sendMessage(user.getName() + " wurde zur DdE 2 hinzugefügt.");
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                }

            }

        });

    }

    public static void removeUserFromGuild() {

        api.addMessageCreateListener(event -> {

            String[] message = event.getMessageContent().split(" ");
            if (message.length == 4) {
                if (message[0].equalsIgnoreCase(".sao") && message[1].equalsIgnoreCase("dde") && message[2].equalsIgnoreCase("remove")) {

                    try {
                        String numberOnly= message[3].replaceAll("[^0-9]", "");
                        User user = api.getUserById(numberOnly).get();
                        SQLite.onUpdate("DELETE FROM sao WHERE userid = '" + numberOnly + "'");
                        event.getChannel().sendMessage(user.getName() + " wurde von der DdE 1 entfernt.");
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                }

                if (message[0].equalsIgnoreCase(".sao") && message[1].equalsIgnoreCase("dde2") && message[2].equalsIgnoreCase("remove")) {

                    try {
                        String numberOnly= message[3].replaceAll("[^0-9]", "");
                        User user = api.getUserById(numberOnly).get();
                        SQLite.onUpdate("DELETE FROM sao WHERE userid = '" + numberOnly + "'");
                        event.getChannel().sendMessage(user.getName() + " wurde von der DdE 2 entfernt.");
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                }

            }

        });

    }

}
