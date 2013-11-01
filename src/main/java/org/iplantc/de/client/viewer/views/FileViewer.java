/**
 * 
 */
package org.iplantc.de.client.viewer.views;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author sriram
 * 
 */
public interface FileViewer extends IsWidget {

    public static final int MIN_PAGE_SIZE_KB = 8;
    public static final int MAX_PAGE_SIZE_KB = 64;
    public static final int PAGE_INCREMENT_SIZE_KB = 8;

    public interface Presenter extends org.iplantc.core.uicommons.client.presenter.Presenter {
        void composeView(JSONObject manifest);
    }

    void setPresenter(Presenter p);

    void setData(Object data);

}
