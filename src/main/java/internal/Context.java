package internal;

import exceptions.PropertyNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import org.jasypt.encryption.StringEncryptor;

import java.io.IOException;
import java.util.*;

import static encryption_utils.Encryptor.getStringEncryptor;
import static internal.Utils.loadProperties;

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
    private final RoleIds roleIds;
    @Getter
    private final List<GamingRole> gamingRoles;
    @Getter
    private final List<CheerMeUp> genericCheerMeUps;
    @Getter
    private final Map<String, CheerMeUp> specialCheerMeUps;
    @Getter
    private final CheerMeUp ultraRareCheerMeUp;

    public Context(int encryptorPoolSize, String encryptorPassword, String encryptorSalt) throws IOException {
        encryptor = getStringEncryptor(encryptorPoolSize, encryptorPassword, encryptorSalt);

        Properties properties = loadProperties(Constants.CONTEXT_PROPERTIES);

        botId = initializeBotId(properties);
        commands = initializeCommands(properties);
        messages = initializeMessages(properties);
        channelIds = initializeChannelIds(properties);
        roleIds = initializeMemberRoleIds(properties);
        gamingRoles = initializeGamingRoles(properties);
        genericCheerMeUps = initializeGenericCheerMeUps(properties);
        specialCheerMeUps = initializeSpecialCheerMeUps(properties);
        ultraRareCheerMeUp = initializeUltraRareCheerMeUps(properties);
    }

    public boolean hasGenericCheerMeUps() {
        return genericCheerMeUps != null;
    }

    public boolean hasSpecialCheerMeUps() {
        return specialCheerMeUps != null;
    }

    public boolean hasUltraRareCheerMeUp() {
        return ultraRareCheerMeUp != null;
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

    private RoleIds initializeMemberRoleIds(Properties properties) {
        String roleNew = properties.getProperty("role_id.new");
        String roleInternal = properties.getProperty("role_id.internal");
        String offline = properties.getProperty("role_id.invisible");

        if (roleNew == null || roleInternal == null) {
            throw new PropertyNotFoundException("One or more Role ID Properties not found");
        }

        return new RoleIds(
                decrypt(roleNew),
                decrypt(roleInternal),
                offline == null ? "" : decrypt(offline));
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

    private List<CheerMeUp> initializeGenericCheerMeUps(Properties properties) {
        List<CheerMeUp> list = new ArrayList<>();

        int i = 0;
        while (true) {
            String type = properties.getProperty("cheer_me_up.generic.type." + i);
            String content = properties.getProperty("cheer_me_up.generic.content." + i);
            String extra = properties.getProperty("cheer_me_up.generic.extra." + i);

            if (type == null || content == null) {
                break;
            }

            list.add(new CheerMeUp(
                    Constants.CHEER_ME_UP_TYPE.valueOf(decrypt(type).toUpperCase()),
                    decrypt(content),
                    extra == null ? "" : decrypt(extra)));

            i++;
        }
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

    private Map<String, CheerMeUp> initializeSpecialCheerMeUps(Properties properties) {
        Map<String, CheerMeUp> map = new HashMap<>();

        int i = 0;
        while (true) {
            String trigger = properties.getProperty("cheer_me_up.special.trigger." + i);
            String type = properties.getProperty("cheer_me_up.special.type." + i);
            String content = properties.getProperty("cheer_me_up.special.content." + i);
            String extra = properties.getProperty("cheer_me_up.special.extra." + i);

            if (trigger == null || type == null || content == null) {
                break;
            }

            map.put(trigger, new CheerMeUp(
                    Constants.CHEER_ME_UP_TYPE.valueOf(decrypt(type).toUpperCase()),
                    decrypt(content),
                    extra == null ? "" : decrypt(extra)));

            i++;
        }
        if (map.isEmpty()) {
            return null;
        }
        return map;
    }

    private CheerMeUp initializeUltraRareCheerMeUps(Properties properties) {
        String type = properties.getProperty("cheer_me_up.ultra_rare.type");
        String content = properties.getProperty("cheer_me_up.ultra_rare.content");
        String extra = properties.getProperty("cheer_me_up.ultra_rare.extra");

        if (type == null || content == null) {
            return null;
        }
        return new CheerMeUp(
                Constants.CHEER_ME_UP_TYPE.valueOf(decrypt(type).toUpperCase()),
                decrypt(content),
                extra == null ? "" : decrypt(extra));
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
    public class RoleIds {
        private final String newMember;
        private final String internalMember;
        private final String invisible;

        public boolean hasInvisibleRole() {
            return !invisible.isEmpty();
        }
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

    @Getter
    @AllArgsConstructor
    public class CheerMeUp {
        private final Constants.CHEER_ME_UP_TYPE type;
        private final String content;
        private final String extraMessage;
    }
}
