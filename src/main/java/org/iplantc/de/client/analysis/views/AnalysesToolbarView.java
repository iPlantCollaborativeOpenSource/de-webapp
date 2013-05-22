/**
 * 
 */
package org.iplantc.de.client.analysis.views;

import org.iplantc.de.client.analysis.models.Analysis;
import org.iplantc.de.client.analysis.widget.AnalysisSearchField;

import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;

/**
 * @author sriram
 * 
 */
public interface AnalysesToolbarView extends IsWidget {

    public interface Presenter extends org.iplantc.core.uicommons.client.presenter.Presenter {
        void onDeleteClicked();

        void onViewParamClicked();

        void onCancelClicked();

        void setRefreshButton(TextButton refreshBtn);
    }

    void setDeleteButtonEnabled(boolean enabled);

    void setViewParamButtonEnabled(boolean enabled);

    void setCancelButtonEnabled(boolean enabled);

    void setPresenter(Presenter p);

    void setRefreshButton(TextButton refreshBtn);

    PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> getLoader();

    AnalysisSearchField getFilterField();
}