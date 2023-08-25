package listeners;

import internal.Context;
import internal.ExtendedListenerAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;

import java.util.concurrent.TimeUnit;

public class NewMemberListener extends ExtendedListenerAdapter {

    public NewMemberListener(Context context) {
        super(context);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();

        Role role = guild.getRoleById(context.getRoleIds().getNewMember());
        guild.addRoleToMember(event.getMember(), role).complete();

        TextChannel newMemberChannel = guild.getTextChannelById(context.getChannelIds().getNewMembers());
        newMemberChannel.sendMessage(String.format(context.getMessages().getWelcome(),
                event.getMember().getAsMention())).queueAfter(5L, TimeUnit.SECONDS);
    }
}
