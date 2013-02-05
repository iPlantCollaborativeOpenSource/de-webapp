package org.iplantc.de.client.events;

import org.iplantc.de.client.events.WindowShowRequestEvent.WindowShowRequestEventHandler;
import org.iplantc.de.client.views.windows.configs.WindowConfig;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This event is fired when one desires a Discovery Environment window to be shown.
 * 
 * @author jstroot
 * 
 */
public class WindowShowRequestEvent extends GwtEvent<WindowShowRequestEventHandler> {

    public interface WindowShowRequestEventHandler extends EventHandler {

        void onWindowShowRequest(WindowShowRequestEvent event);

    }

    public static final GwtEvent.Type<WindowShowRequestEventHandler> TYPE = new GwtEvent.Type<WindowShowRequestEventHandler>();
    private final WindowConfig windowConfig;

    public WindowShowRequestEvent(WindowConfig config) {
        this.windowConfig = config;
    }

    @Override
    public GwtEvent.Type<WindowShowRequestEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(WindowShowRequestEventHandler handler) {
        handler.onWindowShowRequest(this);
    }

    public WindowConfig getWindowConfig() {
        return windowConfig;
    }

}
