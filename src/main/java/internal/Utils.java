package internal;

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
}
