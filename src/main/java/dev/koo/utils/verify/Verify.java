package dev.koo.utils.verify;

import dev.koo.api.Tamaki;
import dev.koo.database.SQLite;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.ServerTextChannelBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class Verify {

    public static DiscordApi api = Tamaki.getApi();
    public static Emoji sao = api.getCustomEmojiById(798518605595148308L).get();
    public static Emoji bs = api.getCustomEmojiById(798518660909367326L).get();
    public static Emoji tow = api.getCustomEmojiById(798518772252016651L).get();
    public static Emoji gi = api.getCustomEmojiById(798518693599641601L).get();
    public static Emoji cr = api.getCustomEmojiById(798518727342948373L).get();
    public static Emoji q = api.getCustomEmojiById(798520850882101270L).get();
    public static EmbedBuilder embedBuilder = new EmbedBuilder()
            .setTitle("Willkommen bei der Anmeldung!")
            .setColor(Color.ORANGE)
            .addField("*Anleitung*", "Hier wirst du ganz einfach durch die Anmeldung geleitet! \n Wenn du bereit bist, drücke einfach unten auf das ✅!")
            .setFooter("Falls es doch noch Probleme gibt, einfach bei einen Supporter Fragen!");
    public static EmbedBuilder howold = new EmbedBuilder()
            .setColor(Color.ORANGE)
            .setTitle("Wie alt bist du?")
            .setDescription("Hier kannst du dein Alter auswählen, bitte nur korrekte Angaben. Falls wir dich bei einer Falschaussage erwischen gibt es einen strickten Ban.")
            .addInlineField("13-16 Jahre", ":boy:")
            .addInlineField("17-18 Jahre", ":adult:")
            .addInlineField("18+", ":man:")
            .setFooter("Falls du über 100 Jahre alt sein solltest, kontaktiere bitte einen Administrator, dieser wird dir weiter helfen können.");
    public static EmbedBuilder nsfw = new EmbedBuilder()
            .setColor(Color.ORANGE)
            .setTitle("NSFW-Content?")
            .setDescription("Weil du älter als 16 bist, bieten wir dir die möglichkeit, zu entscheiden ob du auf einen NSFW-Kanal zugriff haben möchtest? Reagiere einfach mit dem entsprechenden Emote.")
            .addInlineField("NSFW Ja", "☑️")
            .addInlineField("NSFW Nein", ":x:")
            .setFooter("Diese Entscheidung kann später nicht noch einmal getroffen werden, entscheide Weise.");
    public static EmbedBuilder clan = new EmbedBuilder()
            .setColor(Color.ORANGE)
            .setTitle("Clan?")
            .setDescription("Bist du schon in einem Clan von uns, falls nicht, wähle einfach das Kreuz aus, falls doch, wähle das Häkchen aus.")
            .addInlineField("Clan [Ja]", "[emote]")
            .addInlineField("Clan [Nein]", "[emote]")
            .setFooter("Falls es Probleme gibt, kontaktiere bitte den Support oder einen Admin.");

    public static void init() {

        finalMessage();
        sendMessage();
        systemMessage();
        debugMessage();
        reactionOne();
        nsfwRoute();
        sfwRoute();
        afterNSFW();
        saoRoute();

    }

    public static void finalMessage() {

        api.addServerMemberJoinListener(serverMemberJoinEvent -> {

            User user = serverMemberJoinEvent.getUser();
            System.out.println("[" + new Date() + "] " + user.getDiscriminatedName() + " joined.");
            user.addRole(api.getRoleById(797550949551833129L).get());
            Server server = serverMemberJoinEvent.getServer();

            try {
                generateNewMemberChannel(user, server);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            ResultSet set = SQLite.onQuery("SELECT channelid FROM verify WHERE userid = '" + serverMemberJoinEvent.getUser().getIdAsString() + "' ORDER BY id DESC LIMIT 1");

            try {
                assert set != null;
                if (set.next()) {

                    String channelid = set.getString("channelid");
                    System.out.println("[ID:" + channelid + "] TextChannel generated");
                    TextChannel channel = api.getTextChannelById(channelid).get();
                    try {
                        channel.sendMessage(embedBuilder).get().addReaction("✅");
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    channel.sendMessage("<@" + serverMemberJoinEvent.getUser().getIdAsString() + ">").get().delete();

                }
            } catch (SQLException | ExecutionException | InterruptedException throwables) {
                throwables.printStackTrace();
            }

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
        SQLite.onUpdate("INSERT INTO verify(userid, channelid) VALUES('" + user.getIdAsString() + "', '" + channelBuilder.create().get().getIdAsString() + "')");

    }

    public static void reactionOne() {

        api.addReactionAddListener(event -> {

            Emoji emoji = event.getEmoji();
            TextChannel channel = event.getChannel();

            ResultSet set = SQLite.onQuery("SELECT channelid FROM verify WHERE userid = '" + event.getUserIdAsString() + "' ORDER BY id DESC LIMIT 1");

            try {
                if (set.next()) {
                    String channelid = set.getString("channelid");
                    if (channel.getIdAsString().equalsIgnoreCase(channelid)) {

                        if (emoji.equalsEmoji("✅")) {

                            event.getMessage().get().edit(howold);
                            System.out.println("[STEP:1] complete by " + event.getUser().get().getDiscriminatedName());
                            event.getMessage().get().removeAllReactions();

                            event.getMessage().get().addReactions("\uD83D\uDC66", "\uD83E\uDDD1", "\uD83D\uDC68");

                        }

                    }

                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });

    }

    public static void stepOne(Message message) {

        message.edit(new EmbedBuilder()
                .setTitle("Von welchem Spiel hast du zu uns gefunden?")
                .setDescription("Klicke unten einfach auf das zugehörige Emote.")
                .addInlineField("SAO:IF", "<:sao:798518605595148308>")
                .addInlineField("Tales Of Wind", "<:tow:798518772252016651>")
                .addInlineField("Brawl Stars", "<:bs:798518660909367326>")
                .addInlineField("Clash Royale", "<:cr:798518727342948373>")
                .addInlineField("Genshin Imapct", "<:gi:798518693599641601>")
                .addInlineField("Andere", ":grey_question:")
                .setColor(Color.ORANGE)
        );

    }

    public static void nsfwRoute() {

        api.addReactionAddListener(event -> {

            ResultSet set = SQLite.onQuery("SELECT channelid FROM verify WHERE userid = '" + event.getUserIdAsString() + "' ORDER BY id DESC LIMIT 1");

            try {
                if (set.next()) {

                    String channelid = set.getString("channelid");
                    if (event.getChannel().getIdAsString().equalsIgnoreCase(channelid)) {

                        if (event.getEmoji().equalsEmoji("\uD83E\uDDD1") || event.getEmoji().equalsEmoji("\uD83D\uDC68")) {

                            event.getMessage().get().removeAllReactions();
                            event.getMessage().get().edit(nsfw);
                            event.getMessage().get().addReactions("☑️", "❌");

                        }

                    }

                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

    }

    public static void afterNSFW() {

        api.addReactionAddListener(event -> {

            ResultSet set = SQLite.onQuery("SELECT channelid FROM verify WHERE userid = '" + event.getUserIdAsString() + "' ORDER BY id DESC LIMIT 1");

            try {
                if (set.next()) {

                    String channelid = set.getString("channelid");
                    if (event.getChannel().getIdAsString().equalsIgnoreCase(channelid)) {

                        if (event.getEmoji().equalsEmoji("☑️")) {

                            event.getMessage().get().removeAllReactions();
                            event.getUser().get().addRole(api.getRoleById(798530464309837874L).get());
                            stepOne(event.getMessage().get());
                            event.getMessage().get().addReactions(sao, tow, bs, cr, gi, q);

                        } else if (event.getEmoji().equalsEmoji("❌")) {

                            event.getMessage().get().removeAllReactions();
                            stepOne(event.getMessage().get());
                            event.getMessage().get().addReactions(sao, tow, bs, cr, gi, q);

                        }

                    }

                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void sfwRoute() {

        api.addReactionAddListener(event -> {

            ResultSet set = SQLite.onQuery("SELECT channelid FROM verify WHERE userid = '" + event.getUserIdAsString() + "' ORDER BY id DESC LIMIT 1");

            try {
                if (set.next()) {

                    String channelid = set.getString("channelid");
                    if (event.getChannel().getIdAsString().equalsIgnoreCase(channelid)) {

                        if (event.getEmoji().equalsEmoji("\uD83D\uDC66")) {

                            event.getMessage().get().removeAllReactions();
                            stepOne(event.getMessage().get());
                            event.getMessage().get().addReactions(sao, tow, bs, cr, gi, q);

                        }

                    }

                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

    }

    public static void closeChannel() {

        api.addMessageCreateListener(event -> {

            String[] message = event.getMessageContent().split(" ");

            if (message[0].equalsIgnoreCase(".close") && isSupporterOrHigher(event.getMessageAuthor().asUser().get(), event.getServer().get())) {

                TextChannel channel = event.getChannel();
                event.getServer().get().getChannelById(channel.getId()).get().delete();

            }

        });

    }

    public static boolean isSupporterOrHigher(User user, Server server) {

        Role supporter = api.getRoleById(34534673462L).get();

        if (user.canManageRole(supporter) || user.getRoles(server).contains(supporter)) {

            return true;

        } else return false;

    }

    public static void saoRoute() {

        api.addReactionAddListener(event -> {

            ResultSet set = SQLite.onQuery("SELECT channelid FROM verify WHERE userid = '" + event.getUserIdAsString() + "' ORDER BY id DESC LIMIT 1");

            try {
                if (set.next()) {

                    String channelid = set.getString("channelid");
                    if (event.getChannel().getIdAsString().equalsIgnoreCase(channelid)) {

                        Emoji emoji = api.getCustomEmojiById(798518605595148308L).get();
                        if (event.getEmoji().equalsEmoji(emoji)) {


                            event.getMessage().get().removeAllReactions();
                            event.getMessage().get().edit(new EmbedBuilder()
                                    .setTitle("Level?")
                                    .setDescription("Gib hier einfach dein ungefähres Level an, damit wir dich einordnen können.")
                                    .addInlineField("Level 0-20", ":one:")
                                    .addInlineField("Level 20-40", ":two:")
                                    .addInlineField("Level 40-60", ":three:")
                                    .addInlineField("Level 60-80", ":four:")
                                    .addInlineField("Level 80-100", ":five:")
                                    .addInlineField("Level 100-120", ":six:")
                                    .addInlineField("Level 120+", ":seven:")
                                    .setFooter("Falls es Probleme gibt, kontaktiere bitte einen Supporter oder ein höher gestelltes Teammitglied, damit wir dir helfen können.")
                                    .setColor(Color.ORANGE)
                            );
                            event.getMessage().get().addReactions("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣");


                        }

                    }

                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public static void giRoute() {

        api.addReactionAddListener(event -> {

            ResultSet set = SQLite.onQuery("SELECT channelid FROM verify WHERE userid = '" + event.getUserIdAsString() + "' ORDER BY id DESC LIMIT 1");

            try {
                if (set.next()) {

                    String channelid = set.getString("channelid");
                    if (event.getChannel().getIdAsString().equalsIgnoreCase(channelid)) {

                        if (event.getEmoji().equalsEmoji(gi)) {

                            event.getMessage().get().removeAllReactions();
                            event.getChannel().sendMessage(new EmbedBuilder()
                                    .setColor(Color.ORANGE)
                                    .setTitle("Info")
                                    .setDescription("Wir haben dir die Rolle 'Genshin Impact' gegeben. Mit dieser kannst du auf den Genshin Impact Channel zugreifen und dich mit deinen Kameraden austauschen. Falls es noch Fragen zu Genshin Impact gibt, wende dich an unseren Genshin Impact Chat!")
                                    .addField("Voila!", "Klicke auf das [emote] um weiter zu kommen.")
                                    .setFooter("Falls es noch andere Fragen gibt, wende dich an einen Supporter oder Admin.")
                            ).get().addReaction("");

                        }

                    }

                }
            } catch (SQLException | InterruptedException | ExecutionException throwables) {
                throwables.printStackTrace();
            }
        });

    }

}
