package org.iplantc.de.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.iplantc.de.client.views.panels.FileUploadDialogPanel;
import org.iplantc.de.shared.services.MultiPartServiceWrapper;

/**
 * A class to accept files from the client.
 * 
 * This class extends the UploadAction class provided by the GWT Upload library. The executeAction method
 * must be overridden for custom behavior.
 * 
 * @author sriram
 * 
 */
public class FileUploadServlet extends UploadAction {
    private static final long serialVersionUID = 1L;

    /**
     * The logger for error and informational messages.
     */
    private static Logger LOG = Logger.getLogger(FileUploadServlet.class);

    /**
     * Performs the necessary operations for an upload action.
     * 
     * @param request the HTTP request associated with the action.
     * @param sessionFiles the file associated with the action.
     * @return a string representing data in JSON format.
     * @throws UploadActionException if there is an issue invoking the dispatch to the servlet
     */
    @Override
    public String executeAction(HttpServletRequest request, List<FileItem> sessionFiles)
            throws UploadActionException {
        String json = null;
        String idFolder = null;
        String user = null;
        String type = "AUTO"; //$NON-NLS-1$

        LOG.debug("Upload Action started."); //$NON-NLS-1$

        List<FileItem> fileItems = new ArrayList<FileItem>();

        for (FileItem item : sessionFiles) {
            if (item.isFormField()) {
                String name = item.getFieldName();
                String contents = new String(item.get());

                if (name.equals(FileUploadDialogPanel.HDN_PARENT_ID_KEY)) {
                    idFolder = contents;
                } else if (name.equals(FileUploadDialogPanel.HDN_USER_ID_KEY)) {
                    user = contents;
                } else if (name.equals(FileUploadDialogPanel.FILE_TYPE)) {
                    type = contents;
                }
            } else if (validFileInfo(item)) {
                fileItems.add(item);
            }
        }

        // do we have enough information to make a service call?
        if (sufficientData(user, idFolder, fileItems)) {
            json = invokeService(request, idFolder, user, type, fileItems);
        }

        // remove files from session. this avoids duplicate submissions
        removeSessionFileItems(request, false);

        LOG.debug("FileUploadServlet::executeAction - JSON returned: " + json); //$NON-NLS-1$
        return json;
    }

    /**
     * Handles the invocation of the file upload service.
     * 
     * @param request current HTTP request
     * @param idFolder the folder identifier for where the file will be related
     * @param user the name of the user account that is uploading the file
     * @param type the file type. It can be AUTO or CSVNAMELIST
     * @param fileItems a list of files to be uploaded
     * @return a string representing data in JSON format.
     * @throws UploadActionException if there is an issue invoking the dispatch to the servlet
     */
    private String invokeService(HttpServletRequest request, String idFolder, String user, String type,
            List<FileItem> fileItems) throws UploadActionException {
        String filename;
        long fileLength;
        String mimeType;
        InputStream fileContents;

        JSONObject jsonResults = new JSONObject();
        JSONArray jsonResultsArray = new JSONArray();

        for (FileItem item : fileItems) {
            filename = item.getName();
            fileLength = item.getSize();
            mimeType = item.getContentType();

            try {
                fileContents = item.getInputStream();
            } catch (IOException e) {
                LOG.error(
                        "FileUploadServlet::executeAction - Exception while getting file input stream.", //$NON-NLS-1$
                        e);
                e.printStackTrace();

                // add the error to the results array, in case some files successfully uploaded already.
                jsonResultsArray.add(buildJsonError(idFolder, type, filename, e));
                jsonResults.put("results", jsonResultsArray);
                throw new UploadActionException(jsonResults.toString());
            }

            MultiPartServiceWrapper wrapper = createServiceWrapper(idFolder, user, type, filename,
                    fileLength, mimeType, fileContents);

            // call the RESTful service and get the results.
            try {
                DEServiceDispatcher dispatcher = new DEServiceDispatcher();
                dispatcher.init(getServletConfig());
                dispatcher.setRequest(request);
                String repsonse = dispatcher.getServiceData(wrapper);
                LOG.debug("FileUploadServlet::executeAction - Making service call."); //$NON-NLS-1$

                jsonResultsArray.add(JSONObject.fromObject(repsonse));
            } catch (Exception e) {
                LOG.error("FileUploadServlet::executeAction - unable to upload file", e); //$NON-NLS-1$
                e.printStackTrace();

                // add the error to the results array, in case some files successfully uploaded already.
                jsonResultsArray.add(buildJsonError(idFolder, type, filename, e));
                jsonResults.put("results", jsonResultsArray);
                throw new UploadActionException(jsonResults.toString());
            }
        }

        jsonResults.put("results", jsonResultsArray);
        return jsonResults.toString();
    }

    private JSONObject buildJsonError(String idFolder, String type, String filename, Throwable e) {
        JSONObject ret = new JSONObject();

        ret.put("action", "file-upload");
        ret.put("status", "failure");
        ret.put("reason", e.getMessage());
        ret.put("id", idFolder + "/" + filename);
        ret.put("label", filename);
        ret.put("type", type);

        return ret;
    }

    /**
     * Constructs and configures a multi-part service wrapper.
     * 
     * @param idFolder the folder identifier for where the file will be related
     * @param user the name of the user account that is uploading the file
     * @param type the file type. It can be AUTO or CSVNAMELIST
     * @param filename the name of the file being uploaded
     * @param fileLength the length of the file being uploaded.
     * @param fileContents the content of the file
     * @return an instance of a multi-part service wrapper.
     */
    private MultiPartServiceWrapper createServiceWrapper(String idFolder, String user, String type,
            String filename, long fileLength, String mimeType, InputStream fileContents) {
        // TODO: Should there be a FileServices class that is wrapping all of
        // this like
        // FolderServices/etc.???
        String address = DiscoveryEnvironmentProperties.getUploadFileServiceBaseUrl();

        // build our wrapper
        MultiPartServiceWrapper wrapper = new MultiPartServiceWrapper(MultiPartServiceWrapper.Type.POST,
                address);
        wrapper.addPart(new FileHTTPPart(fileContents, "file", filename, mimeType, fileLength)); //$NON-NLS-1$
        wrapper.addPart(idFolder + "/" + filename, "dest"); //$NON-NLS-1$ //$NON-NLS-2$
        wrapper.addPart(user, "user"); //$NON-NLS-1$
        wrapper.addPart(type, "type"); //$NON-NLS-1$

        return wrapper;
    }

    /**
     * Determines if sufficient data is present to perform an action.
     * 
     * @param user the name of the user account that is uploading the file
     * @param idFolder the folder identifier for where the file will be related
     * @param fileItems a list of files to be uploaded
     * @return true if all argument have valid values; otherwise false
     */
    private boolean sufficientData(String user, String idFolder, List<FileItem> fileItems) {
        boolean validFileItems = false;
        if (fileItems != null) {
            for (FileItem item : fileItems) {
                if (validFileInfo(item)) {
                    validFileItems = true;
                    break;
                }
            }
        }

        return validFileItems && user != null && !user.isEmpty() && idFolder != null
                && !idFolder.isEmpty();
    }

    private boolean validFileInfo(FileItem item) {
        return item != null && item.getName() != null && !item.getName().isEmpty()
                && item.getContentType() != null && !item.getContentType().isEmpty()
                && item.getSize() > 0;
    }
}
