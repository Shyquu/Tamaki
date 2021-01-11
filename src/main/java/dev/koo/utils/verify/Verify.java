package dev.koo.utils.verify;

import dev.koo.api.Tamaki;
import dev.koo.utils.text.Test;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.util.concurrent.ExecutionException;

public class Verify {

    public static DiscordApi api = Tamaki.getApi();
    public static EmbedBuilder embedBuilder = new EmbedBuilder()
            .setTitle("Willkommen bei der Anmeldung!")
            .setColor(Color.ORANGE)
            .addField("*Anleitung*", "Hier wirst du ganz einfach durch die Anmeldung geleitet! \n Wenn du bereit bist, drücke einfach unten auf das ✅!")
            .setFooter("Falls es doch noch Probleme gibt, einfach bei einen Supporter Fragen!");

    public static void init() {

        finalMessage();
        sendMessage();
        systemMessage();
        debugMessage();

    }

    public static void finalMessage() {

        api.addServerMemberJoinListener(serverMemberJoinEvent -> {

            User user = serverMemberJoinEvent.getUser();
            System.out.println(user.getDiscriminatedName() + " gejoint.");
            user.addRole(api.getRoleById(797550949551833129L).get());
            Server server = serverMemberJoinEvent.getServer();
            try {
                generateNewMemberChannel(user, server);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Erfolg");
            TextChannel channel = api.getTextChannelById(Test.getTest()).get();
            try {
                channel.sendMessage(embedBuilder).get().addReaction("✅");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            api.addReactionAddListener(event -> {

                if (event.getEmoji().asUnicodeEmoji().get().equals("✅")) {

                    try {
                        stepOne(channel.getMessages(1).get().getNewestMessage().get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }

                }

            });

        });

    }

    public static void systemMessage() {

        api.addMessageCreateListener(event -> {

            String[] message = event.getMessageContent().split(" ");

            if (message[0].equalsIgnoreCase(".admin") && message[1].equalsIgnoreCase("debug")) {

                System.out.println("Neuer Member gejoint.");
                User user = event.getMessageAuthor().asUser().get();
                user.addRole(api.getRoleById(797550949551833129L).get());
                Server server = event.getServer().get();
                try {
                    generateNewMemberChannel(user, server);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Erfolg");

            }

        });

    }

    public static void sendMessage() {

        api.addServerJoinListener(event -> {
            if (event.getServer().getSystemChannel().isPresent()) {
                TextChannel channel = event.getServer().getSystemChannel().get();
                channel.sendMessage("test");
            }
        });

    }

    public static void debugMessage() {

        api.addMessageCreateListener(event -> {

            String[] message = event.getMessageContent().split(" ");

            if (message[0].equalsIgnoreCase(".admin") && message[1].equalsIgnoreCase("welcome")) {

                event.getChannel().sendMessage(embedBuilder);
                try {
                    event.getChannel().getMessages(1).get().getNewestMessage().get().addReaction("✅");
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

            }

        });

    }

    public static void generateNewMemberChannel(User user, Server server) throws ExecutionException, InterruptedException {


        ChannelCategory channelCategory = api.getChannelCategoryById(797467711034359819L).get();
        Role neu = api.getRoleById(797550949551833129L).get();
        ServerTextChannelBuilder channelBuilder = new ServerTextChannelBuilder(server)
                .setName(user.getDiscriminatedName())
                .setAuditLogReason("new member joined.")
                .setCategory(channelCategory)
                .setTopic("Hier findet die Anmeldung von " + user.getName() + " statt.")
                .addPermissionOverwrite(user, new PermissionsBuilder(Permissions.fromBitmask(0x00000400)).setAllowed(PermissionType.READ_MESSAGES).setAllowed(PermissionType.READ_MESSAGE_HISTORY).setAllowed(PermissionType.ADD_REACTIONS).build())
                .addPermissionOverwrite(neu, new PermissionsBuilder(Permissions.fromBitmask(0x00000400)).setAllDenied().build());
        Test.setTest(String.valueOf(channelBuilder.create().get().getId()));

    }

    public static void stepOne(Message message) {

        message.edit(new EmbedBuilder()
                .setTitle("Von welchem Spiel hast du zu uns gefunden?")
                .setDescription("Klicke unten einfach auf das zugehörige Emote.")
                .addInlineField("Sword Art Online: Integral Factor", "[emote:sao]")
                .addInlineField("Tales Of Wind", "[emote:tow]")
                .addInlineField("Brawl Stars", "[emote:bs]")
                .addInlineField("Clash Royale", "[emote:cr]")
                .addField("Andere", "[emote:?]")
        );

    }

}
