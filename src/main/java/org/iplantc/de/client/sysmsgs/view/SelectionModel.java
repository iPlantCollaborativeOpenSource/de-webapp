package org.iplantc.de.client.sysmsgs.view;

import org.iplantc.de.client.sysmsgs.model.Message;
import org.iplantc.de.client.sysmsgs.view.DefaultMessagesViewResources.Style;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.sencha.gxt.widget.core.client.ListViewSelectionModel;
import com.sencha.gxt.widget.core.client.event.XEvent;

final class SelectionModel extends ListViewSelectionModel<Message> {

	private static final Style CSS;

    static {
        CSS = GWT.<DefaultMessagesViewResources> create(DefaultMessagesViewResources.class).style();
    	CSS.ensureInjected();
    }
 
	@Override
	protected void handleMouseDown(final MouseDownEvent mouseEvent) {
	    final XEvent event = mouseEvent.getNativeEvent().<XEvent> cast();
	    if (!event.getEventTargetEl().hasClassName(CSS.dismiss())) {
	    	super.handleMouseDown(mouseEvent);
	    }
	}
	
}

