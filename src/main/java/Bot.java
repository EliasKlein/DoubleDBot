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
import net.dv8tion.jda.api.utils.cache.CacheFlag;

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

            JDABuilder.createDefault(System.getenv(properties.getProperty("env.token_name")))
                    .setStatus(OnlineStatus.ONLINE)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableCache(CacheFlag.ONLINE_STATUS)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_PRESENCES)
                    .addEventListeners(new MessageListener(context),
                            new GamingRolesListener(context),
                            new NewMemberListener(context),
                            new CommandListener(context))
                    .build();
        } catch (IOException e) {
            System.out.println("Property File not found");
        }
    }
}
