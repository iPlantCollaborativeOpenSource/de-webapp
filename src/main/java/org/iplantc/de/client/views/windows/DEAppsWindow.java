package org.iplantc.de.client.views.windows;

import org.iplantc.core.uiapplications.client.CommonAppDisplayStrings;
import org.iplantc.core.uiapplications.client.I18N;
import org.iplantc.core.uiapplications.client.models.CatalogWindowConfig;
import org.iplantc.core.uiapplications.client.models.autobeans.Analysis;
import org.iplantc.core.uiapplications.client.models.autobeans.AnalysisGroup;
import org.iplantc.core.uiapplications.client.presenter.AppsViewPresenter;
import org.iplantc.core.uiapplications.client.services.AppTemplateServiceFacade;
import org.iplantc.core.uiapplications.client.views.AnalysisColumnModel;
import org.iplantc.core.uiapplications.client.views.AppsView;
import org.iplantc.core.uiapplications.client.views.AppsViewImpl;
import org.iplantc.core.uicommons.client.events.EventBus;
import org.iplantc.core.uicommons.client.models.UserInfo;
import org.iplantc.core.uicommons.client.models.WindowConfig;
import org.iplantc.de.client.Constants;
import org.iplantc.de.client.dispatchers.WindowDispatcher;
import org.iplantc.de.client.factories.EventJSONFactory.ActionType;
import org.iplantc.de.client.factories.WindowConfigFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;

public class DEAppsWindow extends Gxt3IplantWindow {

    private final AppsView.Presenter presenter;

    public DEAppsWindow(String tag, WindowConfig config) {
        super(tag, config);
        // FIXME JDS Use dependency injection to get the classes needed in the constructor.
        CommonAppDisplayStrings commonAppDisplayStrings = org.iplantc.core.uiapplications.client.I18N.DISPLAY;
        CommonAppDisplayStrings deDisplayStrings = I18N.DISPLAY;
        EventBus eventBus = EventBus.getInstance();
        AppTemplateServiceFacade templateService = GWT.create(AppTemplateServiceFacade.class);
        UserInfo userInfo = UserInfo.getInstance();

        TreeStore<AnalysisGroup> treeStore = new TreeStore<AnalysisGroup>(
                new AnalysisGroupModelKeyProvider());
        ListStore<Analysis> listStore = new ListStore<Analysis>(new AnalysisModelKeyProvider());
        AnalysisColumnModel cm = new AnalysisColumnModel(eventBus, commonAppDisplayStrings);

        AppsView view = new AppsViewImpl(treeStore, listStore, cm);
        presenter = new AppsViewPresenter(view, templateService, deDisplayStrings, userInfo,
                (CatalogWindowConfig)config);

        setSize("800", "410");
        presenter.go(this);
    }

    private final class AnalysisGroupModelKeyProvider implements ModelKeyProvider<AnalysisGroup> {
        @Override
        public String getKey(AnalysisGroup item) {
            return item.getId();
        }
    }

    private final class AnalysisModelKeyProvider implements ModelKeyProvider<Analysis> {
        @Override
        public String getKey(Analysis item) {
            return item.getId();
        }
    }

    @Override
    public JSONObject getWindowState() {
        CatalogWindowConfig configData = new CatalogWindowConfig(config);
        storeWindowViewState(configData);

        if (presenter.getSelectedAnalysis() != null) {
            configData.setAppId(presenter.getSelectedAnalysis().getId());
        }

        if (presenter.getSelectedAnalysisGroup() != null) {
            configData.setCategoryId(presenter.getSelectedAnalysisGroup().getId());
        }

        // Build window config
        WindowConfigFactory configFactory = new WindowConfigFactory();
        JSONObject windowConfig = configFactory.buildWindowConfig(Constants.CLIENT.deCatalog(),
                configData);
        WindowDispatcher dispatcher = new WindowDispatcher(windowConfig);
        return dispatcher.getDispatchJson(Constants.CLIENT.deCatalog(), ActionType.DISPLAY_WINDOW);
        // return config;
    }

    @Override
    public void setWindowConfig(WindowConfig config) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMinimized(boolean min) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isMinimized() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getHeaderOffSetHeight() {
        // TODO Auto-generated method stub
        return 0;
    }

}
