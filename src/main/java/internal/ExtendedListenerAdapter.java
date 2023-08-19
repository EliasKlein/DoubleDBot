package internal;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class ExtendedListenerAdapter extends ListenerAdapter {

    protected final Context context;

    public ExtendedListenerAdapter(Context context) {
        this.context = context;
    }
}
