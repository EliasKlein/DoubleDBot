import internal.Constants;
import internal.Context;
import listeners.CommandListener;
import listeners.GamingRolesListener;
import listeners.MessageListener;
import listeners.NewMemberListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.IOException;
import java.util.Properties;

import static internal.Utils.loadProperties;

public class Bot {

    public static void main(String[] args) {
        try {
            Properties properties = loadProperties(Constants.CONFIG_PROPERTIES);

            Context context = new Context(
                    Integer.parseInt(properties.getProperty("encryptor.pool_size")),
                    System.getenv(properties.getProperty("env.encryption_pw_name")),
                    System.getenv(properties.getProperty("env.encryption_salt_name")));

            JDABuilder builder = JDABuilder.createDefault(System.getenv(properties.getProperty("env.token_name")));
            builder.setStatus(OnlineStatus.ONLINE);
            builder.setChunkingFilter(ChunkingFilter.ALL);
            builder.setMemberCachePolicy(MemberCachePolicy.ALL);
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
            builder.addEventListeners(new MessageListener(context));
            builder.addEventListeners(new GamingRolesListener(context));
            builder.addEventListeners(new NewMemberListener(context));
            builder.addEventListeners(new CommandListener(context));
            builder.build();
        } catch (IOException e) {
            System.out.println("Property File not found");
        }
    }
}
