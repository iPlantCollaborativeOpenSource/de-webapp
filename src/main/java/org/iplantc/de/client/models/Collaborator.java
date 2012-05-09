/**
 * 
 */
package org.iplantc.de.client.models;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * A model class to store collaborators attributes
 * 
 * @author sriram
 * 
 */
public class Collaborator extends BaseModelData {

    /**
     * 
     */
    private static final long serialVersionUID = 3956502462940674969L;

    public static final String ID = "id";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL = "email";
    public static final String USERNAME = "userName";
    public static final String NAME = "name";

    public Collaborator(JsCollaborators jsCollaborators) {
        set(ID, jsCollaborators.getId());
        set(NAME, jsCollaborators.getFirstName() + " " + jsCollaborators.getLastName());
        set(EMAIL, jsCollaborators.getEmail());
        set(USERNAME, jsCollaborators.getUserName());
    }

    public String getId() {
        return get(ID);
    }

    /**
     * Get users full name formatted
     * 
     * @return get first name + last name
     */
    public String getName() {
        return get(NAME);
    }

    public String getEmail() {
        return get(EMAIL);
    }

    public String getUserName() {
        return get(USERNAME);
    }

    @Override
    public boolean equals(Object c) {
        if (c == null) {
            return false;
        }
        if (!(c instanceof Collaborator)) {
            return false;
        } else {
            Collaborator collab = (Collaborator)c;
            if (getId().equals(collab.getId())) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(getId());
    }
}
