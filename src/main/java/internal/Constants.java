package internal;

public abstract class Constants {

    public static final String CONFIG_PROPERTIES = "config.properties";
    public static final String CONTEXT_PROPERTIES = "context.properties";

    public static final String RESOURCE_FILE_PATH = "files/";

    public static final String CHEER_ME_UP_NAME = "cheer_me_up";
    public static final String CHEER_ME_UP_DESCRIPTION = "If you are feeling down";
    public static final int ULTRA_RARE_RANDOM_UPPER_BOUND = 420;

    public enum CHEER_ME_UP_TYPE {
        LINK,
        FILE,
        MESSAGE
    }
}
