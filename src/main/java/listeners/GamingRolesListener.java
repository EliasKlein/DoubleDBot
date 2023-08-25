package listeners;

import internal.Context;
import internal.ExtendedListenerAdapter;
import internal.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public class GamingRolesListener extends ExtendedListenerAdapter {

    public GamingRolesListener(Context context) {
        super(context);
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getMember().getId().equals(context.getBotId())) {
            if (event.getChannel().getId().equals(context.getChannelIds().getRoles())) {
                Context.GamingRole reactionRole = Utils.getGamingRoleFromEmojiUnicode(context,
                        event.getReaction().getEmoji().asUnicode().getAsCodepoints());
                if (reactionRole != null) {
                    Guild guild = event.getGuild();
                    Role role = guild.getRoleById(reactionRole.getId());
                    guild.addRoleToMember(event.getMember(), role).queue();
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (!event.getMember().getId().equals(context.getBotId())) {
            if (event.getChannel().getId().equals(context.getChannelIds().getRoles())) {
                Context.GamingRole reactionRole = Utils.getGamingRoleFromEmojiUnicode(context,
                        event.getReaction().getEmoji().asUnicode().getAsCodepoints());
                if (reactionRole != null) {
                    Guild guild = event.getGuild();
                    Role role = guild.getRoleById(reactionRole.getId());
                    guild.removeRoleFromMember(event.getMember(), role).queue();
                }
            }
        }
    }
}
