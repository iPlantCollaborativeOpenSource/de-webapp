package org.iplantc.de.client.views.windows.configs;

import java.util.List;

import org.iplantc.core.uidiskresource.client.models.DiskResource;
import org.iplantc.core.uidiskresource.client.models.Folder;

public interface IDropLiteWindowConfig extends WindowConfig {

    Folder getUploadFolderDest();

    List<DiskResource> getResourcesToDownload();

    Folder getCurrentFolder();

    int getDisplayMode();

    void setResourcesToDownload(List<DiskResource> resources);

    void setDisplayMode(int displayMode);

    void setCurrentFolder(Folder currentFolder);

    void setUploadFolderDest(Folder uploadDest);

}
