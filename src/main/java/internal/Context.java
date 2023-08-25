package internal;

import exceptions.PropertyNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import org.jasypt.encryption.StringEncryptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static encryption_utils.Encryptor.getStringEncryptor;

public class Context {

    private final StringEncryptor encryptor;

    @Getter
    private final String botId;
    @Getter
    private final Commands commands;
    @Getter
    private final Messages messages;
    @Getter
    private final ChannelIds channelIds;
    @Getter
    private final MemberRoleIds roleIds;
    @Getter
    private final List<GamingRole> gamingRoles;

    public Context(int encryptorPoolSize, String encryptorPassword, String encryptorSalt) throws IOException {
        encryptor = getStringEncryptor(encryptorPoolSize, encryptorPassword, encryptorSalt);

        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("context.properties");
        Properties properties = new Properties();
        properties.load(in);
        in.close();

        botId = initializeBotId(properties);
        commands = initializeCommands(properties);
        messages = initializeMessages(properties);
        channelIds = initializeChannelIds(properties);
        roleIds = initializeMemberRoleIds(properties);
        gamingRoles = initializeGamingRoles(properties);
    }

    private String decrypt(String value) {
        if (value.startsWith("DEC-")) {
            return encryptor.decrypt(value.split("-", 2)[1]);
        }
        return value;
    }

    private String initializeBotId(Properties properties) {
        String botId = properties.getProperty("bot_id");

        if (botId == null) {
            throw new PropertyNotFoundException("Property bot_id not found");
        }

        return decrypt(botId);
    }

    private Commands initializeCommands(Properties properties) {
        String indicator = properties.getProperty("command.indicator");
        String reactionRole = properties.getProperty("command.reaction_roles");
        String updateReactionRole = properties.getProperty("command.update_reaction_roles");

        if (indicator == null || reactionRole == null || updateReactionRole == null) {
            throw new PropertyNotFoundException("One or more Command Properties not found");
        }

        return new Commands(decrypt(indicator), decrypt(reactionRole), decrypt(updateReactionRole));
    }

    private Messages initializeMessages(Properties properties) {
        String embedHexColor = properties.getProperty("message.embed_hex_color");
        String embedText = properties.getProperty("message.embed_text");
        String welcome = properties.getProperty("message.welcome");
        String badName = properties.getProperty("message.bad_name");
        String longName = properties.getProperty("message.long_name");

        if (embedHexColor == null || embedText == null || welcome == null || badName == null || longName == null) {
            throw new PropertyNotFoundException("One or more Message Properties not found");
        }

        return new Messages(decrypt(embedHexColor), decrypt(embedText), decrypt(welcome), decrypt(badName),
                decrypt(longName));
    }

    private ChannelIds initializeChannelIds(Properties properties) {
        String channelNew = properties.getProperty("channel_id.new");
        String channelRoles = properties.getProperty("channel_id.roles");

        if (channelNew == null || channelRoles == null) {
            throw new PropertyNotFoundException("One or more Channel ID Properties not found");
        }

        return new ChannelIds(decrypt(channelNew), decrypt(channelRoles));
    }

    private MemberRoleIds initializeMemberRoleIds(Properties properties) {
        String roleNew = properties.getProperty("role_id.new");
        String roleInternal = properties.getProperty("role_id.internal");

        if (roleNew == null || roleInternal == null) {
            throw new PropertyNotFoundException("One or more Role ID Properties not found");
        }

        return new MemberRoleIds(decrypt(roleNew), decrypt(roleInternal));
    }

    private List<GamingRole> initializeGamingRoles(Properties properties) {
        List<GamingRole> list = new ArrayList<>();

        int i = 0;
        while (true) {
            String id = properties.getProperty("game.id." + i);
            String name = properties.getProperty("game.name." + i);
            String unicode = properties.getProperty("game.emoji_code." + i);

            if (id == null || name == null || unicode == null) {
                break;
            }

            list.add(new GamingRole(decrypt(id), decrypt(name), Emoji.fromUnicode(decrypt(unicode))));
            i++;
        }

        if (list.isEmpty()) {
            throw new PropertyNotFoundException("No Game Properties found");
        }

        return list;
    }

    @Getter
    public class Commands {
        private final String indicator;
        private final String reactionRoles;
        private final String updateReactionRoles;

        public Commands(String indicator, String reactionRoles, String updateReactionRoles) {
            this.indicator = indicator;
            this.reactionRoles = this.indicator + reactionRoles;
            this.updateReactionRoles = this.indicator + updateReactionRoles;
        }
    }

    @Getter
    @AllArgsConstructor
    public class Messages {
        private final String embedColorHex;
        private final String embedText;
        private final String welcome;
        private final String badName;
        private final String longName;
    }

    @Getter
    @AllArgsConstructor
    public class ChannelIds {
        private final String newMembers;
        private final String roles;
    }

    @Getter
    @AllArgsConstructor
    public class MemberRoleIds {
        private final String newMember;
        private final String internalMember;
    }

    @Getter
    @AllArgsConstructor
    public class GamingRole {
        private final String id;
        private final String name;
        private final UnicodeEmoji emoji;

        public String formattedOutput() {
            return emoji.getFormatted() + " for '" + name + "'\n";
        }
    }
}
