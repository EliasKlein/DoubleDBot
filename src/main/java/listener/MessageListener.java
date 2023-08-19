package listener;

import internal.Context;
import internal.ExtendedListenerAdapter;
import internal.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

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
        }
    }

    private void actOnCommand(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        if (event.getMessage().getContentStripped().equals(context.getCommands().getReactionRoles())) {
            TextChannel rolesChannel = guild.getTextChannelById(context.getChannelIds().getRoles());
            if (event.getChannel().equals(rolesChannel)) {
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.decode(context.getMessages().getEmbedColorHex()));
                embedBuilder.setTitle(context.getMessages().getEmbedText());

                StringBuilder stringBuilder = new StringBuilder();
                for (Context.GamingRole entry : context.getGamingRoles()) {
                    stringBuilder.append(entry.formattedOutput());
                }
                embedBuilder.setDescription(stringBuilder.toString());

                rolesChannel.sendMessageEmbeds(embedBuilder.build()).queue(embed -> {
                    String embedId = embed.getId();
                    for (Context.GamingRole entry : context.getGamingRoles()) {
                        rolesChannel.addReactionById(embedId, entry.getEmoji()).queue();
                    }
                });
            }
        }
    }

    private void actOnNewMemberMessage(MessageReceivedEvent event) {
        Guild guild = event.getGuild();
        Member author = event.getMember();
        String content = event.getMessage().getContentStripped();
        if (Utils.hasOnlyLetters(content)) {
            Role newMemberRole = guild.getRoleById(context.getRoleIds().getNewMember());
            Role internalRole = guild.getRoleById(context.getRoleIds().getInternalMember());

            guild.modifyNickname(author, content).queue();
            guild.addRoleToMember(author, internalRole).queue();
            guild.removeRoleFromMember(author, newMemberRole).queue();
        } else {
            guild.getTextChannelById(context.getRoleIds().getNewMember())
                    .sendMessage(String.format(context.getMessages().getBadName(), author.getAsMention())).queue();
        }
    }
}
