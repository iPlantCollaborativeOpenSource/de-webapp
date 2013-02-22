package org.iplantc.de.client.views.windows.configs;

import org.iplantc.core.uicommons.client.models.HasId;

public interface AppsWindowConfig extends WindowConfig {

    HasId getSelectedAppGroup();

    HasId getSelectedApp();

    void setSelectedAppGroup(HasId appGroup);

    void setSelectedApp(HasId app);

}
