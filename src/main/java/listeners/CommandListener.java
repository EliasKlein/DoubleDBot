package listeners;

import internal.Constants;
import internal.Context;
import internal.ExtendedListenerAdapter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.InputStream;
import java.util.List;
import java.util.Random;

public class CommandListener extends ExtendedListenerAdapter {

    public CommandListener(Context context) {
        super(context);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(Constants.CHEER_ME_UP_NAME)) {
            Random rand = new Random();
            if (context.hasUltraRareCheerMeUp()) {
                if (rand.nextInt(Constants.ULTRA_RARE_RANDOM_UPPER_BOUND) == 0) {
                    sendCheerMeUp(event, context.getUltraRareCheerMeUp());
                    return;
                }
            }
            if (context.hasSpecialCheerMeUps()) {
                Guild guild = event.getGuild();
                String nickname = guild.getMemberById(event.getUser().getId()).getNickname();
                if (context.getSpecialCheerMeUps().containsKey(nickname)) {
                    sendCheerMeUp(event, context.getSpecialCheerMeUps().get(nickname));
                    return;
                }
            }
            if (context.hasGenericCheerMeUps()) {
                List<Context.CheerMeUp> cheerMeUps = context.getGenericCheerMeUps();
                sendCheerMeUp(event, cheerMeUps.get(rand.nextInt(cheerMeUps.size())));
                return;
            }
            event.reply(String.format("Hang in there, %s. Oh, kick your admins butt to add some funny cheer me ups!",
                    event.getUser().getAsMention())).queue();
        }
    }

    public void sendCheerMeUp(SlashCommandInteractionEvent event, Context.CheerMeUp cheerMeUp) {
        try {
            switch (cheerMeUp.getType()) {
                case MESSAGE:
                    event.reply(cheerMeUp.getContent()).queue();
                    break;
                case LINK:
                    event.reply(cheerMeUp.getExtraMessage() +
                            (cheerMeUp.getExtraMessage().isEmpty() ? "" : "\n") +
                            cheerMeUp.getContent()).queue();
                    break;
                case FILE:
                    InputStream in = Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream(Constants.RESOURCE_FILE_PATH + cheerMeUp.getContent());
                    FileUpload file = FileUpload.fromData(in, cheerMeUp.getContent());
                    event.reply(cheerMeUp.getExtraMessage()).addFiles(file).queue();
                    break;
            }
        } catch (Exception e) {
            event.reply(String.format(String.format(
                    "Hey %s, wanna hear a joke? This cheer me up setup. Either the dev can't get their job done or your admin can't set it up, but in the end the joke is on you, I guess.",
                    event.getUser().getAsMention()))).queue();
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        event.getGuild().updateCommands()
                .addCommands(Commands.slash(Constants.CHEER_ME_UP_NAME, Constants.CHEER_ME_UP_DESCRIPTION)).queue();
    }
}
