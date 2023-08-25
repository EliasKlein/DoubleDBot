package internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class Utils {

    public static boolean hasOnlyLetters(String string) {
        for (char c : string.toCharArray()) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    public static Context.GamingRole getGamingRoleFromEmojiUnicode(Context context, String unicode) {
        for (Context.GamingRole entry : context.getGamingRoles()) {
            if (entry.getEmoji().getAsCodepoints().equals(unicode)) {
                return entry;
            }
        }
        return null;
    }

    public static Properties loadProperties(String propertiesName) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesName);
        Properties properties = new Properties();
        properties.load(in);
        in.close();
        return properties;
    }
}
