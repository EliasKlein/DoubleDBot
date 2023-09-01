package listeners;

import internal.Context;
import internal.ExtendedListenerAdapter;
import internal.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MessageListener extends ExtendedListenerAdapter {

    public MessageListener(Context context) {
        super(context);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromGuild()) {
            if (event.getMessage().getContentStripped().startsWith(context.getCommands().getIndicator())) {
                actOnCommand(event);
            } else {
                if (event.getChannel().getId().equals(context.getChannelIds().getNewMembers())) {
                    actOnNewMemberMessage(event);
                }
            }
            if (context.getRoleIds().hasInvisibleRole()) {
                brandTheInvisible(event);
            }
        }
    }

    private void actOnCommand(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        String[] command = event.getMessage().getContentStripped().split(" ");
        TextChannel rolesChannel = guild.getTextChannelById(context.getChannelIds().getRoles());
        if (event.getChannel().equals(rolesChannel)) {
            if (command[0].equals(context.getCommands().getReactionRoles())) {
                postReactionRoles(rolesChannel);
            } else if (command[0].equals(context.getCommands().getUpdateReactionRoles())) {
                if (command.length != 2) {
                    rolesChannel.sendMessage(
                            String.format("The command '%s' needs the embed ID as an argument", command[0])).queue();
                } else {
                    updateReactionRoles(rolesChannel, command[1]);
                }
            }
        }
    }

    private void postReactionRoles(TextChannel channel) {
        EmbedBuilder embedBuilder = getReactionRoleEmbed();
        channel.sendMessageEmbeds(embedBuilder.build()).queue(embed -> {
            String embedId = embed.getId();
            for (Context.GamingRole entry : context.getGamingRoles()) {
                channel.addReactionById(embedId, entry.getEmoji()).queue();
            }
        });
    }

    private void updateReactionRoles(TextChannel channel, String embedId) {
        EmbedBuilder embedBuilder = getReactionRoleEmbed();
        MessageHistory history = channel.getHistoryBefore(channel.getLatestMessageId(), 100).complete();
        Message reactionRolesEmbed;
        try {
            reactionRolesEmbed = history.getMessageById(embedId);
        } catch (NumberFormatException e) {
            channel.sendMessage("Embed ID needs to be a number").queue();
            return;
        }
        if (reactionRolesEmbed == null) {
            channel.sendMessage("Embed ID not found").queue();
            return;
        }
        if (!context.getBotId().equals(reactionRolesEmbed.getAuthor().getId())) {
            channel.sendMessage("Message ID does not belong to embed by bot").queue();
            return;
        }
        channel.editMessageEmbedsById(embedId, embedBuilder.build()).queue();

        List<String> reactionEmojis = new ArrayList<>();
        for (MessageReaction reaction : reactionRolesEmbed.getReactions()) {
            reactionEmojis.add(reaction.getEmoji().asUnicode().getAsCodepoints());
        }
        for (Context.GamingRole entry : context.getGamingRoles()) {
            if (!reactionEmojis.contains(entry.getEmoji().getAsCodepoints())) {
                channel.addReactionById(embedId, entry.getEmoji()).queue();
            }
        }
    }

    private EmbedBuilder getReactionRoleEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode(context.getMessages().getEmbedColorHex()));
        embedBuilder.setTitle(context.getMessages().getEmbedText());

        StringBuilder stringBuilder = new StringBuilder();
        for (Context.GamingRole entry : context.getGamingRoles()) {
            stringBuilder.append(entry.formattedOutput());
        }
        embedBuilder.setDescription(stringBuilder.toString());
        return embedBuilder;
    }

    private void actOnNewMemberMessage(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Member author = event.getMessage().getMember();

        Role newMemberRole = guild.getRoleById(context.getRoleIds().getNewMember());
        List<Member> membersWithNewRole = guild.getMembersWithRoles(newMemberRole);
        if (membersWithNewRole.contains(author)) {
            TextChannel channel = guild.getTextChannelById(context.getChannelIds().getNewMembers());
            String content = event.getMessage().getContentStripped();
            if (content.length() > 32) {
                channel.sendMessage(String.format(context.getMessages().getLongName(), author.getAsMention())).queue();
            } else if (!Utils.hasOnlyLetters(content)) {
                channel.sendMessage(String.format(context.getMessages().getBadName(), author.getAsMention())).queue();
            } else {
                Role internalRole = guild.getRoleById(context.getRoleIds().getInternalMember());

                guild.modifyNickname(author, content).complete();
                guild.addRoleToMember(author, internalRole).queue();
                guild.removeRoleFromMember(author, newMemberRole).queue();
            }
        }
    }

    private void brandTheInvisible(MessageReceivedEvent event) {
        Member member = event.getMember();
        if (member.getOnlineStatus().equals(OnlineStatus.INVISIBLE) ||
                member.getOnlineStatus().equals(OnlineStatus.OFFLINE)) {
            Guild guild = event.getGuild();
            Role invisibleRole = guild.getRoleById(context.getRoleIds().getInvisible());
            List<Member> invisibleMembers = guild.getMembersWithRoles(invisibleRole);
            if (!invisibleMembers.contains(member)) {
                guild.addRoleToMember(member, invisibleRole).queue();
            }
        }
    }
}
