package org.iplantc.de.client.idroplite.presenter;

import org.iplantc.core.jsonutil.JsonUtil;
import org.iplantc.core.uicommons.client.ErrorHandler;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uidiskresource.client.events.RequestSimpleDownloadEvent;
import org.iplantc.core.uidiskresource.client.events.RequestSimpleUploadEvent;
import org.iplantc.de.client.Services;
import org.iplantc.de.client.idroplite.util.IDropLiteUtil;
import org.iplantc.de.client.idroplite.views.IDropLiteView;
import org.iplantc.de.client.idroplite.views.IDropLiteView.Presenter;
import org.iplantc.de.client.views.windows.configs.IDropLiteWindowConfig;

import com.google.common.collect.Sets;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;

/**
 * @author sriram
 * 
 */
public class IDropLitePresenter implements Presenter {

    private final IDropLiteView view;
    private final int CONTENT_PADDING = 12;
    private IDropLiteWindowConfig idlwc;

    public IDropLitePresenter(IDropLiteView view, IDropLiteWindowConfig config) {
        this.view = view;
        view.setPresenter(this);
        this.idlwc = config;
    }

    @Override
    public void buildUploadApplet() {
        view.mask();
        Services.DISK_RESOURCE_SERVICE.upload(new IDropLiteServiceCallback() {
            @Override
            protected HtmlLayoutContainer buildAppletHtml(JSONObject appletData) {
                int adjustSize = CONTENT_PADDING * 2;

                appletData.put("uploadDest", new JSONString(idlwc.getUploadFolderDest().getId())); //$NON-NLS-1$

                return IDropLiteUtil.getAppletForUpload(appletData, view.getViewWidth()
                        - CONTENT_PADDING, view.getViewHeight() - adjustSize);
            }
        });

    }

    @Override
    public void buildDownloadApplet() {
        view.mask();
        Services.DISK_RESOURCE_SERVICE.download(idlwc.getDownloadPaths(),
                new IDropLiteServiceCallback() {
            @Override
            protected HtmlLayoutContainer buildAppletHtml(JSONObject appletData) {
                int adjustSize = CONTENT_PADDING * 2;

                return IDropLiteUtil.getAppletForDownload(appletData, view.getViewWidth()
                        - CONTENT_PADDING, view.getViewHeight() - adjustSize);
            }
        });

    }

    /**
     * Common success and failure handling for upload and download service calls.
     * 
     * @author psarando
     * 
     */
    private abstract class IDropLiteServiceCallback implements AsyncCallback<String> {
        /**
         * Builds the Html for the idrop-lite applet from the given JSON applet data returned by the
         * service call.
         * 
         * @param appletData
         * @return Html applet with the given applet params.
         */
        protected abstract HtmlLayoutContainer buildAppletHtml(JSONObject appletData);

        @Override
        public void onSuccess(String response) {
            view.setApplet(buildAppletHtml(JsonUtil.getObject(JsonUtil.getObject(response), "data"))); //$NON-NLS-1$
            view.unmask();
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
        }
    }

    @Override
    public void onSimpleUploadClick() {
        EventBus.getInstance().fireEvent(new RequestSimpleUploadEvent(this, idlwc.getUploadFolderDest()));
    }

    @Override
    public void onSimpleDownloadClick() {
        EventBus.getInstance().fireEvent(new RequestSimpleDownloadEvent(this, Sets.newHashSet(idlwc.getResourcesToDownload()), idlwc.getCurrentFolder()));
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.asWidget());
        int mode = idlwc.getDisplayMode();
        if (mode == IDropLiteUtil.DISPLAY_MODE_UPLOAD) {
            buildUploadApplet();
        } else if (mode == IDropLiteUtil.DISPLAY_MODE_DOWNLOAD) {
            buildDownloadApplet();
        }
        view.setToolBarButton(mode);
    }
}