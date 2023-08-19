import internal.Context;
import listener.GamingRolesListener;
import listener.MessageListener;
import listener.NewMemberListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Bot {

    public static void main(String[] args) {
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
            Properties properties = new Properties();
            properties.load(in);

            Context context = new Context(
                    Integer.parseInt(properties.getProperty("encryptor.pool_size")),
                    System.getenv(properties.getProperty("env.encryption_pw_name")),
                    System.getenv(properties.getProperty("env.encryption_salt_name")));

            JDABuilder builder = JDABuilder.createDefault(System.getenv(properties.getProperty("env.token_name")));

            in.close();

            builder.setStatus(OnlineStatus.ONLINE);
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
            builder.addEventListeners(new MessageListener(context));
            builder.addEventListeners(new GamingRolesListener(context));
            builder.addEventListeners(new NewMemberListener(context));
            builder.build();
        } catch (IOException e) {
            System.out.println("Property File not found");
        }
    }
}
